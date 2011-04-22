;; Copyright (c) 2011 Alfredo Di Napoli, https://github.com/CharlesStain/clj3D

;; Permission is hereby granted, free of charge, to any person obtaining
;; a copy of this software and associated documentation files (the
;; "Software"), to deal in the Software without restriction, including
;; without limitation the rights to use, copy, modify, merge, publish,
;; distribute, sublicense, and/or sell copies of the Software, and to
;; permit persons to whom the Software is furnished to do so, subject to
;; the following conditions:

;; The above copyright notice and this permission notice shall be
;; included in all copies or substantial portions of the Software.

;; THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
;; EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
;; MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
;; NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
;; LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
;; OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
;; WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.


(ns clj3D.fenvs
  (:refer-clojure :rename {+ core-+})
  (:use
    [matchure]
    [clj3D curry math])
  (:import
    [com.jme3.math Vector3f Vector2f ColorRGBA Quaternion Transform]
    [com.jme3.asset AssetManager]
    [com.jme3.system JmeSystem]
    [com.jme3.material Material]
    [com.jme3.scene.shape Box Line Sphere Cylinder Torus Quad Dome]
    [com.jme3.scene Node Geometry Spatial Mesh Mesh$Mode VertexBuffer VertexBuffer$Type]
    [jme3tools.optimize GeometryBatchFactory]
    [com.jme3.asset.plugins FileLocator]))


;;For performance tweaking. Just ignore this.
(set! *warn-on-reflection* true)


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Graphics stuff.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(def ^{:private true} colors
  { :gray (ColorRGBA/Gray),
    :green (ColorRGBA/Green),
    :black (ColorRGBA/Black),
    :blue (ColorRGBA/Blue),
    :brown (ColorRGBA/Brown),
    :cyan (ColorRGBA/Cyan),
    :magenta (ColorRGBA/Magenta),
    :orange (ColorRGBA/Orange),
    :purple (ColorRGBA. 0.5 0.0 0.8 1.0),
    :pink (ColorRGBA/Pink),
    :white (ColorRGBA/White),
    :red (ColorRGBA/Red),
    :yellow (ColorRGBA/Yellow)})


(def ^{:private true} default-color (ColorRGBA/LightGray))


(def ^{:private true} asset-manager
     (JmeSystem/newAssetManager
      (.getResource (.getContextClassLoader (Thread/currentThread))
		    "com/jme3/asset/Desktop.cfg")))


(defn- default-material []
  (doto (Material. asset-manager "Common/MatDefs/Light/Lighting.j3md")
    (.setBoolean "UseMaterialColors" true)
    (.setColor "Ambient"  (get colors :black))
    (.setColor "Diffuse" default-color)
    (.setColor "Specular" (get colors :white))
    (.setFloat "Shininess" 10)))


(defn- unlit-material []
  (doto (Material. asset-manager "Common/MatDefs/Misc/Unshaded.j3md")
    (.setColor "Color"  default-color)))


(defn- colored-material
  [color-keyword]
  (doto (Material. asset-manager "Common/MatDefs/Light/Lighting.j3md")
    (.setBoolean "UseMaterialColors" true)
    (.setColor "Ambient"  (get colors :black))
    (.setColor "Diffuse" (get colors color-keyword))
    (.setColor "Specular" (get colors :white))
    (.setFloat "Shininess" 10)))


(defhigh color
  "Change the color of the shape, then return the spatial itself."
  [color-symbol ^Spatial object]
  (.setMaterial object (colored-material color-symbol))
  object)


(.registerLocator ^AssetManager asset-manager
		  (str (. System getProperty "user.dir") "/models")
		  "com.jme3.asset.plugins.FileLocator")


(defn- optimize
  "Optimize a Spatial. Internal use."
  [spatial]
  (doto ^Node (GeometryBatchFactory/optimize spatial)
	(.setName "structured")))


(defhigh merge-geometries
  [geom1 geom2]
  (let [out-mesh (Mesh.)]
    (GeometryBatchFactory/mergeGeometries [geom1 geom2] out-mesh)
    (doto (Geometry. "structured" out-mesh)
      (optimize)
      (.setMaterial (default-material)))))


(extend-protocol Addable
  Geometry
  (+ [g1 g2] (merge-geometries g1 g2)))


