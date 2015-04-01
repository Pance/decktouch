(ns decktouch.widgets
  (:require [decktouch.util :as util]))

(defn count-mana-freq [mana-map mana]
  (if (contains? mana-map mana)
    (assoc mana-map mana (inc (get mana-map mana)))
    mana-map))

(defn get-mana-symbol-counts [cards]
  (let [mana-costs (reduce str (map #(get % "manaCost") cards))]
    (reduce count-mana-freq {"R" 0 "G" 0 "U" 0 "W" 0 "B" 0} mana-costs)))

(defn mana-symbol [color number]
  (let [mana-labels {"B" (fn [n] [:span.label.label-default (str n " " color)])
                     "U" (fn [n] [:span.label.label-primary (str n " " color)])
                     "G" (fn [n] [:span.label.label-success (str n " " color)])
                     "W" (fn [n] [:span.label.label-warning (str n " " color)])
                     "R" (fn [n] [:span.label.label-danger (str n " " color)])}]
    ((mana-labels color) number)))

(defn mana-color-composition [cards]
  (let [mana-symbol-counts (get-mana-symbol-counts cards)
        sorted-mana-symbol-counts
          (into (sorted-map-by (fn [key1 key2]
                                 ; the compare below is a workaround
                                 (compare [(get mana-symbol-counts key2) key2]
                                          [(get mana-symbol-counts key1) key1])))
            mana-symbol-counts)]
    [:div [:h3.text-right
            (for [m (keys sorted-mana-symbol-counts)]
              (let [n (sorted-mana-symbol-counts m)]
                (if (> n 0)
                  [mana-symbol m n])))]
      [:br]]))

(defn card-counter [cards]
  [:h3 [:p.text-right (count cards) " cards"]])

(defn card-types [cards]
  [:div
    [:p.text-right (str (count (util/remove-cards-by-type cards "Creature"))) " Non-creature Spells"]
    [:p.text-right (str (count (util/filter-cards-by-type cards "Creature"))) " Creatures"]
    [:p.text-right (str (count (util/filter-cards-by-type cards "Land"))) " Lands"]])

