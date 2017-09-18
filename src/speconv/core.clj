(ns speconv.core
  (:require [clojure.spec.alpha :as s]))

(defmulti convert
  "Convert from data conforming to one spec into data conforming to
  another."
  (fn [from to entity]
    (if (= from to)
      :same
      [from to])))

(defmethod convert :same
  [spec _ entity]
  (if (s/valid? spec entity)
    entity
    (throw (ex-info (str "Input does not conform to " spec)
                    (s/explain-data spec entity)))))

(defmacro conversion
  "Declare a new conversion for use with the convert multimethod.  The
  input data must conform to the `from` spec and the returned value
  will conform to the `to` spec."
  [from to [entity-binding] & body]
  `(defmethod convert [~from ~to]
     [_# _# ~entity-binding]
     (if (s/valid? ~from ~entity-binding)
       (let [result# (do ~@body)]
         (if (s/valid? ~to result#)
           result#
           (throw (ex-info (str "Result does not conform to " ~to)
                           (s/explain-data ~to result#)))))
       (throw (ex-info (str "Input does not conform to " ~from)
                       (s/explain-data ~from ~entity-binding))))))
