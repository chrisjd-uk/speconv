# speconv

[![License](https://img.shields.io/github/license/chrisjd-uk/speconv.svg)](LICENSE)
[![Clojars Project](https://img.shields.io/clojars/v/chrisjd/speconv.svg)](https://clojars.org/chrisjd/speconv)
[![Build Status](https://travis-ci.org/chrisjd-uk/speconv.svg?branch=master)](https://travis-ci.org/chrisjd-uk/speconv)

A very simple Clojure library that provides a `convert` multimethod
for doing data conversions and convenient macro for defining
the supported conversions, `conversion`.

Using the `conversion` macro, input data is validated against an input
spec and output data produced by the body is validated against an
output spec.


## Installation

Add the following to your `project.clj`:

```
[chrisjd/speconv "0.1.3"]
```


## Documentation

- [API Docs](https://chrisjd-uk.github.io/speconv/)


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
ExceptionInfo Input does not validate as a clojure.core$int_QMARK_@1f24957a  clojure.core/ex-info (core.clj:4725)
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
ExceptionInfo Input does not validate as a :user/dog  clojure.core/ex-info (core.clj:4725)
```


## Import and Export

Sometimes it's useful to only perform spec validation in one
direction.

For example, when loading data from a configuration file, we want to
ensure that the data we end up with is well-formed in our data model,
but we don't want to explicitly perform validation on the
configuration file itself.  This is what the `importer` macro and
`import` function is for.

Or when exposing data from our data model over a web API, we want to
hide the namespace-qualified keywords mapping our data and just expose
a "simple" subset view.  This is what the `exporter` macro and
`export` function is for.

Examples:

``` clojure
user> (exporter ::dog :api
                [data]
                {:breed (name (:dog/breed data))
                 :weight (str (:dog/weight data) "kg")})
#multifn[export 0x28412494]
user> (export ::dog :api
              {:dog/breed :breed/alsation
                :dog/weight 10
                :dog/colour :colour/brown
                :dog/height 45})
{:breed "alsation", :weight "10kg"}
```

Read this as "exporting a `::dog` **to** `:api`".

``` clojure
user> (importer ::animal :config
                [data]
                (zipmap [:animal/weight :animal/height] data))
#multifn[import 0x5d9c048a]
user> (import ::animal :config [10 35])
#:animal{:weight 10, :height 35}
```

Read this as "importing an `::animal` **from** `:config`.


## License

Copyright © 2017 Chris J-D

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
