(ns initbb.doc-mngr.front.renderer.core
  (:require [reagent.core :refer [atom] :as r]
            [reagent.dom :as rd]))

(enable-console-print!)

(defn select-folder-component
  []
  (let [state (atom ())]
    (js/window.api.receive
      "select-folder"
      (fn [folders]
        (doseq [f (map js->clj folders)]
          (swap! state conj {(hash f) f})
          (js/console.info @state))))
    (fn []
      [:div
       [:button
        {:on-click (fn []
                     (js/window.api.send "select-folder"))}
        (str "Select folder")]
       [:table {:class "table"}
        [:thead
         [:tr
          [:th "Path"]
          [:th "Num of files"]]]
        [:tbody
         (js/console.log @state)
         (for [folder @state]
           (js/console.log (str "folder : " folder)))]]])))


(defn ^:dev/after-load start! []
  (rd/render
    [select-folder-component]
    (js/document.getElementById "app-container")))
