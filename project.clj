(defproject clj3D "0.0.4"
  :description "The first Clojure 3D Library"
  
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [org.clojars.charles-stain/jme "3.0-alpha4"]
                 [org.clojars.charles-stain/eventbus "1.4"]
                 [org.clojars.charles-stain/j-ogg-oggd "3.0"]
                 [org.clojars.charles-stain/j-ogg-vorbisd "3.0"]
                 [org.clojars.charles-stain/jheora-jst-debug "0.6.0"]
                 [org.clojars.charles-stain/lwjgl "3.0"]
                 [org.clojars.charles-stain/jme3-lwjgl-natives "3.0"]
                 [org.clojars.charles-stain/jinput "3.0"]
                 [org.clojars.charles-stain/stack-alloc "3.0"]
                 [org.clojars.charles-stain/xmlpull-xpp3 "1.1.4c"]
                 [incanter/incanter-core "1.2.3"]
                 [incanter/incanter-charts "1.2.3"]
                 [matchure "0.10.1"]]

  :dev-dependencies [[swank-clojure "1.2.0-SNAPSHOT"]]
  :source-path "src"
  :java-source-path "java"
  :jvm-opts ["-Xmx512m"])
