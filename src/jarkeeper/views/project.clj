(ns jarkeeper.views.project
  (:require [clojure.string :as string]
            [jarkeeper.views.common :as common-views]
            [jarkeeper.utils :refer [html5]]
            [hiccup.core :refer [html]]
            [hiccup.page :refer [include-css include-js]]))

(defn- render-deps [deps]
  (for [dep deps]
    [:tr
     [:td (first dep)]
     [:td (second dep)]
     [:td (:version-string (last dep))]
     [:td.status-column
      (if (nil? (last dep))
        [:span.status.up-to-date {:title "Up to date"}]
        [:span.status.out-of-date {:title "Out of date"}])]]))

(defn- render-stats [stats]
  [:section.summary
   [:ul
    [:li.small-12.large-4.columns
     [:span.number (:total stats)]
     [:span.stats-label "dependencies"]]
    [:li.small-12.large-4.columns
     [:span.status.up-to-date]
     [:span.number (:up-to-date stats)]
     [:span.stats-label "up to date"]]
    [:li.small-12.large-4.columns
     [:span.status.out-of-date]
     [:span.number (:out-of-date stats)]
     [:span.stats-label "out of date"]]]])

(defn- render-table [header items]
  [:table.small-12.columns
   [:thead
    [:tr
     [:th header]
     [:th {:width "180"} "Current"]
     [:th {:width "180"} "Latest"]
     [:th {:width "90"} ""]]]
   (render-deps items)])

(defn link
  ([project]
   (str "https://versions.deps.co/"
        (:repo-owner project)
        "/"
        (:repo-name project)))
  ([project suffix]
   (str "https://versions.deps.co/"
        (:repo-owner project)
        "/"
        (:repo-name project)
        "/"
        suffix)))

(defn md-image [title image link]
  (format "[![%s](%s)](%s)" title image link))

(defn html-image [title image link]
  (hiccup.core/html
    [:a {:href link :title title}
     [:img {:src image}]]))

(defn head [project]
  [:head
   [:title (str "Deps Versions: " (:name project))]
   (common-views/common-head)
   (common-views/ga)])

(defn body [project]
  [:body
   (common-views/header)
   [:article.project-content
    [:header.row
     [:div.header-line
      [:h1
       [:a {:href (:github-url project)} (:name project)]
       [:span.version (:version project)]]
      [:div.badges
       [:img {:src (link project "downloads.svg") :alt "Downloads"}]
       [:div.gap]
       (if (> (:out-of-date (:stats project)) 0)
         [:img {:src "/images/out-of-date.svg" :alt "Outdated dependencies"}]
         [:img {:src "/images/up-to-date.svg" :alt "Up to date dependencies"}])]]
     [:h2 (:description project)]
     ]
    [:section.dependencies.row
     (render-stats (:stats project))
     (render-table "Dependency" (:deps project))
     (if (> (count (:plugins project)) 0)
       (list
         (render-stats (:plugins-stats project))
         (render-table "Plugin" (:plugins project))))
     (for [profile (:profiles project)]
       (if (first profile)
         (list
           (render-stats (nth profile 2))
           (render-table (name (first profile)) (second profile)))))]

    [:section.installation-instructions.row
     [:h2 "Markdown with SVG image"]
     [:code (md-image "Dependencies Status" (link project "status.svg") (link project))]
     [:h2 "HTML with SVG image"]
     [:code
      (html-image "Dependencies Status" (link project "status.svg") (link project))]]
    [:section.installation-instructions.row
     [:h2 "Markdown with PNG image"]
     [:code (md-image "Dependencies Status" (link project "status.png") (link project))]
     [:h2 "HTML with PNG image"]
     [:code
      (html-image "Dependencies Status" (link project "status.png") (link project))]]
    [:section.installation-instructions.row
     [:h2 "Clojars downloads badge - Markdown with SVG image"]
     [:code (md-image "Downloads" (link project "downloads.svg") (link project))]
     [:h2 "Clojars downloads badge - HTML with SVG image"]
     [:code
      (html-image "Downloads" (link project "downloads.svg") (link project))]]]
   (common-views/common-footer)])

(defn index [project]
  (html5 {:lang "en"}
    (head project)
    (body project)))
