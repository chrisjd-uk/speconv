# speconv

A very simple Clojure library that provides a `convert` multimethod
for doing data conversions and convenient macro for defining
the supported conversions, `conversion`.

Using the `conversion` macro, input data is validated against an input
spec and output data produced by the body is validated against an
output spec.

## Examples

Simple types:

``` clojure
user> (require '[speconv.core :refer :all])
nil
user> (conversion int? string? [n] (str n))
#multifn[convert 0x4d4452b]
user> (convert [int? string?] 123)
"123"
```

Using specs:

``` clojure
(require '[clojure.set :refer [rename-keys]])
(require '[clojure.spec.alpha :as s])
(require '[speconv.core :refer :all])

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
```

``` clojure
user> (convert ::dog ::animal
               {:dog/breed :breed/alsation
                :dog/weight 10
                :dog/colour :colour/brown
                :dog/height 45})
#:animal{:weight 10, :height 45}
```

## License

Copyright © 2017 Chris J-D

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
