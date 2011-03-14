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
    [com.jme3.scene.shape Box]
    [com.jme3.scene Geometry]))


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


(def colors
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


(def default-color (ColorRGBA/Red))


(defn color
  "Change the color of the shape, then return the shape itself."
  [color-symbol ^Geometry object]
  (let [material (.getMaterial object)]
    (.setColor material "Ambient" (get colors color-symbol default-color))
    (.setColor material "Diffuse" (get colors color-symbol default-color))
    object))


(def asset-manager
  (JmeSystem/newAssetManager
    (.getResource (.getContextClassLoader (Thread/currentThread))
        "com/jme3/asset/Desktop.cfg")))

(defn default-material []
  (doto (Material. asset-manager "Common/MatDefs/Light/Lighting.j3md")
    (.setBoolean "UseMaterialColors" true)
    (.setColor "Ambient"  (get colors :black))
    (.setColor "Diffuse" default-color)
    (.setColor "Specular" (get colors :white))
    (.setFloat "Shininess" 20)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; PRIMITIVES
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn cube
  "Returns a new n*n*n cube in (0,0,0)"
  [side]
  (let* [box (Box. (Vector3f. 0 0 0) side side side)
         cube (Geometry. "cube" box)]
    (.setMaterial cube (default-material))
    cube))
