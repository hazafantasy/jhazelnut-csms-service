package csms.core.jhtools;

import csms.core.jhfiles.JhFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JhTempoRepoFileManager {
//Dynamically return the correct path (Windows or Linux)
    private static final String TEMPO_REPO_FOLDER = "JhazelnutCSMS";

    public static String getTempoRepoPath4File(JhFile jhFile) {

        if(jhFile == null) {
            return null;
        }
        String userId = "1";
        String homePath = System.getProperty("user.home");
        //Replace all Unix File separator slash for the System separator slash
        String cloudFilePath = jhFile.getPath().replace('/', File.separatorChar);
        String cloudStorageId = jhFile.getSourceCloudStorageId();
        if(cloudStorageId == null || cloudStorageId.isEmpty()) {
            cloudStorageId = "unknow";
        }
        Path tempoRepoPath = Paths.get(homePath, TEMPO_REPO_FOLDER, userId, cloudStorageId, cloudFilePath);
        return tempoRepoPath.toString();
    }

}
