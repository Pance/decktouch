(ns decktouch.mtg-card-master-test
  (:require [clojure.test :refer :all]
            [decktouch.mtg-card-master :refer :all]))

(def cards {"foo" {"name" "foo"}
            "bar" {"name" "bar"}})

(def card-multiv {"name" "bar"
                  "multiverseId" 123})

(deftest add-multiv-to-card-in-map-test

  (testing "Add a multiverseId to a card in a map"
    (let [result (add-multiverseid-to-card-in-map cards card-multiv)]
      (is (contains? (get result (get card-multiv "name"))
                     "multiverseId"))))

  (testing "Add a multiverseId to a card that does not exist in map"
    (let [nonexistant-multiv {"name" "qux"
                              "multiverseId" 321}
          result (add-multiverseid-to-card-in-map cards nonexistant-multiv)]
      ; cards in result should be unchanged
      (is (= cards result))))
  (testing "Add a multiverseId to a card with a lower multiverseId"
    (let [lower-multiv {"name" "foo"
                        "multiverseId" 1}
          cards-with-multiv (add-multiverseid-to-card-in-map
                                cards
                                {"name" "foo"
                                 "multiverseId" 999})
          result (add-multiverseid-to-card-in-map cards-with-multiv lower-multiv)]
      (is (= cards-with-multiv result)))))


(run-all-tests)
