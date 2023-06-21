(ns gi.am.server.api
  (:require [clojure.java.data :as j]
            [gi.am.server.users.route :refer [get-users]])
  (:import [io.netty.channel.group DefaultChannelGroup]
           [io.netty.util.concurrent DefaultEventExecutor]
           [java.util.function Consumer Function]
           [java.util.function BiFunction]
           [org.springframework.core ParameterizedTypeReference]
           [org.springframework.http.server.reactive ReactorHttpHandlerAdapter]
           [org.springframework.web.reactive.function BodyInserters]
           [org.springframework.web.reactive.function.server
            HandlerFunction
            HandlerStrategies
            RequestPredicates
            RouterFunctions
            ServerResponse]
           [org.springframework.web.server WebExceptionHandler]
           [reactor.core.publisher Mono]
           [reactor.netty.http.server HttpServer]))

(defn ^BiFunction bi-function [f]  (reify BiFunction (apply [_ arg1 arg2] (f arg1 arg2))))
(defn ^Function function [f] (reify Function (apply [_ x] (f x))))
(defn ^Consumer consumer [f] (reify Consumer (accept [_ x] (f x))))
(defn ^java.lang.Runnable runnable [f] (reify java.lang.Runnable (run [_] (f))))
(defn ^HandlerFunction handler [f] (reify HandlerFunction (handle [_ request] (f request))))
(defn ^reactor.core.Disposable disposable [f] (reify reactor.core.Disposable (dispose [_] (f))))
(defn ^WebExceptionHandler web-exception-handler [f] (reify WebExceptionHandler (handle [_ exchange ex] (f exchange ex))))

(defmacro routes [& body]
  `(.. RouterFunctions
       route
       ~@body
       (build)))

(defn http-server [routes]
  (let [port (System/getenv "PORT")]
    (-> (HttpServer/create)
        (.host "0.0.0.0")
        (.port (if port (Integer/parseInt port) 8081))
        (.handle (ReactorHttpHandlerAdapter.
                  (RouterFunctions/toHttpHandler
                   routes))))))

(defrecord User [name])

(defn r [{:keys [schema] :as _ctx}]
  (prn :schema schema)
  (routes
   (RequestPredicates/GET "/"
     (handler (fn [req]  (-> (ServerResponse/ok)
                             (.body
                              (BodyInserters/fromPublisher
                               (Mono/just (User. "Ryan"))
                               User))))))
   (RequestPredicates/POST "/graphql"
     (handler (fn [_req]
                (-> (ServerResponse/ok)
                    (.body (Mono/just "Hello POST!!") String)))))
   (onError Exception
            (bi-function (fn [e req]
                           (prn :e e)
                           (-> ((ServerResponse/badRequest)
                                (.body (BodyInserters/fromValue "error")))))))
   (before (function (fn [req] (prn :before) req)))
   (after (bi-function (fn [req res] (prn :after) res)))))

(defn started [server]
  (let [host (.host server)
        port (.port server)]
    (prn (format "\n👉 App server available at http://%s:%d" host port))))

(defn start-server
  [ctx]
  (let [event-executor (DefaultEventExecutor.)
        channel-group (DefaultChannelGroup. event-executor)
        server (-> (http-server (r ctx))
                   (.channelGroup channel-group))
        server (-> server (.bindNow))]
    (started server)
    server))

(comment
  @(def server (http-server (r {})))

  @(def event-executor (DefaultEventExecutor.))
  @(def channel-group (DefaultChannelGroup. event-executor))
  @(def server (.channelGroup server channel-group))

  @(def server (.bindNow server))

  @(def server (start-server {}))


  (.. server disposeNow)
  (.. server (disposeNow (java.time.Duration/ofSeconds 10)))
  (.. server channel close sync get))