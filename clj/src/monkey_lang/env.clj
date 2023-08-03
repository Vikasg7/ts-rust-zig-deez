(ns monkey-lang.env
  (:refer-clojure :exclude [get set!]))

(defmacro create 
  ([]
    `(atom {:store {} :outer nil}))
  ([with]
    `(atom {:store ~with :outer nil})))

(defmacro enclosed 
  ([env]
    `(atom {:store {} :outer ~env}))
  ([env with]
    `(atom {:store ~with :outer ~env})))

(defn get [env k]
  (loop [env env]
    (if-let [value (get-in @env [:store k] nil)]
      [value env]
    (when-let [outer (:outer @env)]
      (recur outer)))))

(defmacro set-var! [env k v]
  `(do
     (swap! ~env assoc-in [:store ~k] ~v)
     (-> ~v)))

(defn set-vars! [env kvs]
  (doseq [[k v] kvs]
     (set-var! env k v))
  (-> env))
