(ns clj3D.fenvs
   (:use
    [clojure.contrib.def :only [defalias]])
  (:require
    [clojure.contrib.generic.math-functions :as cl-math]
    [incanter.core :as ictr-core])
  (:import
    [com.jme3.math Vector3f ColorRGBA]
    [com.jme3.asset AssetManager]
    [com.jme3.system JmeSystem]
    [com.jme3.material Material]
    [com.jme3.scene.shape Box Line Sphere]
    [com.jme3.scene Geometry Mesh]
    [jme3tools.optimize GeometryBatchFactory]))


;;For performance tweaking. Just ignore this.
(set! *warn-on-reflection* true)


;; Expose some function into a namespace.
;; http://stackoverflow.com/questions/4732134/can-i-refer-another-
;;  namespace-and-expose-its-functions-as-public-for-the-current
(defmacro pull [ns vlist]
  `(do ~@(for [i vlist]
           `(def ~i ~(symbol (str ns "/" i))))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Imported functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(pull cl-math (sin asin cos acos tan atan atan2 abs ceil floor sqrt exp))
(pull ictr-core (matrix))
(defalias inv ictr-core/solve)


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


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Mixed PLASM functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(defn curry [func arg]
  (partial func arg))


(defn cat
  "Concat the sequences give in input into a vector, for performance purpose.
  Cat-ing list and/or vector will return always a vector."
  [& args] (reduce into (first args) (rest args)))


(defn id
  "IDentity function. For any argument returns the argument"
  [anyvalue] anyvalue)


(defn k
  "FL CONStant Function.
  Simple usage:
  (k 1) -> returns a partial function
  ((k 1) 2) -> 1
  (k 1 2) -> 1"
  ([k1] (partial (fn [x y] x) k1))
  ([k1 k2] k1))


(def tt (k true))


(defn distr
  "Very naive implementation"
  [elem seq] (vec (map #(conj %1 elem) seq)))

(defn distl
  "Very naive implementation"
  [elem seq] (map #(into [elem] %1) seq))

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


(def ^{:private true} default-color (ColorRGBA/Red))


(defn color
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


;; i.e the original STRUCT from Plasm
(defn struct2
  "Merge all the input given meshs into a single one."
  [& meshs]
  (let [out-mesh (Mesh.)]
    (GeometryBatchFactory/mergeGeometries meshs out-mesh)
    (doto (Geometry. "structured" out-mesh)
      (.setMaterial (default-material)))))


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
  "Returns a new x*y*z cuboid in (0,0,0)"
  [x y z]
  (let* [box (Box. (Vector3f. 0 0 0) x y z)
         cuboid (Geometry. "cuboid" box)]
    (.setMaterial cuboid (default-material))
    cuboid))


(defn cube
  "Returns a new n*n*n cube in (0,0,0)"
  [side]
  (cuboid side side side))


(defn hexaedron []
  (cube 1))


(defn line
  "New in clj3D. Draw a simple line from start to end"
  ([[x1 y1 z1] [x2 y2 z2]]
  (let* [line-mesh (Line. (Vector3f. x1 y1 z1) (Vector3f. x2 y2 z2))
         line-geom (Geometry. "line" line-mesh)]
    (.setMaterial line-geom (default-material))
    line-geom)))


(defn sphere
  "Returns a new sphere in (0,0,0).
  (sphere radius) -> returns a sphere of the given radius
  (sphere x y radius) -> returns a sphere composed by x and
  y segment, of the given radius."
  ([radius]
  (let* [sphere-mesh (Sphere. 50 50 radius)
         sphere (Geometry. "sphere" sphere-mesh)]
    (.setMaterial sphere (default-material))
    sphere))

  ([radius z-seg r-seg]
  (let* [sphere-mesh (Sphere. z-seg r-seg radius)
         sphere (Geometry. "sphere" sphere-mesh)]
    (.setMaterial sphere (default-material))
    sphere)))
