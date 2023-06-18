(ns gi.am.server.api
  (:import [java.util.function Consumer Function]
           [org.springframework.http.server.reactive ReactorHttpHandlerAdapter]
           [org.springframework.web.reactive.function.server
            HandlerFunction
            HandlerStrategies
            RequestPredicates
            RouterFunctions
            ServerResponse]
           [io.netty.channel.group DefaultChannelGroup]
           [io.netty.util.concurrent DefaultEventExecutor]
           [reactor.netty DisposableServer]
           [reactor.core.publisher Mono]
           [reactor.netty.http.server HttpServer]))

(defn function [f] (reify Function (apply [_ x] (f x))))
(defn consumer [f] (reify Consumer (accept [_ x] (f x))))
(defn runnable [f] (reify java.lang.Runnable (run [_] (f))))
(defn handler [f] (reify HandlerFunction (handle [_ request] (f request))))
(defn disposable [f] (reify reactor.core.Disposable (dispose [_] (f))))

(defmacro routes [& body]
  `(.. RouterFunctions route
       ~@body
       (build)))

(defn http-server [routes]
  (let [port (System/getenv "PORT")]
    (-> (HttpServer/create)
        (.host "0.0.0.0")
        (.port (if port (Integer/parseInt port) 8081))
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
                             (.body (Mono/just "Hello GET!?") String)))))
   (RequestPredicates/POST "/graphql"
     (handler (fn [_req]
                (-> (ServerResponse/ok)
                    (.body (Mono/just "Hello POST!!") String)))))))
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
        _ (prn :server server (.getConnectors server))

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