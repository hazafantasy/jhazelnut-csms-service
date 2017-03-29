package csms.core.jhfiles;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import csms.core.jhcloudstorage.JhCloudStorage;

import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.MINUTES;

public abstract class JhFile {

    //TODO: Folders in Google Drive are Case Sensitive, In DropBox f = F. The same as Linux vs Windows
    //TODO: Save the path with Case letters and not just the one in Lower Case
    //Find a way to compare both using path
    private String path;
    private LocalDateTime lastEditDateTime;
    private String sourceCloudStorageId;
    private boolean isDeleteCandidate;
    private boolean isNewFile;
    private boolean isFileOnTempoRepo;
    private String tempoRepoPath;
    private String mimeType;
    private Long size;
    private JhCloudStorage sourceCloudStorage;
    /**
     * Set a time offset of 2 minutes.
     * Between these offsset time two different files with the same name
     * on different drives will be treated the same
     */
    private long offsetMinutes = 5L;
    public JhFile(String path, LocalDateTime lastEditDateTime, String sourceCloudStorageId) {
        this.path = path;
        this.lastEditDateTime = lastEditDateTime;
        this.sourceCloudStorageId = sourceCloudStorageId;
    }
    public JhFile() {
        // A simple constructor for simple developers like me :P
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public JhCloudStorage getSourceCloudStorage() {
        return sourceCloudStorage;
    }

    public void setSourceCloudStorage(JhCloudStorage sourceCloudStorage) {
        this.sourceCloudStorage = sourceCloudStorage;
    }

    public boolean isNewFile() {
        return isNewFile;
    }

    public void setNewFile(boolean newFile) {
        isNewFile = newFile;
    }

    public String getTempoRepoPath() {
        return tempoRepoPath;
    }

    public void setTempoRepoPath(String tempoRepoPath) {
        this.tempoRepoPath = tempoRepoPath;
    }

    public boolean isFileOnTempoRepo() {
        return isFileOnTempoRepo;
    }

    public void setFileOnTempoRepo(boolean fileOnTempoRepo) {
        isFileOnTempoRepo = fileOnTempoRepo;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LocalDateTime getLastEditDateTime() {
        return lastEditDateTime;
    }

    public void setLastEditDateTime(LocalDateTime lastEditDateTime) {
        this.lastEditDateTime = lastEditDateTime;
    }

    public String getSourceCloudStorageId() {
        return sourceCloudStorageId;
    }

    public void setSourceCloudStorageId(String sourceCloudStorageId) {
        this.sourceCloudStorageId = sourceCloudStorageId;
    }

    public boolean isDeleteCandidate() {
        return isDeleteCandidate;
    }

    public void setDeleteCandidate(boolean deleteCandidate) {
        isDeleteCandidate = deleteCandidate;
    }

    public String getFileName() {
        return path.substring(path.lastIndexOf("/")+1);
    }

    /**
     *
     * @param obj
     * @return
     */
    public boolean equalsInTimeAndSize(Object obj){
        //Check if these are same class type
        if( !(obj instanceof JhFile) ){
            return false;
        }

        //Check if they point to same object in memory
        if(obj == this && offsetMinutes == 0){
            return true;
        }

        boolean areEqual;
        JhFile jhFile = (JhFile)obj;
        LocalDateTime otherLastEditDateTime = jhFile.getLastEditDateTime();
        long minsDiff = Math.abs(MINUTES.between(lastEditDateTime, otherLastEditDateTime));

        areEqual = this.path.equals(jhFile.getPath());
        areEqual = areEqual && (minsDiff <= offsetMinutes);
        areEqual = areEqual && (this.size.equals(jhFile.getSize()));

        return areEqual;
    }

    /**
     * Return the most updated JhFile as a new JhFile instance
     * @param otherFile
     * @return
     */
    public JhFile compareJhFiles(JhFile otherFile) {
        JhFile newestFile = null;
        if(otherFile != null){
            if(lastEditDateTime.isAfter(otherFile.getLastEditDateTime())) {
                newestFile = this;
            } else {
                newestFile = otherFile;
            }
        }
        return newestFile;
    }

    @Override
    public boolean equals(Object obj){
        //Check if these are same class type
        if( !(obj instanceof JhFile) ){
            return false;
        }

        //Check if they point to same object in memory
        if(obj==this){
            return true;
        }

        //Compare fields and return equals value
        JhFile jhFile = (JhFile)obj;
        return this.path.equals(jhFile.getPath());
    }

    public JsonNode toJsonNode() {
        JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode root = factory.objectNode();
        root.put("path", path);
        root.put("lastEditDateTime", lastEditDateTime.toString());
        root.put("sourceCloudStorageId", sourceCloudStorageId);
        root.put("isDeleteCandidate", isDeleteCandidate);
        root.put("isNewFile", isNewFile);
        root.put("isFileOnTempoRepo", isFileOnTempoRepo);
        root.put("tempoRepoPath", tempoRepoPath);
        root.put("mimeType", mimeType);
        root.put("size", size);
        return root;
    }

    @Override
    public String toString() {
        return toJsonNode().toString();
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
