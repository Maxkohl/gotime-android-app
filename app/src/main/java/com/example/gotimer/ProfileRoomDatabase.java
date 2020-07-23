package com.example.gotimer;


import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.example.gotimer.dao.ProfileDao;
import com.example.gotimer.entity.Profile;

@Database(entities = {Profile.class}, version = 1, exportSchema = false)
public abstract class ProfileRoomDatabase extends RoomDatabase {

    public abstract ProfileDao profilesDao();

    public static ProfileRoomDatabase INSTANCE;

    @NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    @Override
    public void clearAllTables() {

    }

    public static ProfileRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ProfileRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ProfileRoomDatabase.class, "profile_database").fallbackToDestructiveMigration().addCallback(sRoomDatabaseCallback).build();
                }
            }
        }
        return INSTANCE;
    }

    //TODO Remove after testing
    //These are for populating the database with dummy data
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            new PopulateDbAsync(INSTANCE).execute();
        }
    };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {
        private  final ProfileDao mDao;

        Profile[] dummyProfiles = {new Profile("Profile 1", "start", "stop")};

        public PopulateDbAsync(ProfileRoomDatabase db) {
            this.mDao = db.profilesDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mDao.deleteAllProfiles();
            for (Profile profile : dummyProfiles) {
                mDao.insertProfile(profile);
            }
            return null;
        }
    }
}
