(defproject decktouch "0.1.0-SNAPSHOT"
  :description "Build magic card deck lists easily."
  :url ""

  :min-lein-version "2.5.0"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [reagent "0.5.0"]
                 [reagent-forms "0.3.9"]
                 [reagent-utils "0.1.2"]
                 [secretary "1.2.1"]
                 [org.clojure/clojurescript "0.0-3126" :scope "provided"]
                 [com.cemerick/piggieback "0.1.4"]
                 [weasel "0.5.0"]
                 [ring "1.3.2"]
                 [ring/ring-defaults "0.1.3"]
                 [prone "0.8.0"]
                 [compojure "1.3.1"]
                 [selmer "0.7.9"]
                 [environ "1.0.0"]
                 [leiningen "2.5.0"]
                 [figwheel "0.1.6-SNAPSHOT"]
                 [cljs-ajax "0.3.9"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]]

  :plugins [[lein-cljsbuild "1.0.5"]
            [lein-environ "1.0.0"]
            [lein-ring "0.9.0"]
            [lein-asset-minifier "0.2.2"]]

  :ring {:handler decktouch.handler/app
         :uberwar-name "decktouch.war"
         :port 8080}

  :main decktouch.handler

  :source-paths ["src/clj" "src/cljs"]
  :test-paths ["test/clj" "test/cljs"]

  :uberjar-name "decktouch.jar"

  :clean-targets ^{:protect false} ["resources/public/js/app.js"]

  :cljsbuild {
    :builds {
      :app {
        :source-paths ["src/cljs"]
        :compiler {
          :output-to  "resources/public/js/app.js"
          :output-dir "resources/public/js/out"
          :optimizations :whitespace
          :pretty-print  true}}}}

  :profiles {
    :dev {
      :repl-options {
        :init-ns decktouch.handler
        :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

      :dependencies [[ring-mock "0.1.5"]
                     [ring/ring-devel "1.3.2"]
                     [pjstadig/humane-test-output "0.6.0"]]

      :plugins [[lein-figwheel "0.2.0-SNAPSHOT"]]

      :injections [(require 'pjstadig.humane-test-output)
                   (pjstadig.humane-test-output/activate!)]

      :figwheel {:http-server-root "public"
                 :server-port 3449
                 :css-dirs ["resources/public/css"]
                 :ring-handler decktouch.handler/app}

      :env {:dev? true}

      :cljsbuild {
        :builds {
          :app {
            :source-paths ["env/dev/cljs"]
            :compiler {:output-to "resources/public/js/app.js"}}}}}

    :uberjar {
      :hooks [leiningen.cljsbuild]
      :env {:production true}
      :aot :all
      :omit-source true
      :cljsbuild {:jar true
                  :builds {
                    :app {:source-paths ["env/prod/cljs"]
                          :compiler {
                            :optimizations :whitespace
                            :pretty-print false}}}}}

    :production {
      :ring {
        :open-browser? false
        :stacktraces?  false
        :auto-reload?  false}}})
