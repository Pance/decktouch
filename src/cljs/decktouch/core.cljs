(ns decktouch.core
  (:require [reagent.core :as reagent]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [decktouch.card-data :as card-data]
            [decktouch.card-input :as card-input])
  (:import goog.History))


;; -------------------------
;; Views

(defn img [url]
  (str "<img src='" url "'>"))

(defn card-in-list [card card-id]
  (let [image-name (clojure.string/replace (get card "imageName") #" " "_")
        image-link (str "http://mtgimage.com/card/" image-name ".jpg")]
  [:li
    [:a {:id card-id
         :data-toggle "tooltip"
         :data-placement "bottom"
         :data-html true
         :data-trigger "hover"
         :title (img image-link)}
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


(defn card-list [cards]
  (if (empty? cards)
    [:strong "Add some cards!"]
    ;; else
    [:div.row
      [:div.col-md-2
        [card-type-list cards "Creature"]
        [card-type-list cards "Land"]]
      [:div.col-md-2
        [card-type-list cards "Planeswalker"]
        [card-type-list cards "Instant"]
        [card-type-list cards "Sorcery"]
        (let [noncreature-cards (remove-cards-by-type cards "Creature")]
          [card-type-list noncreature-cards "Enchantment" {:not "Creature"}])
        [card-type-list cards "Artifact"]]]))

(defn record-mana-freq [mana-map mana]
  (if (contains? mana-map mana)
    (assoc mana-map mana (inc (get mana-map mana)))
    mana-map))

(defn get-mana-symbol-counts [cards]
  (let [mana-costs (reduce str (map #(get % "manaCost") cards))]
    (reduce record-mana-freq {"R" 0 "G" 0 "U" 0 "W" 0 "B" 0} mana-costs)))

(defn mana-symbol [color number]
  (let [mana-labels {"B" (fn [n] [:span.label.label-default (str n " " color)])
                     "U" (fn [n] [:span.label.label-primary (str n " " color)])
                     "G" (fn [n] [:span.label.label-success (str n " " color)])
                     "W" (fn [n] [:span.label.label-warning (str n " " color)])
                     "R" (fn [n] [:span.label.label-danger (str n " " color)])}]
    ((mana-labels color) number)))


(defn mana-cost-widget [cards]
  (let [mana-symbol-counts (get-mana-symbol-counts cards)
        sorted-mana-symbol-counts
            (into (sorted-map-by (fn [key1 key2]
                                   (compare [(get mana-symbol-counts key2) key2]
                                            [(get mana-symbol-counts key1) key1])))
                  mana-symbol-counts)]
    [:div [:h3.text-right
            (for [m (keys sorted-mana-symbol-counts)]
              (let [n (sorted-mana-symbol-counts m)]
                (if (> n 0)
                  [mana-symbol m n])))]]))

(defn card-counter [cards]
  [:h3 [:p.text-right (count cards) " cards"]])

(defn home-page []
  [:div.container-fluid
    [:br] [:br] [:br] [:br] [:br]
      [:div.row
        [:div.col-md-offset-4.col-md-6
          [card-input/component card-data/card-list]]]
      [:div.row
        [:div.col-md-offset-2.col-md-2
          [:div.row
                    [card-counter @card-data/card-list]]
          [:div.row
                    [mana-cost-widget @card-data/card-list]
                    [:br]]
          [:div.row
                    [:p.text-right (str (count (remove-cards-by-type @card-data/card-list "Creature"))) " Non-creature Spells"]
                    [:p.text-right (str (count (filter-cards-by-type @card-data/card-list "Creature"))) " Creatures"]
                    [:p.text-right (str (count (filter-cards-by-type @card-data/card-list "Land"))) " Lands"]]]
        [:div.row [card-list @card-data/card-list]]]])

(defn about-page []
  [:div [:h2 "About decktouch"]
   [:div [:a {:href "#/"} "go to the home page"]]])

(defn navbar []
  [:nav.navbar.navbar-fixed-top.navbar-inverse
    [:a.navbar-brand {:href "#"}
      "Decktouch"]
    [:div#navbar.collapse.navbar-collapse
      [:ul.nav.navbar-nav
        [:li [:a {:href "#/about"} "go to about page"]]]]])

(defn current-page []
  [:div
    [navbar]
    [:div [(session/get :current-page)]]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :current-page home-page))

(secretary/defroute "/about" []
  (session/put! :current-page about-page))

;; -------------------------
;; Initialize app
(defn init! []
  (reagent/render-component [current-page] (.getElementById js/document "app")))

;; -------------------------
;; History
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))
;; need to run this after routes have been defined
(hook-browser-navigation!)
