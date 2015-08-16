(ns ws-test.core
  (:gen-class)
  (:use org.httpkit.server
        ring.middleware.reload
        [clojure.data.json :only [json-str read-json]]))

;; (defn app [req]
;;   {:status  200
;;    :headers {"Content-Type" "text/html"}
;;    :body    "hello, darling!"})

(defn answer []
  (pr-str {:sorry "We're out of the dicks you ordered"
           :dish-of-the-day "chocolate caviar fountain with bacon crackers"}))

(defn handler [request]
  (with-channel request channel
    ;; (send! channel "EHLO")
    (println request)
    (on-close channel (fn [status] (println "channel closed: " status "\n")))
    (on-receive channel (fn [data]
                          (let [ans (answer)]
                            (print "---> ")
                            (println data)
                            (print "<--- ")
                            (println ans "\n")
                            (send! channel ans))))))

(defn -main [& args]
  (run-server (wrap-reload #'handler) {:port 8088}))
