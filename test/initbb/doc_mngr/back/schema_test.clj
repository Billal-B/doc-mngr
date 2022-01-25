(ns initbb.doc-mngr.back.schema-test
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.test :refer :all]
            [com.walmartlabs.lacinia :as lacinia]
            [com.walmartlabs.lacinia.util :as util]
            [com.walmartlabs.lacinia.schema :as schema]
            [initbb.doc-mngr.back.schema :as s]))

(deftest resolver-map-test
  (let [schema (-> (io/resource "schema.edn")
                   slurp
                   edn/read-string
                   (util/attach-resolvers (s/resolver-map))
                   schema/compile)]
    (testing "resolve get-metadata"
      (with-redefs [s/resolve-get-metadata (fn [_ _ _] nil)] ; TODO: mock util/metadata fn instead
        (is (= (lacinia/execute (s/load-schema)
                                "{get_metadata(path:\"somepath\"){id}}"
                                nil nil)
               {:id "ok"}))))))


(deftest load-schema-test
  (testing "nominal case"
    (s/load-schema)))
