(ns user
  (:require [jarkeeper.core :refer [config new-system]]
            [reloaded.repl :refer [system init start stop go reset reset-all]]))

(reloaded.repl/set-init! #(new-system :dev))
