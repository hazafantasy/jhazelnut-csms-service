package csms.core.jhfiles;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.client.json.Json;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class JhFileStructure {

    private Map<String, JhFile> files;

    public JhFileStructure() {
        files = new HashMap<>();
    }

    public JhFileStructure(JhFileStructure copyFromFS) {
        this();
        for(String path: copyFromFS.getFiles().keySet()) {
            files.put(path, copyFromFS.getFiles().get(path));
        }
    }

    public JhFileStructure(JhFile... arrayFileStructure){
        files = new HashMap<>();
        addArrayToFiles(arrayFileStructure);
    }

    private void addArrayToFiles(JhFile[] arrayFileStructure) {
        if(files != null) {
            for (JhFile file : arrayFileStructure) {
                files.put(file.getPath(), file);
            }
        } else {
            //Log Error
        }
    }

    public void addFilesFromJhFSJson(String json)
            throws InvalidParameterException {

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readTree(json);
            JsonNode filesNode = jsonNode.get("FileStructure");
            for(JsonNode fileNode: filesNode) {
                String path = fileNode.get("path").asText();
                LocalDateTime lastEditDateTime =
                       LocalDateTime.parse(fileNode.get("lastEditDateTime").asText());
                String sourceCloudStorageId = fileNode.get("sourceCloudStorageId").asText();
                boolean isDeleteCandidate = fileNode.get("isDeleteCandidate").asBoolean();
                boolean isNewFile = fileNode.get("isNewFile").asBoolean();
                boolean isFileOnTempoRepo = fileNode.get("isFileOnTempoRepo").asBoolean();
                String tempoRepoPath = fileNode.get("tempoRepoPath").asText();
                String mimeType = fileNode.get("mimeType").asText();
                Long size = fileNode.get("size").asLong();
                JhFile file;
                if(sourceCloudStorageId.startsWith("dropbox")) {
                    file = new JhFileDropBox();
                } else {
                    file = new JhFileGoogleDrive();
                }
                file.setPath(path);
                file.setLastEditDateTime(lastEditDateTime);
                file.setSourceCloudStorageId(sourceCloudStorageId);
                file.setNewFile(isNewFile);
                file.setFileOnTempoRepo(isFileOnTempoRepo);
                file.setDeleteCandidate(isDeleteCandidate);
                file.setTempoRepoPath(tempoRepoPath);
                file.setMimeType(mimeType);
                file.setSize(size);
                addJhFile(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new InvalidParameterException("Error parsing JSON");
        }

    }

    public void addJhFile(JhFile jhFile) {
        addArrayToFiles(new JhFile[] {jhFile});
    }

    public void clean() {

        //Remove all files that are delete candidates
        files.entrySet().removeIf(entry -> entry.getValue().isDeleteCandidate());

        //Set to default values all the remanent files
        files.replaceAll((k, v) -> {
            v.setDeleteCandidate(false);
            v.setNewFile(false);
            v.setFileOnTempoRepo(false);
            v.setTempoRepoPath("");
            return v;
        });

    }

    /**
     *
     * @param otherFileStruct
     */
    public JhFileActionList mergeFileStructures(JhFileStructure otherFileStruct){
        Map<String, JhFile> otherFileMap = new HashMap<>(otherFileStruct.getFiles());
        Map<String, JhFile> changeCandidateFiles = new HashMap<>();
        JhFileActionList actions2Apply = new JhFileActionList();
        for(String filePath: this.files.keySet()){//Iterate each file in this file structure
            JhFile thisFile = files.get(filePath);
            if(otherFileMap.containsKey(filePath)) {//This file exists
                JhFile otherFile = otherFileMap.get(filePath);
                otherFileMap.remove(filePath);
                if(!thisFile.equalsInTimeAndSize(otherFile)) {//They are different Files
                    JhFile mostUpdatedFile = thisFile.compareJhFiles(otherFile);
                    changeCandidateFiles.put(filePath, mostUpdatedFile);
                    if(mostUpdatedFile.isDeleteCandidate()){
                        actions2Apply.addAction(JhFileAction.JhActionType.DELETE, mostUpdatedFile);
                    } else {
                        actions2Apply.addAction(JhFileAction.JhActionType.UPDATE, mostUpdatedFile);
                    }
                } else {//The files are considered to be equal (In size and time range)
                    //Check for delete candidates
                    if(thisFile.isDeleteCandidate() || otherFile.isDeleteCandidate()) {
                        thisFile.setDeleteCandidate(true);
                        changeCandidateFiles.put(filePath, thisFile);
                        actions2Apply.addAction(JhFileAction.JhActionType.DELETE, thisFile);
                    }
                }
            } else {//This file may be a DELETE candidate
                if(!thisFile.isNewFile()) {
                    //Change it to Delete Candidate if this file is not new
                    thisFile.setDeleteCandidate(true);
                    changeCandidateFiles.put(filePath, thisFile);
                    actions2Apply.addAction(JhFileAction.JhActionType.DELETE, thisFile);
                }
            }
        }//End iteration in this File Structure

        // Replace all the values to update
        files.putAll(changeCandidateFiles);

        //The remanent files should be candidates for Create
        //Update the list of actions2apply with the remanent files
        for(String filePath: otherFileMap.keySet()) {
            JhFile otherFile = otherFileMap.get(filePath);
            if(!otherFile.isDeleteCandidate()) {
                otherFile.setNewFile(true);
                files.put(filePath, otherFile);
                actions2Apply.addAction(JhFileAction.JhActionType.CREATE, otherFile);
            }
        }

        return actions2Apply;
    }

    public Map<String, JhFile> getFiles() {
        return files;
    }

    public JsonNode toJsonNode() {
        JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode root = factory.objectNode();
        ArrayNode fileList = factory.arrayNode();
        for(String filePath: files.keySet()) {
            fileList.add(files.get(filePath).toJsonNode());
        }
        root.set("FileStructure", fileList);
        return root;
    }

    @Override
    public String toString(){
        return toJsonNode().toString();
    }

}
