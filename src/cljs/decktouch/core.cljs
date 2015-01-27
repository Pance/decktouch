(ns decktouch.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [goog.events :as events]
              [goog.history.EventType :as EventType]
              [decktouch.card-data :as card-data])
    (:import goog.History))

(defn get-card-input-value []
  (.-value (.getElementById js/document "the-input")))
;; -------------------------
;; Views
(defn card-in-list [card]
  [:li
   (str card)])

(defn card-input []
  [:form {:on-submit #(do
                       (swap! card-data/cards (conj @card-data/cards)
                                              (get-card-input-value))
                       false)}
   [:input#the-input {:type "text"}]])

(defn card-list [cards]
  [:ul
   (for [card cards]
     ^{:key card} (card-in-list (:name card)))])

(defn home-page []
  [:div [:h2 "Welcome to decktouch"]
   [:div [card-list @card-data/cards]]
   [:div [:a {:href "#/about"} "go to about page"]]])

(defn about-page []
  [:div [:h2 "About decktouch"]
   [:div [:a {:href "#/"} "go to the home page"]]])

(defn current-page []
  [:div [(session/get :current-page)]])

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
