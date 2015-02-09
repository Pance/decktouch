(ns decktouch.handler
  (:require [decktouch.dev :refer [browser-repl start-figwheel]]
            [compojure.core :refer [GET POST defroutes]]
            [compojure.route :refer [not-found resources]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.adapter.jetty :as jetty]
            [selmer.parser :refer [render-file]]
            [environ.core :refer [env]]
            [prone.middleware :refer [wrap-exceptions]]
            [decktouch.mtg-card-master :as mtg-card-master]
            [clojure.data.json :as json]))

(defroutes routes
  (GET "/" [] (render-file "templates/index.html" {:dev (env :dev?)}))
  (GET "/data/input/:query" [query]
       (json/write-str
         (mtg-card-master/get-cards-matching-query query)))
  (POST "/data/card" request
        (let [card-name ((request :params) "card-name")]
          (str (mtg-card-master/get-card-info card-name))))
  (resources "/")
  (not-found "Not Found"))

(def app
  (let [handler (wrap-reload (wrap-params routes site-defaults))]
    (if (env :dev?) (wrap-reload (wrap-exceptions handler)) handler)))

(defonce server
  (jetty/run-jetty app {:port 8080}))
