(ns user
  (:require [clojure.java.io :as io]
            gi.am.server.core
            [integrant.core :as ig]
            [integrant.repl :as ig-repl]))

(ig-repl/set-prep! (constantly (-> "config.edn"
                                   io/resource
                                   slurp
                                   ig/read-string)))

(def go ig-repl/go)
(def halt ig-repl/halt)
(def reset ig-repl/reset)
(def reset-all ig-repl/reset-all)

(comment
  (go)
  (halt)
  (reset)
  (reset-all))
