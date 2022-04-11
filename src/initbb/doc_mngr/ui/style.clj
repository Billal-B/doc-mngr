(ns initbb.doc-mngr.ui.style
  (:require [cljfx.css :as css]))

(def style
  (css/register ::style
                (let [padding 10
                      text-color "#111111"]

                  ;; you can put style settings that you need to access from code at keyword keys in a
                  ;; style map and access them directly in an app

                  {::padding padding
                   ::text-color text-color

                   ;; string key ".root" defines `.root` selector with these rules: `-fx-padding: 10;`

                   ".root" {:-fx-padding padding}
                   ".label" {:-fx-text-fill text-color
                             :-fx-wrap-text true}
                   ".button" {:-fx-text-fill text-color
                              ;; vector values are space-separated
                              :-fx-padding ["4px" "8px"]
                              ;; nested string key defines new selector: `.button:hover`
                              ":hover" {:-fx-text-fill :black}}
                   ".to-scan" {:-fx-text-fill :grey
                               :-fx-border-width 1
                               :-fx-border-style :solid
                               :-fx-border-color :lightgray
                               :-fx-border-radius 4
                               :-fx-padding [0 2 0 2]}})))
