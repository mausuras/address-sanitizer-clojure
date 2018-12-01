(ns address-sanitizer-clojure.core-test
  (:require [clojure.test :refer :all]
            [address-sanitizer-clojure.core :refer :all]))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 1))))

;; TESTS

;; (get-full-address '("Geissbergstrasse 3 8302 kloten"))
;; (write-csv "/tmp/results.csv"
;;            (flatten-to-vector
;;              (load-data "/home/miguel/TEST_DATA/address_list.txt" 10 [:display_name]))
;;            [:display_name])
;; ;; (write-csv "/tmp/results.csv" (into [] (flatten '(({:display_name "street"})))) [:display_name])
;; (flatten-to-vector '(({:display_name "street"})))
;; (write-csv "/tmp/results.csv" [{:a "testt"}] [:a])
;; (into [] (flatten '(({:display_name "street"}))))
