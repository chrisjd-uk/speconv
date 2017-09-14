(ns speconv.core-test
  (:require [clojure.spec.alpha :as s]
            [clojure.set :refer [rename-keys]]
            [clojure.test :refer :all]
            [speconv.core :refer :all]))

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
