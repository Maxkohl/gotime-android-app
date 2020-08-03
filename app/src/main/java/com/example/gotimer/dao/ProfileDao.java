package com.example.gotimer.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.gotimer.entity.Profile;

import java.util.List;

@Dao
public interface ProfileDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertProfile(Profile profile);

    @Query("SELECT * FROM profiles_table")
    LiveData<List<Profile>> getAllProfiles();

    @Query("DELETE FROM profiles_table WHERE profileId = :profileId")
    void deleteProfile(int profileId);

    @Query("DELETE FROM profiles_table")
    void deleteAllProfiles();

    @Update
    void updateProfile(Profile profile);

    @Query("SELECT * FROM profiles_table WHERE isOn = :isOn")
    LiveData<List<Profile>> getActiveProfile(boolean isOn);

    //TODO getTotalHoursPerDay

}
