package com.example.gotimer.ui.timer;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gotimer.R;
import com.example.gotimer.entity.Profile;
import com.example.gotimer.interfaces.OnDeleteClickListener;
import com.example.gotimer.interfaces.OnSwitchChange;
import com.example.gotimer.services.OverlayService;
import com.example.gotimer.util.EndTimePickerFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class TimerFragment extends Fragment implements OnSwitchChange, OnDeleteClickListener {

    private TimerViewModel timerViewModel;
    private Context mContext;
    private List<Profile> mProfileList;
    private boolean mServiceOn;
    private Profile activeProfile;
    Intent serviceIntent;
    private Button quickBlockButton;
    private Button startTimerButton;
    private TextView timerCountdown;
    private Profile selectedQuickBlockProfile;
    private long mEndTime;
    private boolean isQuickBlockActive = false;
    private AlarmManager alarmManager;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timerViewModel = new ViewModelProvider(requireActivity()).get(TimerViewModel.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_timer, container, false);
        alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);

        RecyclerView recyclerView = root.findViewById(R.id.profilesRecycler);
        final ProfilesListAdapter adapter = new ProfilesListAdapter(mContext,
                switchListenerInterface, deleteClickInterface);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        recyclerView.setAdapter(adapter);

        timerCountdown = root.findViewById(R.id.timer_countdown);

        quickBlockButton = root.findViewById(R.id.quick_block_bttn);
        quickBlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createQuickBlock();
            }
        });

        startTimerButton = root.findViewById(R.id.start_timer_bttn);
        if (isQuickBlockActive) {
            startTimerButton.setText("STOP TIMER");
        } else {
            startTimerButton.setText("START");
        }
        startTimerButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (selectedQuickBlockProfile != null) {
                    if (isQuickBlockActive) {
                        isQuickBlockActive = false;
                    } else {
                        startQuickBlock();
                        isQuickBlockActive = true;
                        adapter.notifyDataSetChanged();
                    }
                }

            }
        });


        timerViewModel.getAllProfiles().observe(getViewLifecycleOwner(), profiles -> {
            mProfileList = profiles;
            adapter.setProfiles(profiles);
        });


        timerViewModel.getActiveAlarmProfiles(true).observe(getViewLifecycleOwner(), profiles -> {
            for (int i = 0; i < profiles.size(); i++) {
                Profile currentProfile = profiles.get(i);
                long startTime = currentProfile.getStartTime();
                int randomNum = new Random().nextInt(1000);
                if (currentProfile.getAlarmId() == 0) {
                    Intent intent = new Intent("StartAlarm");
                    intent.putExtra("profileId", currentProfile.getProfileId());
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(),
                            randomNum, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    currentProfile.setAlarmId(randomNum);
                    timerViewModel.updateProfile(currentProfile);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, startTime, pendingIntent);
                    Toast.makeText(mContext, "Alarm Set", Toast.LENGTH_SHORT).show();
                }
            }
        });

        timerViewModel.getActiveAlarmProfiles(false).observe(getViewLifecycleOwner(), profiles -> {
            for (int i = 0; i < profiles.size(); i++) {
                Profile currentProfile = profiles.get(i);
                int profileAlarmId = currentProfile.getAlarmId();
                if (currentProfile.getAlarmId() != 0) {
                    Intent intent = new Intent("StartAlarm");
                    PendingIntent cancelStartIntent = PendingIntent.getBroadcast(getActivity(),
                            profileAlarmId, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.cancel(cancelStartIntent);
                    currentProfile.setAlarmId(0);
                    timerViewModel.updateProfile(currentProfile);
                    Toast.makeText(mContext, "Alarm Turned Off", Toast.LENGTH_SHORT).show();
                }
            }
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
            Profile switchedProfile = updatedProfileList.get(positionOfChanged);
            switchedProfile.setAlarmActive(!switchedProfile.isAlarmActive());
            timerViewModel.updateProfile(switchedProfile);
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

    public void createQuickBlock() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose a profile");

        String[] profiles = new String[mProfileList.size()];
        for (int i = 0; i < profiles.length; i++) {
            profiles[i] = mProfileList.get(i).getProfileName();
        }

        int checkedItem = 0;
        final int[] selectedItem = {0};
        builder.setSingleChoiceItems(profiles, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedItem[0] = which;
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedQuickBlockProfile = mProfileList.get(selectedItem[0]);
                showTimePickerDialog();
            }
        });
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void showTimePickerDialog() {
        DialogFragment newFragment = new EndTimePickerFragment();
        newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startCountdown() {
        mEndTime = timerViewModel.getEndTime();
        long currentTime = System.currentTimeMillis();
        long durationTime = mEndTime - currentTime;
        new CountDownTimer(durationTime, 1000) {
            public void onTick(long millisUntilFinished) {
                timerCountdown.setText("Time Remaining: " + new SimpleDateFormat(
                        "HH:mm:ss").format(new Date(millisUntilFinished)));
                if (timerCountdown.getText().equals("00:00:00")) {
                    onFinish();
                }
            }

            public void onFinish() {
                timerCountdown.setText("done!");
            }
        }.start();
        Intent intent = new Intent("QuickBlockAlarm");
        intent.putExtra("profileId", selectedQuickBlockProfile.getProfileId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 9, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, mEndTime, pendingIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startQuickBlock() {
        for (Profile profile : mProfileList) {
            profile.setBlockActive(false);
            timerViewModel.updateProfile(profile);
        }
        selectedQuickBlockProfile.setBlockActive(true);
        timerViewModel.updateProfile(selectedQuickBlockProfile);
        startCountdown();

    }

    //Inner broadcast receiver class to change isActive boolean of profile when time starts/ends
    BroadcastReceiver startAlarmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Alarm Started", Toast.LENGTH_SHORT).show();
            int profileId = intent.getIntExtra("profileId", 0);
            for (Profile profile : mProfileList) {
                if (profile.getProfileId() == profileId) {
                    profile.setBlockActive(true);
                    timerViewModel.updateProfile(profile);
                }
            }
        }

    };

    BroadcastReceiver endAlarmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Block Alarm Ended", Toast.LENGTH_SHORT).show();
            int profileId = intent.getIntExtra("profileId", 0);
            for (Profile profile : mProfileList) {
                if (profile.getProfileId() == profileId) {
                    profile.setBlockActive(false);
                    timerViewModel.updateProfile(profile);
                }
            }
        }

    };

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter startFilter = new IntentFilter("StartAlarm");
        getContext().registerReceiver(startAlarmReceiver, startFilter);
        IntentFilter endFilter = new IntentFilter("EndAlarm");
        getContext().registerReceiver(endAlarmReceiver, endFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
//        getContext().unregisterReceiver(alarmReceiver);
    }
}