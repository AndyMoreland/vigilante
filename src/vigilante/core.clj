(ns vigilante.core
  (:import java.io.File
           java.util.Date
           java.lang.Thread))

(def keep-running (atom {}))

(defn last-modified
  "returns the last modified time of a given file"
  [file-path]
  (let [file (File. file-path)]
    (.lastModified file)))

(defn list-dir-files [directory]
  "returns a list of files on the desktop"
  []
  (let [files (.list (File. directory))]
    (map #(str directory "/" %) (seq files))))

(defn process [directory callback poll-time]
  (def keep-running (atom (merge @keep-running {(Thread/currentThread) true})))
  (loop [date (Date.)]
    (Thread/sleep poll-time)
    (doseq [file (filter #(>= 0 (.compareTo date (Date. (last-modified %)))) (list-dir-files directory))]
      (future (callback file)))
    (if (@keep-running (Thread/currentThread))
      (recur (Date.)))))

(defn watch [directory callback poll-time]
  (doto (Thread. #(process directory callback poll-time))
      (.start)))
