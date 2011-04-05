(ns clj3D.fenvs
  (:use
    [matchure]
    [clj3D curry]
    [clojure.contrib.def :only [defalias]])
  (:require
    [clojure.contrib.generic.math-functions :as cl-math]
    [incanter.core :as ictr-core]
    [incanter.charts :as ictr-charts])
  (:import
    [com.jme3.math Vector3f Vector2f ColorRGBA Quaternion]
    [com.jme3.asset AssetManager]
    [com.jme3.system JmeSystem]
    [com.jme3.material Material]
    [com.jme3.scene.shape Box Line Sphere Cylinder Torus Quad]
    [com.jme3.scene Geometry Mesh Mesh$Mode VertexBuffer VertexBuffer$Type]
    [jme3tools.optimize GeometryBatchFactory]))


;;For performance tweaking. Just ignore this.
(set! *warn-on-reflection* true)


;; Expose some function into a namespace.
;; http://stackoverflow.com/questions/4732134/can-i-refer-another-
;; namespace-and-expose-its-functions-as-public-for-the-current
(defmacro pull [ns vlist]
  `(do ~@(for [i vlist]
           `(def ~i ~(symbol (str ns "/" i))))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Imported functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(pull cl-math (sin asin cos acos tan atan atan2 abs ceil floor sqrt exp))
(pull ictr-core (matrix))
(def inv ictr-core/solve)


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Math functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(def PI (Math/PI))


(defn sinh
  "Returns the hyperbolic sin of the argument x."
  [x] (Math/sinh x))


(defn cosh
  "Returns the hyperbolic cos of the argument x."
  [x] (Math/cosh x))


(defn tanh
  "Returns the hyperbolic tan of the argument x."
  [x] (Math/tanh x))


(def ln cl-math/log)


(defn chr
  "Coerce an int into the correspondent char value."
  [x]
  (if (integer? x)
    (char x)
    (throw (ClassCastException. "Argument is not a valid integer."))))


(defn ord
  "Coerce a character into the correspondent int value."
  [x]
  (if (char? x)
    (int x)
    (throw (ClassCastException. "Argument is not a valid character."))))


(def power cl-math/pow)


(defn fact [n]
  (if (zero? n) 1 (reduce * (cons 1 (range 1 (inc n))))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Mixed PLASM functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(defn curry [func arg]
  (partial func arg))


(def cat concat)


(defn id
  "IDentity function. For any argument returns the argument"
  [anyvalue] anyvalue)


(defhigh k
  "FL CONStant Function.
  Simple usage:
  (k 1) -> returns a partial function
  ((k 1) 2) -> 1
  (k 1 2) -> 1"
  [k1 k2] k1)


(def tt (k true))


(defn distr
  "Very naive implementation"
  [elem seq] (map #(concat %1 [elem]) seq))

(defn distl
  "Very naive implementation"
  [elem seq] (map #(cons elem %1) seq))


(defhigh aa
  "Like (map f seq)."
  [f seq] (map f seq))


(defhigh insl
  "Like (reduce func seq) but high order."
  [func seq]
  (reduce func seq))


(defhigh insr
  "Like (reduce func (reverse seq)) but high order."
  [func seq]
  (reduce func (reverse seq)))


(defhigh cons2
  "It's the PLaSM cons function.
  It takes a sequence of functions and an input, and returns
  a sequence created applying the given functions to the input."
  [func-lst arg]
  (for [func func-lst] (func arg)))

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


(defhigh color
  "Change the color of the shape, then return the shape itself."
  [color-symbol ^Geometry object]
  (let [material (.getMaterial object)]
    (.setColor material "Ambient" (get colors color-symbol default-color))
    (.setColor material "Diffuse" (get colors color-symbol default-color))
    object))


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



(defhigh merge-geometries

  [geom1 geom2]
  (let [out-mesh (Mesh.)]
    (GeometryBatchFactory/mergeGeometries [geom1 geom2] out-mesh)
    (doto (Geometry. "structured" out-mesh)
      (.setMaterial (default-material)))))


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
        [[com.jme3.scene.Geometry com.jme3.scene.Geometry] [x y]] (merge-geometries x y)
        [[com.jme3.scene.Geometry ?func] [x y]] (func x)
        [[?func com.jme3.scene.Geometry] [x y]] (func x)
        [? [x y]] (throw (IllegalArgumentException. "Invalid input for struct")))) rev-seq)))


(defn- jvector
  "Returns a Vector3f given values and axis"
  [axes value]
  (cond-match
    
    [java.lang.Integer axes]
    (doto (Vector3f.)
      (.set (dec axes) value))

    [clojure.lang.IPersistentCollection axes]
    (let [result (Vector3f.)
          av-vec (map vector axes value)]
      (doseq [[a v] av-vec] (.set result (dec a) v))
      result)

    [? axes] (throw (IllegalArgumentException. "Invalid input for jvector"))))


(defhigh t
  "Translate function. High order function.
  Usage:
  (t 1 2.0 (cube 1)) -> move the cube from <0,0,0> to <2.0,0,0>
  (t [1 2] [3.0 2.0] (cube 1) -> move the cube to <3.0,2.0,0>"
  [axes value geom]
  (doto ^Geometry geom
    (.move (jvector axes value))))


(defhigh s
  "Scale function. High order function.
  Usage:
  (s 1 0.5 (cube 1)) -> scale 50% of cube x-side
  (s [1 2] [0.5 0.7] (cube 1)) -> scale 50% x and 70% y."
  [axes value geom]
  (cond-match

    [java.lang.Integer axes]
    (let [av-map (hash-map (dec axes) value)]
      (doto ^Geometry geom
      (.scale (get av-map 0 1.0) (get av-map 1 1.0) (get av-map 2 1.0))))

    [clojure.lang.IPersistentCollection axes]
    (let [av-map (reduce into (map hash-map (map dec axes) value))]
      (doto ^Geometry geom
      (.scale (get av-map 0 1.0) (get av-map 1 1.0) (get av-map 2 1.0))))

    [? axes] (throw (IllegalArgumentException. "Invalid input for scale"))))


(defhigh r
  "Rotate function. High order function."
  [axis rotation geom]
  (let [quaternion (Quaternion.)]
    (.fromAngleAxis quaternion rotation (jvector axis 1.0))
    (doto ^Geometry geom
      (.setLocalRotation quaternion))))


(defn skeleton
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
      (.. getMaterial getAdditionalRenderState (setWireframe true)))

    [? dim] geom))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; PRIMITIVES
