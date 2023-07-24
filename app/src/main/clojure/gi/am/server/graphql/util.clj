(ns gi.am.server.graphql.util)

(defn parse-fdecl
  [fdecl]
  (let [[option fdecl]          (if (map? (first fdecl))
                                  [(first fdecl) (rest fdecl)]
                                  [{} fdecl])
        [qualified-field fdecl] (if (keyword? (first fdecl))
                                  [(first fdecl) (rest fdecl)]
                                  [nil fdecl])
        [docstring args]        (if (string? (first fdecl))
                                  [(first fdecl) (rest fdecl)]
                                  [nil fdecl])
        [args & body]          args]
    {:qualified-field qualified-field
     :docstring       docstring
     :option          option
     :args            args
     :body            body}))

(def ^:private qualified-field-regex #":(\w+)/(\w+)")

(defn parse-qualified-field
  [^clojure.lang.Keyword qualified-field]
  (let [[_ object-type field] (re-matches
                               qualified-field-regex
                               (str qualified-field))]
    {:object-type object-type
     :field       field}))
