package csms.service;

public interface JhAutoSyncService {
    boolean autoSync(int userid, String driveId1, String driveId2);
    boolean autoSyncAll(int userid);
}
