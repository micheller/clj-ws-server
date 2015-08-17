(ns ws-test.core
  (:gen-class)
  (:require [datomic.api :as d]
            ;; [datomic.samples.repl :as repl]
            [org.httpkit.server :as serv]
            [ring.middleware.reload :refer [wrap-reload]]
            [clojure.pprint :refer [pprint]]
            ;; [clojure.data.json :refer [json-str read-json]]
            ))

(def db-uri-base "datomic:mem://")

(defn scratch-conn
  "Create a connection to an anonymous, in-memory database."
  []
  (let [uri (str db-uri-base (d/squuid))]
    (d/delete-database uri)
    (d/create-database uri)
    (d/connect uri)))

(def conn (scratch-conn))

@(d/transact
  conn
  [{:db/id #db/id[:db.part/db]
    :db/ident :user/name
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/id #db/id[:db.part/db]
    :db/ident :user/lastname
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   ;; {:db/id #db/id[:db.part/db]
   ;;  :db/ident :order/id
   ;;  :db/valueType :db.type/long
   ;;  :db/cardinality :db.cardinality/one
   ;;  :db.install/_attribute :db.part/db}
   ])


(defn add-order
  "add EDN object received via ws://"
  [order]
  @(d/transact
    conn
    [order]))

;; (defn app [req]
;;   {:status  200
;;    :headers {"Content-Type" "text/html"}
;;    :body    "hello, darling!"})

(defn answer []
  (pr-str {:sorry "We're out of the dicks you ordered"
           :dish-of-the-day "chocolate caviar fountain with bacon crackers"}))

(defn handler [request]
  (serv/with-channel request channel
    ;; (send! channel "EHLO")
    (println "REQ:" request)
    (println "CHAN:" channel)
    (serv/on-close channel (fn [status] (println "channel closed: " status "\n")))
    (serv/on-receive channel (fn [data]
                          (let [ans (answer)]
                            (add-order {:db/id (d/tempid :db.part/user)
                                        :user/name "Mike"
                                        :user/lastname "Koltsov"})
                            (serv/send! channel ans)
                            (print "---> ")
                            (println data)
                            (print "<--- ")
                            (println ans)
                            (println (d/q '[:find ?e
                                            :where [?e :user/name]]
                                          (d/db conn)) "\n"))))))

(defn -main [& args]
  (serv/run-server (wrap-reload #'handler) {:port 8088}))
