(ns grow-blob.core
  (:import 
    (org.newdawn.slick AppGameContainer BasicGame Sound Font Graphics GameContainer))
  (:gen-class))

(def ^:dynamic *slick-thread* (atom nil))

(def ^:dynamic *text* (atom "=)"))
(def ^:dynamic *x* (atom 0))
(def ^:dynamic *xden* (atom 4))

(defn update [container delta]
  (swap! *x* + (/ delta @*xden*)))

(defn render [container graphics]
  (.drawString graphics @*text* @*x* 100))

(def key-fns
  {
   57 #(reset! *x* 0)
   203 #(swap! *xden* * 2)
   205 #(swap! *xden* / 2)
   200 #(reset! *text* "=D")
   208 #(reset! *text* "=)")
   })

(defn key-pressed [k c]
  ((get
     key-fns k
     #(println "No op defined for" k))))

(def simple-test
  (proxy [BasicGame] ["SimpleTest"]
    (init [container])
    (update [container delta]
      (update container delta))
    (render [container graphics]
      (render container graphics))
    (keyPressed [k c]
      (key-pressed k c))
    ))

(defn start-game []
  (reset!
    *slick-thread*
    (future
      (doto
        (new AppGameContainer simple-test)
        (.setTargetFrameRate 60)
        (.setAlwaysRender true)
        (.start)
        ))))

(defn -main
  [& args]
  (start-game))
