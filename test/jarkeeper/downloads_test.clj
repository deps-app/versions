(ns jarkeeper.downloads-test
  (:require [clojure.test :refer :all])
  (:require [jarkeeper.downloads :refer [format-downloads]]))

(deftest format-downloads-test
  (are [given expected] (= expected (format-downloads given))
    0 "0"
    1 "1"
    10 "10"
    100 "100"
    999 "999"
    1000 "1000"
    10000 "10K"
    100000 "100K"
    100999 "100K"
    999999 "999K"
    1000000 "1000K"
    9999999 "9999K"))
