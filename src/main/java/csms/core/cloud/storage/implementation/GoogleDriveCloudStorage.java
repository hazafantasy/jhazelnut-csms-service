package csms.core.cloud.storage.implementation;

import csms.core.JhCloudStorage;
import csms.core.JhFile;

public class GoogleDriveCloudStorage extends JhCloudStorage {


    public GoogleDriveCloudStorage(String cloudStorageId) {
        super(cloudStorageId);
    }

    @Override
    public boolean fetchFileStructure() {
        return false;
    }

    @Override
    public boolean createFile(JhFile file2Create) {
        return false;
    }

    @Override
    public boolean deleteFile(JhFile file2Delete) {
        return false;
    }

    @Override
    public JhFile download2TempoRepo(JhFile file2Download) {
        return null;
    }
}
