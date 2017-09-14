# speconv

[![Clojars Project](https://img.shields.io/clojars/v/chrisjd/speconv.svg)](https://clojars.org/chrisjd/speconv)
[![Continuous Integration status](https://api.travis-ci.org/chrisjd-uk/speconv.png)](http://travis-ci.org/chrisjd-uk/speconv)

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
user> (convert int? string? 3.14)
ExceptionInfo Input does not conform to clojure.core$int_QMARK_@1f24957a  clojure.core/ex-info (core.clj:4725)
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
user> (convert ::dog ::animal "Spot")
ExceptionInfo Input does not conform to :user/dog  clojure.core/ex-info (core.clj:4725)
```

## License

Copyright © 2017 Chris J-D

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
