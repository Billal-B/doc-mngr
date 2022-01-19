(ns doc-mngr.utils
  (:import
    (java.io File FileInputStream FileNotFoundException)
    (java.nio.file Files LinkOption NoSuchFileException)
    (java.nio.file.attribute BasicFileAttributes FileTime)
    (org.apache.tika.parser AutoDetectParser ParseContext)
    (org.apache.tika.sax BodyContentHandler)
    (org.apache.tika.metadata Metadata)
    (org.apache.tika.exception ZeroByteFileException)
    (java.time LocalDateTime ZoneId ZoneOffset))
  (:require [clojure.spec.alpha :as s]))

(defn list-dir [^String path]
  (let [dir (File. path)]
    (.listFiles dir)))

(defn file-time-to-local-date-time
  [^FileTime file-time]
  (-> file-time
      .toInstant
      (LocalDateTime/ofInstant ZoneOffset/UTC)))


(defn extract-base-metadata [^File file]
  (try (let [base-attr (as-> file i
                             (.toPath i)
                             (Files/readAttributes i BasicFileAttributes (into-array [LinkOption/NOFOLLOW_LINKS])))]
         {:creation-time     (-> (.creationTime base-attr)
                                 file-time-to-local-date-time)
          :access-time       (-> (.lastAccessTime base-attr)
                                 file-time-to-local-date-time)
          :modification-time (-> (.lastModifiedTime base-attr)
                                 file-time-to-local-date-time)})
       (catch NoSuchFileException _ nil)))

(defn extract-content-type
  [^File file]
  (try
    (let [parser (AutoDetectParser.)
          handler (BodyContentHandler.)
          metadata (Metadata.)
          fis (FileInputStream. file)
          pc (ParseContext.)]
      (.parse parser fis handler metadata pc)
      (print (seq (.names metadata)))
      {:content-type (.get metadata "Content-Type")})
    (catch FileNotFoundException _ nil)
    (catch ZeroByteFileException _ nil)))

(if-some [metadata (Files/getFileAttributeView
                     (.toPath (File. "/home/billal/Projects/doc-mngr/CHANGELOG.md"))
                     (.getClass BasicFileAttributes)
                     (into-array [LinkOption/NOFOLLOW_LINKS]))]
  (print metadata))
