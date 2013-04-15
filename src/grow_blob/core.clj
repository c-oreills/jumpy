(ns grow-blob.core
  (:import
    (org.newdawn.slick AppGameContainer BasicGame Color Sound Font Graphics GameContainer SpriteSheet)
    )
  (:gen-class))

(def ^:dynamic *slick-thread* (atom nil))

(def ^:dynamic *text* (atom "=)"))

(defn new-player [id]
  {:id id :type :frog
   :x 0 :y 0 :dx 0 :dy 0 :grounded true
   :key-left false :key-right false
   :key-up false :key-down false})

(def ^:dynamic *world* (atom {:players {}}))

(def speed 4)

(def jump-init-dy -5)
(def jump-cont-dy -1)
(def gravity-dy 2)

(defn handle-player-keys [player]
  (conj
    player
    {:dx (+
          (if (player :key-left) -1 0)
          (if (player :key-right) 1 0))
     :dy (+
          (player :dy)
          gravity-dy
          (or
            (if (player :key-up)
              (if (player :grounded)
                jump-init-dy
                (if (neg? (player :dy))
                  jump-cont-dy)))
            0))}))

(defn update-player-pos [player]
  (let [player (handle-player-keys player)]
    (conj
      player
      {:x (+ (player :x) (player :dx))
       :y (min
            (+ (player :y) (player :dy))
            400)})))

(defn update [container delta]
  (swap! *world*
         (fn [w]
           (conj
             w
             {:players (into {} (for [[id p] (w :players)] [id (update-player-pos p)]))}
  ))))

(defn render [container graphics]
  (let [w @*world*
        ss (new SpriteSheet "res/animals.png" 32 32)]
    (doseq [[_ p] (w :players)]
      (.drawImage graphics (.getSubImage ss 0 0) (p :x) (p :y)))))

(defn set-player-key [player-id key value]
  (swap! *world* (fn [w]
                   (let [p
                         (assoc
                           (get-in w [:players player-id]
                                   (new-player player-id))
                           key value)]
                     (assoc-in w [:players player-id] p)))))

(defn set-player-key-fns-vec [player-id key]
  (vec
    (for [b [true false]]
      (partial set-player-key player-id key b))))

(defn set-player-key-fns [player-id l r u d]
  {
   l (set-player-key-fns-vec player-id :key-left)
   r (set-player-key-fns-vec player-id :key-right)
   u (set-player-key-fns-vec player-id :key-up)
   d (set-player-key-fns-vec player-id :key-down)})

(def key-fns
  (conj
    (set-player-key-fns 1 203 205 200 208)
    (set-player-key-fns 2 30 31 17 19)
    (set-player-key-fns 3 75 77 72 76)))

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
