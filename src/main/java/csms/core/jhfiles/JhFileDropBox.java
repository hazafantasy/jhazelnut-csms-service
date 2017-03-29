package csms.core.jhfiles;

import java.time.LocalDateTime;

public class JhFileDropBox extends JhFile {

    public JhFileDropBox() {

    }

    public JhFileDropBox(String path, LocalDateTime lastEditDateTime, String mainSourceDriveId) {
        super(path, lastEditDateTime, mainSourceDriveId);
    }
}
