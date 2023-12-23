(ns hw
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [clojure.string :as s]))

(defn ok [body]
  {:status 200 :body body})

(defn not-found []
  {:status 404 :body "Not found\n"})

(def unmentionables #{"YHWH"
                      "Voldemort"
                      "Mxyzptlk"
                      "Rumplestiltskin"
                      "曹操"})


(defn greeting-for [nm]
  (cond
    (unmentionables (s/capitalize nm)) nil
    (empty? nm) "Hello, world!\n"
    :else (str "Hello, " nm "\n")))

(defn respond-hello [request]
  (let [nm (get-in request [:query-params :name])
        body (greeting-for nm)]
    (if body
      (ok body)
      (not-found))))


(def routes
  (route/expand-routes
    #{["/greet" :get respond-hello :route-name :greet]}))

(def service-map
  {::http/routes routes
   ::http/type :jetty
   ::http/port 8890})

(defn start []
  (-> service-map http/create-server http/start))

;; For interactive development
(defonce server (atom nil))

(defn start-dev []
  (reset! server (-> (assoc service-map ::http/join? false)
                     http/create-server
                     http/start)))

(defn stop-dev []
  (http/stop @server))

(defn restart []
  (stop-dev)
 	(start-dev))