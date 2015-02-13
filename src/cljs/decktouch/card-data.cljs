(ns decktouch.card-data
  (:require [reagent.core :as reagent :refer [atom]]))

(defn get-card-list-from-storage []
  "Get the list of cards from localStorage, or [] if it is empty"
  (let [key "card-list"
        list (.getItem js/localStorage key)]
    (if (undefined? list)
      []
      (map (fn [n] {:name n}) (js->clj (.parse js/JSON list))))))

(def card-list (atom (get-card-list-from-storage)))