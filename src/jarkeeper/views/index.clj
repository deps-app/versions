(ns jarkeeper.views.index
  (:require [clojure.string :as string]
            [jarkeeper.views.common :as common-views]
            [jarkeeper.utils :refer [html5]]
            [hiccup.core :refer [html]]
            [hiccup.page :refer [include-css include-js]]))

(defn example-project
  ([title slug]
   (example-project title slug false))
  ([title slug end?]
   [:div.example-project
    {:class (str "columns small-12 medium-6 large-4"
                 (when end? " end"))}
    [:a {:href (str "https://github.com/" slug)}
     title]
    [:br]
    [:a {:href (str "/" slug) :title "Dependencies Status"}
     [:img {:src (format "https://versions.deps.co/%s/status.svg" slug)}]]])
  )

(defn index []
  (html5 {:lang "en"}
    [:head
     [:title "Deps Versions: identify out of date dependencies"]
     (common-views/common-head)
     (common-views/ga)]
    [:body
     (common-views/header)
     [:article.index-content
      [:div.row
       [:h1 "Deps Versions identifies outdated dependencies in your Clojure projects"]]
      [:form.find-form {:method "POST" :action "find"}
       [:div.row
        [:div.small-12.large-3.columns
         [:label.inline {:for "repo-url"} "Repo name"]]
        [:div.small-12.large-6.columns
         [:input#repo-url {:type "text" :name "repo-url" :placeholder "e.x. korma/Korma" :autocomplete "false"}]]
        [:div.small-12.large-3.columns
         [:button "Check!"]]]
       [:div.row
        [:h3 "Supports Leiningen and Boot"]]]]
     [:div.row
      [:h2 "Example projects"]
      (example-project "Reagent" "reagent-project/reagent")
      (example-project "Buddy Sign" "funcool/buddy-sign")
      (example-project "boot-reload" "adzerk-oss/boot-reload")
      (example-project "Elastisch" "clojurewerkz/elastisch")
      (example-project "Honey SQL" "jkk/honeysql")
      (example-project "Eastwood" "jonase/eastwood" true)]
     (common-views/common-footer)]))
