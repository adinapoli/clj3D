(ns clj3D.test.curry
  (:use
    [clojure.test]
    [clj3D.curry] :reload))


(defhigh sum-no-doc [a b]
  (+ a b))


(defhigh sum-doc
  "Sum with docstring."
  [a b c d] (+ a b c d))


(deftest defcurry-test
  "Testing the defcurry macro."
  (is (function? (sum-no-doc 1)))
  (is (= 10 ((sum-no-doc 1) 9)))
  (is (function? (sum-doc 1)))
  (is (function? ((sum-doc 1) 2)))
  (is (function? (((sum-doc 1) 2) 3)))
  (is (= 10 ((((sum-doc 1) 2) 3) 4))))
