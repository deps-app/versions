(defproject co.deps/versions "0.6.0-SNAPSHOT"
  :description "Identify outdated dependencies in your Clojure project."
  :url "https://versions.deps.co"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.7.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.6.0"]
                 [hiccup "2.0.0-alpha1"]
                 [ring/ring-defaults "0.3.1"]
                 [ring/ring-core "1.6.3"]
                 [ring-server "0.5.0"]
                 [org.clojure/tools.logging "0.4.0"]
                 [ring/ring-jetty-adapter "1.6.3"]
                 [ancient-clj "0.3.11"]
                 [ring/ring-json "0.4.0"]
                 [slingshot/slingshot "0.12.2"]
                 [environ "1.1.0"]
                 [ring.middleware.conditional "0.2.0"]
                 [clj-http "3.7.0"]
                 [io.sentry/sentry-clj "0.5.1"]
                 [com.taoensso/carmine "2.16.0"]
                 [ch.qos.logback/logback-classic
                  "1.2.3"
                  :exclusions
                  [org.slf4j/slf4j-api]]
                 [aero "1.1.2"]
                 [com.stuartsierra/component "0.3.2"]]
  :main jarkeeper.core
  :profiles {:dev {:dependencies [[reloaded.repl "0.2.4"]]
                   :source-paths ["dev"]
                   :repl-options {:init-ns user}}})
