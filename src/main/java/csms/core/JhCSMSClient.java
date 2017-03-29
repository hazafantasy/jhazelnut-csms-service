package csms.core;

import csms.core.jhcloudstorage.JhCloudStorage;
import csms.core.jhfiles.JhFileStructure;

import java.util.ArrayList;
import java.util.List;

public class JhCSMSClient {

    private List<JhCloudStorage> jhCloudStorageList;
    private JhFileStructure jhBaseFileStructure;

    public JhCSMSClient() {
        this(new JhFileStructure());
    }

    public JhCSMSClient(JhFileStructure jhBaseFileStructure) {
        this.jhBaseFileStructure = jhBaseFileStructure;
        jhCloudStorageList = new ArrayList<>();
    }

    public JhFileStructure getJhBaseFileStructure() {
        return jhBaseFileStructure;
    }

    public void addJhCloudStorage(JhCloudStorage jhCloudStorage) {
        jhCloudStorageList.add(jhCloudStorage);
    }

    public void autoSync() {
        for(JhCloudStorage cS: jhCloudStorageList) {
            //Merge all the File Structures in order to
            //update the Base File Structure
            cS.fetchFileStructure();//Force a fetch
            jhBaseFileStructure.mergeFileStructures(cS.getFileStructure());
        }

        for(JhCloudStorage cS: jhCloudStorageList) {
            //Apply to each Cloud Storage the new Base File Structure
            cS.applyFileStructure(jhBaseFileStructure);
        }
        jhBaseFileStructure.clean();
    }

}
