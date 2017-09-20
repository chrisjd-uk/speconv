(ns speconv.core-test
  (:require [clojure.spec.alpha :as s]
            [clojure.set :refer [rename-keys]]
            [clojure.test :refer :all]
            [speconv.core :refer :all])
  (:refer-clojure :exclude [import]))

(s/def :animal/weight int?)
(s/def :animal/height int?)
(s/def ::animal (s/keys :req [:animal/weight
                              :animal/height]))

(s/def :dog/breed keyword?)
(s/def :dog/weight int?)
(s/def :dog/colour keyword?)
(s/def :dog/height int?)
(s/def ::dog (s/keys :req [:dog/breed
                           :dog/weight
                           :dog/colour
                           :dog/height]))

(s/def :fish/species keyword?)
(s/def :fish/weight int?)
(s/def :fish/height int?)
(s/def :fish/name string?)
(s/def ::fish (s/keys :req [:fish/species
                            :fish/weight
                            :fish/height
                            :fish/name]))

(conversion ::dog ::animal
            [dog]
            (-> dog
                (select-keys [:dog/weight :dog/height])
                (rename-keys {:dog/weight :animal/weight,
                              :dog/height :animal/height})))

(deftest basic-conversion
  (is (= {:animal/weight 10
          :animal/height 45}
         (convert ::dog ::animal
                  {:dog/breed :breed/alsation
                   :dog/weight 10
                   :dog/colour :colour/brown
                   :dog/height 45}))))

(deftest bad-input
  (testing "obviously wrong"
    (is (thrown? RuntimeException
                 (convert ::dog ::animal "hello"))))
  (testing "subtley wrong"
    (is (thrown? RuntimeException
                 (convert ::dog ::animal
                          {:dog/breed "alsation"
                           :dog/weight 5000
                           :dog/colour :colour/brown
                           :dog/height 45})))))

(conversion ::fish ::animal
            [fish]
            {:animal/height (:fish/height fish)})

(deftest underspecified-conversion
  (is (thrown? RuntimeException
               (convert ::fish ::animal
                        {:fish/species :fish.species/goldfish
                         :fish/weight 10
                         :fish/height 3
                         :fish/name "Freddy"}))))

(deftest missing-conversion
  (is (thrown? RuntimeException
               (convert ::fish ::dog
                        {:fish/species :fish.species/goldfish
                         :fish/weight 10
                         :fish/height 3
                         :fish/name "Freddy"}))))

(deftest automatic-support-for-same-types
  (let [freddy {:fish/species :fish.species/goldfish
                :fish/weight 10
                :fish/height 3
                :fish/name "Freddy"}]
    (is (= freddy (convert ::fish ::fish freddy)))))

(importer ::dog :config
          [data]
          {:dog/breed  (:breed data)
           :dog/weight (:weight data)
           :dog/colour (:colour data)
           :dog/height (:height data)})

(deftest import-normal-test
  (let [in {:breed :border-collie
            :weight 4000
            :colour :colour/black
            :height 30}
        res (import ::dog :config in)]
    (is (= {:dog/breed :border-collie
            :dog/weight 4000
            :dog/colour :colour/black
            :dog/height 30}
           res))))

(deftest import-malformed-test
  (let [in {:breed :border-collie
            :weight 4000
            :colour "black"
            :height 30}]
    (is (thrown? RuntimeException (import ::dog :config in)))))

(exporter ::fish :api
          [data]
          {:species (name (:fish/species data))
           :weight (:fish/weight data)
           :height (:fish/height data)
           :name (:fish/name data)})

(deftest export-normal-test
  (let [in {:fish/species :fish.species/goldfish
            :fish/weight 10
            :fish/height 3
            :fish/name "Freddy"}
        res (export ::fish :api in)]
    (is (= {:species "goldfish"
            :weight 10
            :height 3
            :name "Freddy"}))))

(deftest export-malformed-test
  (let [in {:fish/species :fish.species/goldfish
            :fish/height 3
            :fish/name "Freddy"}]
    (is (thrown? RuntimeException (export ::fish :api in)))))
