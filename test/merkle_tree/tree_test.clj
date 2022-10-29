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

(deftest test-merkle-proof-single
  (testing "single"
    (is (= [] (compute-merkle-proof ["a"] 0)))))

(deftest test-merkle-proof-two-transactions
  (testing "two transactions"
    (is (= [{:left "ca978112ca1bbdcafac231b39a23dc4da786eff8147c4e72b9807785afee48bb"}]
           (compute-merkle-proof ["a" "b"] 1)))))

(deftest test-merkle-proof-three-transactions
  (testing "three transactions"
    (is (= [{:left "ca978112ca1bbdcafac231b39a23dc4da786eff8147c4e72b9807785afee48bb"}
            {:right  "2e7d2c03a9507ae265ecf5b5356885a53393a2029d241394997265a1a25aefc6"}]
           (compute-merkle-proof ["a" "b" "c"] 1)))))

(deftest test-merkle-proof-four
  (testing "four transactions"
    (is (= [{:right "18ac3e7343f016890c510e93f935261169d9e3f565436429830faf0934f4f8e4"}
            {:left "e5a01fee14e0ed5c48714f22180f25ad8365b53f9779f79dc4a3d7e93963f94a"}]
           (compute-merkle-proof ["a" "b" "c" "d"] 2)))))

(deftest test-merkle-proof-five
  (testing "five transactions"
    (is (= [{:right "18ac3e7343f016890c510e93f935261169d9e3f565436429830faf0934f4f8e4"}
            {:left "e5a01fee14e0ed5c48714f22180f25ad8365b53f9779f79dc4a3d7e93963f94a"}
            {:right "3f79bb7b435b05321651daefd374cdc681dc06faa65e374e38337b88ca046dea"}]
           (compute-merkle-proof ["a" "b" "c" "d" "e"] 2)))))

(deftest test-merkle-proof-six
  (testing "six transactions"
    (is (= [{:left "2e7d2c03a9507ae265ecf5b5356885a53393a2029d241394997265a1a25aefc6"}
            {:left "e5a01fee14e0ed5c48714f22180f25ad8365b53f9779f79dc4a3d7e93963f94a"}
            {:right "04fa33f8b4bd3db545fa04cdd51b462509f611797c7bfe5c944ee2bb3b2ed908"}]
           (compute-merkle-proof ["a" "b" "c" "d" "e" "f"] 3)))))