(ns decktouch.card-data)

(defn get-card-list-from-storage []
  "Get the list of cards from localStorage, or [] if it is empty"
  (let [key "card-list"
        list (.getItem js/localStorage key)]
    (do
      (.log js/console key list)
      (if (undefined? list)
        []
        (.parse (js/JSON))))))

(def cards [])