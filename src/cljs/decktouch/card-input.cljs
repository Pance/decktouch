(ns decktouch.card-input
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET]]))

(def llist
     [{:name "foo"}
      {:name "bar"}
      {:name "lol"}])

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
  (let [things (map :name llist)]
    (.autocomplete (js/$ "#the-input")
                   (cljs.core/js-obj "source" (clj->js things)))))

(defn component [the-cards]
  (reagent/create-class {:render #(card-input the-cards)
                         :component-did-mount card-input-did-mount}))

