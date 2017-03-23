package csms.core;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class JhFileStructure {

    private Map<String, JhFile> files;

    public JhFileStructure() {
        files = new HashMap<>();
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

    public void addJhFile(JhFile jhFile) {
        addArrayToFiles(new JhFile[] {jhFile});
    }

    /**
     *
     * @param otherFileStruct
     */
    public JhActionList mergeFileStructures(JhFileStructure otherFileStruct){
        Map<String, JhFile> otherFileMap = new HashMap<>(otherFileStruct.getFiles());
        Map<String, JhFile> changeCandidateFiles = new HashMap<>();
        JhActionList actions2Apply = new JhActionList();
        for(String filePath: this.files.keySet()){//Iterate each file in this file structure
            JhFile thisFile = files.get(filePath);
            if(otherFileMap.containsKey(filePath)) {//This file exists
                JhFile otherFile = otherFileMap.get(filePath);
                otherFileMap.remove(filePath);
                if(!thisFile.equalsInTimeRange(otherFile)) {//They are different Files
                    JhFile mostUpdatedFile = thisFile.compareJhFiles(otherFile);
                    changeCandidateFiles.put(filePath, mostUpdatedFile);
                    if(mostUpdatedFile.isDeleteCandidate()){
                        actions2Apply.addAction(JhAction.JhActionType.DELETE, mostUpdatedFile);
                    } else {
                        actions2Apply.addAction(JhAction.JhActionType.UPDATE, mostUpdatedFile);
                    }
                } else {//The files are considered to be equal

                }
            } else {//This file may be a DELETE candidate
                if(!thisFile.isNewFile()) {//Change it to Delete Candidate if this file is not new
                    JhFile deleteCandidateFile = new JhFile(
                            thisFile.getPath(),
                            thisFile.getLastEditDateTime(),
                            thisFile.getMainSourceDriveId(),
                            true
                    );
                    changeCandidateFiles.put(filePath, deleteCandidateFile);
                    actions2Apply.addAction(JhAction.JhActionType.DELETE, deleteCandidateFile);
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
                JhFile newFile = new JhFile(
                        otherFile.getPath(), otherFile.getLastEditDateTime(),
                        otherFile.getMainSourceDriveId(), otherFile.isDeleteCandidate(),
                        true, otherFile.isFileOnTempoRepo(), otherFile.getTempoRepoPath()
                );
                files.put(filePath, newFile);
                actions2Apply.addAction(JhAction.JhActionType.CREATE, newFile);
            }
        }

        return actions2Apply;
    }

    public Map<String, JhFile> getFiles() {
        return files;
    }

    @Override
    public String toString(){
        ObjectMapper mapper = new ObjectMapper();
        String json;
        try {
            json = mapper.writeValueAsString(files);
        } catch (Exception ex) {
            ex.printStackTrace();
            json = "ERROR mapping JhFileStructure to JSON";
        }
        return json;
    }

}
