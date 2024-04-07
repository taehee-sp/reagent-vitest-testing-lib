(ns reagent-test
   (:require [promesa.core :as p]
             [reagent.core :as r]
             [siheom :refer [render click! visible? query run-siheom]]
             ["@testing-library/react" :as tlr :refer [screen]]
             ["@testing-library/user-event" :default user-event]
             [vitest-support :refer [describe it]]
             [test-util :as util]))

(defn counter []
  (let [counter-atom (r/atom 0)]
    (fn []
      [:button
       {:on-click #(swap! counter-atom inc)}
       "click: " @counter-atom])))

(describe
   "counter"
   
   (it
     "click counter then 1 up : plain interop"
     (p/do
       (tlr/render (r/as-element [counter]))
       (.click user-event (.getByRole screen "button" #js{:name "click: 0"}))
       ;; workaround: https://www.metosin.fi/blog/reagent-towards-react-18#reagent-test-suite
       (util/wait 25)
       (.click user-event (.getByRole screen "button" #js{:name "click: 1"}))
       (util/wait 25)
       (-> (.findByRole screen "button" #js{:name "click: 2"})
           (js/expect)
           (.-resolves)
           (.toBeVisible))
       nil))

   (it
      "click counter then 1 up : util"
      (p/do 
        (util/render-cp [counter])
        ;; click and wait as promise
        (util/click! (util/query "button" "click: 0"))
        (util/click! (util/query "button" "click: 1"))
        (util/visible? #(util/query "button" "click: 2") true)
        nil))
   (it
      "click counter then 1 up : siheom"
      ;; test as data. with playwright like locator
      (run-siheom
        (render [counter])
        (click! (query "button" "click: 0"))
        (click! (query "button" "click: 1"))
        (visible? (query "button" "click: 2")))))
