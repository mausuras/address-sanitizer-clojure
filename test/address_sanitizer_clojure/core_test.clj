(ns address-sanitizer-clojure.core-test
  (:require [clojure.test :refer :all]
            [address-sanitizer.core :refer :all]))

(deftest get-real-address
  (is (=
       "Geissbergstrasse, Kloten, Bezirk Bülach, Zürich, 8302, Schweiz/Suisse/Svizzera/Svizra"
       (:display_name (first (flatten-to-vector (get-full-address '("Geissbergstrasse 3 8302 kloten"))))))))

(deftest get-empty-address
  (is (=
       true
       (empty? (flatten-to-vector (get-full-address '()))))))

(deftest get-fake-address
  (is (=
       true
       (empty? (:display_name(first (flatten-to-vector (get-full-address '("foobar fake street")))))))))

(deftest test-fallback
  (is (=
       "foodasdasdasd56"
       (:fallback (first(result-or-fallback "foodasdasdasd56"))))))

(defn count-lines
  [file-path]
  (with-open [rdr (clojure.java.io/reader file-path)]
    (count(filter #(not (clojure.string/blank? %))(line-seq rdr)))))

(deftest test-result-number
  (load-data "dummy_data/address_list.txt" 30 [:display_name :lat :lon :fallback] "/tmp/results.csv")
  (is (=
       (+ 1 (count-lines  "dummy_data/address_list.txt")
       (count-lines "/tmp/results.csv")))))

(run-tests)
