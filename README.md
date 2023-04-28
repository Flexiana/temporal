# Temporal

Temporal is a testing library inspired by "Temporal Logic":[https://github.com/Flexiana/temporal]

An ideal use case is for testing cooperation of systems in time, even running on different computers (like microservices, SOA).

With temporal, it is easy to test for situations like:

* Events X in service A should always happen before events Y in a service B.
* There should be exactly 3 events T in any of our services A, B, and C.
* Event P on any service never happens at the same time as event Q on any service.

As there's a centralized service collecting events that expect just simple POST event={event name}, it's trivially easy for you to add a support for more programming languages if needed.

## Usage

### Starting collecting service:

(temporal.core/start-server 6589) ; running on http://localhost:6589

Then any POST request to `(base url)/add-event` with parameter event={name} is saved.

You can get your timeline at `(base url)/get-timeline`

You can reset state with `(base url)/reset-timeline`

## Writing tests

Example of test using temporal:

```clojure
(ns temporal.core-test
  (:require [clojure.test :refer :all]
            [temporal.core :as tl]
            [clj-http.client :as http]
            [clojure.edn :as edn]))


(defn get-http-timeline [base-url]
  (-> (http/get (str base-url "/get-timeline"))
      :body
      edn/read-string))

(defn reset-http-timeline [base-url]
  (http/post (str base-url "/reset-timeline")))

(deftest test-a-happens-after-b

  (reset-http-timeline "http://localhost:6589")

  (tl/add-event :event-1)
  (tl/add-event :event-2)
  (tl/add-event :event-3)
  (tl/add-event :event-4)

  (let [http-timeline (get-http-timeline "http://localhost:6589")]

    (testing "event-1 should happen before event-2"
      (is (tl/a-happens-after-b http-timeline :event-2 :event-1)))
    (testing "event-2 should happen before event-3"
      (is (tl/a-happens-after-b http-timeline :event-3 :event-2)))
    (testing "event-3 should happen before event-4"
      (is (tl/a-happens-after-b http-timeline :event-4 :event-3)))
    (testing "event-4 should not happen before event-1"
      (is (not (tl/a-happens-after-b http-timeline :event-1 :event-4))))))

```

You can easily create your own combinators. Below are current combinators Temporal supports:

```clojure

(defn a-happens-after-b [timeline event-a event-b]
  (> (.indexOf timeline event-a)
     (.indexOf timeline event-b)))

(defn a-happens-before-b [timeline event-a event-b]
  (< (.indexOf timeline event-a)
     (.indexOf timeline event-b)))

(defn a-and-b-are-consecutive [timeline event-a event-b]
  (= 1 (- (.indexOf timeline event-b)
          (.indexOf timeline event-a))))

(defn a-happens-immediately-before-b [timeline event-a event-b]
  (a-and-b-are-consecutive timeline event-a event-b))

(defn a-happens-immediately-after-b [timeline event-a event-b]
  (a-and-b-are-consecutive timeline event-b event-a))

(defn a-happens-n-times [timeline event n]
  (= n (count (filter #(= % event) timeline))))

(defn a-happens-at-least-n-times [timeline event n]
  (>= (count (filter #(= % event) timeline)) n))

(defn a-happens-at-most-n-times [timeline event n]
  (<= (count (filter #(= % event) timeline)) n))

(defn a-happens-exactly-n-times [timeline event n]
  (a-happens-n-times timeline event n))

(defn a-never-happens [timeline event]
  (a-happens-n-times timeline event 0))

(defn a-always-happens [timeline event]
  (every? #(= % event) timeline))

(defn a-happens-before-b-n-times [timeline event-a event-b n]
  (= n (count (filter #(and (= % event-a)
                            (a-happens-before-b timeline event-a event-b))
                      timeline))))

(defn a-happens-after-b-n-times [timeline event-a event-b n]
  (= n (count (filter #(and (= % event-a)
                            (a-happens-after-b timeline event-a event-b))
                      timeline))))

(defn a-or-b-happens [timeline event-a event-b]
  (or (some #(= % event-a) timeline)
      (some #(= % event-b) timeline)))

(defn a-and-b-happen [timeline event-a event-b]
  (and (some #(= % event-a) timeline)
       (some #(= % event-b) timeline)))

(defn a-and-b-never-happen-together [timeline event-a event-b]
  (not-any? #(= % [event-a event-b]) (partition 2 1 timeline)))

(defn a-happens-within-n-events-of-b [timeline event-a event-b n]
  (some #(<= (Math/abs (- %1 %2)) n)
        (.indexOf timeline event-a)
        (.indexOf timeline event-b)))

(defn a-happens-between-b-and-c [timeline event-a event-b event-c]
  (let [b-index (.indexOf timeline event-b)
        c-index (.indexOf timeline event-c)
        a-index (.indexOf timeline event-a)]
    (and (< b-index a-index) (< a-index c-index))))

(defn a-happens-before-all [timeline event events]
  (every? #(a-happens-before-b timeline event %) events))

(defn a-happens-after-all [timeline event events]
  (every? #(a-happens-after-b timeline event %) events))

(defn a-happens-immediately-before-any [timeline event-a events]
  (some #(a-happens-immediately-before-b timeline event-a %) events))

(defn a-happens-immediately-after-any [timeline event-a events]
  (some #(a-happens-immediately-after-b timeline event-a %) events))

(defn a-happens-exactly-n-times-between-b-and-c [timeline event-a event-b event-c n]
  (let [b-index (.indexOf timeline event-b)
        c-index (.indexOf timeline event-c)
        a-indices (keep-indexed #(when (= %2 event-a) %1) timeline)]
    (= n (count (filter #(and (< b-index %) (< % c-index)) a-indices)))))

(defn a-happens-at-least-n-times-between-b-and-c [timeline event-a event-b event-c n]
  (let [b-index (.indexOf timeline event-b)
        c-index (.indexOf timeline event-c)
        a-indices (keep-indexed #(when (= %2 event-a) %1) timeline)]
    (>= (count (filter #(and (< b-index %) (< % c-index)) a-indices)) n)))

(defn a-happens-at-most-n-times-between-b-and-c [timeline event-a event-b event-c n]
  (let [b-index (.indexOf timeline event-b)
        c-index (.indexOf timeline event-c)
        a-indices (keep-indexed #(when (= %2 event-a) %1) timeline)]
    (<= (count (filter #(and (< b-index %) (< % c-index)) a-indices)) n)))

(defn all-of-events-happen [timeline events]
  (every? #(some #(= % %2) timeline) events))

(defn none-of-events-happen [timeline events]
  (not-any? #(some #(= % %2) timeline) events))

(defn events-happen-in-order [timeline events]
  (let [event-indices (map #(keep-indexed #(when (= %2 %1) %1) timeline) events)]
    (= event-indices (sort event-indices))))

(defn a-happens-before-b-or-c [timeline event-a event-b event-c]
  (or (a-happens-before-b timeline event-a event-b)
      (a-happens-before-b timeline event-a event-c)))

(defn a-happens-after-b-or-c [timeline event-a event-b event-c]
  (or (a-happens-after-b timeline event-a event-b)
      (a-happens-after-b timeline event-a event-c)))

(defn a-and-b-happen-consecutively-n-times [timeline event-a event-b n]
  (= n (count (filter #(and (= % event-a)
                            (a-happens-immediately-before-b timeline event-a event-b))
                    timeline))))

(defn a-happens-between-b-and-c-n-times [timeline event-a event-b event-c n]
  (let [a-indices (keep-indexed #(when (= %2 event-a) %1) timeline)
        b-indices (keep-indexed #(when (= %2 event-b) %1) timeline)
        c-indices (keep-indexed #(when (= %2 event-c) %1) timeline)]
    (= n (count (filter
                 #(let [a-index %]
                    (some
                     #(and (< % a-index) (some #(> % a-index) c-indices))
                     b-indices))
                 a-indices)))))

(defn a-happens-immediately-before-b-or-c [timeline event-a event-b event-c]
  (or (a-happens-immediately-before-b timeline event-a event-b)
      (a-happens-immediately-before-b timeline event-a event-c)))

(defn a-happens-immediately-after-b-or-c [timeline event-a event-b event-c]
  (or (a-happens-immediately-after-b timeline event-a event-b)
      (a-happens-immediately-after-b timeline event-a event-c)))

(defn a-happens-immediately-before-b-and-c [timeline event-a event-b event-c]
  (and (a-happens-immediately-before-b timeline event-a event-b)
       (a-happens-immediately-before-b timeline event-a event-c)))

(defn a-happens-immediately-after-b-and-c [timeline event-a event-b event-c]
  (and (a-happens-immediately-after-b timeline event-a event-b)
       (a-happens-immediately-after-b timeline event-a event-c)))

(defn a-b-and-c-happen-consecutively [timeline event-a event-b event-c]
  (and (a-happens-immediately-before-b timeline event-a event-b)
       (a-happens-immediately-before-b timeline event-b event-c)))

(defn a-b-and-c-happen-in-any-order [timeline event-a event-b event-c]
  (let [indices (map #(keep-indexed #(when (= %2 %1) %1) timeline) [event-a event-b event-c])]
    (every? (partial < -1) (apply map + indices))))

(defn a-happens-before-b-and-c [timeline event-a event-b event-c]
  (and (a-happens-before-b timeline event-a event-b)
       (a-happens-before-b timeline event-a event-c)))

(defn a-happens-after-b-and-c [timeline event-a event-b event-c]
  (and (a-happens-after-b timeline event-a event-b)
       (a-happens-after-b timeline event-a event-c)))

(defn a-b-and-c-happen-together [timeline event-a event-b event-c]
  (let [event-set #{event-a event-b event-c}]
    (some #(= event-set (set (subvec timeline % (+ % 3))))
          (range 0 (- (count timeline) 2)))))
(defn a-happens [timeline event-a]
  (some #(= % event-a) timeline))

(defn a-happens-n-times [timeline event-a n]
  (= n (count (filter #(= % event-a) timeline))))

(defn a-happens-at-least-n-times [timeline event-a n]
  (>= (count (filter #(= % event-a) timeline)) n))

(defn a-happens-at-most-n-times [timeline event-a n]
  (<= (count (filter #(= % event-a) timeline)) n))

(defn a-happens-within-n-time-units [timeline event-a n]
  (<= (.indexOf timeline event-a) n))

(defn a-happens-after-n-time-units [timeline event-a n]
  (>= (.indexOf timeline event-a) n))

(defn a-happens-exactly-n-time-units-apart [timeline event-a n]
  (let [a-indices (keep-indexed #(when (= %2 event-a) %1) timeline)]
    (every? #(= (first %) (- (second %) n))
            (partition 2 1 a-indices))))

(defn a-happens-at-least-n-time-units-apart [timeline event-a n]
  (let [a-indices (keep-indexed #(when (= %2 event-a) %1) timeline)]
    (every? #(<= (first %) (- (second %) n))
            (partition 2 1 a-indices))))

(defn a-happens-at-most-n-time-units-apart [timeline event-a n]
  (let [a-indices (keep-indexed #(when (= %2 event-a) %1) timeline)]
    (every? #(>= (first %) (- (second %) n))
            (partition 2 1 a-indices))))

(defn a-happens-after-any-event [timeline event-a]
  (some #(= % event-a) (rest timeline)))
```

## TODO

* make integration with clojure.test easier (0.3)
* provide base_url & port as a parameter everywhere (0.4)
* consider adding not only event to the timeline, but also a service so assertions can be made on a service too (0.5)
* write tests for all combinators (0.6)
* test it with our services and improve usability (0.7)
* consider a generator of tests based on temporal-logic-like spec (temporal specs -> Instaparse -> tests) (0.8)
* make parallel running of more tests easier (0.9)
* document properly (1.0)

## License

Copyright © 2023

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
