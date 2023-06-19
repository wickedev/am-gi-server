(ns gi.am.server.users.route
  (:require [next.jdbc :as jdbc]))

(def ds (jdbc/get-datasource {:dbtype            "mariadb"
                               :dbname            "change_me"
                               :host              "localhost"
                               :user              "change_me"
                               :password          "change_me"
                               :port              3306
                               :useUnicode        true
                               :characterEncoding "utf-8"}))

(defn get-users [_req] (jdbc/execute! ds ["SELECT * FROM users;"]))

(comment
  @(def ds (jdbc/get-datasource {:dbtype            "mariadb"
                                 :dbname            "change_me"
                                 :host              "localhost"
                                 :user              "change_me"
                                 :password          "change_me"
                                 :port              3306
                                 :useUnicode        true
                                 :characterEncoding "utf-8"}))
  (jdbc/execute! ds ["SELECT * FROM users;"]))
