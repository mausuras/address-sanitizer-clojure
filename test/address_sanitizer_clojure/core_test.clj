(ns address-sanitizer-clojure.core-test
  (:require [clojure.test :refer :all]
            [address-sanitizer.core :refer :all]))

;; TESTS

(deftest get-real-address
  (is (= "Geissbergstrasse, Kloten, Bezirk Bülach, Zürich, 8302, Schweiz/Suisse/Svizzera/Svizra" (:display_name ((flatten-to-vector (get-full-address '("Geissbergstrasse 3 8302 kloten"))) 0)))))

(deftest get-empty-address
  (is (= true  (empty? (flatten-to-vector (get-full-address '()))))))

(deftest get-fake-addressblank?
  (is (= true  (empty? (flatten-to-vector (get-full-address '("foobar fake street")))))))

(deftest test-fallback
  (is (="foodasdasdasd56" (result-or-fallback "foodasdasdasd56"))))

(defn count-lines
  [file-path]
  (with-open [rdr (clojure.java.io/reader file-path)]
    (count(filter #(not (clojure.string/blank? %))(line-seq rdr)))))

(deftest test-result-number
  (load-data "dummy_data/address_list.txt" 30 [:display_name :fallback])
  (is (= (+ 1 (count-lines  "dummy_data/address_list.txt") (count-lines "/tmp/results.csv")))))

;; Test number of collumn with valid collumns
;; Test number of collumn with invalid collumns

;; (result-or-fallback "Geissbergstrasse 3 8302 kloten")

;; (result-or-fallback "Geissbasdasdasd")
;; (run-tests)
;; (count-lines "dummy_data/address_list.txt")

(defn nil-to-empty
  [data]
  (replace [nil ""] data))

(defn test
  [row-data key-vector]
  (let [columns key-vector
        headers (map name columns)
        rows (mapv #(mapv % columns) row-data)]
    (cons headers rows)) )

(nil-to-empty (test '({:a 1 :b 2 :c 3} {:a 4 :c 5} {}) [:a :b :c]))
;; (test '({:a 1 :b 2 :c 3} {:a 4 :c 5} {}) [:a :b :c])
