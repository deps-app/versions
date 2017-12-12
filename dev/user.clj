(ns user
  (:require [reloaded.repl :refer [system init start stop go reset reset-all]]
            [jarkeeper.core :refer [config new-system]]))

(reloaded.repl/set-init! #(new-system :dev))
