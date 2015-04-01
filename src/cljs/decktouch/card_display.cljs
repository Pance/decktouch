(ns decktouch.card-display
  (:require [reagent.core :as reagent]))

(def url (reagent/atom ""))

(defn card-img [url]
  [:img {:src url
         :style (js-obj "width" "240" "height" "340")}])

(defn component []
  (if (clojure.string/blank? @url)
      [:div [:b "Click on a card!"]]
      (card-img @url)))
