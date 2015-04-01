(ns decktouch.util)

(defn is-card-of-type? [card type]
  (contains? (set (get card "types")) type))

(defn remove-cards-by-type [cards type]
  (filter #(not (is-card-of-type? % type)) cards))

(defn filter-cards-by-type [cards type]
  (filter (fn [card]
            (let [c (js->clj card)]
              (is-card-of-type? c type))) cards))

