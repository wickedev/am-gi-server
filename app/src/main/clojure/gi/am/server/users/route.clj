(ns gi.am.server.users.route
  (:require [next.jdbc :as jdbc]
            [gi.am.server.users.db :as db]))

(def ds (jdbc/get-datasource {:dbtype            "mariadb"
                               :dbname            "change_me"
                               :host              "localhost"
                               :user              "change_me"
                               :password          "change_me"
                               :port              3306
                               :useUnicode        true
                               :characterEncoding "utf-8"}))

(defn get-users [_req] (db/execute! db/conn-factory ["SELECT * FROM users;"]))

(comment
  @(def ds (jdbc/get-datasource {:dbtype            "mariadb"
                                 :dbname            "change_me"
                                 :host              "localhost"
                                 :user              "change_me"
                                 :password          "change_me"
                                 :port              3306
                                 :useUnicode        true
                                 :characterEncoding "utf-8"}))
  (get-users nil))

