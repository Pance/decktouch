(ns decktouch.card-input
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET]]))

(defn card-input [the-cards]
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
    [:div.ui-widget
      [:form {:onSubmit #(do
                           (save)
                           false)}
        [:input#the-input {:type "text"}]]]))

(defn ^:export card-input-did-mount []
  (let [query-card-master
        (fn [request response]
          (let [query-url (str "../data/input/" (.-term request))]
            (GET query-url {:handler #(response (.parse js/JSON %))})))]
    (.autocomplete (js/$ "#the-input")
                   (cljs.core/js-obj "source" query-card-master
                                     "minLength" 2))))

(defn component [the-cards]
  (reagent/create-class {:render #(card-input the-cards)
                         :component-did-mount card-input-did-mount}))

