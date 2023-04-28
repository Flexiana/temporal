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
