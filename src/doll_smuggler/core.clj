;; Dynamic Programming Solution to Doll Smuggler Programming Challenge
;;
;; Copyright (c) 2011 Mike English
;;

(ns doll_smuggler.core
  (:gen-class) )

;; The default maximum weight the nice old lady can carry (in grams)
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

(declare memoized-pack-dolls) ;; defined below
;; this declaration is necessary because pack-dolls recurs, calling memoized-pack-dolls

(defn pack-dolls [dolls i W]
  "Based on algo at http://en.wikipedia.org/wiki/Knapsack_problem#0-1_knapsack_problem"
  (if (or (< i 0) (= W 0)) ;; i is an index, W is max avail. weight 
    [0 []] ;; if true: return value 0 and empty set of indices

    ;; else: eval this let block
    ;; bind wi and vi locally to weight and value of the doll under 
    ;; question. note that dolls is not passed as an arg, but is defined 
    ;; in the same namespace and therefore is accessible here
    (let [{wi :weight vi :value } (get dolls i)]
      (if (> wi W) ;; weight of doll at i is > available weight to add

        (#'memoized-pack-dolls dolls (dec i) W) ;; recur w/ i-- (~ don't pack this doll)
        
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
          [[v-notpacking set-notpacking] (#'memoized-pack-dolls dolls (dec i) W)

           ;; bind locally the value of packing doll i
           ;; cont. below
           [v-packing set-packing] (#'memoized-pack-dolls dolls (dec i) (- W wi))]
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
;; - saves time at the cost of space
;; - reduces complexity
(def memoized-pack-dolls (memoize pack-dolls))


(defn parse-int [x]
  "Parse a string to an Integer, catching exceptions"
  (try (Integer/parseInt x)
    (catch NumberFormatException e 0))
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
  (println "KLAAS: Hold your horses and I'll tell you.")
        (let [[best-value best-set] (pack-dolls dolls (dec (count dolls)) max-weight)]
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
  (println "ARTURO: Thanks, Klaas!") 
)

(defn -main [& args]
  (println "DOLL SMUGGLER")
  (run-story)
)

;; mocks

(defn mock-shipment []
 "Mock shipment fn w/ known solution"
  ;; 1030 [0 1 2 3 4 6 10 15 16 17 18 20]
  [(struct doll 9 150)   
   (struct doll 13 35)   
   (struct doll 153 200) 
   (struct doll 50 160)  
   (struct doll 15 60)   
   (struct doll 68 45)   
   (struct doll 27 60)   
   (struct doll 39 40)   
   (struct doll 23 30)   
   (struct doll 52 10)   
   (struct doll 11 70)   
   (struct doll 32 30)   
   (struct doll 24 15)   
   (struct doll 48 10)   
   (struct doll 73 40)   
   (struct doll 42 70)   
   (struct doll 43 75)   
   (struct doll 22 80)    
   (struct doll 7 20)     
   (struct doll 18 12)    
   (struct doll 4 50)     
   (struct doll 30 10)]   
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

