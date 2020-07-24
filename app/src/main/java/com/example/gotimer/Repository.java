package com.example.gotimer;

import android.app.Application;
import android.os.AsyncTask;

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

    public void insertNewTimerProfile(Profile profile) {
        mProfileDao.insertProfile(profile);
    }


    //TODO Replace AsyncTasks with NOT DEPRECATED tech
    private class insertAsyncTask extends AsyncTask<Profile, Void, Void> {
        private ProfileDao mAsyncTaskDao;
        insertAsyncTask(ProfileDao dao) {
            mAsyncTaskDao = mProfileDao;
        }

        @Override
        protected Void doInBackground(Profile... profiles) {
            mAsyncTaskDao.insertProfile(profiles[0]);
            return null;
        }
    }
}
