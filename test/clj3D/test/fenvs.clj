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


(deftest cat-test
  (is (= nil (cat)))
  (is (= [] (cat [])))
  (is (= [1 2 3]) (cat [1 2 3]))
  (is (= [1 2 3 4 5 6]) (cat [1 2 3] [4 5 6]))
  (is (= [1 2 3 4]) (cat [1 2] '(3 4)))
  (is (= [1 2 3 4 5 6]) (cat [1 2] '(3 4) [5 6]))
  (is (= [1 2 3 4 8 8]) (cat [1 2] '(3 4) '(8 8)))
  (is (= [1 2 3 4 8 8 9 8]) (cat [1 2] '(3 4) [8 8] '(9 8))))


(deftest id-test
  (is (= true (id true)))
  (is (= 4 (id 4)))
  (is (= [1 2] (id [1 2]))))


(deftest k-test
  (is (function? (k 1)))
  (is (= 4 (k 4 2)))
  (is (= ((k 2) 3) (k 2 3))))


(deftest distl-test
  (is (= [[9 1] [9 2 2] [9 3]] (distl 9 [[1] [2 2] [3]])))
  (is (= [[9 0 0] [9 1 1] [9 2 2]] (distl 9 [[0 0] [1 1] [2 2]]))))


(deftest distr-test
  (is (= [[1 9] [2 2 9] [3 9]] (distr 9 [[1] [2 2] [3]])))
  (is (= [[0 0 9] [1 1 9] [2 2 9]] (distr 9 [[0 0] [1 1] [2 2]]))))