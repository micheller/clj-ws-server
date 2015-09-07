(ns ws-test.core
  (:gen-class)
  (:require [datomic.api :as d]
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
   {:db/id #db/id[:db.part/db]
    :db/ident :order/id
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   ])


(defn add-order
  "add EDN object received via ws://"
  [order]
  @(d/transact
    conn
    [(assoc (read-string order) :db/id (d/tempid :db.part/user))]))

(defn answer []
  (pr-str {:restaurant "Le Telepathe"
           :sorry "We're out of the di..shes you ordered"
           :dish-of-the-day "chocolate caviar fountain with bacon crackers"}))

(defn handler [request]
  (serv/with-channel request channel
    (println "REQ:" request)
    (println "CHAN:" channel)
    (serv/on-close channel (fn [status] (println "channel closed: " status "\n")))
    (serv/on-receive channel (fn [data]
                          (let [ans (answer)
                                all-db-data (d/q '[:find [(pull ?e [*]) ...]
                                               :where [?e :order/id _]]
                                             (d/db conn))
                                ]
                            (add-order data)
                            (print "---> ")
                            (println data)
                            (print "<--- ")
                            (println all-db-data)
                            (println "type of db data:" (type all-db-data) "\n")
                            (serv/send!
                             channel
                             (str all-db-data)))))))

(defn -main [& args]
  (serv/run-server (wrap-reload #'handler) {:port 1488}))
