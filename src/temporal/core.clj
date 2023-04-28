(ns temporal.core
  (:require [temporal.timeline-server :as timeline-server])
  (:require [clj-http.client :as http]))

#_{:clj-kondo/ignore [:unused-binding]}
#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn start-server [& args]
  (timeline-server/start-server 6589))

(defn add-event [event]
  (http/post
   "http://localhost:6589/add-event"
   {:form-params {:event (pr-str event)}}))


(defn a-happens-after-b [timeline event-a event-b]
  (> (.indexOf timeline event-a)
     (.indexOf timeline event-b)))


;; (defn a-happens-before-b [timeline event-a event-b]
;;   (< (.indexOf timeline event-a)
;;      (.indexOf timeline event-b)))


;; (defn a-and-b-are-consecutive [timeline event-a event-b]
;;   (= 1 (- (.indexOf timeline event-b)
;;           (.indexOf timeline event-a))))

;; (defn a-happens-immediately-before-b [timeline event-a event-b]
;;   (a-and-b-are-consecutive timeline event-a event-b))

;; (defn a-happens-immediately-after-b [timeline event-a event-b]
;;   (a-and-b-are-consecutive timeline event-b event-a))

;; (defn a-happens-n-times [timeline event n]
;;   (= n (count (filter #(= % event) timeline))))

;; (defn a-happens-at-least-n-times [timeline event n]
;;   (>= (count (filter #(= % event) timeline)) n))

;; (defn a-happens-at-most-n-times [timeline event n]
;;   (<= (count (filter #(= % event) timeline)) n))

;; (defn a-happens-exactly-n-times [timeline event n]
;;   (a-happens-n-times timeline event n))

;; (defn a-never-happens [timeline event]
;;   (a-happens-n-times timeline event 0))

;; (defn a-always-happens [timeline event]
;;   (every? #(= % event) timeline))

;; (defn a-happens-before-b-n-times [timeline event-a event-b n]
;;   (= n (count (filter #(and (= % event-a)
;;                             (a-happens-before-b timeline event-a event-b))
;;                       timeline))))

;; (defn a-happens-after-b-n-times [timeline event-a event-b n]
;;   (= n (count (filter #(and (= % event-a)
;;                             (a-happens-after-b timeline event-a event-b))
;;                       timeline))))

;; (defn a-or-b-happens [timeline event-a event-b]
;;   (or (some #(= % event-a) timeline)
;;       (some #(= % event-b) timeline)))

;; (defn a-and-b-happen [timeline event-a event-b]
;;   (and (some #(= % event-a) timeline)
;;        (some #(= % event-b) timeline)))

;; (defn a-and-b-never-happen-together [timeline event-a event-b]
;;   (not-any? #(= % [event-a event-b]) (partition 2 1 timeline)))

;; (defn a-happens-within-n-events-of-b [timeline event-a event-b n]
;;   (some #(<= (Math/abs (- %1 %2)) n)
;;         (.indexOf timeline event-a)
;;         (.indexOf timeline event-b)))

;; (defn a-happens-between-b-and-c [timeline event-a event-b event-c]
;;   (let [b-index (.indexOf timeline event-b)
;;         c-index (.indexOf timeline event-c)
;;         a-index (.indexOf timeline event-a)]
;;     (and (< b-index a-index) (< a-index c-index))))

;; (defn a-happens-before-all [timeline event events]
;;   (every? #(a-happens-before-b timeline event %) events))

;; (defn a-happens-after-all [timeline event events]
;;   (every? #(a-happens-after-b timeline event %) events))

;; (defn a-happens-immediately-before-any [timeline event-a events]
;;   (some #(a-happens-immediately-before-b timeline event-a %) events))

;; (defn a-happens-immediately-after-any [timeline event-a events]
;;   (some #(a-happens-immediately-after-b timeline event-a %) events))

;; (defn a-happens-exactly-n-times-between-b-and-c [timeline event-a event-b event-c n]
;;   (let [b-index (.indexOf timeline event-b)
;;         c-index (.indexOf timeline event-c)
;;         a-indices (keep-indexed #(when (= %2 event-a) %1) timeline)]
;;     (= n (count (filter #(and (< b-index %) (< % c-index)) a-indices)))))

;; (defn a-happens-at-least-n-times-between-b-and-c [timeline event-a event-b event-c n]
;;   (let [b-index (.indexOf timeline event-b)
;;         c-index (.indexOf timeline event-c)
;;         a-indices (keep-indexed #(when (= %2 event-a) %1) timeline)]
;;     (>= (count (filter #(and (< b-index %) (< % c-index)) a-indices)) n)))

;; (defn a-happens-at-most-n-times-between-b-and-c [timeline event-a event-b event-c n]
;;   (let [b-index (.indexOf timeline event-b)
;;         c-index (.indexOf timeline event-c)
;;         a-indices (keep-indexed #(when (= %2 event-a) %1) timeline)]
;;     (<= (count (filter #(and (< b-index %) (< % c-index)) a-indices)) n)))

;; (defn all-of-events-happen [timeline events]
;;   (every? #(some #(= % %2) timeline) events))

;; (defn none-of-events-happen [timeline events]
;;   (not-any? #(some #(= % %2) timeline) events))

;; (defn events-happen-in-order [timeline events]
;;   (let [event-indices (map #(keep-indexed #(when (= %2 %1) %1) timeline) events)]
;;     (= event-indices (sort event-indices))))

;; (defn a-happens-before-b-or-c [timeline event-a event-b event-c]
;;   (or (a-happens-before-b timeline event-a event-b)
;;       (a-happens-before-b timeline event-a event-c)))

;; (defn a-happens-after-b-or-c [timeline event-a event-b event-c]
;;   (or (a-happens-after-b timeline event-a event-b)
;;       (a-happens-after-b timeline event-a event-c)))

;; (defn a-and-b-happen-consecutively-n-times [timeline event-a event-b n]
;;   (= n (count (filter #(and (= % event-a)
;;                             (a-happens-immediately-before-b timeline event-a event-b))
;;                     timeline))))

;; (defn a-happens-between-b-and-c-n-times [timeline event-a event-b event-c n]
;;   (let [a-indices (keep-indexed #(when (= %2 event-a) %1) timeline)
;;         b-indices (keep-indexed #(when (= %2 event-b) %1) timeline)
;;         c-indices (keep-indexed #(when (= %2 event-c) %1) timeline)]
;;     (= n (count (filter
;;                  #(let [a-index %]
;;                     (some
;;                      #(and (< % a-index) (some #(> % a-index) c-indices))
;;                      b-indices))
;;                  a-indices)))))

;; (defn a-happens-immediately-before-b-or-c [timeline event-a event-b event-c]
;;   (or (a-happens-immediately-before-b timeline event-a event-b)
;;       (a-happens-immediately-before-b timeline event-a event-c)))

;; (defn a-happens-immediately-after-b-or-c [timeline event-a event-b event-c]
;;   (or (a-happens-immediately-after-b timeline event-a event-b)
;;       (a-happens-immediately-after-b timeline event-a event-c)))

;; (defn a-happens-immediately-before-b-and-c [timeline event-a event-b event-c]
;;   (and (a-happens-immediately-before-b timeline event-a event-b)
;;        (a-happens-immediately-before-b timeline event-a event-c)))

;; (defn a-happens-immediately-after-b-and-c [timeline event-a event-b event-c]
;;   (and (a-happens-immediately-after-b timeline event-a event-b)
;;        (a-happens-immediately-after-b timeline event-a event-c)))

;; (defn a-b-and-c-happen-consecutively [timeline event-a event-b event-c]
;;   (and (a-happens-immediately-before-b timeline event-a event-b)
;;        (a-happens-immediately-before-b timeline event-b event-c)))

;; (defn a-b-and-c-happen-in-any-order [timeline event-a event-b event-c]
;;   (let [indices (map #(keep-indexed #(when (= %2 %1) %1) timeline) [event-a event-b event-c])]
;;     (every? (partial < -1) (apply map + indices))))

;; (defn a-happens-before-b-and-c [timeline event-a event-b event-c]
;;   (and (a-happens-before-b timeline event-a event-b)
;;        (a-happens-before-b timeline event-a event-c)))

;; (defn a-happens-after-b-and-c [timeline event-a event-b event-c]
;;   (and (a-happens-after-b timeline event-a event-b)
;;        (a-happens-after-b timeline event-a event-c)))

;; (defn a-b-and-c-happen-together [timeline event-a event-b event-c]
;;   (let [event-set #{event-a event-b event-c}]
;;     (some #(= event-set (set (subvec timeline % (+ % 3))))
;;           (range 0 (- (count timeline) 2)))))
;; (defn a-happens [timeline event-a]
;;   (some #(= % event-a) timeline))

;; (defn a-happens-n-times [timeline event-a n]
;;   (= n (count (filter #(= % event-a) timeline))))

;; (defn a-happens-at-least-n-times [timeline event-a n]
;;   (>= (count (filter #(= % event-a) timeline)) n))

;; (defn a-happens-at-most-n-times [timeline event-a n]
;;   (<= (count (filter #(= % event-a) timeline)) n))

;; (defn a-happens-within-n-time-units [timeline event-a n]
;;   (<= (.indexOf timeline event-a) n))

;; (defn a-happens-after-n-time-units [timeline event-a n]
;;   (>= (.indexOf timeline event-a) n))

;; (defn a-happens-exactly-n-time-units-apart [timeline event-a n]
;;   (let [a-indices (keep-indexed #(when (= %2 event-a) %1) timeline)]
;;     (every? #(= (first %) (- (second %) n))
;;             (partition 2 1 a-indices))))

;; (defn a-happens-at-least-n-time-units-apart [timeline event-a n]
;;   (let [a-indices (keep-indexed #(when (= %2 event-a) %1) timeline)]
;;     (every? #(<= (first %) (- (second %) n))
;;             (partition 2 1 a-indices))))

;; (defn a-happens-at-most-n-time-units-apart [timeline event-a n]
;;   (let [a-indices (keep-indexed #(when (= %2 event-a) %1) timeline)]
;;     (every? #(>= (first %) (- (second %) n))
;;             (partition 2 1 a-indices))))

;; (defn a-happens-after-any-event [timeline event-a]
;;   (some #(= % event-a) (rest timeline)))
