package com.example.gotimer.interfaces;

import com.example.gotimer.entity.Profile;

import java.util.List;

public interface OnSwitchChange {
    void onSwitchChange(List<Profile> updatedProfileList, int positionOfChanged);
}
