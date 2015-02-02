(ns decktouch.handler
  (:require [decktouch.dev :refer [browser-repl start-figwheel]]
            [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [not-found resources]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
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
  (resources "/")
  (not-found "Not Found"))

(def app
  (let [handler (wrap-defaults routes site-defaults)]
    (if (env :dev?) (wrap-exceptions handler) handler)))
