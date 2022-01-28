(ns user
  (:require
    [initbb.doc-mngr.back.schema :as s]
    [com.walmartlabs.lacinia :as lacinia]
    [com.walmartlabs.lacinia.pedestal :as lp]
    [io.pedestal.http :as http]
    [clojure.java.browse :refer [browse-url]]
    [clojure.walk :as walk]
    [clojure.tools.namespace.repl :refer [refresh]])
  (:import (clojure.lang IPersistentMap)))

(defn simplify
  "Converts all ordered maps nested within the map into standard hash maps, and
 sequences into vectors, which makes for easier constants in the tests, and eliminates ordering problems."
  [m]
  (walk/postwalk
    (fn [node]
      (cond
        (instance? IPersistentMap node) (into {} node)
        (seq? node)
        (vec node)

        :else node))
    m))

(defn q
  [query-string]
  (-> (lacinia/execute (s/load-schema) query-string nil nil)
      simplify))

(defn start-server
  []
  (let [server (-> (s/load-schema)
                   (lp/service-map {:graphiql true})
                   http/create-server
                   http/start)]
    (browse-url "http://localhost:8888/")
    server))

(def server (start-server))
(defn refresh-server []
  (http/stop server)
  (refresh))