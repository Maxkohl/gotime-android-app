package com.maxkohl.gotimer.interfaces;

import com.maxkohl.gotimer.entity.Profile;

import java.util.List;

public interface OnSwitchChange {
    void onSwitchChange(List<Profile> updatedProfileList, int positionOfChanged);
}
