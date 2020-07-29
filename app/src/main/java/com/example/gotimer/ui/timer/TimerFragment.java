package com.example.gotimer.ui.timer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gotimer.R;
import com.example.gotimer.entity.Profile;
import com.example.gotimer.interfaces.OnSwitchChange;
import com.example.gotimer.services.AppMonitorService;

import java.util.List;

public class TimerFragment extends Fragment implements OnSwitchChange {

    private TimerViewModel timerViewModel;
    private Context mContext;
    private List<Profile> mProfileList;
    private boolean mServiceOn;

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
        final ProfilesListAdapter adapter = new ProfilesListAdapter(mContext, switchListenerInterface);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        recyclerView.setAdapter(adapter);

        timerViewModel.getAllProfiles().observe(getViewLifecycleOwner(), profiles -> {
            adapter.setProfiles(profiles);
        });
        
        starAppMonitorService();

        return root;
    }

    OnSwitchChange switchListenerInterface = new OnSwitchChange() {
        @Override
        public void onSwitchChange(List<Profile> updatedProfileList, int positionOfChanged) {
            mProfileList = updatedProfileList;
            for (int i = 0; i < mProfileList.size(); i++) {
                Profile current = mProfileList.get(i);
                if (i == positionOfChanged) {
                    current.setOn(true);
                } else {
                    current.setOn(false);
                }
                timerViewModel.updateProfile(current);
            }
        }
    };

    @Override
    public void onSwitchChange(List<Profile> updatedProfileList, int positionOfChanged) {

    }

    private void starAppMonitorService() {
        Intent intent = new Intent(getActivity(), AppMonitorService.class);
        intent.putExtra("serviceOn", mServiceOn);
        getActivity().startService(intent);
    }
}