(ns jarkeeper.views.common
  (:use [hiccup.core :only (html)]
        [hiccup.page :only (html5 include-css include-js)])
  (:require [clojure.string :as string]))

(defn apple-touch-icon [size]
  [:link {:rel "apple-touch-icon-precomposed" :sizes size :href (format "/images/logos/apple-touch-icon-%s.png" size)}])

(defn favicon [size]
  [:link {:rel "icon" :type "image/png" :sizes size :href (format "/images/logos/favicon-%s.png" size)}])

(defn common-head []
  (list
    [:meta {:charset "utf-8"}]
    [:meta {:name "description" :content "Identifies out of date dependencies for Clojure projects hosted on GitHub"}]
    [:meta {:name "keywords" :content "clojure, dependencies, version, up to date version, out of date version"}]
    [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge,chrome=1"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0, user-scalable=no"}]
    [:meta {:name "author" :content "Deps"}]
    (include-css "https://fonts.googleapis.com/css?family=Karla:400,700" "/app.css")
    [:link {:rel "shortcut icon" :href "/favicon.ico"}]

    (apple-touch-icon "57x57")
    (apple-touch-icon "114x114")
    (apple-touch-icon "72x72")
    (apple-touch-icon "144x144")
    (apple-touch-icon "60x60")
    (apple-touch-icon "120x120")
    (apple-touch-icon "76x76")
    (apple-touch-icon "152x152")

    (favicon "196x196")
    (favicon "128x128")
    (favicon "96x96")
    (favicon "32x32")
    (favicon "16x16")
    ))

(defn common-footer []
  [:footer.footer.row
   [:div.small-12.columns
    [:p "Brought to you by "
     [:a {:href "https://www.deps.co/"} "Deps - Private Hosted Maven Repositories"] "."]
    [:p
     [:a {:href "http://github.com/deps-app/versions"} "Versions"]
     " is an open source project hosted on GitHub."]]])

(defn ga []
  [:script "
   (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
   (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
   m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
   })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

    ga('create', 'UA-89420342-2', 'auto');
    ga('send', 'pageview');
   "])

(defn header []
  [:header.header.row
   [:div.logo-collection
    [:a {:href "/"}
     [:div.logo]
     [:h1 "Deps Versions"]]]])
