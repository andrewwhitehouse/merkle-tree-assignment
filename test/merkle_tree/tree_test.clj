(ns merkle-tree.tree-test
  (:require [clojure.test :refer :all]
            [merkle-tree.tree :refer :all]))

(deftest test-sha256
  (= "ca978112ca1bbdcafac231b39a23dc4da786eff8147c4e72b9807785afee48bb" (-> "a" ->bytes sha256 ->hex-string)))

(deftest test-concat-bytes
  (testing "byte array concatenation"
  (let [ba1 (byte-array (list 1 2 3 4))
        ba2 (byte-array (list 5 6))]
    (is (= (list 1 2 3 4 5 6) (seq (concat-bytes ba1 ba2)))))))


(deftest test-single-transction
  (testing "single transaction"
    (is (= "ca978112ca1bbdcafac231b39a23dc4da786eff8147c4e72b9807785afee48bb" (compute-merkle-root ["a"])))))

(deftest test-two-transactions
  (testing "two transaction"
    (is (= "e5a01fee14e0ed5c48714f22180f25ad8365b53f9779f79dc4a3d7e93963f94a" (compute-merkle-root ["a" "b"])))))

(deftest test-three-transactions
  (testing "three transactions"
    (is (= "7075152d03a5cd92104887b476862778ec0c87be5c2fa1c0a90f87c49fad6eff" (compute-merkle-root ["a" "b" "c"])))))

