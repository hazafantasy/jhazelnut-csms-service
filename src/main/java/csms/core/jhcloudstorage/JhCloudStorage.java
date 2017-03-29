package csms.core.jhcloudstorage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import csms.core.jhfiles.JhFileAction;
import csms.core.jhfiles.JhFileActionList;
import csms.core.jhfiles.JhFileStructure;
import csms.core.jhfiles.JhFile;

public abstract class JhCloudStorage {
    protected String cloudStorageId;
    protected JhFileStructure jhFileStructure;
    protected String ACCESS_TOKEN;
    protected boolean isFetched;

    public JhCloudStorage(String cloudStorageId) {
        this.cloudStorageId = cloudStorageId;
        retrieveCloudStorageData();
    }

    public boolean isFetched() {
        return isFetched;
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
        if(!isFetched) {
            isFetched = fetchFileStructure();
        }
        return jhFileStructure;
    }

    public boolean updateFile(JhFile file) {
        return (deleteFile(file) && createFile(file));
    }

    public void applyFileStructure(JhFileStructure baseFileStructure) {
        //Clone this file structure just for getting an Action List
        JhFileStructure tempoStructure = new JhFileStructure(jhFileStructure);
        JhFileActionList actionList = tempoStructure.mergeFileStructures(baseFileStructure);
        if(actionList.getActions().size() > 0) {
            System.out.println("Applying actions to " + cloudStorageId + ":");
            for(JhFileAction action: actionList.getActions()) {
                System.out.println(action);
                if(action.getAction().equals(JhFileAction.JhActionType.DELETE)) {
                    deleteFile(action.getFile());
                } else if(action.getAction().equals(JhFileAction.JhActionType.CREATE)) {
                    createFile(action.getFile());
                } else if(action.getAction().equals(JhFileAction.JhActionType.UPDATE)) {
                    updateFile(action.getFile());
                }
            }
        } else {
            String msg = "Nothing to sync for " + cloudStorageId;
            System.out.println(msg);
        }

    }

    public JsonNode toJsonNode() {
        JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode root = factory.objectNode();
        root.put("cloudStorageId", cloudStorageId);
        root.put("isFetched", isFetched);
        root.put("ACCESS_TOKEN", ACCESS_TOKEN);
        root.set("jhFileStructure", jhFileStructure.toJsonNode());
        return root;
    }

    @Override
    public String toString() {
        return toJsonNode().toString();
    }

    public abstract boolean fetchFileStructure();
    public abstract boolean createFile(JhFile file2Create);
    public abstract boolean deleteFile(JhFile file2Delete);
    public abstract JhFile download2TempoRepo(JhFile file2Download);
}
