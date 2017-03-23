package csms.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service("jhAutoSyncService")
@Scope("session")
public class JhAutoSyncServiceImpl implements JhAutoSyncService {

    public JhAutoSyncServiceImpl() {
        System.out.println("JhAutoSyncServiceImpl created...");
    }

    @Override
    public boolean autoSync(int userid, String driveId1, String driveId2) {
        boolean syncOk = false;

        System.out.println("AutoSync starting...");
        System.out.println("userid: " + userid);
        System.out.println("driveId1: " + driveId1);
        System.out.println("driveId2: " + driveId2);

        //Get File Mapping from Base

        //Get File Mapping drive1

        //Get File Mapping drive2

        //Compare

        return syncOk;
    }

    @Override
    public boolean autoSyncAll(int userid) {



        return false;
    }
}
