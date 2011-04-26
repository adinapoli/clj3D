(ns clj3D.examples.leonardo
  (:refer-clojure :rename {+ core-+})
  (:use
   [clj3D math fl fenvs viewer] :reload))


;;main building
(def sq-l16-est (cuboid 16 16 15))
(def cil-cupolone (cylinder 8 8))
(def cal-cupolone (t 3 8 (dome 8)))
(def cupolone-est (struct2 (t 3 15) cil-cupolone cal-cupolone))

(def rect-puntale (t 3 31 (cuboid 1 1 2)))
(def base1-puntale (t 3 30.8 (cuboid 1.2 1.2 0.2)))
(def base2-puntale (t 3 32.5 (cuboid 1.2 1.2 0.5)))
(def cil-puntale (t 3 33 (cylinder 1 0.5)))
(def punta-puntale (t 3 33.5 (cone 1 2.5)))
(def puntale (struct2 rect-puntale base1-puntale base2-puntale cil-puntale punta-puntale))

(def cil-cupola-r4-est (cylinder 4 4))
(def cal-cupola-r4-est (t 3 4 (dome 4)))
(def cupola-r4-est (struct2 (t 3 15) (t 1 12) (t 2 12) cil-cupola-r4-est cal-cupola-r4-est))

(def puntalino (struct2 (t 3 22.9) (t 1 12) (t 2 12) (dome 0.5)))
(def cupola-r4-est (struct2 cupola-r4-est puntalino))

(def ed-abside-nord (t 2 16 (cuboid 4 8 10)))

(def cil-cupola-r3-nord (cylinder 3 4))
(def cupola-r3-nord (struct2 (t 3 11) (t 2 20) (r 3 (- (/ PI 2)))
			     cil-cupola-r3-nord (t 3 4 (dome 3))))
(def base-cup-r3-nord (struct2 (t 3 10) (t 2 16) (cuboid 3 7 1)))
(def cupola-r3-nord (struct2 cupola-r3-nord base-cup-r3-nord))
(def puntalino-r3 ((comp (t 3 17.9) (t 2 20) (r 3 (/ PI 2))) (dome 0.5)))
(def cupola-r3-nord (struct2 puntalino-r3 cupola-r3-nord))

(def cil-conc-r2-n-est (cylinder 2 6))
(def cal-conc-r2-n-est (t 3 6 (dome 2)))
(def conc-r2-n-est (struct2 (t 1 4) (t 2 20) (r 3 (- (/ PI 2)))
			    cil-conc-r2-n-est cal-conc-r2-n-est))

(def cil-conc-r2-nord (cylinder 2 6))
(def cal-conc-r2-nord (t 3 6 (dome 2)))
(def conc-r2-nord (struct2 (t 2 24) cil-conc-r2-nord cal-conc-r2-nord))

(def abside-nord (struct2 ed-abside-nord cupola-r3-nord conc-r2-n-est conc-r2-nord))
(def abside-est (struct2 (r 3 (- (/ PI 2))) (s 1 -1) abside-nord))

(def tempio-dx (struct2 sq-l16-est cupolone-est rect-puntale base1-puntale
			base2-puntale cupola-r4-est abside-nord abside-est))

(def tempio-sud (s 2 -1 tempio-dx))
(def tempio-first-half (struct2 tempio-dx tempio-sud))
(def tempio-second-half (s 1 -1 tempio-first-half))
(def tempio (struct2 (t 3 1) puntale tempio-first-half tempio-second-half))

(def base-tempio (t [1 2] [-20 -20] (cuboid 40 40 1)))
(def tempio (struct2 tempio base-tempio))