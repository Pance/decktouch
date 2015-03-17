(ns decktouch.core
  (:require [reagent.core :as reagent]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [decktouch.card-data :as card-data]
            [decktouch.card-input :as card-input]
            [decktouch.widgets :as widgets]
            [decktouch.card-display :as card-display]
            [decktouch.card-list :as card-list])
  (:import goog.History))

;; -------------------------
;; Views

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
                    [:p.text-right (str (count (card-list/remove-cards-by-type @card-data/card-list "Creature"))) " Non-creature Spells"]
                    [:p.text-right (str (count (card-list/filter-cards-by-type @card-data/card-list "Creature"))) " Creatures"]
                    [:p.text-right (str (count (card-list/filter-cards-by-type @card-data/card-list "Land"))) " Lands"]]]
        [:div.col-md-4 [card-list/component @card-data/card-list]]
        [:div.col-md-4 [card-display/component]]]])

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
