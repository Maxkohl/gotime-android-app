package com.example.gotimer.ui.timer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gotimer.R;
import com.example.gotimer.entity.Profile;
import com.example.gotimer.interfaces.OnSwitchChange;
import com.example.gotimer.services.AppMonitorService;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class TimerFragment extends Fragment implements OnSwitchChange {

    private TimerViewModel timerViewModel;
    private Context mContext;
    private List<Profile> mProfileList;
    private boolean mServiceOn;
    private Profile activeProfile;

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

        RecyclerView recyclerView = root.findViewById(R.id.profilesRecycler);
        final ProfilesListAdapter adapter = new ProfilesListAdapter(mContext,
                switchListenerInterface);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        recyclerView.setAdapter(adapter);

        timerViewModel.getAllProfiles().observe(getViewLifecycleOwner(), profiles -> {
            adapter.setProfiles(profiles);
        });

        timerViewModel.getActiveProfiles(true).observe(getViewLifecycleOwner(),
                new Observer<List<Profile>>() {
            @Override
            public void onChanged(List<Profile> profileList) {
                if (profileList != null && profileList.size() >= 1) {
                    activeProfile = profileList.get(0);
                }
            }
        });

        if (activeProfile != null) {
            mServiceOn = true;
        } else {
            mServiceOn = false;
        }


        startAppMonitoringService(activeProfile);

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

    private void startAppMonitoringService(Profile activeProfile) {
        Intent intent = new Intent(getActivity(), AppMonitorService.class);
        ArrayList<String> processList = new ArrayList<>();
        if (activeProfile != null) {
            processList = new ArrayList<>(activeProfile.getBlockedProcessNames());
        }
        intent.putExtra("serviceOn", mServiceOn);
        intent.putStringArrayListExtra("processList", processList);
        getActivity().startService(intent);
    }
}