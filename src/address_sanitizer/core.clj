(ns address-sanitizer.core
  (:gen-class)
  (:require [clj-http.client :as client]
            [cheshire.core :refer :all]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [address-sanitizer.interface :as interface]))

(defn url-encode-without-plus
  [unencoded]
  (clojure.string/replace (clj-http.util/url-encode unencoded) "+" "%20"))

(defn build-url
  [address]
  (str "https://nominatim.openstreetmap.org/search/"
       (url-encode-without-plus address) "?format=json&addressdetails=1&limit=1&polygon_svg=1") )

(defn get-post
  "http client test"
  [address]
  (:body
    (client/get (build-url address) {:as :json})))

(defn result-or-fallback
  [line]
  (let [address (get-post line)]
    (if-not (empty? line)
      (if (empty? address)
        [{:fallback line}]
        address))))

(defn get-full-address
  [piece]
  (map result-or-fallback piece))

(defn get-partial-address
  [addresses keys]
  (for [list addresses]
    (for [map list]
      (select-keys map keys))))

(defn nil-to-empty
  [data]
  (map (fn [m]
         (into [] 
               (map (fn [v]
                      (if (nil? v) "" v)) m)))
       data))

(defn write-csv
  [path row-data key-vector ]
  (let [columns key-vector
        headers (map name columns)
        rows (mapv #(mapv % columns) row-data)
        output (nil-to-empty (cons headers rows))]
    (with-open [file (io/writer path :append true)]
      (csv/write-csv file output))))


(defn flatten-to-vector
  [nested-list]
  (into []
        (flatten nested-list)))


(defn process-chunk
  [chunk output-format out-file]
  (write-csv out-file
   (flatten-to-vector (get-partial-address (get-full-address chunk) output-format))
   output-format))

(defn load-data
  [file size output-format out-file]
  (with-open [rdr (clojure.java.io/reader file)]
    (doseq [chunk (partition-all size (line-seq rdr))]
      (process-chunk chunk output-format out-file))))

(defn string-to-keywords
  [string]
  (into []
        (map keyword (clojure.string/split string #"\s+"))))


(defn -main [& args]
  (let [{:keys [file options exit-message ok?]} (interface/validate-args args)]
    (if exit-message
      (interface/exit (if ok? 0 1) exit-message)
      (load-data file (:chunk options) (string-to-keywords(:format options)) (:output options)))))

