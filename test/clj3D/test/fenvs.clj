(ns clj3D.test.fenvs
  (:use [clj3D.fenvs] :reload)
  (:use [clojure.test]))

(deftest curry-test
  (let [sum2 #(+ %1 %2)]
    (is (= 3 ((curry sum2 1) 2)) "Simple currying ((sum 1) 2)")
    (is (function? (curry sum2 1)))))