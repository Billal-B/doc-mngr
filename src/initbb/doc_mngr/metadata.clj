(ns initbb.doc-mngr.metadata
  (:import
    (java.io File FileInputStream FileNotFoundException)
    (java.nio.file Files LinkOption NoSuchFileException)
    (java.nio.file.attribute BasicFileAttributes)
    (org.apache.tika.parser AutoDetectParser ParseContext)
    (org.apache.tika.sax BodyContentHandler)
    (org.apache.tika.metadata Metadata)
    (java.time LocalDateTime ZoneOffset)))

(defrecord FileMetadata [^LocalDateTime creation_time
                         ^LocalDateTime access_time
                         ^LocalDateTime modification_time
                         ^String content_type])

(defn- extract-base-metadata [^File file]
  (let [base-attr (as-> file i
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
                            file-time-to-local-date-time)}))

(defn- extract-content-type [^File file]
  (let [parser (AutoDetectParser.)
        handler (BodyContentHandler.)
        metadata (Metadata.)
        fis (FileInputStream. file)
        pc (ParseContext.)]
    (.parse parser fis handler metadata pc)
    {:content_type (.get metadata "Content-Type")}))


(defn extract-metadata ^FileMetadata [^File file]
  (try
    (let [base-metadata (extract-base-metadata file)
          content-type (extract-content-type file)]
      (map->FileMetadata (merge base-metadata content-type)))
    (catch NoSuchFileException _ nil)
    (catch FileNotFoundException _ nil)))
