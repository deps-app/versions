(ns jarkeeper.views.index
  (:require [clojure.string :as string]
            [jarkeeper.views.common :as common-views]
            [hiccup.core :refer [html]]
            [hiccup.page :refer [html5 include-css include-js]]
            [hiccup.util :refer [escape-html]]))


(defn index []
  (html5 {:lang "en"}
    [:head
     [:title "Deps Versions: identify out of date dependencies"]
     (common-views/common-head)
     (common-views/ga)]
    [:body
     (common-views/header)
     [:article.index-content
      [:h1 "Deps Versions identifies outdated dependencies in your Clojure projects"]
      [:form.find-form {:method "POST" :action "find"}
       [:div.row
        [:div.small-3.columns
         [:label.right.inline {:for "repo-url"} "Repo name"]]
        [:div.small-6.columns
         [:input#repo-url {:type "text" :name "repo-url" :placeholder "e.x. korma/Korma" :autocomplete "false"}]]
        [:div.small-3.columns
         [:button "Check!"]]]]
      #_ [:h2 "Example projects"]
      #_[:div.example-project
       [:a {:href "https://github.com/reagent-project/reagent"}
        "Reagent"]
       [:br]
       [:a {:href "/reagent-project/reagent" :title "Dependencies Status"}
        [:img {:src "https://versions.deps.co/reagent-project/reagent/status.svg"}]]]
      #_[:div.example-project


       ]]
     (common-views/common-footer)]))
