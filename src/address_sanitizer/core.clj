(ns address-sanitizer.core
  (:require [address-sanitizer.interface :as interface]
            [clj-http.client :as client]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clj-http.util :as util])
  (:gen-class))

;; Atoms
(def current-chunk (atom 0))

;; Utils functions
(defn flatten-to-vector
  "Flattens a nested list into a vector"
  [nested-list]
  (into []
        (flatten nested-list)))

(defn string-to-keywords
  "Returns a vector with keywords based on the input string"
  [string]
  (into []
        (map keyword (str/split string #"\s+"))))

(defn nil-to-empty
  "Replaces the nil values in a nested collection with empty string"
  [data]
  (map (fn [m]
         (into []
               (map (fn [v]
                      (if (nil? v) "" v)) m)))
       data))

(defn url-encode-without-plus
  "Encodes the URL and replaces '+' with space encoded"
  [url]
  (str/replace (util/url-encode url) "+" "%20"))

;; Address Processing
(defn osm-search
  "Returns OSM search URL with the address"
  [address]
  (str "https://nominatim.openstreetmap.org/search/"
       (url-encode-without-plus address) "?format=json&addressdetails=1&limit=1&polygon_svg=1"))

(defn fetch-osm-results
  "Fetches address results from OSM API for the given address"
  [address]
  (first (:body (client/get (osm-search address) {:as :json}))))

(defn result-or-fallback
  "Returns the enriched address with the 'address' nested object merged into the root
  If no address is found than it will fallback to the original input"
  [line]
  (if-not (empty? line)
    (let [address (fetch-osm-results line)]
      (if address
        (dissoc (merge address (:address address)) :address)
        {:fallback line}))))

(defn partial-address
  "Returns a subset of the enriched address map only with the keys chosen"
  [addresses keys]
  (for [address addresses]
    (select-keys (result-or-fallback address) keys)))

;; File handling
(defn append-headers
  "Return the headers with the rows if it is the first time."
  [headers rows]
  (if (< @current-chunk 1)
    (nil-to-empty (cons headers rows))
    rows))

(defn write-csv
  "Writes a chunk to a CSV file, will append the headers if it is the first chunk"
  [path row-data key-vector]
  (let [columns key-vector
        headers (map name columns)
        rows (mapv #(mapv % columns) row-data)
        output (append-headers headers rows)]
    (with-open [file (io/writer path :append true)]
      (csv/write-csv file output)
      (swap! current-chunk inc))))

(defn process-chunk
  [chunk output-format out-file]
  (write-csv out-file
             (flatten-to-vector (partial-address chunk output-format))
             output-format))

(defn transform-data
  "Reads a file with unstructured addresses and creates a csv file with structured addresses following an output format.
   The output format are the desired keys from the supported fields of Nomatim OSM API https://wiki.openstreetmap.org/wiki/Nominatim"
  [file chunk-size output-format out-file]
  (io/delete-file out-file true)
  (with-open [rdr (clojure.java.io/reader file)]
    (doseq [chunk (partition-all chunk-size (line-seq rdr))]
      (process-chunk chunk output-format out-file)
      (print "At element " (* @current-chunk chunk-size)))))

(defn -main [& args]
  (let [{:keys [file options exit-message ok?]} (interface/validate-args args)]
    (if exit-message
      (interface/exit (if ok? 0 1) exit-message)
      (transform-data file (:chunk options) (string-to-keywords (:format options)) (:output options)))))
