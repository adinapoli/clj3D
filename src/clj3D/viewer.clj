(ns clj3D.viewer
  (:gen-class)
  (:use (clj3D fenvs))
  (:require [incanter.core])
  (:require [incanter.stats])
  (import (clj3D.ObjectViewer))
  (:import (java.awt Dimension Toolkit))
  (:import (com.jme3.system AppSettings))
  (:import (com.jme3.scene Node)))


(defn view
  "Display on the screen the geometry or node given in input."
  [object]
  (let [geometry-node (Node.)
        viewer (clj3D.ObjectViewer.)
        settings (AppSettings. true)
        screen (.getScreenSize (Toolkit/getDefaultToolkit))]

    (.setResolution settings
      (- (.width screen) 20)
      (- (.height screen) 50))
    (.setTitle settings "CLJ-3D")
    (.setRenderer settings (AppSettings/LWJGL_OPENGL2))
    (.setBitsPerPixel settings 32)
    (.setVSync settings true)
    (.setSettings viewer settings)
    (.attachChild geometry-node object)
    (.view viewer geometry-node)))
