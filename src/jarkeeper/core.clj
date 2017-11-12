(ns jarkeeper.core
  (:require [compojure.core :refer [defroutes GET POST PUT DELETE HEAD]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.ssl :as ssl]
            [ring.middleware.conditional :as cond]
            [hiccup.middleware :refer [wrap-base-url]]
            [ring.middleware.defaults :refer :all]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.util.response :as resp]
            [clojure.tools.logging :as log]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [jarkeeper.downloads :as downloads]
            [jarkeeper.statuses :as statuses]
            [jarkeeper.views.index :as index-view]
            [jarkeeper.views.project :as project-view]
            [jarkeeper.views.json :as project-json]
            [environ.core :refer [env]]
            [sentry-clj.ring :as sentry-ring]
            [clojure.string :as str]
            [sentry-clj.core :as sentry])

  (:import (java.io PushbackReader)
           [java.text SimpleDateFormat]
           [java.util Locale TimeZone]))

(def last-modified-formatter "EEE, dd MMM yyyy HH:mm:ss zzz")

(defn- ^SimpleDateFormat formatter [format]
  (doto (SimpleDateFormat. ^String format Locale/US)
    (.setTimeZone (TimeZone/getTimeZone "GMT"))))

(defn last-modified []
  (.format (formatter last-modified-formatter) (java.util.Date.)))


(defn- repo-redirect [{:keys [params]}]
  (log/info params)
  (resp/redirect (str "/" (:repo-url params))))

(defn png-status-resp [filepath]
  (log/info "serving status image" filepath)
  (-> filepath
      (resp/resource-response)
      (resp/header "cache-control" "no-cache")
      (resp/header "last-modified" (last-modified))
      (resp/header "content-type" "image/png")))

(defn svg-status-resp [filepath]
  (log/info "serving status image" filepath)
  (-> filepath
      (resp/resource-response)
      (resp/header "cache-control" "no-cache")
      (resp/header "last-modified" (last-modified))
      (resp/header "content-type" "image/svg+xml")))

(defroutes app-routes
  (GET "/" [] (index-view/index))

  (HEAD "/" [] "")

  (POST "/find" [] repo-redirect)

  (GET "/:repo-owner/:repo-name" [repo-owner repo-name :as r]
    (if-let [project (statuses/project-map repo-owner repo-name)]
      (do
        (log/info "project-def" project)
        (project-view/index project))
      (do
        (sentry/send-event (assoc (sentry-ring/request->event r nil)
                             :message "Error while checking project"))
        (resp/redirect "/"))))

  (GET "/:repo-owner/:repo-name/status.png" [repo-owner repo-name]
    (let [project (statuses/project-map repo-owner repo-name)
          out-of-date-count (:out-of-date (:stats project))]
      (if (> out-of-date-count 0)
        (png-status-resp "public/images/out-of-date.png")
        (png-status-resp "public/images/up-to-date.png"))))

  (GET "/:repo-owner/:repo-name/status.svg" [repo-owner repo-name]
    (let [project (statuses/project-map repo-owner repo-name)
          out-of-date-count (:out-of-date (:stats project))]
      (if (> out-of-date-count 0)
        (svg-status-resp "public/images/out-of-date.svg")
        (svg-status-resp "public/images/up-to-date.svg"))))

  (GET "/:repo-owner/:repo-name/downloads.svg" [repo-owner repo-name]
    (-> (downloads/get-badge repo-owner repo-name)
        (resp/response)
        (resp/header "cache-control" "no-cache")
        (resp/header "last-modified" (last-modified))
        (resp/header "content-type" "image/svg+xml")))

  (GET "/:repo-owner/:repo-name/status.json" [repo-owner repo-name]
    (let [project (statuses/project-map repo-owner repo-name)]
      (project-json/render project)))

  (GET "/:any" []
    (resp/redirect "/")))

(defn production? [req]
  (= (:server-name req) "versions.deps.co"))

(defn wrap-referrer-policy
  [handler]
  (fn [request]
    (let [response (handler request)]
      (assoc-in response [:headers "Referrer-Policy"] "strict-origin"))))

(def csp (str/join "; " ["object-src 'none'"
                         "script-src 'strict-dynamic' 'unsafe-inline' http: https:;"
                         "base-uri 'none';"
                         "report-uri https://e33d8929ff48e13fdc2abfafda55bd99.report-uri.com/r/d/csp/enforce"]))

(defn wrap-content-security-policy
  [handler]
  (fn [request]
    (let [response (handler request)]
      (assoc-in response [:headers "Content-Security-Policy"] csp))))

(def app
  (-> #'app-routes
      (wrap-json-response)
      (wrap-resource "public")
      (wrap-base-url)
      (wrap-referrer-policy)
      #_(wrap-content-security-policy)
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
      (cond/if production? ssl/wrap-ssl-redirect)
      (cond/if production? ssl/wrap-hsts)
      (ssl/wrap-forwarded-scheme)
      (sentry-ring/wrap-report-exceptions nil {})))

(defn -main [& args]
  (let [ip "0.0.0.0"
        port (Integer/parseInt (get (System/getenv) "PORT" "3002"))]
    (run-jetty app {:host ip :port port})))
