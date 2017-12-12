(ns jarkeeper.core
  (:gen-class)
  (:require [aero.core :as aero]
            [clojure.java.io :as io]
            [com.stuartsierra.component :as component]
            [jarkeeper.redis :refer [new-redis]]
            [jarkeeper.web :refer [new-jetty-web-server]]))

(defn config
  "Read EDN config, with the given profile. See Aero docs at
  https://github.com/juxt/aero for details."
  [profile]
  (-> "config.edn"
      io/resource
      (aero/read-config {:profile profile})))

(defn new-system
  "Start the :dev system by default, with no arguments. Otherwise start
  the system with a given profile."
  ([]
   (new-system :dev))
  ([profile]
   (let [config (config profile)
         _ (println "new-system ====================> with profile:" profile)]
     (component/system-map
      :redis (new-redis (:redis-uri config))
      :host (:host config)
      :port (:port config)
      :webserver (component/using (new-jetty-web-server) [:redis :host :port])))))

(defn -main
  []
  (new-system :prod)
  @(promise))
