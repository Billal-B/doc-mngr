(ns initbb.doc-mngr.back.schema
  (:require
    [clojure.java.io :as io]
    [initbb.doc-mngr.back.metadata :refer [extract-metadata]]
    [com.walmartlabs.lacinia :as l]
    [com.walmartlabs.lacinia.util :as util]
    [com.walmartlabs.lacinia.schema :as s]
    [clojure.edn :as edn]))


(defn resolve-get-metadata
  [_ args _]
  {:file_path (:file_path args)
   :metadata (extract-metadata (:file_path args))})

(defn resolver-map
  []
  {:query/get-metadata resolve-get-metadata})

(defn load-schema []
  (-> (io/resource "schema.edn")
      slurp
      edn/read-string
      (util/attach-resolvers (resolver-map))
      s/compile))

