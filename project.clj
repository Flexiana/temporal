(defproject temporal "0.2.0-SNAPSHOT"
  :description "Temporal Logic inspired testing library for microservices."
  :url "https://github.com/flexiana/temporal"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [ring/ring-jetty-adapter "1.9.2"]
                 [ring/ring-core "1.9.2"]
                 [com.taoensso/carmine "3.1.0"]
                 [clj-http "3.12.3"]]
  :repl-options {:init-ns temporal.core}
  :main ^:skip-aot temporal.core/start-server
  )
