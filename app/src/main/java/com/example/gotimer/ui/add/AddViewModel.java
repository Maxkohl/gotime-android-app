package com.example.gotimer.ui.add;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.gotimer.R;
import com.example.gotimer.Repository;
import com.example.gotimer.entity.Profile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddViewModel extends AndroidViewModel {

    long mTime;
    private static final String TAG = AddViewModel.class.getSimpleName();
    private Repository mReposity;


    public AddViewModel(@NonNull Application application) {
        super(application);
        mReposity = new Repository(application);
    }

    public void processTimePickerResult(int hourOfDay, int minute) {
        String hourString = Integer.toString(hourOfDay);
        String minuteString = Integer.toString(minute);

        SimpleDateFormat format = new SimpleDateFormat("HH:MM");
        try {
            Date stringFormatted = format.parse(hourString + ":" + minuteString);
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
        mReposity.insertNewTimerProfile(newProfile);
    }

}
