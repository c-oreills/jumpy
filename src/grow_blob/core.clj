(ns grow-blob.core
  (:import 
    (org.newdawn.slick AppGameContainer BasicGame Sound Font Graphics GameContainer))
  (:gen-class))

(def simple-test
  (proxy [BasicGame] ["SimpleTest"]
    (init [container])
    (update [container delta])
    (render [container graphics]
      (.drawString graphics "Hellow Slick world!" 0 100)
      )))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (.start (new AppGameContainer simple-test))
  )
