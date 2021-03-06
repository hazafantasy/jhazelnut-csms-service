package csms.model;

import csms.core.jhcloudstorage.JhCloudStorage;
import csms.core.jhcloudstorage.JhDropBoxCloudStorage;

import java.util.ArrayList;
import java.util.List;

public class User {
    private Long userId;
    private List<JhCloudStorage> cloudStorageList;

    public User(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public List<JhCloudStorage> getCloudStorageList() {
        return cloudStorageList;
    }

    //*************************************************************************************
    //Tempo methods 2 delete
    public void setCloudStorageListDummyMethod(List<JhCloudStorage> cloudStorageList) {
        this.cloudStorageList = cloudStorageList;
    }

    private User getUserDummyMethod(Long userId) {
        User testUser = new User(1L);
        List<JhCloudStorage> cloudStorageList = new ArrayList<>();

        //DropBox Cloud Stroage
        cloudStorageList.add(new JhDropBoxCloudStorage("dropBox1"));

        return testUser;
    }
    //*************************************************************************************
}
