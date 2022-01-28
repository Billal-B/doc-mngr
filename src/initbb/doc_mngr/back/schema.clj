(ns initbb.doc-mngr.back.schema
  (:require
    [clojure.java.io :as io]
    [initbb.doc-mngr.back.metadata :refer [extract-metadata]]
    [com.walmartlabs.lacinia :as l]
    [com.walmartlabs.lacinia.util :as util]
    [com.walmartlabs.lacinia.schema :as s]
    [clojure.edn :as edn])
  (:import (java.io File)))


(defn resolve-extract-metadata
  [_ args _]
  (map
    (fn [path] {:file_path path
                   :metadata  (extract-metadata (File. path))})
    (:file_paths args)))

(defn resolver-map
  []
  {:query/extract-metadata resolve-extract-metadata})

(defn load-schema []
  (-> (io/resource "schema.edn")
      slurp
      edn/read-string
      (util/attach-resolvers (resolver-map))
      s/compile))

