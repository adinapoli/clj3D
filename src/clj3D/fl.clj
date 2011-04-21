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


(ns clj3D.fl
  (:refer-clojure :rename {+ core-+})
  (:use
    [matchure]
    [clj3D curry math]
    [clojure.contrib.def :only [defalias]])
  (:require
    [incanter.core :as ictr-core]
    [incanter.charts :as ictr-charts]))


;;For performance tweaking. Just ignore this.
(set! *warn-on-reflection* true)


(defn curry [func arg]
  (partial func arg))


(defn cat
  "Similar to the orginal PLaSM one, but check this difference:
  1) (cat x) where x is a single data structure (even nested) have the
     same result of the original PLaSM CAT.
     Example (cat [1 2]) => 3

  2) (cat & args) where args is a list of args concat all arguments.
     Example (cat [1 2] [3 4]) => [1 2 3 4]"
  
  ([arg] (if-not (empty? arg) (reduce #(+ %1 %2) arg) []))
  ([e1 & args] (+ e1 (flatten args))))


(defn id
  "IDentity function. For any argument returns the argument"
  [anyvalue] anyvalue)


(defhigh k
  "FL CONStant Function.
  Simple usage:
  (k 1) -> returns a partial function
  ((k 1) 2) -> 1
  (k 1 2) -> 1"
  [k1 k2] k1)


(def tt (k true))


(defhigh distr
  "Very naive implementation"
  [elem seq] (map #(concat %1 [elem]) seq))


(defhigh distl
  "Very naive implementation"
  [elem seq] (map #(cons elem %1) seq))


(defhigh aa
  "Like (map f seq)."
  [f seq] (map f seq))


(defhigh insl
  "Like (reduce func seq) but high order."
  [func seq]
  (reduce func seq))


(defhigh insr
  "Like PLaSM original INSR. Beware, it's not the same
   thing of (reduce func (reverse seq)!. It is equivalent
   to the following:
   Be f a function and [x1,x2,x3] a seq:
   => (f x1 (f x2 x3)) and so on..."
  [func seq]
  (reduce #(func %2 %1) (reverse seq)))


(defhigh cons2
  "It's the PLaSM cons function.
  It takes a sequence of functions and an input, and returns
  a sequence created applying the given functions to the input."
  [func-lst arg]
  (for [func func-lst] (func arg)))


(def len count)


(defn div
  [& args]
  (reduce #(/ %1 (float %2)) args))


(def n repeat)


(defhigh nn
  [times seq]
  (repeat times (cat seq)))