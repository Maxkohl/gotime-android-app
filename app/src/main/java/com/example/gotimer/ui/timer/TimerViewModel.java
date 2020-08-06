package com.example.gotimer.ui.timer;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gotimer.Repository;
import com.example.gotimer.entity.Profile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

public class TimerViewModel extends AndroidViewModel {

    private Repository mRepository;
    private static final String TAG = TimerViewModel.class.getSimpleName();
    private long mEndTime;


    public TimerViewModel(@NonNull Application application) {
        super(application);
        mRepository = new Repository(application);
    }

    public LiveData<List<Profile>> getAllProfiles() {
        return mRepository.getAllProfiles();
    }

    public LiveData<List<Profile>> getActiveAlarmProfiles(boolean isAlarmActive) {
        return mRepository.getActiveAlarmProfiles(isAlarmActive);
    }

    public void updateProfile(Profile profile) {
        mRepository.updateTimerProfile(profile);
    }

    public LiveData<List<Profile>> getActiveProfiles(boolean isBlockActive) {
        return mRepository.getActiveProfiles(isBlockActive);
    }

    public void deleteProfile(int profileId) {
        mRepository.deleteProfile(profileId);
    }


    public void processTimePickerResult(long receivedEndTime) {
        mEndTime= receivedEndTime;
    }

    public long getEndTime() {
        return mEndTime;
    }

}