(defn mknode
  "It takes a list or an undefined number of geometries and put them into
   a single node. Materials are preserverd."
  [& objs]
  (let [flattened-args (flatten objs)
	new-node (Node. "node")]
    (doseq [geom flattened-args] (.attachChild new-node geom))
    (optimize new-node)))


(extend-protocol Addable
  Node
  (+ [nd1 nd2] (mknode nd1 nd2)))


;; i.e the original STRUCT from Plasm
(defn struct2
  "Merge all the input given meshs into a single one.
  It's a fairly powerful function, since its input can be
  an arbitrary number of functions and geometrical object.
  The input is visited from right to left, applying (if present)
  the functions to the Geometry objects. The only constrain is
  that the result must be a Geometry object. You can even pass a
  sequence of transformation/geometries.
  Usage:
  (struct2 (cube 1) (t 1 1) (sphere 1) -> returns a translated sphere joined
  with a cube.
  (struct 2 [(color :red) (cube 1)]) -> returns a red cube."
  [& args]
  (let [flattened-args (flatten args)
        rev-seq (reverse flattened-args)]
    (reduce (fn [x y]
	      (cond-match
	       [[com.jme3.scene.Spatial com.jme3.scene.Spatial] [x y]] (+ x y)
	       [[com.jme3.scene.Spatial ?func] [x y]] (func x)
	       [[?func com.jme3.scene.Spatial] [x y]] (func x)
	       [? [x y]] (throw (IllegalArgumentException. "Invalid input for struct")))) rev-seq)))


(defhigh attach
  "Attach a child to the given node."
  [^Node node child]
  (.attachChild node child))


(defhigh t
  "Translate function. High order function.
  Usage:
  (t 1 2.0 (cube 1)) -> move the cube from <0,0,0> to <2.0,0,0>
  (t [1 2] [3.0 2.0] (cube 1) -> move the cube to <3.0,2.0,0>"
  [axes value geom]
  (doto ^Node geom
    (.move (jvector axes value))))


(defhigh old-r
  "Rotate function. High order function."
  [axis rotation geom]
  (let [quaternion (Quaternion.)]
    (.fromAngleAxis quaternion rotation (jvector axis 1.0))
    (doto ^Node geom
	  (.setLocalRotation quaternion))))

(defhigh r
  "Rotate function. High order function."
  [axis rotation spatial]
  (let [rotation-pivot (Node.)
	quaternion (Quaternion.)]
    (.attachChild rotation-pivot spatial)
    (.setLocalTranslation rotation-pivot (Vector3f/ZERO))
    (.fromAngleAxis quaternion rotation (jvector axis 1.0))
    (.setLocalRotation rotation-pivot quaternion)
    (let [world-transform ^Transform (.getWorldTransform rotation-pivot)
	  prev-transform ^Transform (.getLocalTransform ^Spatial spatial)]
      (doto ^Spatial spatial
	(.setLocalTransform (.combineWithParent prev-transform world-transform))))))


(defhigh s
  "Scale function. High order function.
  Usage:
  (s 1 0.5 (cube 1)) -> scale 50% of cube x-side
  (s [1 2] [0.5 0.7] (cube 1)) -> scale 50% x and 70% y."
  [axes value geom]
  (cond-match

   [-1 value]
   (cond
    (= 1 axes) (r 3 (/ PI 2) geom)
    (= 2 axes) (r 1 (/ PI 2) geom)
    (= 3 axes) (r 2 (/ PI 2) geom)
    :else (throw (IllegalArgumentException. "Can't mirror along given axis.")))

    [java.lang.Integer axes]
    (let [av-map (hash-map (dec axes) value)]
      (doto ^Node geom
	    (.scale (get av-map 0 1.0) (get av-map 1 1.0) (get av-map 2 1.0))))

    
    [clojure.lang.IPersistentCollection axes]
    (let [av-map (reduce into (map hash-map (map dec axes) value))]
      (doto ^Node geom
	    (.scale (get av-map 0 1.0) (get av-map 1 1.0) (get av-map 2 1.0))))

    [? axes] (throw (IllegalArgumentException. "Invalid input for scale"))))


(defhigh skeleton
  [dim geom]
  (cond-match

   [0 dim]
   (let [mesh (.getMesh ^Geometry geom)]
     (.setMode mesh Mesh$Mode/Points)
     (.setPointSize mesh 5.0)
     (.updateBound mesh)
     (.setStatic mesh)
     (doto ^Geometry (Geometry. "skeleton-0" mesh)
	   (.setMaterial (unlit-material))
	   (.. getMaterial (setColor "Color" ColorRGBA/Red))))

    [1 dim]
    (doto ^Geometry geom
	  (.setMaterial (unlit-material))
	  (.. getMaterial getAdditionalRenderState (setWireframe true)))

    [? dim] geom))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; PRIMITIVES
;; NOTE: Apparently PLaSM and JMonkey use the same coordinate system, but in
;; jmonkey the camera is rotated. Need to fix this.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(defn cuboid
  "Returns a new x*y*z cuboid with bottom-left corner in (0,0,0)"
  [x y z]
  (let [[px py pz] (map #(/ %1 2.0) [x y z])
        box (Box. (Vector3f. px py pz) px py pz)
        cuboid (Geometry. "cuboid" box)]
    (.setMaterial cuboid (default-material))
    (mknode cuboid)))


(defn cube
  "Returns a new n*n*n cube with bottom-left corner in (0,0,0)"
  [side]
  (mknode (cuboid side side side)))


(defn hexaedron [] (mknode (cube 1)))


(defn line
  "New in clj3D. Draw a simple line from start to end"
  [[x1 y1 & z1] [x2 y2 & z2]]
     (let [z1 (if (seq z1) (first z1) 0)
	   z2 (if (seq z2) (first z2) 0)
	   line-mesh (Line. (Vector3f. x1 y1 z1) (Vector3f. x2 y2 z2))
	   line-geom (Geometry. "line" line-mesh)]
       (.setMaterial line-geom (unlit-material))
       (mknode line-geom)))


(defn sphere
  "Returns a new sphere in (0,0,0).
  (sphere radius) -> returns a sphere of the given radius
  (sphere radius x y) -> returns a sphere composed by x and
  y segment, of the given radius."
  ([radius]
     (let [sphere-mesh (Sphere. 50 50 radius)
	   sphere (Geometry. "sphere" sphere-mesh)]
       (.setMaterial sphere (default-material))
       (mknode sphere)))

  ([radius z-seg r-seg]
     (let [sphere-mesh (Sphere. z-seg r-seg radius)
	   sphere (Geometry. "sphere" sphere-mesh)]
       (.setMaterial sphere (default-material))
       (mknode sphere))))


(defn cylinder
  "Returns a new cylinder in (0,0,0).
  (cylinder radius height) -> returns a cylinder of the given radius and height
  (cylinder radius height x y) -> returns a cylinder composed by x and
  y segment, of the given radius and height."
  ([radius height]
  (let [cylinder-mesh (Cylinder. 50 50 radius height true)
         cylinder (Geometry. "cylinder" cylinder-mesh)]
    (t 3 (/ height 2.0) cylinder)
    (mknode
     (doto ^Geometry cylinder
	   (.setMaterial (default-material))))))

  ([radius height z-seg r-seg]
  (let [cylinder-mesh (Cylinder. z-seg r-seg radius height true)
         cylinder (Geometry. "cylinder" cylinder-mesh)]
    (t 3 (/ height 2.0) cylinder)
    (mknode
     (doto ^Geometry cylinder
	   (.setMaterial (default-material)))))))


(defn cone
  "Returns a new cone in (0,0,0)"
  ([radius height]
  (let [cone-mesh (Cylinder. 50 50 0 radius height true false)
         cone (Geometry. "cone" cone-mesh)]
    (t 3 (/ height 2.0) cone)
    (mknode
     (doto ^Geometry cone
	   (.setMaterial (default-material))))))

  ([radius height z-seg r-seg]
  (let [cone-mesh (Cylinder. z-seg r-seg 0 radius height true false)
         cone (Geometry. "cone" cone-mesh)]
    (t 3 (/ height 2.0) cone)
    (mknode
     (doto ^Geometry cone
	   (.setMaterial (default-material)))))))


(defn torus
  "Returns a new torus in (0,0,0).
  (torus r1 r2) -> returns a torus of the given radius r1 and r2
  (torus r1 r2 x y) -> returns a torus composed by x and
  y segment, of the given radius r1 and r2."
  ([r1 r2]
  (let [torus-mesh (Torus. 50 50 r1 r2)
         torus (Geometry. "torus" torus-mesh)]
    (mknode
     (doto torus
      (.setMaterial (default-material))))))

  ([r1 r2 z-seg r-seg]
  (let [torus-mesh (Torus. z-seg r-seg r1 r2)
         torus (Geometry. "torus" torus-mesh)]
    (mknode
     (doto torus
      (.setMaterial (default-material)))))))


(defn triangle
  "Returns a new 2D triangle in with vertexes v1 v2 v3."
  [v1 v2 v3]
  (let [v1 (when (= 2 (count v1)) (conj (vec v1) 0))
	v2 (when (= 2 (count v2)) (conj (vec v2) 0))
	v3 (when (= 2 (count v3)) (conj (vec v3) 0))
	triangle-mesh (Mesh.)
        vertices (float-array (flatten [v1 v2 v3]))
        tex-cord (float-array [0 0 1 0 0 1])
        indexes (int-array [2 0 1 1 0 2])
        normals (float-array (flatten (repeat 3 [0 0 1])))]

    (doto ^Mesh triangle-mesh
      (.setBuffer VertexBuffer$Type/Position 3 vertices)
      (.setBuffer VertexBuffer$Type/Normal 3 normals)
      (.setBuffer VertexBuffer$Type/TexCoord 2 tex-cord)
      (.setBuffer VertexBuffer$Type/Index 1 indexes)
      (.updateBound))

    (mknode
     (doto (Geometry. "triangle" triangle-mesh)
      (.setMaterial (default-material))))))


(defn trianglestripe
  "Returns a new 2D trianglestripe."
  [& points]
  (let [stripe-mesh (Mesh.)
	points (for [pt points] (if (= 2 (count pt)) (conj (vec pt) 0) pt))
	size (count points)
        vertices (float-array (flatten points))
        tex-cord (float-array (flatten (repeat size [0 0 1])))
	idx-triples (flatten
		     (map vector (repeat size 0) (range 1 (dec size)) (range 2 size)))
        indexes (int-array (+ idx-triples (reverse idx-triples)))
        normals (float-array (flatten (repeat (count points) [0 0 1])))]

    (doto ^Mesh stripe-mesh
      (.setBuffer VertexBuffer$Type/Position 3 vertices)
      (.setBuffer VertexBuffer$Type/Normal 3 normals)
      (.setBuffer VertexBuffer$Type/TexCoord 2 tex-cord)
      (.setBuffer VertexBuffer$Type/Index 1 indexes)
      (.updateBound))

    (mknode
     (doto (Geometry. "stripe" stripe-mesh)
      (.setMaterial (default-material))))))


(defn quad
  [width height]
  (let [quad-mesh (Quad. width height)]
    (mknode
     (doto (Geometry. "quad" quad-mesh)
       (.setMaterial (default-material))))))


(defn dome
  "A hemisphere."
  ([radius]
     (mknode ^Geometry
      (doto (r 1 (/ PI 2) ^Geometry (Geometry. "dome" (Dome. 50 50 radius)))
       (.setMaterial (default-material))))))


(defn load-obj
  "Load an .obj file and makes it available for rendering.
   Remember to load a single model with this function. For
   example, in Maya, before exporting a model Combine the
   meshes into a single one and triangulate the resulting object."
  [filename]
  (let [imported-model ^Geometry (.loadModel asset-manager filename)]
    (mknode ^Geometry
     (doto ^Spatial imported-model
      (.setMaterial (default-material))))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Other operations
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(defhigh mkpol
  "Mk polyedra.

   Vertices is a list of vertices, and dim is a keyword
   representing the dimension of the shape been created.
   Legal values for dim: :0 , :1, :2.
   :0 -> Returns a 0 skeleton of the given points.
   :1 -> Returns a 1D representation of the given points.
   :2 -> Returns a 2D representation of the given points (for now only convex 'r supported)"
  [vertices dim]

  (cond-match

   [:0 dim]
   (let [result (struct2 (map #(line %1 %2) (butlast vertices) (next vertices)))]
     (skeleton 0 result))

   [:1 dim]
   (doto (struct2 (map #(line %1 %2) (butlast vertices) (next vertices)))
     (.setMaterial (unlit-material)))
   
   [:2 dim]
   (doto (apply trianglestripe vertices)
     (.setMaterial (default-material)))
   
   [? dim] (throw (IllegalArgumentException. (str "mkpol: " dim " is not a valid dimension.")))))


(defn quote
  [& dash]
  (println "TODO"))