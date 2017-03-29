package csms;

import csms.core.JhCSMSClient;
import csms.core.jhcloudstorage.JhCloudStorage;
import csms.core.jhcloudstorage.JhDropBoxCloudStorage;
import csms.core.jhcloudstorage.JhGoogleDriveCloudStorage;
import csms.core.jhfiles.JhFileStructure;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Scanner;

public class ApplicationCLI {

    public static void main(String[] args) {


        //TODO: Put SQLite DB here
        JhFileStructure jhFileStructure = null;
        Path baseJhFSFile = Paths.get("baseJhFS.json");
        if(Files.exists(baseJhFSFile)) {
            try {
                List<String> jsonLines = Files.readAllLines(baseJhFSFile);
                jhFileStructure = new JhFileStructure();
                jhFileStructure.addFilesFromJhFSJson(jsonLines.get(0));
            } catch (Exception e) {
                e.printStackTrace();
                jhFileStructure = null;
            }
        }

        if(jhFileStructure == null) {
            //fetch Google Drive's file structure if not yet build
            JhGoogleDriveCloudStorage jhGDCS =
                    new JhGoogleDriveCloudStorage("gDrive1");
            jhGDCS.fetchFileStructure();
            jhFileStructure = jhGDCS.getFileStructure();
        }

        //Create the JhCSMS Client
        JhCSMSClient jhCSMSClient = new JhCSMSClient(jhFileStructure);
        JhCloudStorage jhCloudStorage1 = new JhGoogleDriveCloudStorage("gDrive1");
        JhCloudStorage jhCloudStorage2 = new JhDropBoxCloudStorage("dropBox1");
        jhCSMSClient.addJhCloudStorage(jhCloudStorage1);
        jhCSMSClient.addJhCloudStorage(jhCloudStorage2);

        Scanner scanner = new Scanner(System.in);
        do {
            jhCSMSClient.autoSync();
            System.out.println("Sync again?? y/n ... ");
            String yn = scanner.next();
            if(yn.equals("n")) {
                break;
            }
        } while (true);

        System.out.println("Saving base JhFileStructure to json file... ");
        String jsonBaseJhFS = jhCSMSClient.getJhBaseFileStructure().toString();
        try {
            Files.write(baseJhFSFile, jsonBaseJhFS.getBytes(), StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            System.out.println("Base JhFileStructure saved to json file.");
        } catch (IOException e) {
            System.err.println("Failed saving base JhFileStructure to json file");
            e.printStackTrace();
        }
        System.out.println("Jhazelnut CSMS CLI closed");

    }
}
