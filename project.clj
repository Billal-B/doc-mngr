(defproject doc-mngr "0.no_metadata.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.apache.tika/tika-core "1.28"]
                 [org.apache.tika/tika-parsers "1.28"]]
  :repl-options {:init-ns doc-mngr.core}
  :profiles {:dev {:resources ["dev-resources"]}}
  )
