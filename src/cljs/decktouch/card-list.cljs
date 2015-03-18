(ns decktouch.card-list
  (:require [reagent.core :as reagent]
            [decktouch.card-display :as card-display]))

(defn card-img [url]
  [:img {:src url
         :style (js-obj "width" "240" "height" "340")}])

(defn card-in-list [card card-id]
  (let [multiverseId (get card "multiverseId")
        image-link (str "https://api.mtgdb.info/content/hi_res_card_images/" multiverseId ".jpg")]
  [:li
    [:a {:id card-id
         :data-toggle "tooltip"
         :data-placement "bottom"
         :data-html true
         :data-trigger "hover"
         :data-content (reagent/render-component-to-string [card-img image-link])
         :onClick (fn [] (reset! card-display/url image-link))}
         (str (get card "name"))]]))

(defn card-in-list-did-mount [card-id]
    (.popover (js/$ (str "#" card-id))))

(defn card-in-list-component [card]
  (let [card-id (str (clojure.string/replace (get card "imageName") #"\W" "_"))]
    (reagent/create-class {:render #(card-in-list card card-id)
                           :component-did-mount #(card-in-list-did-mount card-id)
                           })))

(defn is-card-of-type? [card type]
  (contains? (set (get card "types")) type))

(defn filter-cards-by-type [cards type]
  (filter (fn [card] (let [c (js->clj card)]
                       (is-card-of-type? c type))) cards))

(defn remove-cards-by-type [cards type]
  (filter #(not (is-card-of-type? % type)) cards))

(defn card-type-list [cards type]
  (let [filtered-cards-by-type (filter-cards-by-type cards type)]
    (if (not-empty filtered-cards-by-type)
      [:div
        [:p type]
        [:ul
          (for [card (filter-cards-by-type cards type)]
               ^{:key (get card "imageName")} [card-in-list-component card])]])))

(defn component [cards]
  (if (empty? cards)
    [:strong "Add some cards!"]
    ;; else
    [:div.row
      [:div.col-md-6
        [card-type-list cards "Creature"]
        [card-type-list cards "Land"]]
      (let [noncreature-cards (remove-cards-by-type cards "Creature")]
        [:div.col-md-6
          [card-type-list cards "Planeswalker"]
          [card-type-list cards "Instant"]
          [card-type-list cards "Sorcery"]
          [card-type-list noncreature-cards "Enchantment"]
          [card-type-list noncreature-cards "Artifact"]])]))
