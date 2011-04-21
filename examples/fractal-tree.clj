;;A simple fractal tree using clj3D

(ns clj3D.examples.fractal-tree
  (:use [clj3D fenvs viewer] :reload))


(defn tree2d [teta length maxl]
  (let [initial-branch (cylinder 4 length)]
    (loop [tree initial-branch, teta teta, length length, maxl maxl]
      (if (<= length 2)
	(color :brown tree)
	(let [branch (cylinder (* (/ 1 (/ maxl 5.0)) length) length)
	      left-branch (r 2 teta branch)
	      right-branch (r 2 (- teta) branch)
	      branches (struct2 (r 2 (- teta)) (t 3 length) left-branch right-branch)]
	  (recur (struct2 branches tree) teta (* (/ 2 3.0) length) maxl))))))