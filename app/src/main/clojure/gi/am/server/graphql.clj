(ns gi.am.server.graphql
  (:require [clojure.java.io :as io]))

(def greeting-schema (-> "schema/greeting.graphqls"
                         io/resource
                         slurp))

(comment
  (prn greeting-schema)
  )