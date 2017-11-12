(ns jarkeeper.redis
  (:require [taoensso.carmine :as car :refer [wcar]]
            [environ.core :refer [env]]))

(def server1-conn {:spec {:uri (:redis-url env)}}) ; See `wcar` docstring for opts
(defmacro wcar* [& body] `(car/wcar server1-conn ~@body))
