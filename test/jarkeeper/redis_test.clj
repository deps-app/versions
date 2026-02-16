(ns jarkeeper.redis-test
  (:require [clojure.test :refer [deftest is]]
            [com.stuartsierra.component :as component]
            [jarkeeper.redis :as redis]))

(deftest rediss-insecure-tls-adds-custom-ssl-fn
  (let [started (component/start (redis/new-redis "rediss://redis.example.com:6379/0" "true"))]
    (is (= "rediss://redis.example.com:6379/0"
           (get-in started [:redis :spec :uri])))
    (is (fn? (get-in started [:redis :spec :ssl-fn])))))

(deftest rediss-without-insecure-tls-uses-uri-only
  (let [started (component/start (redis/new-redis "rediss://redis.example.com:6379/0" false))]
    (is (= {:uri "rediss://redis.example.com:6379/0"}
           (get-in started [:redis :spec])))))
