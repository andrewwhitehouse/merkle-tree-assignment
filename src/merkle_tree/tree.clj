(ns merkle-tree.tree
  (:import (java.security MessageDigest)))

(defn sha256 [bytes]
  (.digest (MessageDigest/getInstance "SHA-256") bytes))

(defn ->bytes [utf8-string]
  (.getBytes utf8-string "UTF-8"))

(defn ->hex-string [digest]
  (apply str (map (partial format "%02x") digest)))

(defn hash-single [txn-bytes]
  (sha256 txn-bytes))

(defn concat-bytes [txn-bytes1 txn-bytes2]
  (byte-array
    (mapcat
      seq
      [txn-bytes1 txn-bytes2])))

(defn hash-pair [txn-bytes1 txn-bytes2]
  (sha256
    (concat-bytes txn-bytes1 txn-bytes2)))

(defn compute-merkle-root [transactions]
  (loop [remaining (map (comp hash-single ->bytes) transactions)]
    (if (= 1 (count remaining))
      (->hex-string (first remaining))
      (let [re-hash (map (fn [tx-bytes]
                           (if (= 2 (count tx-bytes))
                             (hash-pair (first tx-bytes) (second tx-bytes))
                             (first tx-bytes)))
                         (partition-all 2 remaining))]
        (recur re-hash)))))

(defn compute-merkle-proof [transactions leaf-index]
  [])

