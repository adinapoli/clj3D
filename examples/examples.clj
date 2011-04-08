(ns clj3D.examples
  (:use
   [clj3D fenvs viewer] :reload))


;;Display a 1x1x1 cube
(view (cube 1))

;;view is a multimethod inherited from Incanter,
;;you can visualize data structures as well
(view (matrix [[0 1 2] [3 4 5]]))

;;You can use almost all Plasm's functions
;;Apply to all
(aa #(* %1 %1) [1 2 3])

;;Almost all functions are currified and high order
((aa #(* %1 %1)) [1 2 3])

;;Struct2 is similar to Plasm STRUCT, but it merge
;;geometries into one, so you will lose informations
;;about materials; use mk-node to view an arbitrary number of geometries

;;Creates a green torus
(def green-torus (color :green (torus 0.5 1.0)))

;;Creates a red torus rotated by PI/2 on X axes and translated on X by -1.0
(def red-torus (struct2 (t 1 -1.0) (r 1 (/ PI 2.0)) (color :red) (torus 0.5 1.0)))

(view (struct2 green-torus red-torus))
(view (mk-node green-torus red-torus)) ;;Check the difference!


;; Affine trasnformation are supported as well! t,r,s
;;This is only the tip of iceberg! clj3D is still under development! Have fun!