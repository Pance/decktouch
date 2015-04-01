(ns decktouch.card-list
  (:require [reagent.core :as reagent]
            [decktouch.card-display :as card-display]
            [decktouch.card-data :as card-data]
            [decktouch.util :as util]))

(defn card-img [url]
  [:img {:src url
         :style (js-obj "width" "240" "height" "340")}])

(defn card-in-list [card card-id]
  (let [multiverseId (get card "multiverseId")
        image-link (str "https://api.mtgdb.info/content/hi_res_card_images/" multiverseId ".jpg")]
  [:li
    [:small
      {:onClick (fn [] (card-data/add-card-to-list! (get card "name") 1))}
      [:span.glyphicon.glyphicon-plus-sign]]
    [:a {:id card-id
         :data-toggle "tooltip"
         :data-placement "bottom"
         :data-html true
         :data-trigger "hover"
         :data-content (reagent/render-component-to-string [card-img image-link])
         :onClick (fn [] (reset! card-display/url image-link))}
        [:span (str " " (get card "quantity") " x " (get card "name") "  ")]]
    [:small
      {:onClick (fn [] (card-data/add-card-to-list! (get card "name") -1))}
      [:span.glyphicon.glyphicon-remove]]]))

(defn card-in-list-did-mount [card-id]
    (.popover (js/$ (str "#" card-id))))

(defn card-in-list-component [card]
  (let [card-id (str (clojure.string/replace (get card "imageName") #"\W" "_"))]
    (with-meta #(card-in-list % card-id)
               {:component-did-mount #(card-in-list-did-mount card-id)})))

(defn card-type-list [cards type]
  (let [filtered-cards-by-type (util/filter-cards-by-type cards type)]
    (if (not-empty filtered-cards-by-type)
      [:div
        [:p type]
        [:ul.list-unstyled
          (for [card (util/filter-cards-by-type cards type)]
               ^{:key (get card "imageName")} [card-in-list-component card])]])))

(defn component [cards]
  (if (empty? cards)
    [:strong "Add some cards!"]
    ;; else
    [:div.row
      [:div.col-md-6
        [card-type-list cards "Creature"]
        [card-type-list cards "Land"]]
      (let [noncreature-cards (util/remove-cards-by-type cards "Creature")]
        [:div.col-md-6
          [card-type-list cards "Planeswalker"]
          [card-type-list cards "Instant"]
          [card-type-list cards "Sorcery"]
          [card-type-list noncreature-cards "Enchantment"]
          [card-type-list noncreature-cards "Artifact"]])]))
