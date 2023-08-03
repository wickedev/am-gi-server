(ns gi.am.server.graphql.core
  (:require [gi.am.server.graphql.util :as util]
            [clojure.java.data :as jd]
            [clojure.walk :as walk])
  (:import [graphql ExecutionInput]
           [com.google.gson GsonBuilder]))

(def gson (.. (GsonBuilder.)
              setPrettyPrinting
              create))

(defn execute
  ([schema query]
   (execute schema query {} {} {}))
  ([schema query variables]
   (execute schema query variables {} {}))
  ([schema query variables context]
   (execute schema query variables context {}))
  ([{:keys [executable] :as _schema}
    query variables context {:keys [key-fn as-json] :as _options}]
   (let [variables (jd/to-java
                    java.util.Map
                    (walk/stringify-keys variables))
         input (.. (ExecutionInput/newExecutionInput)
                   (query query)
                   (variables variables)
                   build)
         result (.execute executable input)]
     (cond->> (jd/from-java-deep result {})
       key-fn (util/transform-keys key-fn)
       as-json (.toJson gson)))))
