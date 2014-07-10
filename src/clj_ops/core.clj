(ns clj-ops.core
  (:require [compojure.core :refer [context GET]]
            [cheshire.core :as json]
            [hiccup.page :refer [html5]]))

(defn json-response
  [content]
  {:headers {"Content-Type" "application/json"}
   :status 200
   :body (json/generate-string content)})

(defn seq->hiccup-table
  "transforms a sequence of sequences into a hiccup-style html table"
  [tbl-vals]
  (reduce
   (fn [tbl vals]
     (conj tbl
           (reduce (fn [tr v] (conj tr [:td v])) [:tr] vals)))
   [:table]
   tbl-vals))

(defn ops-routes
  "Generate RentPath's standard ops routes.

   ARGS:
     - build-info :: a map containing the build information for this particular jar.
     - env :: a delay containing a map of the environment variables
     - config :: a function returning the app specific configuration (ie. Confusion)"
  [build-info env config]
  (context "/ops" []
    (GET "/heartbeat" [] "OK")
    (GET "/version" []
      (json-response build-info))
    (GET "/env" []
      (html5 (seq->hiccup-table (sort-by first @env))))
    (GET "/env.json" []
      (json-response @env))
    (GET "/config" []
      (html5 (seq->hiccup-table (config))))
    (GET "/config.json" []
      (json-response (config)))))