(defproject clj "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]]
  :plugins [[lein-git-deps "0.0.2-SNAPSHOT"]]
  :git-dependencies {"https://github.com/vikasg7/jdsl.git" "master"}
  :global-vars {*warn-on-reflection* true}
  :repl-options {:init-ns monkey-lang.core}
  :main ^:skip-aot monkey-lang.core)
