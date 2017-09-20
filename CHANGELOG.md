# Change Log

## [0.1.3]
- Add "context" argument to importers and exporters.  We typically
  import and export from/to specific places and this is what the
  context argument denotes.  Typical examples would be :config or
  :api.

## [0.1.2]
- Simplify body of `convert`.
- Add `import` and `export` that do spec validations one-way: on the
  input arg for `export` and the return value for `import`.  Intended
  for use in, e.g., exposing well-formed and validated data to a
  location that doesn't care: a web API or config file.

## [0.1.1] - 2017-09-14
### Changes
- Change behaviour of `convert` for when `from` and `to` are the same:
  return `:same` instead of `:default` so that we still get errors for
  missing implementations.

## 0.1.0
- Initial release.

[0.1.3]: https://github.com/chrisjd-uk/speconv/compare/0.1.2...0.1.3
[0.1.2]: https://github.com/chrisjd-uk/speconv/compare/0.1.1...0.1.2
[0.1.1]: https://github.com/chrisjd-uk/speconv/compare/0.1.0...0.1.1
