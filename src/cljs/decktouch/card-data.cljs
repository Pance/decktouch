(ns decktouch.card-data
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [POST]]
            [cljs.core.async :refer [chan >! <!]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn get-card-list-from-storage []
  "Get the list of cards from localStorage, or [] if it is empty"
  (let [key "card-list"
        list (.getItem js/localStorage key)]
    (if (undefined? list)
      []
      (map (fn [n] {"name" n}) (js->clj (.parse js/JSON list))))))

;; This collection is a vector of maps of strings to strings
;; [ {"a" "z"} ]
(def card-list (atom (into [] (get-card-list-from-storage))))

(defn <lookup-card-data [card-name]
  (let [c (chan)]
    (POST "/data/card" {:handler #(go (>! c %))
                        :params {:card-name card-name}
                        :format :raw
                        :error-handler (fn [{:keys [status status-text]}]
                                         (.log js/console
                                               (str "Error: " status " "
                                                    status-text)))})
    c))

(defn add-more-card-data [list-o-cards card-data]
  "Return a vector based on list-o-cards but every item in
   list-o-cards with a matching 'name' value as card-data
   will be merged with card-data"
  (into []
        (for [c list-o-cards]
          (do
          (if (= (get card-data "name") (get c "name"))
            card-data
            c)))))

(defn add-card-to-list! [card-name]
  (do
    (swap! card-list conj {"name" card-name})
    (go
      (let [response (js->clj (.parse js/JSON (<! (<lookup-card-data card-name))))]
        (swap! card-list add-more-card-data response)
        (.log js/console (str @card-list))))))