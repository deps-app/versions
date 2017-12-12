(ns jarkeeper.core
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
  [profile]
  (println "new-system ====================> with profile:" profile)
  (let [config (config profile)]
    (component/system-map
     :redis (new-redis (:redis-uri config))
     :host (:host config)
     :port (:port config)
     :webserver (component/using (new-jetty-web-server) [:redis :host :port]))))
