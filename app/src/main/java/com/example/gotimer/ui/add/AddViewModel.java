package com.example.gotimer.ui.add;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class AddViewModel extends AndroidViewModel {

    String timeString;


    public AddViewModel(@NonNull Application application) {
        super(application);
    }

    public void processTimePickerResult(int hourOfDay, int minute) {
        String hourString = Integer.toString(hourOfDay);
        String minuteString = Integer.toString(minute);

        timeString = hourString + ":" + minuteString;
    }

    public String getTimeString() {
        return timeString;
    }
}
