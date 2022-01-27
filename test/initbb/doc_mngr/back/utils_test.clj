(ns initbb.doc-mngr.back.utils-test
  (:require [clojure.test :refer :all]
            [schema.core :as s]
            [initbb.doc-mngr.back.metadata :as m])
  (:import (java.io File)
           (java.time LocalDateTime)))

(deftest extract-metadata-test
  (testing "should handle invalid file path"
    (let [metadata (m/extract-metadata (File. "dev-resources/initbb/doc_mngr/back/dont_exist"))]
      (is (= metadata
             nil))))
  (let [metadata (m/extract-metadata (File. "dev-resources/initbb/doc_mngr/back/test"))]
    (testing "schema"
      (is (s/validate
            {(s/required-key :creation-time)     LocalDateTime
             (s/required-key :access-time)       LocalDateTime
             (s/required-key :modification-time) LocalDateTime
             (s/required-key :content-type)      s/Str}
            metadata)))
    (testing "should extract content type"
      (is (= (:content-type metadata)
             "text/plain; charset=ISO-8859-1")))
    (testing "should extract creation-time for a given file"
      (is (= (:creation-time metadata)
             (LocalDateTime/parse "2022-01-19T00:15:11.368237"))))
    (testing "should extract access-time for a given file"
      (is (= (type (:access-time metadata)) LocalDateTime)))
    (testing "should extract modification-time for a given file"
      (is (= (:modification-time metadata)
             (LocalDateTime/parse "2022-01-19T00:15:11.368237"))))))
