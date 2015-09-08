(defproject ws-test "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [http-kit "2.1.18"]
                 [ring/ring-devel "1.1.8"]
                 [ring/ring-core "1.1.8"]
                 [com.datomic/datomic-free "0.9.5052" :exclusions [joda-time org.slf4j/jul-to-slf4j org.slf4j/slf4j-nop]]
                 [org.slf4j/slf4j-jdk14 "1.6.4"]
                 [clj-logging-config "1.9.12"]
                 ;; [com.novemberain/validateur "2.4.2"]
                 ]
  :main ws-test.core)
