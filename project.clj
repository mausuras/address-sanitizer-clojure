(defproject address-sanitizer-clojure "0.1.0-SNAPSHOT"
  :description "Cli tool to sanitize and enrich addresses"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clj-http "3.9.1"]
                 [cheshire "5.8.1"]
                 [org.clojure/data.csv "0.1.4"]
                 [org.clojure/tools.cli "0.4.1"]]
  :profiles {:uberjar {:aot :all}}
  :main address-sanitizer.core)
