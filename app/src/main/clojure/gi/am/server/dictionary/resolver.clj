(ns gi.am.server.dictionary.resolver)

(defmacro defresolver
  {:arglists '([qualified-field args body]
               [qualified-field docstring|options args body]
               [qualified-field docstring options args body])}
  ([& fdecl]
   (let [{:keys [qualified-field option args body]} (parse-fdecl fdecl)
         {:keys [batch]} option
         [_ object-type field] (re-matches
                                #":(\w+)/(\w+)"
                                (str qualified-field))]
     )))

(defresolver
  :Query/greeting
  [_ctx _args _parent]
  "hello")