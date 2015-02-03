(defproject decktouch "0.1.0-SNAPSHOT"
  :description "Build magic card deck lists easily."
  :url ""

  :source-paths ["src/clj" "src/cljs"]

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.facebook/react "0.11.2"]
                 [reagent "0.4.3"]
                 [reagent-forms "0.3.9"]
                 [reagent-utils "0.1.2"]
                 [secretary "1.2.1"]
                 [org.clojure/clojurescript "0.0-2760" :scope "provided"]
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
                 [cljs-ajax "0.3.9"]]

  :plugins [
            [lein-cljsbuild "1.0.4"]
            [lein-environ "1.0.0"]
            [lein-ring "0.9.0"]
            [lein-asset-minifier "0.2.2"]]

  :ring {:handler decktouch.handler/app
         :uberwar-name "decktouch.war"}

  :min-lein-version "2.5.0"

  :uberjar-name "decktouch.jar"

  :clean-targets ^{:protect false} ["resources/public/js"]

  :minify-assets
  {:assets
    {"resources/public/css/site.min.css" "resources/public/css/site.css"}}

  :cljsbuild {:builds {:app {:source-paths ["src/cljs"]
                             :compiler {:output-to     "resources/public/js/app.js"
                                        :output-dir    "resources/public/js/out"
                                        :externs       ["react/externs/react.js"
                                                        "resources/public/js/jquery-ui.js"
                                                        "resources/public/js/jquery-2.1.3.min.js"]
                                        :optimizations :none
                                        :pretty-print  true}}}}

  :profiles {:dev {:repl-options {:init-ns decktouch.handler
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

                   :cljsbuild {:builds {:app {:source-paths ["env/dev/cljs"]
                                              :compiler {:source-map true}}}}}

             :uberjar {:hooks [leiningen.cljsbuild minify-assets.plugin/hooks]
                       :env {:production true}
                       :aot :all
                       :omit-source true
                       :cljsbuild {:jar true
                                   :builds {:app
                                             {:source-paths ["env/prod/cljs"]
                                              :compiler
                                              {:optimizations :advanced
                                               :pretty-print false}}}}}

             :production {:ring {:open-browser? false
                                 :stacktraces?  false
                                 :auto-reload?  false}}})
