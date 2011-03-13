(ns clj3D.fenvs
  (:gen-class)
  (:use [clojure.contrib.generic.math-functions])
  (:import (com.jme3.math Vector3f ColorRGBA))
  (:import (com.jme3.asset AssetManager))
  (:import (com.jme3.system JmeSystem))
  (:import (com.jme3.material Material))
  (:import (com.jme3.scene.shape Box))
  (:import (com.jme3.scene Geometry)))


;;For performance tweaking. Just ignore this.
(set! *warn-on-reflection* true)


(defn curry [func arg]
  (partial func arg))


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


(defn ln
  "Simple wrapper. Mantained for compatibility with Plasm."
  [x] (log x))

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
