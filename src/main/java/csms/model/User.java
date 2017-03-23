package csms.model;

import csms.core.JhCloudStorage;

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
        User user = new User(1L);

        return user;
    }
    //*************************************************************************************
}
