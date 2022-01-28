(ns initbb.doc-mngr.back.metadata
  (:require [schema.core :as s])
  (:import
    (java.io File FileInputStream FileNotFoundException)
    (java.nio.file Files LinkOption NoSuchFileException)
    (java.nio.file.attribute BasicFileAttributes)
    (org.apache.tika.parser AutoDetectParser ParseContext)
    (org.apache.tika.sax BodyContentHandler)
    (org.apache.tika.metadata Metadata)
    (java.time LocalDateTime ZoneOffset)))

(defn- extract-base-metadata
  [file]
  (try (let [base-attr (as-> file i
                             (.toPath i)
                             (Files/readAttributes i BasicFileAttributes (into-array [LinkOption/NOFOLLOW_LINKS])))
             file-time-to-local-date-time (fn [file-time] (-> file-time
                                                              .toInstant
                                                              (LocalDateTime/ofInstant ZoneOffset/UTC)))]
         {:creation_time     (-> (.creationTime base-attr)
                                 file-time-to-local-date-time)
          :access_time       (-> (.lastAccessTime base-attr)
                                 file-time-to-local-date-time)
          :modification_time (-> (.lastModifiedTime base-attr)
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
      {:content_type (.get metadata "Content-Type")})
    (catch FileNotFoundException _ nil)))

(s/defn extract-metadata :- {(s/required-key :content_type)      s/Str
                             (s/required-key :creation_time)     s/Str
                             (s/required-key :access_time)       s/Str
                             (s/required-key :modification_time) s/Str}
  [file :- File]
  (let [base-metadata (extract-base-metadata file)
        content-type (extract-content-type file)]
    (merge base-metadata content-type)))
