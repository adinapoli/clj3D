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


(ns clj3D.math
  (:use
    [matchure]
    [clj3D curry]
    [clojure.contrib.def :only [defalias]])
  (:refer-clojure :rename {+ core-+})
  (:require
    [clojure.contrib.generic.math-functions :as cl-math]
    [incanter.core :as ictr-core]
    [incanter.charts :as ictr-charts])
  (:import
    [com.jme3.math Vector3f]))


;;For performance tweaking. Just ignore this.
(set! *warn-on-reflection* true)


;; Expose some function into a namespace.
;; http://stackoverflow.com/questions/4732134/can-i-refer-another-
;; namespace-and-expose-its-functions-as-public-for-the-current
(defmacro pull [ns vlist]
  `(do ~@(for [i vlist]
           `(def ~i ~(symbol (str ns "/" i))))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Imported functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(pull cl-math (sin asin cos acos tan atan atan2 abs ceil floor sqrt exp))
(pull ictr-core (matrix to-vect vectorize trace trans))
(def inv ictr-core/solve)


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Protocol definition for adding data structures
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defprotocol Addable
  (+ [t1 t2]))


(extend-protocol Addable

  Character
  (+ [c1 c2] (str c1 c2))
  
  String
  (+ [s1 s2] (str s1 s2))

  clojure.lang.IPersistentVector
  (+ [v1 v2] (into v1 v2)) 

  clojure.lang.ISeq
  (+ [s1 s2] (concat s1 s2)) 

  Number 
  (+ [n1 n2] (core-+ n1 n2)))


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


(defhigh pow
  [base exp]
  (cl-math/pow base exp))


(defn fact [n]
  (if (zero? n) 1 (reduce * (cons 1 (range 1 (inc n))))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Vector related operations. A vector is seen not as mathematical entities, but
;; is [x1 x2 .. xn]
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(defn jvector
  "Returns a Vector3f given values and axis"
  [axes value]
  (cond-match
    
    [java.lang.Integer axes]
    (doto (Vector3f.)
      (.set (dec axes) value))

    [clojure.lang.IPersistentCollection axes]
    (let [result (Vector3f.)
          av-vec (map vector axes value)]
      (doseq [[a v] av-vec] (.set result (dec a) v))
      result)

    [? axes] (throw (IllegalArgumentException. "Invalid input for jvector"))))


(defn vectsum
  "It takes an arbitrary number of vector in input and returns
  the vector [v1 v2 .. vn] where, example:
  (vectsum [0 1] [2 1]) => [2 2]"
  [& args]
  (into [] (map #(reduce + %1) (apply map vector args))))


(defn vectdiff
  "It takes an arbitrary number of vector in input and returns
  the vector [v1 v2 .. vn] where, example:
  (vectsum [0 1] [2 1]) => [-2 0]"
  [& args]
  (into [] (map #(reduce - %1) (apply map vector args))))


(defn vectnorm
  [vector]
  (sqrt (reduce + (map (pow 2) vector))))


(defn meanpoint
  [& args]
  (let [coeff (/ 1.0 (count args))]
    (vec (map #(* coeff %1) (apply vectsum args)))))