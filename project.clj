(defproject ws-test "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [http-kit "2.1.18"]
                 [ring/ring-devel "1.1.8"]
                 [ring/ring-core "1.1.8"]]
  :main ^:skip-aot ws-test.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})