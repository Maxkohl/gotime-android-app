package com.example.gotimer;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.gotimer.dao.ProfileDao;
import com.example.gotimer.entity.Profile;

import java.util.List;

public class Repository {
    private ProfileDao mProfileDao;
    private LiveData<List<Profile>> getAllProfiles;

    public Repository(Application application) {
        ProfileRoomDatabase db = ProfileRoomDatabase.getDatabase(application);
        mProfileDao = db.profilesDao();
        getAllProfiles = mProfileDao.getAllProfiles();
    }

    public LiveData<List<Profile>> getAllProfiles() {
        return getAllProfiles;
    }
}
