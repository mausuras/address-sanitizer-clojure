(ns address-sanitizer-clojure.core-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [address-sanitizer.core :refer :all]))

(deftest get-real-address
  (is (=
       "Geissbergstrasse, Kloten, Bezirk Bülach, Zürich, 8302, Schweiz/Suisse/Svizzera/Svizra"
       (:display_name (result-or-fallback "Geissbergstrasse 3 8302 kloten")))))

(deftest get-empty-address
  (is (=
       true
       (nil? (result-or-fallback "")))))

(deftest test-fallback
  (is (=
       "foodasdasdasd56"
       (:fallback (result-or-fallback "foodasdasdasd56")))))

(deftest test-url-encode
  (is (=
       "geissbergstrasse%20kloten"
       (url-encode-without-plus "geissbergstrasse kloten"))))

(defn count-lines
  [file-path]
  (with-open [rdr (io/reader file-path)]
    (count (filter #(not (str/blank? %)) (line-seq rdr)))))

(deftest test-result-number
  (transform-data "dummy_data/address_list.txt" 5 [:display_name :lat :lon :fallback] "/tmp/results.csv")
  (is (=
       (+ 1 (count-lines  "dummy_data/address_list.txt"))
       (count-lines "/tmp/results.csv"))))

(run-tests)
