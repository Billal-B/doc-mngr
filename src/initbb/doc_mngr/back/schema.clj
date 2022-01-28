(ns initbb.doc-mngr.back.schema
  (:require
    [clojure.java.io :as io]
    [initbb.doc-mngr.back.metadata :refer [extract-metadata]]
    [com.walmartlabs.lacinia :as l]
    [com.walmartlabs.lacinia.util :as util]
    [com.walmartlabs.lacinia.schema :as s]
    [clojure.edn :as edn])
  (:import (java.io File)))



(defn resolve-document-metadata
  [_ _ document]
  (extract-metadata (:file_path document)))

(defn resolve-get-metadata
  [_ args _]
  {:file_path (:file_path args)})

(defn resolver-map
  []
  {:query/get-metadata resolve-get-metadata
   :Document/metadata resolve-document-metadata})

(defn load-schema []
  (-> (io/resource "schema.edn")
      slurp
      edn/read-string
      (util/attach-resolvers (resolver-map))
      s/compile))

