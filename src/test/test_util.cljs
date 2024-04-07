(ns test-util
  (:require ["@testing-library/react" :refer [render screen waitFor]]
            ["@testing-library/user-event" :default user-event]
            [reagent.core :as r]))

(defn wait [duration]
  (new js/Promise (fn [resolve] (js/setTimeout resolve duration))))

(defn render-cp [element]
  (let [el (r/as-element element)]
    (render el)
    (wait 25)))

(defn query
  ([role name]
   (query role name false))  
  ([role name optional?]
   (if optional?
     (.queryByRole screen role #js{:name name})
     (.getByRole screen role #js{:name name}))))

(defn query-all [role name]
  (.queryAllByRole screen role #js{:name name}))

(defn click!
  [element]
  (-> (.click user-event element)
      (.then (fn [] (r/flush)))
      (.then (fn [] (wait 25)))))

(defn hover! 
  "요소를 호버합니다."
  [element]
  (-> (.hover user-event element)
      (.then #(r/flush))))

(defn clear! 
  "input이나 textarea 등의 요소를 지웁니다."
  [element]
  (-> (.clear user-event element)
      (.then #(r/flush))))

(defn keyboard!
  "주어진 키를 누릅니다."
  [key]
  (let [promise (.keyboard user-event key)]
    (.then promise (fn [] (r/flush)))))

(defn type!
  "input이나 textarea 등의 요소에 텍스트를 입력합니다."
  [element text]
  (-> (.type user-event element text)
      (.then #(r/flush))))


(defn value?
  [element expected]
  (waitFor #(if (nil? (.-value element))
              (-> (js/expect element)
                  (.toHaveAttribute "value" expected))
              (-> (js/expect element)
                  (.toHaveValue expected)))))

(defn count?
  [get-elements expected]
  (waitFor #(-> (js/expect (get-elements))
                (.toHaveLength expected))))

(def fallback-element (js/document.createElement "div"))

(defn visible?
  [get-element expected]
  (waitFor (if expected
             #(get-element)
             (fn []
               (-> (js/expect (or (get-element) fallback-element))
                (.-not)
                (.toBeVisible))))))

(defn checked?
  [element expected]
  (waitFor #(if expected
              (-> (js/expect element)
                  (.toBeChecked))
              (-> (js/expect element)
                  (.-not)
                  (.toBeChecked)))))

(defn expanded?
  [element expected]
  (waitFor #(if expected
              (-> (js/expect element)
                  (.toHaveAttribute "aria-expanded" "true"))
              (-> (js/expect element)
                  (.-not)
                  (.toHaveAttribute  "aria-expanded" "true")))))

(defn have-href?
  [element expected]
  (waitFor #(-> (js/expect element)
                (.toHaveAttribute "href" expected))))

(defn assert-equal [a b]
  (.toBe (js/expect a) b))
