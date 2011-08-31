# Doll Smuggler

A solution to Atomic Object's Doll Smuggler programming challenge using dynamic programming.

## Challenge
_originally posted on the [Atomic Spin blog](http://spin.atomicobject.com/2011/05/31/use-clojure-to-move-drugs-a-programming-challenge/ "Use Clojure to Move Drugs – A Programming Challenge")_

You are a drug trafficker. Every day you meet with a different nice old lady (the mule) and find out how much weight she can carry in her handbag. You then meet with your supplier who has packed various drugs into a myriad of collectible porcelain dolls. Once packed with drugs, each of the precious dolls has a unique combination of weight and street value. Sometimes your supplier has more dolls than grandma can carry, though space in her handbag is never an issue. Your job is to choose which dolls the nice old lady will carry, maximizing street value, while not going over her weight restriction.

Write a program in Clojure that chooses the optimal set of drug-packed porcelain dolls from a set where each has a unique weight and value combination, while staying within a given weight restriction, W, that maximizes the street value of drugs delivered by the grandma, including a set of executable high-level tests for your solution.

## Usage

### Run tests

`lein test`

### Build standalone .jar

`lein uberjar`

### Run interactive storyline

`./run.sh`

_NB: This requires that you already have the uberjar built._

## Notes

- Using `lein run` to launch this application will not work because stdin is not made available. See: [technomancy/leiningen#169](https://github.com/technomancy/leiningen/issues/169 "Programs started with `lein run' have no stdin")
- Although names were taken from a list of notorious drug lords and their affiliates, they are not intended to denigrate any particular persons or groups; the characters in this story are entirely fictive.
- Don't do drugs. Stay in school.


## License

Copyright (C) 2011 Mike English

Distributed under an MIT license; see LICENSE.
