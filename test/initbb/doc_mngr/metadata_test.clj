(ns initbb.doc-mngr.metadata-test
  (:require [clojure.test :refer :all]
            [initbb.doc-mngr.metadata :as m])
  (:import (java.io File)
           (java.time LocalDateTime)))

(deftest extract-metadata-test
  (testing "should handle invalid file path"
    (let [metadata (m/extract-metadata (File. "test/resources/dont_exist"))]
      (is (= metadata
             nil))))
  (let [metadata (m/extract-metadata (File. "test/resources/test_file"))]
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