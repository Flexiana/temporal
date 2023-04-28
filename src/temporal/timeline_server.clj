(ns temporal.timeline-server
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.params :refer [wrap-params]]
            [ring.util.response :as response]
            [clojure.string :as clojure.string]))

(def timeline (atom []))

(defn add-event-handler [request] 
  (let [event (-> (get-in request [:params "event"])
                  (clojure.string/replace ":" "")
                  keyword)]
    (swap! timeline conj event)
    (response/response (str "Event added " event))))


(defn get-timeline-handler [_]
  (response/response (pr-str @timeline)))

(defn reset-timeline [_]
  (reset! timeline []))

(defn routes [request]
  (case (:uri request)
    "/add-event" (add-event-handler request)
    "/get-timeline" (get-timeline-handler request)
    "/reset-timeline" (reset-timeline request)
    (response/not-found "Not Found")))


(def app (wrap-params routes))

(defn start-server [port]
  (jetty/run-jetty app {:port port :join? false}))

; calling this:
; http POST http://localhost:6589/add-event event="a B" -f