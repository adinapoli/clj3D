(ns clj3D.viewer
  (:use
    [clj3D.fenvs :only [pull]]
    [clojure.contrib.def :only [defalias]])
  (:require
    [incanter.core :as ictr-core])
  (:import
    [clj3D ObjectViewer]
    [java.awt Dimension Toolkit]
    [com.jme3.system AppSettings]
    [com.jme3.scene Node Spatial Geometry]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Imported functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(pull ictr-core (view))


;; Display on the screen the geometry or node given in input.
;; I have used the defmethod awesomeness to extend the existing
;; view function, inherited by Incanter
(defmethod view Spatial [object]
  (let [geometry-node (Node.)
        viewer (clj3D.ObjectViewer.)
        settings (AppSettings. true)
        screen (.getScreenSize (Toolkit/getDefaultToolkit))]

    (doto settings
      (.setResolution (- (.width screen) 20) (- (.height screen) 50))
      (.setTitle "CLJ-3D")
      (.setRenderer (AppSettings/LWJGL_OPENGL2))
      (.setBitsPerPixel 32)
      (.setVSync true))

    (.setSettings viewer settings)
    (.attachChild geometry-node object)
    (.view viewer geometry-node)))
