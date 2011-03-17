(ns clj3D.benchs
  (:use
    [clj3D fenvs]
    [criterium core]))


;;(with-progress-reporting (bench (Thread/sleep 1000) :verbose))
;;(with-progress-reporting (bench (concat (range 1000000000) (range 1000000000)) :verbose))