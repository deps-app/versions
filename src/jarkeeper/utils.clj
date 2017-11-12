(ns jarkeeper.utils
  (:require
    [hiccup.page :as page]
    [hiccup2.core :refer [html]]
    [compojure.response])
  (:import (hiccup.util RawString)))

(extend-protocol compojure.response/Renderable
  RawString
  (render [body request]
    (compojure.response/render
      (str body)
      request)))

(defmacro html5
  "Create a HTML5 document with the supplied contents."
  [options & contents]
  (if-not (map? options)
    `(html5 {} ~options ~@contents)
    `(let [options# (dissoc ~options :xml?)]
       (html {:mode :html}
             (page/doctype :html5)
             [:html options# ~@contents]))))
