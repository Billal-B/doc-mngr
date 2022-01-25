(ns initbb.doc-mngr.back.schema
  (:require
    [clojure.java.io :as io]
    [com.walmartlabs.lacinia :as l]
    [com.walmartlabs.lacinia.util :as util]
    [com.walmartlabs.lacinia.schema :as s]
    [clojure.edn :as edn]))


(defn resolve-get-metadata
  [context args value]
  {:id "ok"})

(defn resolver-map
  []
  {:query/get-metadata resolve-get-metadata})

(defn load-schema []
  (-> (io/resource "schema.edn")
      slurp
      edn/read-string
      (util/attach-resolvers (resolver-map))
      s/compile))
