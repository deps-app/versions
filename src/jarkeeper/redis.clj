(ns jarkeeper.redis
  (:require [com.stuartsierra.component :as component]
            [taoensso.carmine :as car :refer [wcar]]))

(defmacro wcar* [redis & body] `(car/wcar (:redis ~redis) ~@body))

(defrecord Redis [uri]
  component/Lifecycle
  (start [component]
    (if (:redis component)
      component
      (assoc component :redis {:spec {:uri uri}})))
  (stop [component]
    (dissoc component :redis)))

(defn new-redis [uri]
  (map->Redis {:uri uri}))
