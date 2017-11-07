(ns jarkeeper.views.project-test
  (:require [clojure.test :refer :all])
  (:require [jarkeeper.views.project :as project :refer [index]]))

(deftest index-test
  (is (= "[![Hi](/img.png)](/)" (project/md-image "Hi" "/img.png" "/"))))
