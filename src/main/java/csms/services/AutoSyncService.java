package csms.services;

import org.springframework.stereotype.Service;

@Service("autoSyncService")
public class AutoSyncService {


    public boolean autoSync(int userid, String driveId1, String driveId2 ) {
        boolean syncOk = false;

        System.out.println("userid: " + userid);
        System.out.println("driveId1: " + driveId1);
        System.out.println("driveId2: " + driveId2);

        return syncOk;
    }

}
