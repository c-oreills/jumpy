(ns grow-blob.core
  (:import 
    (org.newdawn.slick AppGameContainer BasicGame Color Sound Font Graphics GameContainer SpriteSheet)
    )
  (:gen-class))

(def ^:dynamic *slick-thread* (atom nil))

(def ^:dynamic *text* (atom "=)"))

(def ^:dynamic *world* (atom {:players {}}))

(def speed 4)

(defn new-player [id]
  {:id id :type :frog
   :x 0 :y 0 :dx 0 :dy 0
   :key-left false :key-right false
   :key-up false :key-down false})

(defn update [container delta]
  )

(defn render [container graphics]
  (let [ss (new SpriteSheet "res/animals.png" 32 32)]
    (.drawImage graphics (.getSubImage ss 0 0) 10 10))
  )

(defn set-player-key [player-id key value]
  (swap! *world* #(assoc-in % [:players player-id key] value)))

(defn set-player-key-vec [player-id key]
  (vec
    (for [b [true false]]
      (partial set-player-key player-id key b))))

(defn set-player-key-fns [player-id l r u d]
  [for [[d v] [
    l (set-player-key-vec :key-left)
     r (set-player-key-vec :key-right)
     u (set-player-key-vec :key-up)
     d (set-player-key-vec :key-down)

(def key-fns
  (conj
    (set-player-key-fns 1 205 203 200 208)
    (set-player-key-fns 2 31 30 17 19)
    (set-player-key-fns 3 77 75 72 76)))

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
