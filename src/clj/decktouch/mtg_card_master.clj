(ns decktouch.mtg-card-master
  (:require [clojure.data.json :as json]))

(def mtg-cards (json/read-str (slurp "resources/AllCards.json")))

(def card-names (keys mtg-cards))

(defn match
  "Returns true if query is a substring in string"
  [query string]
  (some? (re-matches (re-pattern (str "(?i)" ".*" query ".*"))
                    string)))

(defn find-cards-matching-query-in-names [names query]
  (let [match-query (partial match query)]
    (map (fn [thing] (str thing))
         (filter match-query names))))

(defn get-cards-matching-query
  "Return a list of maps of strings of card names that match the query for the card input autocomplete"
  [query]
  (let [query-args (clojure.string/split query #"\s")]
    (reduce find-cards-matching-query-in-names card-names query-args)))

(defn get-card-info
  "Given a card name, return a map containing all the info for the card"
  [card-name]
  (mtg-cards card-name))
