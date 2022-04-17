(ns initbb.doc-mngr.metadata-test
  (:require [clojure.test :refer :all]
            [initbb.doc-mngr.metadata :as m])
  (:import (java.io File)
           (java.nio.file Path)
           (java.time LocalDateTime)))

(deftest extract-metadata-test
  (testing "should handle invalid file path"
    (let [metadata (m/extract-metadata (File. "test/resources/metadata/dont_exist"))]
      (is (= metadata
             []))))
  (let [ms (m/extract-metadata (File. "test/resources/metadata/test_file"))
        m (first ms)]
    ;(testing "should extract path"
    ;  (is (= (.toString (.getFileName ^Path (:path m)))
    ;         "test_file")))
    (testing "should extract content type"
     (is (= (:content_type m)
            "text/plain; charset=ISO-8859-1")))
    (testing "should extract creation-time for a given file"
      (is (= (:creation_time m)
             (LocalDateTime/parse "2022-02-08T23:02:24"))))
    (testing "should extract access-time for a given file"
      (is (= (type (:access_time m)) LocalDateTime)))
    (testing "should extract modification-time for a given file"
      (is (= (:modification_time m)
             (LocalDateTime/parse "2022-02-08T23:02:24")))))
  (testing "should extract metadata for all files under dir"
    (let [metadata (m/extract-metadata (File. "test/resources/metadata/"))]
      (is (= (map (fn [m] (-> m :path .toString)) metadata)
             ["test/resources/metadata/folder/4"
              "test/resources/metadata/folder/3"
              "test/resources/metadata/2"
              "test/resources/metadata/test_file"])))))
