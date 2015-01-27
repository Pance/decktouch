(ns decktouch.card-data)

(defonce cards (atom [
                      {:name "Bile Blight"
                       :mana-cost [:B :B]}
                      {:name "Seeker of the Way"
                       :mana-cost [:1 :W]}
                      {:name "Magma Jet"
                       :mana-cost [:1 :R]}]))