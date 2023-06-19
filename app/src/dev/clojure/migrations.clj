(ns migrations
  (:require [migratus.core :as migratus]))

(def config {:store               :database
             :migration-dir       "migrations/"
             :db {:connection-uri "jdbc:mariadb://127.0.0.1:3306/change_me?user=change_me&password=change_me"}})

(comment
  (migratus/init config)
  (migratus/create config "change-here-to-your-file-name")
  (migratus/pending-list config)
  (migratus/migrate config)
  (migratus/rollback config))


