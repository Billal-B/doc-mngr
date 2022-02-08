(ns initbb.doc-mngr.front.renderer.core
  (:require [reagent.core :refer [atom] :as r]
            [reagent.dom :as rd]))

(enable-console-print!)

(defn select-folder-component []
  (let [state (atom [:table {:class "table"}
                     [:thead
                      [:tr
                       [:th "Path"]
                       [:th "Num of files"]]]])]
    (js/window.api.receive
      "select-folder"
      (fn [folders]
        (doseq [f folders]
          (swap! state inc)
          (js/console.info f))))
    (fn []
      [:div
       [:button
        {:on-click (fn []
                     (js/window.api.send "select-folder"))}
        (str "Select folder")]
       @state])))


(defn ^:dev/after-load start! []
  (rd/render
    [select-folder-component]
    (js/document.getElementById "app-container")))
