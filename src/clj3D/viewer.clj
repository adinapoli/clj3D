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
