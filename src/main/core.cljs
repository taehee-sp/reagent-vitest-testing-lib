(ns core (:require
          [reagent.core :as r]
          [reagent.dom.client :as rdomc]))

(defn app []
  (let [counter-atom (r/atom 0)]
    (fn []
      [:button
       {:on-click #(swap! counter-atom inc)}
       "click: " @counter-atom])))

(def app-node (.getElementById js/document "app"))
(def root  (rdomc/create-root app-node))

(defn ^:dev/after-load start []
  (rdomc/render root [app]))

(start)
