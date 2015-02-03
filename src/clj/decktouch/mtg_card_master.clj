(ns decktouch.mtg-card-master
  (:require [clojure.data.json :as json]))

(def mtg-cards (json/read-str (slurp "resources/AllCards.json")))

(def card-names (keys mtg-cards))

(defn match
  "Returns true if query is a substring in string"
  [query string]
  (some? (re-matches (re-pattern (str "(?i)" query ".*"))
                    string)))

(defn get-cards-matching-query
  "Return a list of maps of strings of card names that match the query for the card input autocomplete"
  [query]
  (let [match-query (partial match query)]
    (map (fn [thing] (str thing))
      (filter match-query card-names))))
