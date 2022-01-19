(ns initbb.doc-mngr.utils-test
  (:require [clojure.test :refer :all]
            [initbb.doc-mngr.utils :as u])
  (:import (java.io File)
           (java.time LocalDateTime Instant)
           (java.nio.file.attribute FileTime)))

(deftest list-dir-test
  (testing "should list all files under a given path"
    (is (= (map #(.getName %) (u/list-dir "dev-resources/doc_mngr"))
           ["1",
            "2"]))))


(deftest file-metadata-test
  (testing "should handle invalid file path"
    (let [metadata (u/extract-content-type (File. "dev-resources/doc_mngr/dont_exist"))]
      (is (= metadata
             nil))))
  (testing "should handle file with no metadata"
    (let [metadata (u/extract-content-type (File. "dev-resources/doc_mngr/no_metadata"))]
      (is (= metadata
             nil)))))



(deftest file-time-to-local-date-time-test
  (testing "should convert"
    (let [input (-> "2022-01-18T21:16:01.457349Z"
                    (Instant/parse)
                    (FileTime/from))]
      (is (= (u/file-time-to-local-date-time input)
             (LocalDateTime/parse "2022-01-18T21:16:01.457349"))))))

(deftest extract-base-metadata-test
  (testing "should handle invalid file path"
    (let [metadata (u/extract-base-metadata (File. "dev-resources/doc_mngr/dont_exist"))]
      (is (= metadata
             nil))))
  (testing "should extract creation-time for a given file"
    (let [metadata (u/extract-base-metadata (File. "dev-resources/doc_mngr/test"))]
      (is (= (:creation-time metadata)
             (LocalDateTime/parse "2022-01-19T00:15:11.368237")))))
  (testing "should extract access-time for a given file"
    (let [metadata (u/extract-base-metadata (File. "dev-resources/doc_mngr/test"))]
      (is (= (:access-time metadata)
             (LocalDateTime/parse "2022-01-19T00:15:13.304259")))))
  (testing "should extract modification-time for a given file"
    (let [metadata (u/extract-base-metadata (File. "dev-resources/doc_mngr/test"))]
      (is (= (:modification-time metadata)
             (LocalDateTime/parse "2022-01-19T00:15:11.368237"))))))

(deftest extract-metadata-test
  (testing "should merge all metadata"
    (let [metadata (u/extract-metadata (File. "dev-resources/doc_mngr/test"))]
      (is (= metadata
             {:access-time (LocalDateTime/parse "2022-01-19T00:15:13.304259")
              :content-type      "text/plain; charset=ISO-8859-1"
              :creation-time (LocalDateTime/parse "2022-01-19T00:15:11.368237")
              :modification-time (LocalDateTime/parse "2022-01-19T00:15:11.368237")})))))
