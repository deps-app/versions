(ns jarkeeper.core
  (:gen-class)
  (:require [aero.core :as aero]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]
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
   (let [config (config profile)]
     (log/info "new-system ====================> with profile:" profile)
     (log/info "config:" (prn-str (dissoc config :redis-uri)))
     (component/system-map
      :redis (new-redis (:redis-uri config)
                        (:redis-insecure-tls? config))
      :host (:host config)
      :port (:port config)
      :webserver (component/using (new-jetty-web-server) [:redis :host :port])))))

(defn -main
  [& args]
  (let [profile (some-> args first keyword)
        system (new-system (or profile :prod))]
    (component/start-system system)
    @(promise)))
