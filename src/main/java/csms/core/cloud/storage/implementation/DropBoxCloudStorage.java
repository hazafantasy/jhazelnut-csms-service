package csms.core.cloud.storage.implementation;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.*;
import csms.core.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public class DropBoxCloudStorage extends JhCloudStorage {

    private DbxRequestConfig config;
    private DbxClientV2 client;
    private boolean isDBClientConfigured;

    public DropBoxCloudStorage(String cloudStorageID){
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
                        LocalDateTime lastEditDateTime = Tools.date2LocalDateTime(fileMetadata.getServerModified());
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
        String tempoRepoPath;
        String dropBoxPath;
        boolean uploadOk;

        if(file2Create == null) {
            return false;
        }

        if(file2Create.isFileOnTempoRepo()) {
            tempoRepoPath = file2Create.getTempoRepoPath();
            dropBoxPath = file2Create.getPath();
        } else {
            tempoRepoPath = "";
            dropBoxPath = "";
            //Find a way to download the file
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
        JhFile fileJustDownloaded = null;
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
        filePathOnTempoRepo = JhTempoRepoFileManager.getTempoRepoPath4User() + dropBoxFilePath;
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
            fileJustDownloaded = new JhFile(
                    dropBoxFilePath,
                    Tools.date2LocalDateTime(fileMetadata.getServerModified()),
                    cloudStorageId,
                    false, false, true,
                    filePathOnTempoRepo
            );
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return fileJustDownloaded;
    }
}
