(ns jarkeeper.web-test
  (:require [clojure.test :refer [deftest testing is]]
            [jarkeeper.web :refer [img-status-resp last-modified]]
            [ring.util.response :as resp]))

(deftest image-rendering-test
  (testing "png image"
    (is (= (img-status-resp "public/images/out-of-date.png")
           (-> (resp/resource-response "public/images/out-of-date.png")
               (resp/header "cache-control" "no-cache")
               (resp/header "last-modified" (last-modified))
               (resp/header "content-type" "image/png")))))
  (testing "svg image"
    (is (= (img-status-resp "public/images/out-of-date.svg")
           (-> (resp/resource-response "public/images/out-of-date.svg")
               (resp/header "cache-control" "no-cache")
               (resp/header "last-modified" (last-modified))
               (resp/header "content-type" "image/svg+xml"))))))
