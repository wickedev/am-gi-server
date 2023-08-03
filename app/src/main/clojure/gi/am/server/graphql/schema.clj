(ns gi.am.server.graphql.core
  (:require [gi.am.server.graphql.util :as util]
            [clojure.java.data :as jd]
            [clojure.walk :as walk])
  (:import [graphql ExecutionInput]
           [com.google.gson GsonBuilder]))

(defn merge-sdls [sdls])

(defn make-executable-schema [sdl resolvers]

  nil)

(comment
  (apply str (map #(str % "\n") ["1" "2"])))