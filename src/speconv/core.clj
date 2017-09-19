(ns speconv.core
  (:require [clojure.spec.alpha :as s])
  (:refer-clojure :exclude [import]))

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
           (throw (ex-info (str "Result is not a valid " ~to)
                           (s/explain-data ~to result#)))))
       (throw (ex-info (str "Input is not a valid " ~from)
                       (s/explain-data ~from ~entity-binding))))))

(defmulti export
  "Take a specified input map and produced some output.  The output is
  not validated against any spec, but the input is."
  (fn [to entity]
    to))

(defmacro exporter
  [from [entity-sym] & body]
  `(defmethod export ~from
     [_# ~entity-sym]
     (if (s/valid? ~from ~entity-sym)
       (do ~@body)
       (throw (ex-info (str "Input is not a valid " ~from)
                       (s/explain-data ~from ~entity-sym))))))

(defmulti import
  "Take some input and produce a specified output.  The input is not
  validated against any spec, but the output is."
  (fn [from entity]
    from))

(defmacro importer
  [to [entity-sym] & body]
  `(defmethod import ~to
     [_# ~entity-sym]
     (let [result# (do ~@body)]
       (if (s/valid? ~to result#)
         result#
         (throw (ex-info (str "Result is not a valid " ~to)
                         (s/explain-data ~to result#)))))))
