package csms.core.jhcloudstorage;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.*;
import csms.core.jhfiles.JhFile;
import csms.core.jhfiles.JhFileDropBox;
import csms.core.jhfiles.JhFileStructure;
import csms.core.jhtools.JhTempoRepoFileManager;
import csms.core.jhtools.JhTools;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public class JhDropBoxCloudStorage extends JhCloudStorage {

    private DbxRequestConfig config;
    private DbxClientV2 client;
    private boolean isDBClientConfigured;

    public JhDropBoxCloudStorage(String cloudStorageID){
        super(cloudStorageID);
        isDBClientConfigured = false;
    }

    private void configureDBConnection() {
        config = new DbxRequestConfig("dropbox/java-tutorial","en_US");
        client = new DbxClientV2(config, ACCESS_TOKEN);
        isDBClientConfigured = true;
    }

    @Override
    public boolean fetchFileStructure() {
        jhFileStructure = new JhFileStructure();

        if(!isDBClientConfigured) {
            configureDBConnection();
        }

        //FullAccount account = null;
        //account = client.users().getCurrentAccount();
        //Log User account login with: account.getName().getDisplayName();

        //Recursively build the JhFileStructure starting at the root
        isFetched = fethFileStructureForFolder("");

        return isFetched();
    }

    private boolean fethFileStructureForFolder(String path) {
        boolean fetchOk = true;
        try {
            ListFolderResult result = client.files().listFolder(path);
            while (true) {
                for (Metadata metadata : result.getEntries()) {
                    if(metadata instanceof FileMetadata) {
                        FileMetadata fileMetadata = (FileMetadata) metadata;
                        LocalDateTime lastEditDateTime = JhTools.date2LocalDateTime(fileMetadata.getServerModified());
                        JhFileDropBox jhFile = new JhFileDropBox();
                        jhFile.setPath(fileMetadata.getPathLower());
                        jhFile.setLastEditDateTime(lastEditDateTime);
                        jhFile.setSize(fileMetadata.getSize());
                        jhFile.setSourceCloudStorageId(cloudStorageId);
                        jhFile.setSourceCloudStorage(this);
                        //TODO: Get DropBox mimetype
                        //jhFile.setMimeType("");
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
        String tempoRepoPath;
        String dropBoxPath;
        boolean uploadOk;

        if(file2Create == null) {
            return false;
        }

        dropBoxPath = file2Create.getPath();
        if(file2Create.isFileOnTempoRepo()) {
            tempoRepoPath = file2Create.getTempoRepoPath();
        } else {
            //Download the File 2 Tempo Repo
            JhCloudStorage jhCloudStorage = file2Create.getSourceCloudStorage();
            if(jhCloudStorage == null) {
                System.err.println("No Source Cloud Storage registered");
                return false;
            }
            JhFile jhFileDownloaded = jhCloudStorage.download2TempoRepo(file2Create);
            if(jhFileDownloaded == null){
                System.err.println("An error happening downloading file 2 Tempo Repo");
                return false;
            }
            tempoRepoPath = jhFileDownloaded.getTempoRepoPath();
        }

        try (InputStream in = new FileInputStream(tempoRepoPath)) {
            client.files().uploadBuilder(dropBoxPath).uploadAndFinish(in);
            uploadOk = true;
        } catch(Exception ex) {
            ex.printStackTrace();
            uploadOk = false;
        }

        return uploadOk;
    }

    @Override
    public boolean deleteFile(JhFile file2Delete) {
        boolean deletionOk;
        if( (file2Delete == null) ||
                file2Delete.getPath().isEmpty()) {
            return false;
        }

        try {
            client.files().delete(file2Delete.getPath());
            deletionOk = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            deletionOk = false;
        }

        return deletionOk;
    }

    @Override
    public JhFile download2TempoRepo(JhFile file2Download) {
        JhFileDropBox fileJustDownloaded = null;
        String filePathOnTempoRepo;
        String dropBoxFilePath;
        Path pathOnTempoRepo;

        if(file2Download == null) {
            return null;
        }

        if(!isDBClientConfigured) {
            configureDBConnection();
        }

        dropBoxFilePath = file2Download.getPath();
        filePathOnTempoRepo = JhTempoRepoFileManager.getTempoRepoPath4File(file2Download);
        pathOnTempoRepo = Paths.get(filePathOnTempoRepo);

        if(Files.exists(pathOnTempoRepo)) {
            try {
                Files.delete(pathOnTempoRepo);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        try {
            File fileFromDB = new File(filePathOnTempoRepo);
            fileFromDB.getParentFile().mkdirs();
            fileFromDB.createNewFile();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        try {
            DbxDownloader<FileMetadata> downloader = client.files().download(dropBoxFilePath);
            FileOutputStream outputStream = new FileOutputStream(filePathOnTempoRepo);
            FileMetadata fileMetadata;

            //Download the file
            fileMetadata = downloader.download(outputStream);
            downloader.close();
            outputStream.close();

            //Create the JhFile with the metadata
            fileJustDownloaded = new JhFileDropBox();
            fileJustDownloaded.setPath(dropBoxFilePath);
            fileJustDownloaded.setLastEditDateTime(
                    JhTools.date2LocalDateTime(fileMetadata.getServerModified()));
            fileJustDownloaded.setSourceCloudStorageId(cloudStorageId);
            fileJustDownloaded.setFileOnTempoRepo(true);
            fileJustDownloaded.setNewFile(false);
            fileJustDownloaded.setDeleteCandidate(false);
            fileJustDownloaded.setTempoRepoPath(filePathOnTempoRepo);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return fileJustDownloaded;
    }
}
