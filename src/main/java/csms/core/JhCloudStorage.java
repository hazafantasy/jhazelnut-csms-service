package csms.core;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class JhCloudStorage {
    protected String cloudStorageId;
    protected JhFileStructure jhFileStructure;
    protected String ACCESS_TOKEN;
    protected boolean fsFetched;

    public JhCloudStorage(String cloudStorageId) {
        this.cloudStorageId = cloudStorageId;
        retrieveCloudStorageData();
    }

    public boolean isFsFetched() {
        return fsFetched;
    }

    public String getCloudStorageId() {
        return cloudStorageId;
    }

    protected void retrieveCloudStorageData() {
        //************************************
        //Dummy code to be deleted
        if(cloudStorageId.startsWith("dropBox")) {
            ACCESS_TOKEN = "Q2kIvxC3egcAAAAAAAB6djAoh-nOSaV_XdvtvrkpNTMTfaZwLqHLzCQFmqeXfpUH";
        } else if(cloudStorageId.startsWith("gDrive")) {

        }
        //************************************
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

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        String json;
        try {
            json = mapper.writeValueAsString(this);
        } catch (Exception ex) {
            ex.printStackTrace();
            json = "ERROR mapping JhCloudStorage to JSON";
        }
        return json;
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
