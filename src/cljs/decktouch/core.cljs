(ns decktouch.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [goog.events :as events]
              [goog.history.EventType :as EventType]
              [decktouch.card-data :as card-data])
    (:import goog.History))

(def the-cards (atom card-data/cards))


;; -------------------------
;; Views
(defn card-in-list [card]
  [:li
   (str card)])

(defn card-input []
  (let [get-val #(.-value (.getElementById js/document "the-input"))
        reset-input #(set! (.-value (.getElementById js/document
                                                       "the-input"))
                           "")
        save #(let [v (-> (get-val) str clojure.string/trim)]
                (do
                  (if-not (empty? v)
                    (do
                      (swap! the-cards conj {:name v})
                      (reset-input)))
                  false))]
    [:form {:onSubmit #(do
                         (save))}
     [:input#the-input {:type "text"}]]))

(defn card-list [cards]
  [:ul
   (for [card cards]
     ^{:key card} (card-in-list (:name card)))])

(defn home-page []
  [:div [:h2 "Welcome to decktouch"]
   [:div (card-input)]
   [:div [card-list @the-cards]]
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
