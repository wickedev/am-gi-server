(ns gi.am.server.graphql.util-test
  (:require [gi.am.server.graphql.util :as util]
            [clojure.test :refer :all]))

(deftest parse-fdecl-test
  (testing "parse-fdecl should parse basic fdecl"
    (let [result (util/parse-fdecl `(:Query/greeting
                                     [ctx args parent]
                                     "Greeting"))]
      (are [expect result] (= expect result)
        :Query/greeting (:qualified-field result)
        nil (:docstring result)
        `{} (:option result)
        `[ctx args parent] (:args result)
        `("Greeting") (:body result))))

  (testing "parse-fdecl should parse fdecl with docstring"
    (let [result (util/parse-fdecl `(:Query/greeting
                                     "greeting resolver"
                                     [ctx args parent]
                                     "Greeting"))]
      (are [expect result] (= expect result)
        :Query/greeting (:qualified-field result)
        "greeting resolver" (:docstring result)
        `{} (:option result)
        `[ctx args parent] (:args result)
        `("Greeting") (:body result))))

  (testing "parse-fdecl should parse fdecl with option"
    (let [result (util/parse-fdecl `({:batch {:args []}}
                                     :Query/greeting
                                     [ctx args parent]
                                     "Greeting"))]
      (are [expect result] (= expect result)
        :Query/greeting (:qualified-field result)
        nil (:docstring result)
        `{:batch {:args []}} (:option result)
        `[ctx args parent] (:args result)
        `("Greeting") (:body result))))

  (testing "parse-fdecl should parse fdecl with docstring & options"
    (let [result (util/parse-fdecl `({:batch {:args []}}
                                     :Query/greeting
                                     "greeting resolver"
                                     [ctx batch-args]
                                     "Greeting"))]
      (are [expect result] (= expect result)
        :Query/greeting      (:qualified-field result)
        "greeting resolver"  (:docstring result)
        `{:batch {:args []}} (:option result)
        `[ctx batch-args]    (:args result)
        `("Greeting")        (:body result)))))

(comment
  (run-tests))