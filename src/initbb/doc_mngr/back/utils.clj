(ns initbb.doc-mngr.back.utils
  (:import
    (java.io File FileInputStream FileNotFoundException)
    (java.nio.file Files LinkOption NoSuchFileException)
    (java.nio.file.attribute BasicFileAttributes FileTime)
    (org.apache.tika.parser AutoDetectParser ParseContext)
    (org.apache.tika.sax BodyContentHandler)
    (org.apache.tika.metadata Metadata)
    (org.apache.tika.exception ZeroByteFileException)
    (java.time LocalDateTime ZoneOffset)))

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
      {:content-type (.get metadata "Content-Type")})
    (catch FileNotFoundException _ nil)
    (catch ZeroByteFileException _ nil)))

(defn extract-metadata [^File file]
  (let [base-metadata (extract-base-metadata file)
        content-type (extract-content-type file)]
    (merge base-metadata content-type)))
