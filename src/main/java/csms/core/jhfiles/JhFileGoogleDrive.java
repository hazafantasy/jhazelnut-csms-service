package csms.core.jhfiles;

import java.time.LocalDateTime;

public class JhFileGoogleDrive extends JhFile {

    private String googleDriveFileId;
    private String googleDriveParentFolderId;

    public JhFileGoogleDrive(String path,
                                LocalDateTime lastEditDateTime,
                                String mainSourceDriveId,
                                String googleDriveFileId,
                                String googleDriveParentFolderId) {
        super(path, lastEditDateTime, mainSourceDriveId);
        this.googleDriveFileId = googleDriveFileId;
        this.googleDriveParentFolderId = googleDriveParentFolderId;
    }

    public String getGoogleDriveFileId() {
        return googleDriveFileId;
    }

    public String getGoogleDriveParentFolderId() {
        return googleDriveParentFolderId;
    }

}
