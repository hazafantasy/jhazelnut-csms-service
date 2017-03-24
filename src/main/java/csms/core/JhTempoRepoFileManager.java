package csms.core;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JhTempoRepoFileManager {
//Dynamically return the correct path (Windows or Linux)
    private static final String TEMPO_REPO_FOLDER = "JhazelnutCSMS";

    public static String getTempoRepoPath4User() {
        return getTempoRepoPath4User("1");
    }

    public static String getTempoRepoPath4User(String userId) {
        String homePath = System.getProperty("user.home");
        Path tempoRepoPath = Paths.get(homePath, TEMPO_REPO_FOLDER,
                                userId);
        return tempoRepoPath.toString() + File.separator;

    }

}