;; NOTE: JMonkey uses a different mapping between coordinates and the
;; coordinate plane itself.
;; In plasm: [x y z]
;; In jmonkey: [x z y]
;; It will be used the jmonkey convention, since it will be simpler to port
;; the codes and it's more natural and intuitive (z is the depthness, y the
;; height).
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(defn cuboid
  "Returns a new x*y*z cuboid with bottom-left corner in (0,0,0)"
  [x y z]
  (let [[px py pz] (map #(/ %1 2.0) [x y z])
        box (Box. (Vector3f. px py pz) px py pz)
        cuboid (Geometry. "cuboid" box)]
    (.setMaterial cuboid (default-material))
    cuboid))


(defn cube
  "Returns a new n*n*n cube with bottom-left corner in (0,0,0)"
  [side]
  (cuboid side side side))


(defn hexaedron [] (cube 1))


(defn line
  "New in clj3D. Draw a simple line from start to end"
  ([[x1 y1 z1] [x2 y2 z2]]
  (let [line-mesh (Line. (Vector3f. x1 y1 z1) (Vector3f. x2 y2 z2))
         line-geom (Geometry. "line" line-mesh)]
    (.setMaterial line-geom (default-material))
    line-geom)))


(defn sphere
  "Returns a new sphere in (0,0,0).
  (sphere radius) -> returns a sphere of the given radius
  (sphere radius x y) -> returns a sphere composed by x and
  y segment, of the given radius."
  ([radius]
  (let [sphere-mesh (Sphere. 50 50 radius)
         sphere (Geometry. "sphere" sphere-mesh)]
    (.setMaterial sphere (default-material))
    sphere))

  ([radius z-seg r-seg]
  (let [sphere-mesh (Sphere. z-seg r-seg radius)
         sphere (Geometry. "sphere" sphere-mesh)]
    (.setMaterial sphere (default-material))
    sphere)))


(defn cylinder
  "Returns a new cylinder in (0,0,0).
  (cylinder radius height) -> returns a cylinder of the given radius and height
  (cylinder radius height x y) -> returns a cylinder composed by x and
  y segment, of the given radius and height."
  ([radius height]
  (let [cylinder-mesh (Cylinder. 50 50 radius height true)
         cylinder (Geometry. "cylinder" cylinder-mesh)]
    (t 3 (/ height 2.0) cylinder)
    (doto ^Geometry cylinder
      (.setMaterial (default-material)))))

  ([radius height z-seg r-seg]
  (let [cylinder-mesh (Cylinder. z-seg r-seg radius height true)
         cylinder (Geometry. "cylinder" cylinder-mesh)]
    (t 3 (/ height 2.0) cylinder)
    (doto ^Geometry cylinder
      (.setMaterial (default-material))))))


(defn torus
  "Returns a new torus in (0,0,0).
  (torus r1 r2) -> returns a torus of the given radius r1 and r2
  (torus r1 r2 x y) -> returns a torus composed by x and
  y segment, of the given radius r1 and r2."
  ([r1 r2]
  (let [torus-mesh (Torus. 50 50 r1 r2)
         torus (Geometry. "torus" torus-mesh)]
    (doto torus
      (.setMaterial (default-material)))))

  ([r1 r2 z-seg r-seg]
  (let [torus-mesh (Torus. z-seg r-seg r1 r2)
         torus (Geometry. "torus" torus-mesh)]
    (doto torus
      (.setMaterial (default-material))))))


(defn triangle
  "Returns a new 2D triangle in with vertexes v1 v2 v3."
  [v1 v2 v3]
  (let [triangle-mesh (Mesh.)
        vertices (float-array (flatten [v1 0 v2 0 v3 0]))
        tex-cord (float-array [0 0 1 0 0 1])
        indexes (int-array [2 0 1 1 0 2])
        normals (float-array (flatten (repeat 3 [0 0 1])))]

    (doto ^Mesh triangle-mesh
      (.setBuffer VertexBuffer$Type/Position 3 vertices)
      (.setBuffer VertexBuffer$Type/Normal 3 normals)
      (.setBuffer VertexBuffer$Type/TexCoord 2 tex-cord)
      (.setBuffer VertexBuffer$Type/Index 1 indexes)
      (.updateBound))

    (doto (Geometry. "triangle" triangle-mesh)
      (.setMaterial (default-material)))))


(defn quad
  [width height]
  (let [quad-mesh (Quad. width height)]
    (doto (Geometry. "quad" quad-mesh)
      (.setMaterial (default-material)))))