(ns grow-blob.core
  (:import 
    (org.newdawn.slick AppGameContainer BasicGame Color Sound Font Graphics GameContainer)
    )
  (:gen-class))

(def ^:dynamic *slick-thread* (atom nil))

(def ^:dynamic *text* (atom "=)"))

(def ^:dynamic *x* (atom 0))
(def ^:dynamic *dx* (atom 0))
(def ^:dynamic *y* (atom 0))
(def ^:dynamic *dy* (atom 0))
(def ^:dynamic *speed* (atom 4))

(def ^:dynamic *x2* (atom 0))
(def ^:dynamic *dx2* (atom 0))
(def ^:dynamic *y2* (atom 0))
(def ^:dynamic *dy2* (atom 0))
(def ^:dynamic *speed2* (atom 4))

(def ^:dynamic *x3* (atom 0))
(def ^:dynamic *dx3* (atom 0))
(def ^:dynamic *y3* (atom 0))
(def ^:dynamic *dy3* (atom 0))
(def ^:dynamic *speed3* (atom 4))

(defn update [container delta]
  (swap! *x* + (* @*speed* @*dx*))
  (swap! *y* + (* @*speed* @*dy*))
  (swap! *x2* + (* @*speed2* @*dx2*))
  (swap! *y2* + (* @*speed2* @*dy2*))
  (swap! *x3* + (* @*speed3* @*dx3*))
  (swap! *y3* + (* @*speed3* @*dy3*))
  )

(defn render [container graphics]
  (.setColor graphics Color/blue)
  (.drawString graphics @*text* @*x* @*y*)
  (.setColor graphics Color/red)
  (.drawString graphics @*text* @*x2* @*y2*)
  (.setColor graphics Color/green)
  (.drawString graphics @*text* @*x3* @*y3*)
  ;_(.drawString graphics @*text* (+ @*x* 100) @*y*)
  ;_(.drawString graphics @*text* (+ @*x* 100) (+ @*y* 100))
  )

(def key-fns
  {
   57 [#(do
          (reset! *x* 0) (reset! *y* 0)
          (reset! *x2* 0) (reset! *y2* 0)
          (reset! *x3* 0) (reset! *y3* 0)) nil]
   205 [#(swap! *dx* inc) #(swap! *dx* dec)]
   203 [#(swap! *dx* dec) #(swap! *dx* inc)]
   200 [#(swap! *dy* dec) #(swap! *dy* inc)]
   208 [#(swap! *dy* inc) #(swap! *dy* dec)]
   31 [#(swap! *dx2* inc) #(swap! *dx2* dec)]
   30 [#(swap! *dx2* dec) #(swap! *dx2* inc)]
   17 [#(swap! *dy2* dec) #(swap! *dy2* inc)]
   19 [#(swap! *dy2* inc) #(swap! *dy2* dec)]
   77 [#(swap! *dx3* inc) #(swap! *dx3* dec)]
   75 [#(swap! *dx3* dec) #(swap! *dx3* inc)]
   72 [#(swap! *dy3* dec) #(swap! *dy3* inc)]
   76 [#(swap! *dy3* inc) #(swap! *dy3* dec)]
   157 [#(reset! *speed* 16) #(reset! *speed* 4)]
   29 [#(reset! *speed2* 16) #(reset! *speed2* 4)]
   82 [#(reset! *speed3* 24) #(reset! *speed3* 4)]
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
