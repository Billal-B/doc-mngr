(ns initbb.doc-mngr.back.utils-test
  (:require [clojure.test :refer :all]
            [schema.core :as s]
            [initbb.doc-mngr.back.metadata :as m])
  (:import (java.io File)
           (java.time LocalDateTime)))

(deftest extract-metadata-test
  (testing "should handle invalid file path"
    (let [metadata (m/extract-metadata (File. "test/resources/dont_exist"))]
      (is (= metadata
             nil))))
  (let [metadata (m/extract-metadata (File. "test/resources/test_file"))]
    (testing "schema"
      (is (s/validate
            {(s/required-key :creation_time)     LocalDateTime
             (s/required-key :access_time)       LocalDateTime
             (s/required-key :modification_time) LocalDateTime
             (s/required-key :content_type)      s/Str}
            metadata)))
    (testing "should extract content type"
      (is (= (:content_type metadata)
             "text/plain; charset=ISO-8859-1")))
    (testing "should extract creation-time for a given file"
      (is (= (:creation_time metadata)
             (LocalDateTime/parse "2022-02-08T23:02:24.462150"))))
    (testing "should extract access-time for a given file"
      (is (= (type (:access_time metadata)) LocalDateTime)))
    (testing "should extract modification-time for a given file"
      (is (= (:modification_time metadata)
             (LocalDateTime/parse "2022-02-08T23:02:24.462150"))))))
