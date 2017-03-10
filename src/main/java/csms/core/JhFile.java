package csms.core;

import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.MINUTES;

public class JhFile {
    private String path;
    private LocalDateTime lastEditDateTime;
    private String mainSourceDriveId;
    private boolean isDeleteCandidate;

    /**
     * Set a time offset of 2 minutes.
     * Between these offsset time two different files with the same name
     * on different drives will be treated the same
     */
    private long offsetMinutes = 5L;

    public JhFile(JhFile sourceFile){
        this(   sourceFile.getPath(),
                sourceFile.getLastEditDateTime(),
                sourceFile.getMainSourceDriveId(),
                sourceFile.isDeleteCandidate);
    }

    public JhFile(String path,
                  LocalDateTime lastEditDateTime,
                  String mainSourceDriveId,
                  boolean isDeleteCandidate){
        this.path = path;
        this.lastEditDateTime = lastEditDateTime;
        this.mainSourceDriveId = mainSourceDriveId;
        this.isDeleteCandidate = isDeleteCandidate;
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
        JhFile newFile = null;
        if(otherFile != null){
            if(lastEditDateTime.isAfter(otherFile.getLastEditDateTime())) {
                newFile = new JhFile(this);
            } else {
                newFile = new JhFile(otherFile);
            }
        }
        return newFile;
    }

    public String download2Box(){
        return "";
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

}
