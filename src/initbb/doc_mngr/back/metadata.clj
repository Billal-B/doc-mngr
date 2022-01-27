(ns initbb.doc-mngr.back.metadata
  (:require [schema.core :as s])
  (:import
    (java.io File FileInputStream FileNotFoundException)
    (java.nio.file Files LinkOption NoSuchFileException)
    (java.nio.file.attribute BasicFileAttributes FileTime)
    (org.apache.tika.parser AutoDetectParser ParseContext)
    (org.apache.tika.sax BodyContentHandler)
    (org.apache.tika.metadata Metadata)
    (org.apache.tika.exception ZeroByteFileException)
    (java.time LocalDateTime ZoneOffset)))

(defn- extract-base-metadata
  [file]
  (try (let [base-attr (as-> file i
                             (.toPath i)
                             (Files/readAttributes i BasicFileAttributes (into-array [LinkOption/NOFOLLOW_LINKS])))
             file-time-to-local-date-time (fn [file-time] (-> file-time
                                                              .toInstant
                                                              (LocalDateTime/ofInstant ZoneOffset/UTC)))]
         {:creation-time     (-> (.creationTime base-attr)
                                 file-time-to-local-date-time)
          :access-time       (-> (.lastAccessTime base-attr)
                                 file-time-to-local-date-time)
          :modification-time (-> (.lastModifiedTime base-attr)
                                 file-time-to-local-date-time)})
       (catch NoSuchFileException _ nil)))

(defn- extract-content-type [^File file]
  (try
    (let [parser (AutoDetectParser.)
          handler (BodyContentHandler.)
          metadata (Metadata.)
          fis (FileInputStream. file)
          pc (ParseContext.)]
      (.parse parser fis handler metadata pc)
      {:content-type (.get metadata "Content-Type")})
    (catch FileNotFoundException _ nil)
    (catch ZeroByteFileException _ nil)))

(s/defn extract-metadata :- {(s/required-key :content-type) s/Str
                             (s/required-key :creation-time) s/Str
                             (s/required-key :access-time) s/Str
                             (s/required-key :modification-time) s/Str}
  [file :- File]
  (let [base-metadata (extract-base-metadata file)
        content-type (extract-content-type file)]
    (merge base-metadata content-type)))
