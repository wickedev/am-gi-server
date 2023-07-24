(ns gi.am.server.dictionary.resolver
  (:require [gi.am.server.graphql.resolver :refer [defresolver]]))

(defresolver :Query/greeting
  [_ctx _args _parent]
  "hello")