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
    private long mStartTime;
    private long mEndTime;
    private static final String TAG = AddViewModel.class.getSimpleName();
    private Repository mRepository;


    public AddViewModel(@NonNull Application application) {
        super(application);
        mRepository = new Repository(application);
    }


    public void processTimePickerResult(long receivedTime) {
        if (mStartTime == 0) {
                mStartTime = receivedTime;
            } else {
                mEndTime = receivedTime;
            }
    }

    public long getStartTime() {
        return mStartTime;
    }

    public long getEndTime() {
        return mEndTime;
    }

    public void insertNewTimerProfile(Profile newProfile) {
        mRepository.insertNewTimerProfile(newProfile);
    }

    public LiveData<List<Profile>> getAllProfiles() {
        return mRepository.getAllProfiles();
    }

    public void updateProfile(Profile profile) {mRepository.updateTimerProfile(profile);}

}
