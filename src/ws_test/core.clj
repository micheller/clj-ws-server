(ns ws-test.core
  (:gen-class)
  (:use org.httpkit.server
        ring.middleware.reload
        ;; [clojure.edn :only [pr-str]]
        [clojure.data.json :only [json-str read-json]])
)

;; (defn app [req]
;;   {:status  200
;;    :headers {"Content-Type" "text/html"}
;;    :body    "hello HOOE!"})

(defn answer []
  ;; (pr-str ["NYET" {:nyet "NYET" :once-more {:NYET "NYET"}}])
  (json-str {:sorry "We're out of dicks" :dish-of-the-day "Vaginas"})
  )

(defn handler [request]
  (with-channel request channel
    (send! channel "EHLO")
    ;; (println request)
    (on-close channel (fn [status] (println "channel closed: " status)))
    (on-receive channel (fn [data] ;; echo it back
                          (print "---> ")
                          (println data)
                          (print "<--- ")
                          (println (answer) "\n")
                          (send! channel (answer))))
))

(defn -main [& args]
  (run-server (wrap-reload #'handler) {:port 8088}))
