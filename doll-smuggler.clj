;; Solution to Doll Smuggler programming challenge
;;
;; Copyright (c) 2011 Mike English
;;
;; Permission is hereby granted, free of charge, to any person obtaining
;; a copy of this software and associated documentation files (the 
;; "Software"), to deal in the Software without restriction, including 
;; without limitation the rights to use, copy, modify, merge, publish, 
;; distribute, sublicense, and/or sell copies of the Software, and to 
;; permit persons to whom the Software is furnished to do so, subject 
;; to the following conditions:
;;
;; The above copyright notice and this permission notice shall be 
;; included in all copies or substantial portions of the Software.
;;
;; THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
;; EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
;; MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
;; NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
;; BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN 
;; ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
;; CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
;; SOFTWARE.

;;(use 'clojure.contrib.trace) ;; Debugging lifesaver!
;; make sure the clojure-contrib jar is in your cp to use
;; try using dotrace on pack-dolls & memoized-pack-dolls

;; The maximum weight the nice old lady can carry (in grams)
(ns doll-smuggler)
(def max-weight 8000)

(defstruct doll :weight :value)

(defn stuff-doll []
  "Stuff a doll full of contraband"
  (def doll-weight (rand-int 3600))
  (def drug-weight (rand-int 1500))
  (def value (* drug-weight (rand-int 175))) ;; up to 175 USD/g
  (struct doll (+ doll-weight drug-weight) value)
  )

(defn generate-shipment [n]
  "Generate a shipment of n dolls"
  (vec (for [x (range n)] (stuff-doll)))
)

(declare dolls)

(declare memoized-pack-dolls) ;; defined below

(defn pack-dolls [i W]
  "Based on algo at http://en.wikipedia.org/wiki/Knapsack_problem#0-1_knapsack_problem"
  (if (or (< i 0) (= W 0)) ;; i is an index, W is max avail. weight 
    [0 []] ;; if true: return value 0 and empty set of indices

    ;; else: eval this let block
    ;; bind wi and vi locally to weight and value of the doll under 
    ;; question. note that dolls is not passed as an arg, but is defined 
    ;; in the same namespace and therefore is accessible here
    (let [{wi :weight vi :value } (get dolls i)]
      (if (> wi W) ;; weight of doll at i is > available weight to add

        (#'memoized-pack-dolls (dec i) W) ;; recur w/ i-- (~ don't pack this doll)
        
        ;; else: 
        ;; (wi is less than or equal to amount of weight than can be added)
        ;;  return the value of whichever is greater,
        ;;  packing the doll, or not packing the doll
        ;;  each of which involves recurring either with
        ;;  or without the value added and weight subtracted
        ;;
        (let 

          ;; bind locally the value of not packing doll i
          ;; and the optimal set of indices returned by recurring
          [[v-notpacking set-notpacking] (#'memoized-pack-dolls (dec i) W)

           ;; bind locally the value of packing doll i
           ;; cont. below
           [v-packing set-packing] (#'memoized-pack-dolls (dec i) (- W wi))]
          ;; body of let block

          ;; adding vi and i in the body for readability
          ;; (def v-packing (+ v-packing vi))
          ;; (def set-packing (conj set-packing i))

          ;; Here's the real question - is the value of packing the doll
          ;; (vi + the optimal value that can be packed in the remaining weight)
          ;; greater than the value of not packing the doll
          ;; (the optimal value that can be packed in the weight without this doll)
          ;; ?
          (if (> (+ v-packing vi) v-notpacking)

            ;; true:
            ;;  return optimized value and set for i, W
            ;;  with doll i included
            [(+ v-packing vi) (conj set-packing i)]

            ;; else:
            ;;  return optimized value and set of indices for i, W
            ;;  with doll i not included
            [v-notpacking set-notpacking]
           )
          )
        )
      )
    )
  )

;; store the results for each unique call so they
;; don't need to be calculated more than once
;; - saves many levels of brancing and recurring
(def memoized-pack-dolls (memoize pack-dolls))


(defn parse-int [x]
  "Parse a string to an Integer, catching exceptions"
  (try (Integer/parseInt x)
    (catch NumberFormatException e 0))
)

(defn timer [myfunc]
  "Time a function, returning the time it takes to run in ms."
  (let [start# (. System (nanoTime))
          junk ~myfunc]
      (/ (double (- (. System (nanoTime)) start#)) 1000000.0)
  )
)

(defn run-story []
  "Play through a sample scenario"
  (println "ARTURO: Hey grandma- how we doin' today?")
  (Thread/sleep 500)
  (print "MAYBEL: ")(flush)
  (def not-listening (read-line))
  (Thread/sleep 200)
  (println "ARTURO: Great, great, good to hear!")
  (Thread/sleep 1200)
  (println "        Now how much are you good for today?") 
  (Thread/sleep 700)
  (println "        Still eight kilos, I hope.")
  (Thread/sleep 500)
  (print "                                      grams.\r")(flush)
  (print "MAYBEL: I can carry no more than ")(flush) 
  (def max-weight (Integer. (read-line)))
  (if (< max-weight 8000) 
    (println "ARTURO: You're killin' me! But I won't push ya.")
    (println "ARTURO: Good girl.")
    )
  (Thread/sleep 1300)
  (println "        Looks like Frank's here. I'll be right back.")
  (Thread/sleep 3300)
  (println "FRANK: I got some pretty dolls here. You're into dolls?")
  (Thread/sleep 2205)
  (print "ARTURO (glares)\r")(flush)
  (Thread/sleep 900)
  (println "ARTURO (glares): Where have you been? Leroy took off hours ago!")
  (Thread/sleep 1500)
  (println "        Nevermind, just give me a hand stuffing these.")
  (Thread/sleep 1100)
  (println "        How many do you got anyways?")
  (Thread/sleep 2500)
  (println "FRANK: Alright, alright. Lemme check.")
  (Thread/sleep 3200)
  (print "                               dolls\r")(flush)
  (print "FRANK: Looks like I've got ")(flush)
    (flush)(def n (Integer. (read-line)))
  (Thread/sleep 1300)
  (println "ARTURO: Great. Let's fill 'em up.")
  (def dolls (generate-shipment n))
  (Thread/sleep 3254)
  (println "ARTURO: OK. Sticker prices?")
  (Thread/sleep 1700)
  (println "FRANK: Here's the rundown...")
  ;; (println (apply str (interpose "\n" dolls)))
  (dorun (for [i (-> dolls count range)] 
                (let [{wi :weight vi :value} (get dolls i)]
                      (print (format "\ta doll that weighs %sg, worth $%s,\n" wi vi))
                )
          )
  )
  (println "FRANK: All ready to go.")
  (Thread/sleep 1500)
  (println "ARTURO: Sounds good.")
  (Thread/sleep 1500)
  (println "ARTURO: Now, Maybel says she can only carry" max-weight "g")
  (Thread/sleep 600)
  (print "        so...\r")(flush)
  (Thread/sleep 1973)
  (println "        so - where's Klaas? He's good with math.")
  (Thread/sleep 900)
  (println "KLAAS: Right here. What's up?")
  (Thread/sleep 1000)
  (println "ARTURO: We need to figure out how to get the best bang for")
  (println "        our buck here.\n")
  (Thread/sleep 300)
  (println "        Maybel can only carry" max-weight "g and I've got")
  (println "        all these dolls I need to get out.\n")
  (Thread/sleep 500)
  (println "        What's the best we can load her up with?")
  (Thread/sleep 1200)
  ;; TODO estimate solution time base on predictable algo
  (def time-est 2)
  (println "KLAAS: Give me " time-est "seconds and I'll tell you.")
  ;; TODO custom func 'timer' returns s of time to run inner
  ;;(def klaas-time 
    ;;(timer
        (let [[best-value best-set] (pack-dolls (dec (count dolls)) max-weight)]
          (println "KLAAS: We'll pack:")
          (apply print 
              (for [i best-set] 
                (let [{wi :weight vi :value} (get dolls i)]
                      (format "\n\tthe doll that weighs %sg, worth $%s," wi vi)
                )
              )
          )
          (print "\n\tand that's the best we can fit-\n")
          (println "\t" (count best-set) "dolls for a value of" (format "$%s" best-value))   
        )
  ;;  )
  ;;)
  (println "ARTURO: Thanks, Klaas!") 
  ;;(println "ARTURO: (it actually took you " klaas-time "s, by the way.")
  ;;(if (< klaas-time time-est)
  ;;  (println "ARTURO: Good work!")
  ;;  (println "ARTURO: Slow poke!")
  ;;)
)

;; mocks

(defn mock-shipment []
 "Mock shipment fn w/ known solution"
  ;; 1030 [0 1 2 3 4 6 10 15 16 17 18 20]
  [(struct doll 9 150)    ;; 0  "map"
   (struct doll 13 35)    ;; 1  "compass
   (struct doll 153 200)  ;; 2  "water"
   (struct doll 50 160)   ;; 3  "sandwich"
   (struct doll 15 60)    ;; 4  "glucose"
   (struct doll 68 45)    ;; 5  "tin"
   (struct doll 27 60)    ;; 6  "banana"
   (struct doll 39 40)    ;; 7  "apple"
   (struct doll 23 30)    ;; 8  "cheese"
   (struct doll 52 10)    ;; 9  "beer"
   (struct doll 11 70)    ;; 10 "suntan cream"
   (struct doll 32 30)    ;; 11 "camera"
   (struct doll 24 15)    ;; 12 "t-shirt"
   (struct doll 48 10)    ;; 13 "trousers"
   (struct doll 73 40)    ;; 14 "umbrella"
   (struct doll 42 70)    ;; 15 "waterproof trousers"
   (struct doll 43 75)    ;; 16 "waterproof overclothes"
   (struct doll 22 80)    ;; 17 "note-case"
   (struct doll 7 20)     ;; 18 "sunglasses"
   (struct doll 18 12)    ;; 19 "towel"
   (struct doll 4 50)     ;; 20 "socks"
   (struct doll 30 10)]   ;; 21 "book"
)

;; Some general tests

(defn mytests []
  ;; SETUP, get a list of dolls and run the solver
  ;; TODO convert this to a fixture
  (def dolls (generate-shipment 25))
  (let [[best-v best-set] 
        (pack-dolls (-> dolls count dec) max-weight)]
     (def best-w 
       (reduce + 
        (map :weight 
           (for 
             [i best-set] 
             (get dolls i)
            )
          )
      )
      )
      ;; TESTS
      ;;
      ;; TODO convert this to clojure.test tests
      ;;
      ;; make sure we're getting back
      ;; the expected data types
      (assert (number? best-v))
        ;; failing example
        ;; (assert (number? "word"))
        ;; passing example
        (assert (number? 1))
      (assert (vector? best-set))
        ;; failing example
        ;; (assert (vector? 98))
        ;; passing example
        (assert (vector? [1 2 3]))

      ;; make sure that best-set
      ;; contains valid indices
      (assert (every? (partial > (count dolls)) best-set))
        ;; failing example
        ;; (assert (every? (partial > (count [1 2 3 4])) [0 2 5]))
        ;; passing example
        (assert (every? (partial > (count [1 2 3 4])) [0 2 3]))
      (assert (every? (partial <= 0) best-set))
        ;; failing example
        ;; (assert (every? (partial <= 0) [1 2 -3 4]))
        ;; passing example
        (assert (every? (partial <= 0) [1 2 3 4]))

      ;; if our set's weight is greater
      ;; than max-weight, we've failed
      ;; miserably and Maybel will likely
      ;; throw out her back 
      (assert (< best-w max-weight))
        ;; yep

      ;; if best-v <= 0, Klaas must be
      ;; stealing from us. bad Klaas.
      (assert (> best-v 0))
        ;; mhm
      )
)  

;; Make it so!
(mytests)
(run-story)

