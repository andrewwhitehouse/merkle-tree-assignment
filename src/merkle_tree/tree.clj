(ns merkle-tree.tree
  (:import (java.security MessageDigest)))

(defn sha256 [bytes]
  (.digest (MessageDigest/getInstance "SHA-256") bytes))

(defn ->bytes [utf8-string]
  (.getBytes utf8-string "UTF-8"))

(defn ->hex-string [digest]
  (apply str (map (partial format "%02x") digest)))

(defn concat-bytes [txn-bytes1 txn-bytes2]
  (byte-array
    (mapcat
      seq
      [txn-bytes1 txn-bytes2])))

(defn sha256-string
  ([s] (->> s ->bytes sha256 ->hex-string))
  ([s1 s2] (->hex-string (sha256 (concat-bytes (->bytes s1) (->bytes s2))))))

(defn hash-single [txn-bytes]
  (sha256 txn-bytes))


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

(defn hash-txns [transactions]
  (let [initial (map (fn [txns]
                       (if (= 2 (count txns))
                         (hash-pair (->bytes (first txns)) (->bytes (second txns)))
                         (hash-single (->bytes (first txns)))))
                     (partition-all 2 transactions))]
    (loop [remaining initial]
      (if (= 1 (count remaining))
        (->hex-string (first remaining))
        (let [re-hash (map (fn [tx-bytes]
                             (hash-pair (first tx-bytes) (second tx-bytes)))
                           (partition-all 2 remaining))]
          (recur re-hash))))))

(defn select-nodes [values index]
  (->> values
       (map-indexed (fn [idx item] [idx item]))
       (partition-all 2)
       (map (fn [pairs] {:indices (mapv first pairs) :values (mapv second pairs)}))
       (filter (fn [{:keys [indices values]}] ((set indices) index)))
       first))

(defn compute-merkle-proof [transactions leaf-index]
  (let [initial (map (comp sha256 ->bytes) transactions)]
    (loop [node-index leaf-index
           remaining initial
           collected []]
      (if (= 1 (count remaining))
        collected
        (let [{:keys [indices values]} (select-nodes remaining node-index)]
          (comment println "remaining" remaining)
          (comment println "indices" indices "values" values "node-index" node-index)
          (let [re-hash (map (fn [hashes]
                               (if (= 2 (count hashes))
                                 (hash-pair (first hashes) (second hashes))
                                 (first hashes)))
                             (partition-all 2 remaining))]
            (recur
              (quot node-index 2)
              re-hash
              (conj collected (if (= node-index (first indices))
                                {:right (->hex-string (second values))}
                                {:left (->hex-string (first values))})))))))))


(comment

  (import '[java.security MessageDigest])

  (defn sha256 [bytes]
    (.digest (MessageDigest/getInstance "SHA-256") bytes))

  (defn ->bytes [utf8-string]
    (.getBytes utf8-string "UTF-8"))

  (defn ->hex-string [digest]
    (apply str (map (partial format "%02x") digest)))

  (defn concat-bytes [txn-bytes1 txn-bytes2]
    (byte-array
      (mapcat
        seq
        [txn-bytes1 txn-bytes2])))

  (defn sha256-string
    ([s] (->> s ->bytes sha256 ->hex-string))
    ([s1 s2] (->hex-string (sha256 (concat-bytes (->bytes s1) (->bytes s2))))))


  (sha256-string
    (sha256-string
      (sha256-string "a")
      (sha256-string "b"))
    (sha256-string
      (sha256-string "c")
      (sha256-string "d")))

  )