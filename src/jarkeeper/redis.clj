(ns jarkeeper.redis
  (:require [clojure.string :as str]
            [com.stuartsierra.component :as component]
            [taoensso.carmine :as car :refer [wcar]])
  (:import [java.net Socket URI]
           [java.security SecureRandom]
           [java.security.cert X509Certificate]
           [javax.net.ssl SSLContext SSLSocketFactory TrustManager X509TrustManager]))

(defmacro wcar* [redis & body] `(car/wcar (:redis ~redis) ~@body))

(defn- true-value? [v]
  (contains? #{"1" "true" "yes" "on"}
             (some-> v str str/trim str/lower-case)))

(defn- rediss-uri? [uri]
  (try
    (= "rediss" (some-> uri URI. .getScheme str/lower-case))
    (catch Exception _
      false)))

;; Heroku Redis uses a self-signed certificate chain, so Java default trust
;; validation fails unless we explicitly opt out of peer verification.
(defn- insecure-trust-manager []
  (proxy [X509TrustManager] []
    (getAcceptedIssuers [] (make-array X509Certificate 0))
    (checkClientTrusted [_ _])
    (checkServerTrusted [_ _])))

(def ^:private insecure-ssl-socket-factory
  (delay
    (let [context (SSLContext/getInstance "TLS")
          trust-managers (into-array TrustManager [(insecure-trust-manager)])]
      (.init context nil trust-managers (SecureRandom.))
      (.getSocketFactory context))))

(defn- insecure-ssl-fn [{:keys [socket host port]}]
  (.createSocket ^SSLSocketFactory @insecure-ssl-socket-factory
                 ^Socket socket
                 ^String host
                 ^Integer port
                 true))

(defrecord Redis [uri insecure-tls?]
  component/Lifecycle
  (start [component]
    (if (:redis component)
      component
      (let [insecure-tls? (true-value? insecure-tls?)
            redis-spec (cond-> {:uri uri}
                         (and insecure-tls? (rediss-uri? uri)) (assoc :ssl-fn insecure-ssl-fn))]
        (assoc component :redis {:spec redis-spec}))))
  (stop [component]
    (dissoc component :redis)))

(defn new-redis
  ([uri]
   (new-redis uri false))
  ([uri insecure-tls?]
   (map->Redis {:uri uri
                :insecure-tls? insecure-tls?})))
