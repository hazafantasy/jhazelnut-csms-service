package csms.core;

public abstract class JhCloudStorage {
    private String cloudStorageId;
    private JhFileStructure jhFileStructure = null;
    private boolean fsFetched = false;

    public  JhCloudStorage(String cloudStorageId) {
        this.cloudStorageId = cloudStorageId;
    }

    public JhFileStructure getFileStructure() {
        if(!fsFetched) {
            fsFetched = fetchFileStructure();
        }
        return jhFileStructure;
    }

    public abstract boolean fetchFileStructure();
    public abstract boolean createFile(JhFile file);
    public abstract boolean deleteFile(String path);
    public abstract JhFile downloadFile(String path);
}
