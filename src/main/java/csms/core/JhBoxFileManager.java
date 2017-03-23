package csms.core;

public class JhBoxFileManager {

    public static String getTempoRepoPath4User() {
        return getTempoRepoPath4User("1");
    }

    public static String getTempoRepoPath4User(String userId) {
        return "/home/hmojica/" + userId + "/";
    }

}
