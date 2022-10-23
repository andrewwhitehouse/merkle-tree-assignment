(ns merkle-tree.core
  (:require [merkle-tree.tree :as tree]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.data.csv :as csv]
            [clojure.pprint :refer [pprint]]))

(defn calculate-root-and-proof
  ([transactions root-fn proof-fn]
   (println transactions (type transactions))
   (let [txns (str/split transactions #",")
         middle-leaf-index (Math/floor (/ (count txns) 2))]
     {:transactions             txns
      :merkle-root              (root-fn txns)
      :middle-left-merkle-proof (proof-fn txns middle-leaf-index)}))
  ([transactions] (calculate-root-and-proof transactions tree/compute-merkle-root tree/compute-merkle-proof)))

(defn read-csv [resource]
  (with-open [reader (-> resource io/resource io/reader)]
    (let [rows (doall (csv/read-csv reader))
          header (first rows)
          data (map (fn [row] (zipmap (map keyword header) row)) (rest rows))]
      data)))

(defn format-transactions [row]
  (str/join "," (row :transactions)))

(defn write-csv [file data]
  (with-open [writer (io/writer file)]
    (csv/write-csv
      writer
      (concat
        [["transactions" "merkleRoot" "middleLeafMerkleProof"]]
        (map (juxt format-transactions :merkle-root :middle-left-merkle-proof) data))
      :quote \")))

(defn -main []
  (let [data (read-csv "original_data.csv")]
    (println "Writing data.csv ...")
    (write-csv "data.csv" (map #(calculate-root-and-proof (:transactions %)) data))))
