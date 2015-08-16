(ns ws-test.core
  (:gen-class)
  (:use org.httpkit.server)
  (:use ring.middleware.reload)
)

;; (defn app [req]
;;   {:status  200
;;    :headers {"Content-Type" "text/html"}
;;    :body    "hello HOOE!"})


(defn handler [request]
  (with-channel request channel
    (send! channel "EHLO")
    ;; (println request)
    (on-close channel (fn [status] (println "channel closed: " status)))
    (on-receive channel (fn [data] ;; echo it back
                          (println "---------------------GOT SOME")
                          (println data)
                          (send! channel "ХУЁВ ТЕБЕ ПАЧКУ, ПЁС!")))
))

(defn -main [& args]
  (run-server (wrap-reload #'handler) {:port 8088}))
