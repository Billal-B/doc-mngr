(ns initbb.doc-mngr.back.schema-test
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.test :refer :all]
            [initbb.doc-mngr.back.metadata :refer [extract-metadata]]
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
    (testing "resolve extract_metadata"
      ; FIXME: apparently with-redef have a strange behavior when tests are run in parallel
      (with-redefs [extract-metadata (fn [path]
                                       {:creation_time (str "for-doc:" path)})]
        (let [res (lacinia/execute schema
                                   "{extract_metadata(file_path:\"some-path\"){file_path metadata {creation_time} }}"
                                   nil nil)]
          (if (not (nil? (:errors res))) (throw (Exception. (apply str (:errors res)))))
          (is (= (get-in res [:data :extract_metadata :file_path]) "some-path"))
          (is (= (get-in res [:data :extract_metadata :metadata])
                 {:creation_time "for-doc:some-path"})))))))


(deftest load-schema-test
  (testing "nominal case"
    (s/load-schema)))
