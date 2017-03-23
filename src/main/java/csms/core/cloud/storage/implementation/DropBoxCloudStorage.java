package csms.core.cloud.storage.implementation;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.*;
import csms.core.JhCloudStorage;
import csms.core.JhFile;
import csms.core.JhFileStructure;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class DropBoxCloudStorage extends JhCloudStorage {

    private DbxRequestConfig config;
    private DbxClientV2 client;

    public DropBoxCloudStorage(String cloudStorageID){
        super(cloudStorageID);
    }

    @Override
    public boolean fetchFileStructure() {
        config = new DbxRequestConfig("dropbox/java-tutorial","en_US");
        client = new DbxClientV2(config, ACCESS_TOKEN);
        jhFileStructure = new JhFileStructure();

        //FullAccount account = null;
        //account = client.users().getCurrentAccount();
        //Log User account login with: account.getName().getDisplayName();

        //Recursively build the JhFileStructure starting at the root
        return fethFileStructureForFolder("");
    }

    private boolean fethFileStructureForFolder(String path) {
        boolean fetchOk = true;
        try {
            ListFolderResult result = client.files().listFolder(path);
            while (true) {
                for (Metadata metadata : result.getEntries()) {
                    if(metadata instanceof FileMetadata) {
                        FileMetadata fileMetadata = (FileMetadata) metadata;
                        LocalDateTime lastEditDateTime = LocalDateTime.ofInstant(
                                fileMetadata.getServerModified().toInstant(), ZoneId.systemDefault());
                        JhFile jhFile = new JhFile(fileMetadata.getPathLower(), lastEditDateTime, cloudStorageId);
                        jhFileStructure.addJhFile(jhFile);
                    } else if(metadata instanceof FolderMetadata) {
                        //Recursively call for this folder JhFileStructure
                        FolderMetadata folderMetadata = (FolderMetadata) metadata;
                        fetchOk = fethFileStructureForFolder(folderMetadata.getPathLower());
                    } else if(metadata instanceof DeletedMetadata) {
                        //Skip out this metadata
                        //Log this ignore
                    }
                }
                if (!result.getHasMore()) {
                    break;
                }
                result = client.files().listFolderContinue(result.getCursor());
            }

        } catch (DbxException e) {
            e.printStackTrace();
            fetchOk = false;
        }
        return fetchOk;
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
