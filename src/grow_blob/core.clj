(ns grow-blob.core
  (:import 
    (org.newdawn.slick AppGameContainer BasicGame Sound Font Graphics GameContainer))
  (:gen-class))

(def ^:dynamic *slick-thread* (atom nil))

(def ^:dynamic *text* (atom "=)"))
(def ^:dynamic *x* (atom 0))
(def ^:dynamic *dx* (atom 0))
(def ^:dynamic *y* (atom 0))
(def ^:dynamic *dy* (atom 0))
(def ^:dynamic *speed* (atom 4))

(defn update [container delta]
  (swap! *x* + (* @*speed* @*dx*))
  (swap! *y* + (* @*speed* @*dy*))
  )

(defn render [container graphics]
  (.drawString graphics @*text* @*x* @*y*)
  (.drawString graphics @*text* @*x* (+ @*y* 100))
  (.drawString graphics @*text* (+ @*x* 100) @*y*)
  )

(def key-fns
  {
   57 [#(do (reset! *x* 0) (reset! *y* 0)) nil]
   205 [#(swap! *dx* inc) #(swap! *dx* dec)]
   203 [#(swap! *dx* dec) #(swap! *dx* inc)]
   200 [#(swap! *dy* dec) #(swap! *dy* inc)]
   208 [#(swap! *dy* inc) #(swap! *dy* dec)]
   29 [#(reset! *speed* 8) #(reset! *speed* 4)]
   2 [#(reset! *text* "=(") nil]
   3 [#(reset! *text* "=)") nil]
   4 [#(reset! *text* "///\\oo/\\\\\\") nil]
   5 [#(reset! *text* "<(^.^)>") nil]
   6 [#(reset! *text* "<`)))><") nil]
   7 [#(reset! *text* "Roach is a twat") nil]
   })

(defn key-handler [k c t]
  ((or
     (nth
       (get
         key-fns k
         [#(println "No op defined for" k) nil])
       (if (= t :pressed) 0 1)
       nil
       )
     #()
     )))

(def simple-test
  (proxy [BasicGame] ["SimpleTest"]
    (init [container])
    (update [container delta]
      (update container delta))
    (render [container graphics]
      (render container graphics))
    (keyPressed [k c]
      (key-handler k c :pressed))
    (keyReleased [k c]
      (key-handler k c :released))
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
