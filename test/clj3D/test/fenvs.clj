(ns clj3D.test.fenvs
  (:use [clj3D.fenvs] :reload)
  (:use [clojure.test]))


(deftest curry-test
  (let [sum2 #(+ %1 %2)]
    (is (= 3 ((curry sum2 1) 2)) "Simple currying ((sum 1) 2)")
    (is (function? (curry sum2 1)))))


(deftest chr-and-ord
  (is (= \c (chr 99)))
  (is (thrown? ClassCastException (chr \c)))
  (is (thrown? ClassCastException (chr 3.41)))
  (is (= 99 (ord \c)))
  (is (thrown? ClassCastException (ord 9.9)))
  (is (thrown? ClassCastException (ord "a")))
  (is (= \c (chr (ord \c))))
  (is (= 99 (ord (chr 99)))))