(ns gi.am.server.core
  (:require [gi.am.server.api :refer [start-server]]
            [integrant.core :as ig]))

(defmethod ig/init-key :app/server [_ ctx]
  (start-server ctx))

(defmethod ig/halt-key! :app/server [_ server]
  (.dispose server))

(defmethod ig/init-key :graphql/schema [_ _opts]
  {})

(defmethod ig/halt-key! :graphql/schema [_ _schema]
  nil)
