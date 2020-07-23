package com.example.gotimer;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.example.gotimer.dao.ProfileDao;
import com.example.gotimer.entity.Profile;

@Database(entities = {Profile.class}, version = 0, exportSchema = false)
public abstract class ProfileRoomDatabase extends androidx.room.RoomDatabase {

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
                            ProfileRoomDatabase.class, "profiles_database").fallbackToDestructiveMigration().addCallback(pRoomDatabaseCallback).build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback pRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
        }
    };
}
