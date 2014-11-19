# nrepl-53-monkeypatch

A Leiningen plugin to fix [NREPL-53](http://dev.clojure.org/jira/browse/NREPL-53),
which can break nrepl add-ons by ordering the middlewares incorrectly.

## Why would you not just release a fork of nrepl if anything I mean

Because Leiningen makes it difficult to use a forked (i.e., different
group-id) version of nrepl.

## Usage

Put `[com.gfredericks/nrepl-53-monkeypatch "0.1.0"]` into the `:plugins` vector of your
`:user` profile.

## License

Copyright © 2014 Gary Fredericks

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
