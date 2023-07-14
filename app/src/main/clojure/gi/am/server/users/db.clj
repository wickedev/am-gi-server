(ns gi.am.server.users.db
  (:import [org.mariadb.r2dbc
            MariadbConnectionConfiguration
            MariadbConnectionFactory]
           [java.util.function Consumer Function BiFunction]))

(def config (-> (MariadbConnectionConfiguration/builder)
                (.host "localhost")
                (.port 3306)
                (.username "change_me")
                (.password "change_me")
                (.database "change_me")
                (.build)))

(defn ^Function function [f] (reify Function (apply [_ x] (f x))))
(defn ^BiFunction bi-function [f]  (reify BiFunction (apply [_ arg1 arg2] (f arg1 arg2))))

(def conn-factory (MariadbConnectionFactory. config))

(defmulti make-derefable type)

(defmethod make-derefable reactor.core.publisher.Mono
  [mono]
  (proxy [reactor.core.publisher.Mono
          clojure.lang.IDeref] []
    (deref []
      (.block mono))))

(defmethod make-derefable reactor.core.publisher.Flux
  [flux]
  (proxy [reactor.core.publisher.Flux
          clojure.lang.IDeref] []
    (deref []
      (.block (.collectList flux)))))

(defn execute! [conn-factory prepared-statement]
  (let [conn (.. conn-factory create)
        sql (first prepared-statement)]
    (-> conn
        (.flatMapMany
         (function
          (fn [conn]
            (-> (.createStatement conn sql)
                (.execute)
                (.flatMap
                 (function
                  (fn [result]
                    (-> result
                        (.map
                         (bi-function
                          (fn [row meta]
                            (->> (.getColumnMetadatas meta)
                                 (map (fn [col]
                                        (let [key (keyword (.getName col))
                                              value (.get row
                                                          (.getName col)
                                                          (.. col getType getJavaType))]
                                          [key value])))
                                 (into {}))))))))))))))))

(defn execute-one! [conn-factory prepared-statement]
  (-> (execute! conn-factory prepared-statement)
      (.take 1)
      (.singleOrEmpty)))

(comment
  @(make-derefable (execute-one! conn-factory ["SELECT * FROM users;"]))
  @(make-derefable (execute! conn-factory ["SELECT * FROM users;"])))
