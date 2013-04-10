(defproject grow-blob "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :jvm-opts ["-Djava.library.path=./native"]
  ;:jvm-opts [~(str "-Djava.library.path=native/:" (System/getProperty "java.library.path"))]
  :dependencies [[org.clojure/clojure "1.5.0"]
                 [org.clojars.jyaan/slick "247.1"]
                 ;[org.clojars.jyaan/slick-lwjgl "247.1"]
                 ;[org.clojars.jyaan/slick-native "247.1"]
                 [org.lwjgl.lwjgl/lwjgl "2.8.5"]
                 [org.lwjgl.lwjgl/lwjgl_util "2.8.5"]
                 ]
  :main grow-blob.core)
