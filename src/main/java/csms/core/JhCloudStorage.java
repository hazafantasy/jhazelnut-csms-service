package csms.core;

public abstract class JhCloudStorage {
    private String cloudStorageId;
    private JhFileStructure jhFileStructure = null;
    private boolean fsFetched = false;

    public JhCloudStorage(String cloudStorageId) {
        this.cloudStorageId = cloudStorageId;
    }

    public JhFileStructure getFileStructure() {
        if(!fsFetched) {
            fsFetched = fetchFileStructure();
        }
        return jhFileStructure;
    }

    public boolean updateFile(JhFile file) {
        return (deleteFile(file) && createFile(file));
    }

    public void applyFileStructure(JhFileStructure baseFileStructure) {
        JhActionList actionList = jhFileStructure.mergeFileStructures(baseFileStructure);
        for(JhAction action: actionList.getActions()) {
            if(action.getAction().equals(JhAction.JhActionType.DELETE)) {
                deleteFile(action.getFile());
            } else if(action.getAction().equals(JhAction.JhActionType.CREATE)) {
                createFile(fileDownloaderWrapper(action.getFile()));
            } else if(action.getAction().equals(JhAction.JhActionType.UPDATE)) {
                updateFile(fileDownloaderWrapper(action.getFile()));
            }
        }
    }

    private JhFile fileDownloaderWrapper(JhFile file) {
        JhFile newFile = file;
        if(!file.isFileOnTempoRepo()) {
            newFile = download2TempoRepo(file);
            if(!newFile.isFileOnTempoRepo() ||
                    newFile.getTempoRepoPath().isEmpty()) {
                //Log ERROR
                newFile = null;
            }
        }
        return newFile;
    }

    public abstract boolean fetchFileStructure();
    public abstract boolean createFile(JhFile file2Create);
    public abstract boolean deleteFile(JhFile file2Delete);
    public abstract JhFile download2TempoRepo(JhFile file2Download);
}
