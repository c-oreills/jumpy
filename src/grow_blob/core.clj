(ns grow-blob.core
  (:import
    (org.newdawn.slick AppGameContainer BasicGame Color Sound Font Graphics GameContainer SpriteSheet)
    )
  (:gen-class))

(def ^:dynamic *slick-thread* (atom nil))

(def ^:dynamic *text* (atom "=)"))

(defn new-player [id]
  {:id id
   :type :frog :heading :left :frame :still :tick 0
   :x 0 :y 0 :dx 0 :dy 0
   :key-left false :key-right false
   :key-up false :key-down false})

(def ^:dynamic *world* (atom {:players {}}))

(def speed-x 2)
(def block-size 32)

(def jump-init-dy -15)
(def jump-cont-dy -2)
(def gravity-dy 3)

(def per-frame 150)

(def animal-map
  {:cat [0 0] :dove-w [3 0] :dove-f [6 0] :rat [9 0]
   :chick [0 4] :chicken [3 4] :frog [6 4] :rabbit [9 4]})

(def loop-types
  (let [alternating [:start :end]
        cycle [:start :end :still]]
    {:cat alternating :dove-w alternating :dove-f alternating :rat alternating
     :chick cycle :chicken alternating :frog cycle :rabbit cycle}))

(def frames [:start :still :end])

(defn grounded? [player]
  (= 400 (player :y)))

(defn update-player-velocity [player delta]
  (let [loop-type (loop-types (player :type))
        new-tick  (mod
                    (+ (player :tick) delta)
                    (* (count loop-type) per-frame))
        grounded (grounded? player)
        new-frame (loop-type (quot new-tick per-frame))
        ]
    (conj
      player
      {:dy (if grounded
             (if (player :key-up)
               jump-init-dy
               0)
             (+
              (player :dy)
              gravity-dy
              (if (and
                    (player :key-up)
                    (neg? (player :dy)))
                jump-cont-dy
                0)))}
      (if (player :key-down) [:type (rand-nth (keys animal-map))])
      (case [(player :key-left) (player :key-right)]
        [true true] {:dx 0}
        [true false] {:dx (- speed-x) :heading :left :frame new-frame :tick new-tick}
        [false true] {:dx speed-x :heading :right :frame new-frame :tick new-tick}
        [false false] {:dx 0 :frame :still :tick 0})
      (if (not grounded)
        {:frame (if (neg? (player :dy)) :start :end)})
      )))

(defn update-player-pos [player]
  (conj
    player
    {:x (+ (player :x) (player :dx))
     :y (min
          (+ (player :y) (player :dy))
          400)}))

(defn move-player [player delta]
  (-> player (update-player-velocity delta) update-player-pos))

(defn update [container delta]
  (swap! *world*
         (fn [w]
           (conj
             w
             {:players (into {} (for [[id p] (w :players)] [id (move-player p delta)]))}
  ))))

(defn init [container]
  (def ss (new SpriteSheet "res/animals.png" block-size block-size))
  )

(defn get-sprite [^SpriteSheet ss animal dir frame]
  (let [dirs-map
        (into {} (map-indexed (comp vec reverse vector)
                              [:down :left :right :up]))
        [x y] (apply map + [(animal-map animal)
                            [0 (dirs-map dir)]
                            [(.indexOf frames frame) 0]])]
    (.getSubImage ss x y)))

(defn render [container graphics]
  (let [w @*world*]
    (doseq [[_ p] (w :players)]
      (.drawImage graphics
                  (get-sprite ss
                              (p :type)
                              (p :heading)
                              (p :frame))
                  (p :x)
                  (p :y)))))

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
    (init [container]
      (init container))
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
