(defproject co.deps/versions "0.7.1-SNAPSHOT"
  :description "Identify outdated dependencies in your Clojure project."
  :url "https://versions.deps.co"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.7.0"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [compojure "1.6.1"]
                 [hiccup "2.0.0-alpha1"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-core "1.7.0"]
                 [ring-server "0.5.0"]
                 [org.clojure/tools.logging "0.4.1"]
                 [ring/ring-jetty-adapter "1.7.0"]
                 [ancient-clj "0.6.15"]
                 [ring/ring-json "0.4.0"]
                 [slingshot/slingshot "0.12.2"]
                 [ring.middleware.conditional "0.2.0"]
                 [clj-http "3.9.1"]
                 [io.sentry/sentry-clj "0.7.2"]
                 [com.taoensso/carmine "3.5.0"]
                 [ch.qos.logback/logback-classic
                  "1.2.3"
                  :exclusions
                  [org.slf4j/slf4j-api]]
                 [aero "1.1.3"]
                 [com.stuartsierra/component "0.3.2"]]
  :main jarkeeper.core
  :profiles {:dev {:dependencies [[reloaded.repl "0.2.4"]]
                   :source-paths ["dev"]
                   :repl-options {:init-ns user}}})
