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

(defn read-csv [file]
  (with-open [reader (-> file io/resource io/reader)]
    (let [rows (doall (csv/read-csv reader))
          header (first rows)
          data (map (fn [row] (zipmap (map keyword header) row)) (rest rows))]
      data)))

(defn -main []
  (let [data (read-csv "original_data.csv")]
    (println data)
    (pprint (map #(calculate-root-and-proof (:transactions %)) data))))
