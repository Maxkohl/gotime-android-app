package com.example.gotimer;

import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.gotimer.dao.ProfileDao;
import com.example.gotimer.entity.Profile;

import java.util.List;

public class Repository {
    private ProfileDao mProfileDao;
    private LiveData<List<Profile>> getAllProfiles;
    private LiveData<List<Profile>> getActiveAlarmProfiles;

    public Repository(Application application) {
        ProfileRoomDatabase db = ProfileRoomDatabase.getDatabase(application);
        mProfileDao = db.profilesDao();
        getAllProfiles = mProfileDao.getAllProfiles();
    }

    public LiveData<List<Profile>> getAllProfiles() {
        return getAllProfiles;
    }

    public LiveData<List<Profile>> getActiveAlarmProfiles(boolean isAlarmActive) {
        return getActiveAlarmProfiles = mProfileDao.getActiveAlarmProfiles(isAlarmActive);
    }

    public void insertNewTimerProfile(Profile profile) {
        new insertAsyncTask(mProfileDao).execute(profile);
    }

    public void updateTimerProfile(Profile profile) {
        new updateAsyncTask(mProfileDao).execute(profile);
    }

    public LiveData<List<Profile>> getActiveProfiles(boolean isBlockActive) {
        return mProfileDao.getActiveProfile(isBlockActive);
    }

    public void deleteProfile(int profileId) {
        new deleteAsyncTask(mProfileDao).execute(profileId);
    }


    //TODO Replace AsyncTasks with NOT DEPRECATED tech
    private class insertAsyncTask extends AsyncTask<Profile, Void, Void> {
        private ProfileDao mAsyncTaskDao;

        insertAsyncTask(ProfileDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Profile... profiles) {
            mAsyncTaskDao.insertProfile(profiles[0]);
            return null;
        }
    }

    private class updateAsyncTask extends AsyncTask<Profile, Void, Void> {
        private ProfileDao mAsyncTaskDao;

        updateAsyncTask(ProfileDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Profile... profiles) {
            mAsyncTaskDao.updateProfile(profiles[0]);
            return null;
        }
    }

    private class deleteAsyncTask extends AsyncTask<Integer, Void, Void> {
        private ProfileDao mAsyncTaskDao;

        deleteAsyncTask(ProfileDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Integer... profileIds) {
            mAsyncTaskDao.deleteProfile(profileIds[0]);
            return null;
        }
    }
}
