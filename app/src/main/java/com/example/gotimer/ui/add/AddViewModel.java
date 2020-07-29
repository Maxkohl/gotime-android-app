package com.example.gotimer.ui.add;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.gotimer.Repository;
import com.example.gotimer.entity.Profile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AddViewModel extends AndroidViewModel {
    private long mTime;
    private static final String TAG = AddViewModel.class.getSimpleName();
    private Repository mRepository;


    public AddViewModel(@NonNull Application application) {
        super(application);
        mRepository = new Repository(application);
    }

    public void processTimePickerResult(int hourOfDay, int minute) {
        String hourString = Integer.toString(hourOfDay);
        String minuteString = Integer.toString(minute);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date stringFormatted = format.parse("1999-12-12 " + hourString + ":" + minuteString + ":00");
            mTime = stringFormatted.getTime() / 1000;
        } catch (ParseException e) {
            Log.d(TAG, "Exception when parsing time into formatted time");
            e.printStackTrace();
        }
    }

    public long getTime() {
        return mTime;
    }

    public void insertNewTimerProfile(Profile newProfile) {
        mRepository.insertNewTimerProfile(newProfile);
    }

    public LiveData<List<Profile>> getAllProfiles() {
        return mRepository.getAllProfiles();
    }

    public void updateProfile(Profile profile) {mRepository.updateTimerProfile(profile);}

}
