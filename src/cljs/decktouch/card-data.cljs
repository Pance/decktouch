(ns decktouch.card-data)

(defn get-card-list-from-storage []
  "Get the list of cards from localStorage, or [] if it is empty"
  (let [key "card-list"
        list (.getItem js/localStorage key)]
    (if (undefined? list)
      []
      (map (fn [n] {:name n}) (js->clj (.parse js/JSON list))))))

(def cards (get-card-list-from-storage))