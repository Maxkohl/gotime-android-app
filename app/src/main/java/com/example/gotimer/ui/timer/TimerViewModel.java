package com.example.gotimer.ui.timer;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gotimer.Repository;
import com.example.gotimer.entity.Profile;

import java.util.List;

public class TimerViewModel extends AndroidViewModel {

    private Repository mRepository;


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

}