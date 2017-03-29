package csms.core.jhfiles;

import java.time.LocalDateTime;

public class JhFileGoogleDrive extends JhFile {

    private String googleDriveFileId;
    private String googleDriveParentFolderId;

    public JhFileGoogleDrive(String path, LocalDateTime lastEditDateTime, String mainSourceDriveId) {
        super(path, lastEditDateTime, mainSourceDriveId);
    }

    public JhFileGoogleDrive() {

    }

    public String getGoogleDriveFileId() {
        return googleDriveFileId;
    }

    public void setGoogleDriveFileId(String googleDriveFileId) {
        this.googleDriveFileId = googleDriveFileId;
    }

    public String getGoogleDriveParentFolderId() {
        return googleDriveParentFolderId;
    }

    public void setGoogleDriveParentFolderId(String googleDriveParentFolderId) {
        this.googleDriveParentFolderId = googleDriveParentFolderId;
    }
}
