(ns initbb.doc-mngr.metadata
  (:import
    (java.io File FileInputStream FileNotFoundException)
    (java.nio.file Files LinkOption NoSuchFileException Path)
    (java.nio.file.attribute BasicFileAttributes)
    (java.time LocalDateTime ZoneOffset)
    (org.apache.tika.exception ZeroByteFileException WriteLimitReachedException)
    (org.apache.tika.metadata Metadata)
    (org.apache.tika.parser AutoDetectParser ParseContext)
    (org.apache.tika.sax BodyContentHandler)))

(defrecord FileMetadata [^Path path
                         ^LocalDateTime creation_time
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
  (try
    (let [parser (AutoDetectParser.)
          handler (BodyContentHandler. -1)
          metadata (Metadata.)
          fis (FileInputStream. file)
          pc (ParseContext.)]
      (.parse parser fis handler metadata pc)
      {:content_type (.get metadata "Content-Type")})
    (catch ZeroByteFileException _ nil)
    (catch WriteLimitReachedException _ nil)))


(defn extract-metadata [^File file]
  ;; TODO: implements filter on file size
  (let [files (filter #(.isFile %) (file-seq file))
        file-fn (fn ^FileMetadata [^File file]
                  (try
                    (let [base-metadata (extract-base-metadata file)
                          content-type (extract-content-type file)]
                      (map->FileMetadata (merge base-metadata content-type {:path (.toPath file)})))
                    (catch NoSuchFileException _ nil)
                    (catch FileNotFoundException _ nil)))]
    (map file-fn files)))
