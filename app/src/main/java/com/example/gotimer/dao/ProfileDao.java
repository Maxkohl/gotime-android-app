package com.example.gotimer.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.gotimer.entity.Profile;

import java.util.List;

@Dao
public interface ProfileDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertProfile(Profile profile);

    @Query("SELECT * FROM profiles_table")
    LiveData<List<Profile>> getAllProfiles();

    @Query("DELETE FROM profiles_table WHERE name = :name")
    void deleteProfile(String name);

    @Query("DELETE FROM profiles_table")
    void deleteAllProfiles();

    //TODO Update profile
    //TODO getTotalHoursPerDay

}
