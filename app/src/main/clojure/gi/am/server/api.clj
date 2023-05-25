(ns gi.am.server.api
  (:import [java.util.function Consumer Function]
           [org.springframework.http.server.reactive ReactorHttpHandlerAdapter]
           [org.springframework.web.reactive.function.server
            HandlerFunction
            HandlerStrategies
            RequestPredicates
            RouterFunctions
            ServerResponse]
           [reactor.core.publisher Mono]
           [reactor.netty.http.server HttpServer]))

(defn function [f] (reify Function (apply [_ x] (f x))))
(defn consumer [f] (reify Consumer (accept [_ x] (f x))))
(defn handler [f] (reify HandlerFunction (handle [_ request] (f request))))

(defmacro routes [& body]
  `(.. RouterFunctions route
       ~@body
       (build)))

(defn http-server [routes]
  (let [port (System/getenv "PORT")]
    (-> (HttpServer/create)
        (.host "0.0.0.0")
        (.port (if port (Integer/parseInt port) 8080))
        (.handle (ReactorHttpHandlerAdapter.
                  (RouterFunctions/toHttpHandler
                   routes
                   (-> (HandlerStrategies/builder)
                       (.build))))))))

(defn r [{:keys [schema] :as _ctx}]
  (prn :schema schema)
  (routes
   (RequestPredicates/GET "/"
     (handler (fn [_req] (-> (ServerResponse/ok)
                             (.body (Mono/just "Hello GET!!") String)))))
   (RequestPredicates/POST "/graphql"
     (handler (fn [_req]
                (-> (ServerResponse/ok)
                    (.body (Mono/just "Hello POST!!") String)))))))
(defn on-started [server]
  (let [host (.host server)
        port (.port server)]
    (printf "\n👉 App server available at http://%s:%d" host port)
    (flush)))

(defn start-server
  [ctx]
  (let [server (-> (http-server (r ctx)) (.bind))]
    (-> server (.subscribe (consumer on-started)))))

(comment
  (def server (start-server {}))
  (.. server dispose))