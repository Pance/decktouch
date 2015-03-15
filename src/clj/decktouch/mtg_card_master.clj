(ns decktouch.mtg-card-master
  (:require [clojure.data.json :as json]))

(defn add-multiverseid-to-card-in-map [mtg-cards {card-name "name" multiv "multiverseId"}]
  (let [existing-card-map (get mtg-cards card-name)
        existing-multiv (get existing-card-map "multiverseId")]
    ;if existing-card is nil or new multiv is less than existing-multiv
    (if (or
          (nil? existing-card-map)
          (< multiv (if (nil? existing-multiv)
                    0
                    existing-multiv)))
      mtg-cards
      (update-in mtg-cards [card-name]
                 assoc "multiverseId" multiv))))

(def card-multiverseids
  (filter #(not (nil? (get % "multiverseId")))
    (flatten
      (let [all-sets (json/read-str (slurp "resources/AllSets.json"))]
        (for [set-code (keys all-sets)]
          (for [set-card (get (get all-sets set-code) "cards")]
            {"name"         (set-card "name")
             "multiverseId" (set-card "multiverseid")}))))))

(def mtg-cards
  (let [allcards (json/read-str (slurp "resources/AllCards.json"))]
    (reduce add-multiverseid-to-card-in-map allcards card-multiverseids)))

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
