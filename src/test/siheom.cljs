(ns siheom
    (:require [clojure.string :as str]
              [promesa.core :as p]
              [test-util :as test-util]))

(defn render [react-element]
  {:run #(p/do (test-util/render-cp react-element))
   :log "컴포넌트 렌더"})

(defn query [role name]
  {:role role
   :name name})

(defn query-first [role name]
  {:first true
   :role  role
   :name  name})

(defn query-last [role name]
  {:last  true
   :role  role
   :name  name})

(defn get-element 
  ([locator]
   (get-element locator false))
  ([locator optional?]
  (cond
    (:first locator)
    (first (test-util/query-all (:role locator) (:name locator)))
    (:last locator)
    (last (test-util/query-all (:role locator) (:name locator)))
    :else
    (test-util/query (:role locator) (:name locator) optional?))))

(defn get-elements [locator]
  (test-util/query-all (:role locator) (:name locator)))

(defn value?
  [locator expected]
  {:run (fn [] (p/do  (test-util/value? (get-element locator) expected)
                      nil))
   :log (str "값이 " expected " 인지?: " (:role locator) " " (:name locator))})


(defn count?
  [locator expected]
  {:run (fn [] (p/do  (test-util/count? #(get-elements locator) expected)
                      nil))
   :log (str expected "개 인지?: " (:role locator) " " (:name locator))})

(defn visible?
  ([locator] (visible? locator true))
  ([locator expected]
   {:run (fn [] (p/do  (test-util/visible? #(get-element locator (not expected)) expected)
                       nil))
    :log (str (if expected "보이는지?: " "안 보이는지?: " ) (:role locator) " " (:name locator))}))

(defn checked?
   "요소가 체크되었는지 검사하고, 아니면 에러를 던집니다. 두 번째 인자로 true를 넘겨도 동일합니다.
   
   ```html
   <input type=\"radio\" checked=\"true\" />
   ```
   
   ```clojure
   ;; checked가 true인지?
   (checked? (query \"radio\" \"실제 결제\"))
   (checked? (query \"radio\" \"실제 결제\") true)
   ```

   체크되어 있지 않은지를 검사하려면 두 번째 인자로 false를 넘깁니다.
   ```clojure
   (checked? (query \"radio\" \"실제 결제\") false)
   ```
  "
  ([locator]
  (checked? locator true))
  ([locator expected]
  {:run (fn [] (p/do  (test-util/checked? (get-element locator) expected)
                      nil))
   :log (str (if expected "체크?: " "체크되지 않음?: ") (:role locator) " " (:name locator))}))


(defn expanded? 
  "combobox나 accordion 등의 요소가 펼쳐져 있는지, 즉 aria-expanded가 true인지 검사하고, 아니면 에러를 던집니다. 두 번째 인자로 true를 넘겨도 동일합니다.
    
   ```clojure
   (expanded? (query \"combobox\" \"1개월 비용\"))
   (expanded? (query \"combobox\" \"1개월 비용\") true)
   ```

   닫혀있는지를 검사하려면 두 번째 인자로 false를 넘깁니다.
   ```clojure
   (expanded? (query \"combobox\" \"1개월 비용\") false)
   ```
  "
  ([locator]
   (expanded? locator true))
  ([locator expected]
  {:run (fn [] (p/do  (test-util/expanded? (get-element locator) expected)
                      nil))
   :log (str (if expected "펼쳐짐?: " "닫힘?: ") (:role locator) " " (:name locator))}))

(defn have-href?
  "주어진 link 요소가 해당 url을 가지고 있는지 확인합니다.
   ```clojure
   [link {:name :device-detail
          :path-params {:device-id \"oubcyxzlhapr-01\"}}]
   ;; ...
   (have-href (query \"link\" \"2402-C001\") \"/device-detail/oubcyxzlhapr-01\")
   ```
  "
  [locator expected]
  {:run (fn [] (p/do (test-util/have-href? (get-element locator) expected)
                      nil))
   :log (str "href=\"" expected "\" ?: " (:role locator) " " (:name locator))})

(defn click!
  "요소를 클릭합니다. hover 등 클릭을 하면서 일어나야 하는 이벤트도 같이 일어납니다.
   ```clojure
   (click! (query \"radio\" \"평균 비용\"))
   ```
  "
  [locator]
  {:run #(test-util/click! (get-element locator))
   :log (str "클릭한다!: " (:role locator) " " (:name locator))})

(defn clear!
  "input이나 textarea 등에 입력된 값을 지웁니다
   ```clojure
   (clear! (query \"input\" \"이메일\"))
   ```
  "
  [locator]
  {:run #(test-util/clear! (get-element locator))
   :log (str "지운다!: " (:role locator) " " (:name locator))})

(defn type!
  "input이나 textarea 등의 요소에 텍스트를 입력합니다.
   ```clojure
   (type! (query \"input\" \"이메일\") \"admin@sherpas.team\")
   ```
  "
  [locator text]
  {:run #(test-util/type! (get-element locator) text)
   :log (str "\"" text "\"를 입력한다!: " (:role locator) " " (:name locator))})


(defn keyboard!
  "주어진 키를 칩니다
   ```clojure
   (keyboard! \"{Enter}\")
   ```
  "
  [text]
  {:run #(test-util/keyboard! text)
   :log (str "\"" text "\"를 입력한다!")})

(defn run-siheom [& lines]
  (let [logs     (atom [])
        dispatch (fn [{:keys [log run]}]
                   (swap! logs conj log)
                   (p/do (run)))]
    (-> (reduce (fn [promise line]
                  (if (p/promise? promise)
                    (p/then promise #(dispatch line))
                    (dispatch line))) nil lines)
        (p/catch (fn [error]
                   (let [max-length (apply max (map (fn [log] (.-length log)) @logs))]
                     (set! (.-message error) (str "\n" (str/join "\n" (map #(.padEnd % (inc max-length) " ") @logs)) " <- !!여기서 실패!!\n\n" (.-message error))))
                   (throw error))))))
