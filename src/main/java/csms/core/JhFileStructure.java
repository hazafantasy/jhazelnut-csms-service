package csms.core;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class JhFileStructure {

    private Map<String, JhFile> files;

    //public JhFileStructure(String jsonFileStructure) {
        //files = new HashSet<>();
        //parseJSON(jsonFileStructure);
    //}

    public JhFileStructure(JhFile... arrayFileStructure){
        files = new HashMap<>();
        array2Set(arrayFileStructure);
    }

    /**
     *
     * @param otherFileStruct
     */
    public void mergeFileStructures(JhFileStructure otherFileStruct){
        Map<String, JhFile> otherFileMap = new HashMap<>(otherFileStruct.getFilesMap());
        Map<String, JhFile> changeCandidateFiles = new HashMap<>();
        for(String filePath: this.files.keySet()){//Iterate each file in this file structure
            JhFile thisFile = files.get(filePath);
            if(otherFileMap.containsKey(filePath)) {//This file exists
                JhFile otherFile = otherFileMap.get(filePath);
                if(!thisFile.equalsInTimeRange(otherFile)) {//They are different Files
                    JhFile mostUpdatedFile = thisFile.compareJhFiles(otherFile);
                    changeCandidateFiles.put(filePath, mostUpdatedFile);
                    otherFileMap.remove(filePath);
                }
            } else {//This file may be a DELETE candidate
                JhFile deleteCandidateFile = new JhFile(
                        thisFile.getPath(),
                        thisFile.getLastEditDateTime(),
                        thisFile.getMainSourceDriveId(),
                        true
                );
                changeCandidateFiles.put(filePath, deleteCandidateFile);
            }
        }//End iteration in this File Structure

        // Replace all the values to update
        files.putAll(changeCandidateFiles);

        //The remanent files should be candidates for Upload
        files.putAll(otherFileMap);
    }



    public Map<String, JhFile> getFilesMap() {
        return files;
    }

    private void array2Set(JhFile[] arrayFileStructure) {
        for(JhFile file: arrayFileStructure){
            files.put(file.getPath(),file);
        }
    }

    @Override
    public String toString(){
        ObjectMapper mapper = new ObjectMapper();
        String json;
        try {
            json = mapper.writeValueAsString(files);
        } catch (Exception ex) {
            ex.printStackTrace();
            json = "ERROR mapping to JSON";
        }
        return json;
    }

}
