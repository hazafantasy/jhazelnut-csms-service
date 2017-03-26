package csms.core.jhfiles;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.MINUTES;

public abstract class JhFile {

    private String path;
    private LocalDateTime lastEditDateTime;
    private String mainSourceDriveId;
    private boolean isDeleteCandidate;
    private boolean isNewFile;
    private boolean isFileOnTempoRepo;
    private String tempoRepoPath;
    /**
     * Set a time offset of 2 minutes.
     * Between these offsset time two different files with the same name
     * on different drives will be treated the same
     */
    private long offsetMinutes = 5L;

//    public JhFile(String path, String mainSourceDriveId) {
//        this(path, LocalDateTime.now(), mainSourceDriveId);
//    }
//
//    public JhFile(String path, LocalDateTime lastEditDateTime, String mainSourceDriveId) {
//        this(path, lastEditDateTime, mainSourceDriveId,false, false,
//                false, "");
//    }
//
//    public JhFile(String path,
//                  LocalDateTime lastEditDateTime,
//                  String mainSourceDriveId,
//                  boolean isDeleteCandidate){
//        this(path, lastEditDateTime, mainSourceDriveId, isDeleteCandidate,
//        false, false, "");
//    }
//
//    public JhFile(JhFile sourceFile){
//        this(sourceFile.getPath(),
//                sourceFile.getLastEditDateTime(),
//                sourceFile.getMainSourceDriveId(),
//                sourceFile.isDeleteCandidate,
//                sourceFile.isNewFile(),
//                sourceFile.isFileOnTempoRepo(),
//                sourceFile.getTempoRepoPath());
//    }
//
//    public JhFile(String path,
//                  LocalDateTime lastEditDateTime,
//                  String mainSourceDriveId,
//                  boolean isDeleteCandidate,
//                  boolean isNewFile,
//                  boolean isFileOnTempoRepo,
//                  String tempoRepoPath) {
//        this.path = path;
//        this.lastEditDateTime = lastEditDateTime;
//        this.mainSourceDriveId = mainSourceDriveId;
//        this.isDeleteCandidate = isDeleteCandidate;
//        this.isNewFile = isNewFile;
//        this.isFileOnTempoRepo = isFileOnTempoRepo;
//        this.tempoRepoPath = tempoRepoPath;
//    }

    public boolean isNewFile() {
        return isNewFile;
    }

    public String getTempoRepoPath() {
        return tempoRepoPath;
    }

    public boolean isFileOnTempoRepo() {
        return isFileOnTempoRepo;
    }

    public String getPath() {
        return path;
    }

    public LocalDateTime getLastEditDateTime() {
        return lastEditDateTime;
    }

    public String getMainSourceDriveId() {
        return mainSourceDriveId;
    }

    public boolean isDeleteCandidate() {
        return isDeleteCandidate;
    }

    /**
     *
     * @param obj
     * @return
     */
    public boolean equalsInTimeRange(Object obj){
        //Check if these are same class type
        if( !(obj instanceof JhFile) ){
            return false;
        }

        //Check if they point to same object in memory
        if(obj == this && offsetMinutes == 0){
            return true;
        }

        JhFile jhFile = (JhFile)obj;
        LocalDateTime otherLastEditDateTime = jhFile.getLastEditDateTime();
        long minsDiff = Math.abs(MINUTES.between(lastEditDateTime, otherLastEditDateTime));

        return ( this.path.equals(jhFile.getPath()) && minsDiff <= offsetMinutes );
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

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        String json;
        try {
            json = mapper.writeValueAsString(this);
        } catch (Exception ex) {
            ex.printStackTrace();
            json = "ERROR mapping JhFile to JSON";
        }
        return json;
    }

}
