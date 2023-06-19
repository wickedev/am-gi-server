(ns gi.am.server.core
  (:require [clojure.java.io :as io]
            [gi.am.server.api :refer [start-server]]
            [integrant.core :as ig]))

(defmethod ig/init-key :app/server [_ ctx]
  (prn :initializing)
  (start-server ctx))

(defmethod ig/halt-key! :app/server [_ server]
  (prn :halting)
  (-> server (.disposeNow)))

(defmethod ig/init-key :graphql/schema [_ _opts]
  {})

(defmethod ig/halt-key! :graphql/schema [_ _schema]
  nil)

(defn -main [& _args]
  (-> "config.edn"
      io/resource
      slurp
      ig/read-string
      ig/prep
      ig/init))
