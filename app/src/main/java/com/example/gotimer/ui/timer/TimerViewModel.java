package com.example.gotimer.ui.timer;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gotimer.Repository;
import com.example.gotimer.entity.Profile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TimerViewModel extends AndroidViewModel {

    private Repository mRepository;
    private static final String TAG = TimerViewModel.class.getSimpleName();
    long mEndTime;


    public TimerViewModel(@NonNull Application application) {
        super(application);
        mRepository = new Repository(application);
    }

    public LiveData<List<Profile>> getAllProfiles() {
        return mRepository.getAllProfiles();
    }

    public void updateProfile(Profile profile) {
        mRepository.updateTimerProfile(profile);
    }

    public LiveData<List<Profile>> getActiveProfiles(boolean isOn) {
        return mRepository.getActiveProfiles(isOn);
    }

    public void deleteProfile(int profileId) {
        mRepository.deleteProfile(profileId);
    }

    public void processTimePickerResult(int hourOfDay, int minute) {
        String hourString = Integer.toString(hourOfDay);
        String minuteString = Integer.toString(minute);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date stringFormatted = format.parse("1999-12-12 " + hourString + ":" + minuteString + ":00");
            mEndTime = stringFormatted.getTime() / 1000;

        } catch (ParseException e) {
            Log.d(TAG, "Exception when parsing time into formatted time");
            e.printStackTrace();
        }
    }

    public long getEndTime() {
        return mEndTime;
    }

}