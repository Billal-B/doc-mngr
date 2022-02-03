(ns ^:figwheel-hooks initbb.doc-mngr.front.core
  (:require
    [goog.dom :as gdom]
    [reagent.core :as reagent :refer [atom]]
    [reagent.dom :as rdom]))

;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom {:text "Hello !"}))

(defn get-app-element []
  (gdom/getElement "app"))

(defn scan-folder []
  (print "scan-folder")
  (let [ele (gdom/getElement "scan-folder")
        files (aget ele "files")]
    (doseq [file files]
      (print (aget file "webkitRelativePath")))))

(defn upload-folder []
  [:div {:class "scan-folder"}
   [:h2 "Scan folder :"]
   [:input {:type "file" :directory "" :webkitdirectory "" :id "scan-folder"}]
   [:button {:type "button" :on-click scan-folder} "Launch scan."]])

(defn app []
  [:div {:class "app"}
   [upload-folder]])

(defn mount [el]
  (rdom/render [app] el))

(defn mount-app-element []
  (when-let [el (get-app-element)]
    (mount el)))

;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app
(mount-app-element)

;; specify reload hook with ^:after-load metadata
(defn ^:after-load on-reload []
  (mount-app-element)
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )
