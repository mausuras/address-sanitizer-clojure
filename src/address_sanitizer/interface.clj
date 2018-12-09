(ns address-sanitizer.interface
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.java.io :as io]))

(def cli-options 
  ;; An option with a required argument
  [["-c" "--chunk CHUNK_SIZE" "Chunk size"
    :default 100
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 %) "Must be a positive number"]]
   ["-o" "--output FILE_PATH" "Output file"
    :default "/tmp/results.csv"]
    ;; :validate (not (.exists #(io/file %)))]
   ["-f" "--format OUTPUT_FORMAT" "Output format"
    :default "display_name"]
   ;; A boolean option defaulting to nil
   ["-h" "--help"]])

(defn usage [options-summary]
  (->> ["Usage: program-name [options] filepath"
        ""
        "Options:"
        options-summary]
       (clojure.string/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (clojure.string/join \newline errors)))

(defn validate-args
  "Validate command line arguments. Either return a map indicating the program
  should exit (with a error message, and optional ok status), or a map
  indicating the action the program should take and the options provided."
  [args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) ; help => exit OK with usage summary
      {:exit-message (usage summary) :ok? true}
      errors ; errors => exit with description of errors
      {:exit-message (error-msg errors)}
      ;; custom validation on arguments
      (and (= 1 (count arguments))
      (.exists (io/file (first arguments))))
      {:file (first arguments) :options options}
      :else ; failed custom validation => exit with usage summary
      {:exit-message (usage summary)})))

(defn exit [status msg]
  (println msg)
  (System/exit status))

