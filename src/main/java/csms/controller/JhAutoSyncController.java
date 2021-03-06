package csms.controller;

import csms.bean.JhCSMSResponseBean;
import csms.service.JhAutoSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController("jhAutoSyncController")
public class JhAutoSyncController {

    @Autowired
    JhAutoSyncService jhAutoSyncService;

    @RequestMapping(value = "/autosync/{userid}/all",
            method = RequestMethod.POST)
    public JhCSMSResponseBean autoSync(
            @PathVariable int userid
    ) {
        JhCSMSResponseBean bean = new JhCSMSResponseBean("lol");
        jhAutoSyncService.autoSyncAll(userid);
        return bean;
    }

    @RequestMapping(value = "/autosync/{userid}/{driveId1}/{driveId2}",
            method = RequestMethod.POST)
    public JhCSMSResponseBean autoSync(
            @PathVariable int userid,
            @PathVariable String driveId1,
            @PathVariable String driveId2
    ) {
        JhCSMSResponseBean bean = new JhCSMSResponseBean("lol");
        jhAutoSyncService.autoSync(userid, driveId1, driveId2);
        return bean;
    }


}
