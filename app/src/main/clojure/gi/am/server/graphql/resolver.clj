(ns gi.am.server.graphql.resolver
  (:require [gi.am.server.graphql.util :as util]))

(defmulti resolve (fn [qualified-field & _rest] qualified-field))

(defmacro defresolver
  {:arglists '([qualified-field args body]
               [qualified-field docstring args body]
               [options qualified-field docstring args body])}
  ([& fdecl]
   (let [{:keys [qualified-field option args body] :as info} (util/parse-fdecl fdecl)
         {:keys [batch]} option
         {:keys [object-type field]} (util/parse-qualified-field qualified-field)]
     `(defmethod resolve ~qualified-field
        [_qualified-field# & ~args]
        (let [opt# {:qualified-field ~qualified-field
                    :option ~option
                    :args ~args
                    :object-type ~object-type
                    :field ~field}]
          ~@body)))))

(comment
  (defresolver :Query/greeting
    [_ctx _args _parent]
    "hello")

  (resolve :Query/greeting {} {} {}))