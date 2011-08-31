(ns doll_smuggler.test.core
  (:use [doll_smuggler.core])
  (:use [midje.sweet]
        [clojure.test]))

(deftest simple-addition 
  (fact (+ 1 1) => 2)
  (fact (+ 2 2) => 4))
