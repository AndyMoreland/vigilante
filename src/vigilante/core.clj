(ns vigilante.core
  (:import java.io.File
           java.lang.Thread
           [java.util.concurrent ScheduledThreadPoolExecutor TimeUnit]))

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
  (let [now (System/currentTimeMillis)]
    (doseq [file (filter #(> (last-modified %) (- now poll-time)) (list-dir-files directory))]
      (future (callback file)))))

(defn watch [directory callback poll-time]
  (doto (ScheduledThreadPoolExecutor. 1)
      (.scheduleWithFixedDelay #(process directory callback poll-time) 0 poll-time TimeUnit/MILLISECONDS)))
