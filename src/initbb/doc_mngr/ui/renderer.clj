(ns initbb.doc-mngr.ui.renderer
  (:require [cljfx.api :as fx]
            [cljfx.css :as css]
            [clojure.tools.logging :as log]
            [initbb.doc-mngr.metadata :refer [extract-metadata]]
            [initbb.doc-mngr.ui.style :refer [style]])
  (:import (javafx.event ActionEvent)
           (javafx.scene Node)
           (javafx.stage DirectoryChooser)
           (java.io File)))

(def initial-state
  {:to-scan []})

(def *state
  (atom initial-state))

(defmulti handle ::event)

(defmethod handle ::open-dir [{:keys [^ActionEvent fx/event]}]
  (log/warn event)
  (let [window (.getWindow (.getScene ^Node (.getTarget event)))
        chooser (doto (DirectoryChooser.)
                  (.setTitle "Open Directory"))]
    (when-let [dir (.showDialog chooser window)]
      (swap! *state update :to-scan conj (.getCanonicalPath dir)))))

;; TODO: make it async
(defmethod handle ::scan-dirs [{:keys [^ActionEvent fx/event]}]
  (let [metadata (map (fn [^String dir] (extract-metadata (File. dir)))
                      (-> *state deref :to-scan))]
    (log/warn metadata)))

(defn to-scan-view [{:keys [to-scan]}]
  {:fx/type     :v-box
   :style-class ["to-scan"]
   :children    (map (fn [dir]
                       {:fx/type  :h-box
                        :spacing  5
                        :padding  5
                        :children [{:fx/type :label
                                    :text    dir}]})
                     to-scan)})



(defn root-view [state]
  (log/warn "state : " state)
  {:fx/type :stage
   :showing true
   :title   "doc-mngr"
   :scene   {:fx/type     :scene
             :stylesheets [(::css/url style)]
             :root        {:fx/type     :v-box
                           :pref-width  800
                           :pref-height 600
                           :children    [{:fx/type   :button
                                          :text      "Select directories..."
                                          :on-action {::event ::open-dir}}
                                         {:fx/type   :button
                                          :text      "Scan directories"
                                          :on-action {::event ::scan-dirs}}
                                         (to-scan-view state)]}}})


(def renderer
  (fx/create-renderer
    :middleware (fx/wrap-map-desc (fn [state] (root-view state)))
    :opts {:fx.opt/map-event-handler handle}))

(fx/mount-renderer *state renderer)
