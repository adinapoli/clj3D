;;A simple fractal tree using clj3D

(ns clj3D.examples.fractal-tree
  (:refer-clojure :rename {+ core-+})
  (:use [clj3D math fl fenvs viewer] :reload))


(defn tree2d [teta length maxl]
  (let [initial-branch (cylinder 4 length)]
    (loop [tree initial-branch, teta teta, len length, mxl maxl, trasl len]
      (if (<= len 2)
	(color :brown tree)
	(let [left-branch (r 2 teta (cylinder (* (/ 1 (/ mxl 5.0)) len) len))
	      right-branch (r 2 (- teta) (cylinder (* (/ 1 (/ mxl 5.0)) len) len))
	      branches (struct2 (r 2 teta) (t 3 trasl) left-branch right-branch)
	      new-trasl (+ trasl (* len (sin (/ PI 2))))]
	  (recur (struct2 branches tree) teta (* (/ 2 3.0) len) mxl new-trasl))))))


(def teta (/ PI 6))
(def length 30)
(def maxl 30)

(def initial-branch (cylinder 4 length))
(def branch-left (r 2 teta (cylinder (* (/ 1 (/ maxl 5.0)) length) length)))
(def branch-right (r 2 (- teta) (cylinder (* (/ 1 (/ maxl 5.0)) length) length)))
(def tree (struct2 initial-branch (r 2 teta) (t 3 length) branch-left branch-right))