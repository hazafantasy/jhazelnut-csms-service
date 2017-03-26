package csms.core.cloud.storage.implementation;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.File;
import csms.core.JhCloudStorage;
import csms.core.JhFileStructure;
import csms.core.Tools;
import csms.core.jhfiles.JhFile;

import com.google.api.services.drive.Drive;
import csms.core.jhfiles.JhFileGoogleDrive;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class JhGoogleDriveCloudStorage extends JhCloudStorage {

    private final String CLIENT_ID = "295153548798-a3bav7lg2ck4esu214hnns38fne247l5.apps.googleusercontent.com";
    private final String CLIENT_SECRET = "vjhP-3wfF927l2P-OAD_Cqdq";
    private boolean isGDConfigured;
    private Drive gDriveService;

    /** Application name. */
    private static final String APPLICATION_NAME =
            "jhazelnutDrive";

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".credentials/jhazelnutDrive");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/drive-java-quickstart
     */
    private final List<String> SCOPES =
            Arrays.asList(DriveScopes.DRIVE, DriveScopes.DRIVE_METADATA,
                            DriveScopes.DRIVE_APPDATA, DriveScopes.DRIVE);

    public JhGoogleDriveCloudStorage(String cloudStorageId) {
        super(cloudStorageId);
        isGDConfigured = false;
        gDriveService = null;
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public Credential authorize() throws IOException {

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, CLIENT_ID, CLIENT_SECRET, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();

        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize("user");

        return credential;
    }

    /**
     * Build and return an authorized Drive client service.
     * @return an authorized Drive client service
     * @throws IOException
     */
    public Drive getDriveService() throws IOException {
        Credential credential = authorize();
        return new Drive.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private boolean configureGoogleDriveConnection() {
        try {
            // Build a new authorized API client service.
            gDriveService = getDriveService();
            isGDConfigured = true;
        } catch (IOException e) {
            e.printStackTrace();
            isGDConfigured = false;
        }
        return isGDConfigured;
    }

    @Override
    public boolean fetchFileStructure() {
        jhFileStructure = new JhFileStructure();
        if(!isGDConfigured) {
            if(!configureGoogleDriveConnection()) {
                return false;
            }
        }

        //Recursively fetch the folder structure
        isFetched = fetchFileStructureForFolder(getJhDriveFolderId(), "/");

        // Print the names and IDs for up to 10 files.
//        FileList result = null;
//
//        try {
//
//
//
//            result = gDriveService.files().list()
//                        .setQ("'0B3WC4CiccSmVRUxOenI5cjhuUVE' in parents").execute();
//
//
////            result = gDriveService.files().list()
////                    .setPageSize(50)
////                    .setFields("nextPageToken, files(id, name)")
////                    .execute();
//
//            List<File> files = result.getFiles();
//            if (files == null || files.size() == 0) {
//                System.out.println("No files found.");
//            } else {
//                System.out.println("Files:");
//                for (File file : files) {
//                    System.out.printf("%s (%s)\n", file.getName(), file.getId());
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return false;
    }

    private boolean fetchFileStructureForFolder(String folderId, String folderPath) {
        boolean fetchOk = true;

        try {
            String pageToken = null;
            do {
                String qParam = String.format("'%s' in parents", folderId);
                String fieldsRequested = "files(id, name, modifiedTime, mimeType)";
                FileList result = gDriveService.files().list()
                        .setQ(qParam)
                        .setFields(fieldsRequested)
                        .setPageToken(pageToken)
                        .execute();
                for (File file : result.getFiles()) {
                    if(file.getMimeType().equals("application/vnd.google-apps.folder")) {
                        //This is a folder: Fetch Recursively
                        //Remember that in Google Drive a folder is just no more than a file
                        //with a different mime type
                        String subFolderPath = folderPath + file.getName()+ "/";
                        fetchOk = fetchFileStructureForFolder(
                                    file.getId(), subFolderPath);
                    } else {
                        //This is a file, add it to the FileStructure
                        String filePath = folderPath + file.getName();
                        LocalDateTime lastEditDateTime = Tools.dateTime2LocalDateTime(file.getModifiedTime());
                        JhFileGoogleDrive jhFile = new JhFileGoogleDrive(
                                filePath, lastEditDateTime, cloudStorageId, file.getId(),folderId);
                        jhFileStructure.addJhFile(jhFile);
                    }
                }
                pageToken = result.getNextPageToken();
            } while (pageToken != null);
        } catch (IOException ex) {
            ex.printStackTrace();
            fetchOk = false;
        }

        return fetchOk;
    }

    private String getJhDriveFolderId() {

        String jhDriveFolderId = null;

        if(!isGDConfigured) {
            if(!configureGoogleDriveConnection()) {
                return null;
            }
        }

        try {
            String qMimeType = "mimeType = 'application/vnd.google-apps.folder'";
            String qParam = String.format("name = '%s' and %s",
                                            APPLICATION_NAME, qMimeType);
            FileList result = gDriveService.files().list()
                                    .setQ(qParam)
                                    .execute();
            List<File> fileList = result.getFiles();
            if(fileList.size() > 0) {
                jhDriveFolderId = fileList.get(0).getId();
            } else {
                //Folder not found :(
                //TODO: Write later the logic for creating the folder
            }
        } catch (IOException e) {
            e.printStackTrace();
            jhDriveFolderId = null;
        }

        return jhDriveFolderId;
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
