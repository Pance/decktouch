(ns decktouch.core
  (:require [reagent.core :as reagent]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [decktouch.card-data :as card-data]
            [decktouch.card-input :as card-input]
            [decktouch.widgets :as widgets])
  (:import goog.History))

(def displayed-card-url (reagent/atom ""))

;; -------------------------
;; Views

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
         :title (reagent/render-component-to-string [card-img image-link])
         :onClick (fn [] (reset! displayed-card-url image-link))}
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


(defn home-page []
  [:div.container-fluid
    [:br] [:br] [:br] [:br] [:br]
      [:div.row
        [:div.col-md-offset-4.col-md-6
          [card-input/component card-data/card-list]]]
      [:div.row
        [:div.col-md-offset-2.col-md-2
          [:div.row
                    [widgets/card-counter @card-data/card-list]]
          [:div.row
                    [widgets/mana-color-composition @card-data/card-list]
                    [:br]]
          [:div.row
                    [:p.text-right (str (count (remove-cards-by-type @card-data/card-list "Creature"))) " Non-creature Spells"]
                    [:p.text-right (str (count (filter-cards-by-type @card-data/card-list "Creature"))) " Creatures"]
                    [:p.text-right (str (count (filter-cards-by-type @card-data/card-list "Land"))) " Lands"]]]
        [:div.col-md-4 [card-list @card-data/card-list]]
        [:div.col-md-4 (if (clojure.string/blank? @displayed-card-url)
                         [:div [:b "Click on a card!"]]
                         (card-img @displayed-card-url))]]])

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
