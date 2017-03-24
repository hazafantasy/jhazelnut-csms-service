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
import csms.core.JhFile;

import com.google.api.services.drive.Drive;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GoogleDriveCloudStorage extends JhCloudStorage {

    private final String CLIENT_ID = "295153548798-a3bav7lg2ck4esu214hnns38fne247l5.apps.googleusercontent.com";
    private final String CLIENT_SECRET = "vjhP-3wfF927l2P-OAD_Cqdq";

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
            Arrays.asList(DriveScopes.DRIVE_METADATA_READONLY);

    public GoogleDriveCloudStorage(String cloudStorageId) {
        super(cloudStorageId);

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

    @Override
    public boolean fetchFileStructure() {
        try {
            // Build a new authorized API client service.
            Drive service = getDriveService();

            // Print the names and IDs for up to 10 files.
            FileList result = service.files().list()
                    .setPageSize(10)
                    .setFields("nextPageToken, files(id, name)")
                    .execute();
            List<File> files = result.getFiles();
            if (files == null || files.size() == 0) {
                System.out.println("No files found.");
            } else {
                System.out.println("Files:");
                for (File file : files) {
                    System.out.printf("%s (%s)\n", file.getName(), file.getId());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return false;
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
