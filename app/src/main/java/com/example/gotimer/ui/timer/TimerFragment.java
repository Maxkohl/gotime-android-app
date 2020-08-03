package com.example.gotimer.ui.timer;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gotimer.R;
import com.example.gotimer.entity.Profile;
import com.example.gotimer.interfaces.OnDeleteClickListener;
import com.example.gotimer.interfaces.OnSwitchChange;
import com.example.gotimer.services.OverlayService;

import java.util.ArrayList;
import java.util.List;

public class TimerFragment extends Fragment implements OnSwitchChange, OnDeleteClickListener {

    private TimerViewModel timerViewModel;
    private Context mContext;
    private List<Profile> mProfileList;
    private boolean mServiceOn;
    private Profile activeProfile;
    Intent serviceIntent;
    private Button quickBlockButton;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        timerViewModel =
                ViewModelProviders.of(this).get(TimerViewModel.class);
        View root = inflater.inflate(R.layout.fragment_timer, container, false);

        quickBlockButton = root.findViewById(R.id.quick_block_bttn);
        quickBlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quickBlock();
            }
        });

        RecyclerView recyclerView = root.findViewById(R.id.profilesRecycler);
        final ProfilesListAdapter adapter = new ProfilesListAdapter(mContext,
                switchListenerInterface, deleteClickInterface);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        recyclerView.setAdapter(adapter);

        timerViewModel.getAllProfiles().observe(getViewLifecycleOwner(), profiles -> {
            mProfileList = profiles;
            adapter.setProfiles(profiles);
        });

        timerViewModel.getActiveProfiles(true).observe(getViewLifecycleOwner(),
                new Observer<List<Profile>>() {
                    @Override
                    public void onChanged(List<Profile> profileList) {
                        if (profileList != null && profileList.size() == 1) {
                            //This is happening twice creating 2 services and I don't know why
                            if (!isMyServiceRunning(OverlayService.class)) {
                                activeProfile = profileList.get(0);
                                mServiceOn = true;
                                toggleAppMonitoringService(mServiceOn);
                            }
                        } else {
                            if (isMyServiceRunning(OverlayService.class)) {
                                mServiceOn = false;
                                toggleAppMonitoringService(mServiceOn);
                                getActivity().stopService(serviceIntent);
                            }
                        }
                    }
                });

        //Calling this outside of recyclerview thread because if it's in recycler view error occurs
        adapter.notifyDataSetChanged();

        return root;
    }

    OnSwitchChange switchListenerInterface = new OnSwitchChange() {
        @Override
        public void onSwitchChange(List<Profile> updatedProfileList, int positionOfChanged) {
            mProfileList = updatedProfileList;
            //CREATING INFINITE LOOP OF CHANGING SWITCH AND TRIGGERING CALLBACK
            for (int i = 0; i < mProfileList.size(); i++) {
                Profile current = mProfileList.get(i);
                if (i == positionOfChanged && !current.isOn()) {
                    current.setOn(true);
                } else if (i == positionOfChanged && current.isOn()) {
                    current.setOn(false);
                } else if (i != positionOfChanged) {
                    current.setOn(false);
                }
                timerViewModel.updateProfile(current);
            }
        }
    };

    @Override
    public void onSwitchChange(List<Profile> updatedProfileList, int positionOfChanged) {

    }

    private void toggleAppMonitoringService(boolean mServiceOn) {
        serviceIntent = new Intent(getActivity(), OverlayService.class);
        if (mServiceOn) {
            ArrayList<String> processList = new ArrayList<>();
            if (activeProfile != null) {
                processList = new ArrayList<>(activeProfile.getBlockedProcessNames());
            }
            serviceIntent.putStringArrayListExtra("processList", processList);
        }
        serviceIntent.putExtra("serviceOn", mServiceOn);
        getActivity().startService(serviceIntent);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager =
                (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service :
                manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    OnDeleteClickListener deleteClickInterface = new OnDeleteClickListener() {
        @Override
        public void OnDeleteClickListener(int profileId) {
            timerViewModel.deleteProfile(profileId);
        }
    };

    @Override
    public void OnDeleteClickListener(int profileId) {
    }

    private void quickBlock() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose a profile");

        String[] profiles = new String[mProfileList.size()];
        for (int i = 0; i < profiles.length; i++) {
            profiles[i] = mProfileList.get(i).getProfileName();
        }

        int checkedItem = 0;
        builder.setSingleChoiceItems(profiles, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user checked an item
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}