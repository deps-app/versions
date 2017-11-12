(ns jarkeeper.statuses
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clj-http.client :as client]
            [clojure.tools.logging :as log]
            [taoensso.carmine :as car]
            [jarkeeper.redis :refer [wcar*]]
            [ancient-clj.core :as anc])

  (:import (java.io PushbackReader)))

(defn- starting-num? [string]
  (some-> string
          name
          first
          str
          read-string
          number?))

(defn read-file
  "Reads all forms in a file lazily."
  [r]
  (binding [*read-eval* false]
    (let [x (read r false ::eof)]
      (if-not (= ::eof x)
        (cons x (lazy-seq (read-file r)))))))

(defn read-lein-project
  "Tries to read first project form in the project.clj file and use that"
  [parsed-project-file]
  (some->> parsed-project-file
           (some (fn [form]
                   (if (= 'defproject (first form))
                     form)))
           ))

(defn read-project-clj [repo-owner repo-name]
  (try
    (let [url (str "https://raw.github.com/" repo-owner "/" repo-name "/master/project.clj")]
      (with-open [rdr (PushbackReader. (io/reader url))]
        (read-lein-project (read-file rdr))))
    (catch Exception _
      nil)))

(defn read-boot-deps
  "Tries to read first set-env! form in the build.boot file and use that for dependencies."
  [parsed-build-file]
  (some->> parsed-build-file
           (some (fn [form]
                   (if (= 'set-env! (first form))
                     form)))
           rest
           (apply hash-map)
           :dependencies
           last))

(defn read-build-boot [repo-owner repo-name]
  (try
    (let [url (str "https://raw.github.com/" repo-owner "/" repo-name "/master/build.boot")]
      (with-open [rdr (PushbackReader. (io/reader url))]
        (read-boot-deps (read-file rdr))))
    (catch Exception _
      nil)))


(defn dependency-key [dependency]
  (let [{:keys [group id version-string]} (anc/read-artifact dependency)]
    (format "%s/%s/%s" group id version-string)))

(defn outdated? [dependency]
  ;; Check redis for answer for version
  ;; If redis has it, return it
  ;; If redis doesn't have it
  ;;   calculate it
  ;;   store it for 3600
  ;;   return it

  (let [k (dependency-key dependency)
        outdated-info (wcar* (car/get k))]
    (if (some? outdated-info)
      ;; Match anc/artifact/outdated? API. We store false, because we can't store nil.
      (if (false? outdated-info) nil outdated-info)
      (let [outdated-info (anc/artifact-outdated? dependency {:snapshots? false :qualified? false})]
        (wcar* (car/setex k 3600 (if (nil? outdated-info) false outdated-info)))
        outdated-info))))

(defn check-deps [deps]
  (map #(conj % (outdated? %)) deps))

(defn calculate-stats [deps]
  (let [up-to-date-deps (remove nil? (map (fn [dep] (if (nil? (last dep)) dep nil)) deps))
        out-of-date-deps (remove nil? (map (fn [dep] (if (nil? (last dep)) nil dep)) deps))
        stats {:total       (count deps)
               :up-to-date  (count up-to-date-deps)
               :out-of-date (count out-of-date-deps)}]
    stats))

(defn check-profiles [profiles]
  (map (fn [profile-entry]
         (let [profile (val profile-entry)
               profile-name (key profile-entry)]
           (if (not (starting-num? profile-name))
             (if-let [dependencies (concat (:dependencies profile) (:plugins profile))]
               (if-let [deps (check-deps dependencies)]
                 [profile-name deps (calculate-stats deps)])))))
       profiles))

(defn boot-project-map [repo-owner repo-name]
  (let [github-url (str "https://github.com/" repo-owner "/" repo-name)]
    (if-let [dependencies (read-build-boot repo-owner repo-name)]
      (let [deps (check-deps dependencies)
            _ (println "boot-build deps" read-boot-deps)
            stats (calculate-stats deps)
            result {:boot?      true
                    :name       repo-name
                    :repo-name  repo-name
                    :repo-owner repo-owner
                    :github-url github-url
                    :deps       deps
                    :stats      stats
                    }]
        (log/info "boot project map" result)
        result))))

(defn lein-project-map [repo-owner repo-name]
  (let [github-url (str "https://github.com/" repo-owner "/" repo-name)]
    (if-let [project-clj-content (read-project-clj repo-owner repo-name)]
      (let [[_ project-name version & info] project-clj-content
            _ (println "project-clj" project-clj-content)
            info-map (apply hash-map info)
            deps (check-deps (:dependencies info-map))
            plugins (check-deps (:plugins info-map))
            profiles (check-profiles (:profiles info-map))
            stats (calculate-stats deps)
            plugins-stats (calculate-stats plugins)
            result (assoc info-map
                     :lein? true
                     :name project-name
                     :repo-name repo-name
                     :repo-owner repo-owner
                     :version version
                     :github-url github-url
                     :deps deps
                     :profiles profiles
                     :plugins plugins
                     :stats stats
                     :plugins-stats plugins-stats)]
        (log/info "project map" result profiles)
        result))))

(defn project-map [repo-owner repo-name]
  (let [lein-result (future (lein-project-map repo-owner repo-name))
        boot-result (future (boot-project-map repo-owner repo-name))]
    (if (nil? @lein-result)
      @boot-result
      @lein-result)))
