package com.example.gotimer.ui.timer;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

import com.example.gotimer.MainActivity;
import com.example.gotimer.R;
import com.example.gotimer.entity.Profile;
import com.example.gotimer.interfaces.OnDeleteClickListener;
import com.example.gotimer.interfaces.OnSwitchChange;
import com.example.gotimer.services.CountdownTimerService;
import com.example.gotimer.services.CountdownTimerWidget;
import com.example.gotimer.services.OverlayService;
import com.example.gotimer.util.EndTimePickerFragment;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

public class TimerFragment extends Fragment implements OnSwitchChange, OnDeleteClickListener {

    private TimerViewModel timerViewModel;
    private Context mContext;
    private List<Profile> mProfileList;
    private boolean mServiceOn;
    private Profile activeProfile;
    private Intent serviceIntent;
    private Intent countdownIntent;
    private ImageView quickBlockButton;
    private Button startTimerButton;
    private TextView timerCountdown;
    private Profile selectedQuickBlockProfile;
    private long mEndTime;
    private boolean isQuickBlockActive;
    private AlarmManager alarmManager;
    private SharedPreferences prefs;
    private String sharedPrefsFile = "come.example.gotimer.ui.timefragment";
    private static final String COUNTDOWN_BR = "com.example.gotimer.services.countdowntimerservice";
    private static final String UPDATE_WIDGET = "com.example.gotimer.services.countdowntimerwidget";

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timerViewModel = new ViewModelProvider(requireActivity()).get(TimerViewModel.class);
        prefs = getActivity().getSharedPreferences(
                sharedPrefsFile, Context.MODE_PRIVATE);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_timer, container, false);
        isQuickBlockActive = prefs.getBoolean("quickBlockKey", false);

        Gson gson = new Gson();
        String json = prefs.getString("selectedQuickBlockProfile", "");
        selectedQuickBlockProfile = gson.fromJson(json, Profile.class);
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
            startTimerButton.setText("STOP");
        } else {
            startTimerButton.setText("START");
        }
        startTimerButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (selectedQuickBlockProfile != null) {
                    if (isQuickBlockActive) {
                        deactivateAllProfiles();
                        isQuickBlockActive = false;
                        startTimerButton.setText("START");
                        if (isMyServiceRunning(OverlayService.class) && serviceIntent != null) {
                            getActivity().stopService(serviceIntent);
                        }
                        countdownIntent = new Intent(getActivity(), CountdownTimerService.class);
                        getActivity().stopService(countdownIntent);
                        timerCountdown.setText("Click to Quick Block");
                        toggleAppMonitoringService(false);
                        adapter.notifyDataSetChanged();
                    } else {
                        startQuickBlock();
                        isQuickBlockActive = true;
                        startTimerButton.setText("STOP");
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(getContext(), "Please Choose Profile", Toast.LENGTH_SHORT).show();
                }
            }

        });


        timerViewModel.getAllProfiles().observe(getViewLifecycleOwner(), profiles -> {
            mProfileList = profiles;
            adapter.setProfiles(profiles);
        });

        //Gets all profiles with isBlockActive boolean TRUE
        timerViewModel.getActiveProfiles(true).observe(getViewLifecycleOwner(),
                new Observer<List<Profile>>() {
                    @Override
                    public void onChanged(List<Profile> profileList) {
                        boolean isOverlayServiceRunning = isMyServiceRunning(OverlayService.class);
                        if (profileList != null && profileList.size() == 1) {
                            if (!isOverlayServiceRunning) {
                                activeProfile = profileList.get(0);
                                toggleAppMonitoringService(true);
                            }
                        } else {
                            if (isOverlayServiceRunning) {
                                isQuickBlockActive = false;
                                deactivateAllProfiles();
                                toggleAppMonitoringService(false);
                                getActivity().stopService(serviceIntent);
                            } else {
                                isQuickBlockActive = false;
                            }
                        }
                    }
                });


        timerViewModel.getActiveAlarmProfiles(true).observe(getViewLifecycleOwner(), profiles -> {
            for (int i = 0; i < profiles.size(); i++) {
                Profile currentProfile = profiles.get(i);
                long startTime = currentProfile.getStartTime();
                long endTime = currentProfile.getEndTime();
                int randomNum = new Random().nextInt(1000);
                if (currentProfile.getAlarmId() == 0) {
                    currentProfile.setAlarmId(randomNum);
                    timerViewModel.updateProfile(currentProfile);
                    Intent intent = new Intent("StartAlarm");
                    intent.putExtra("profileId", currentProfile.getProfileId());
                    PendingIntent pendingStartIntent = PendingIntent.getBroadcast(getActivity(),
                            randomNum, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    Intent endIntent = new Intent("EndAlarm");
                    intent.putExtra("profileId", currentProfile.getProfileId());
                    PendingIntent pendingEndIntent = PendingIntent.getBroadcast(getActivity(),
                            randomNum, endIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, startTime, pendingStartIntent);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, endTime, pendingEndIntent);
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
                    mServiceOn = false;
                    toggleAppMonitoringService(mServiceOn);
                    getActivity().stopService(serviceIntent);
                    timerViewModel.updateProfile(currentProfile);
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
            if (switchedProfile.isBlockActive()) {
                switchedProfile.setBlockActive(false);
            }
            timerViewModel.updateProfile(switchedProfile);
            mServiceOn = !mServiceOn;
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
        if (mServiceOn) {
            getActivity().startService(serviceIntent);
        } else {
            deactivateAllProfiles();
            getActivity().stopService(serviceIntent);
        }
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
            androidx.appcompat.app.AlertDialog.Builder builder =
                    new androidx.appcompat.app.AlertDialog.Builder(getContext());
            builder.setTitle(R.string.delete_profile_dialog).setMessage(R.string.delete_profile_prompt);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    timerViewModel.deleteProfile(profileId);
                    isQuickBlockActive = false;
                    startActivity(new Intent(getContext(), MainActivity.class));
                    Toast.makeText(getContext(),
                            R.string.deleted_toast
                            , Toast.LENGTH_LONG).show();
                }
            });
            builder.setNegativeButton(R.string.cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(getContext(), R.string.delete_canceled_toast,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
            builder.show();
        }
    };

    @Override
    public void OnDeleteClickListener(int profileId) {
    }

    public void createQuickBlock() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.choose_profile_dialog);

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

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedQuickBlockProfile = mProfileList.get(selectedItem[0]);
                showTimePickerDialog();
            }
        });
        builder.setNegativeButton(R.string.cancel, null);

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void showTimePickerDialog() {
        DialogFragment newFragment = new EndTimePickerFragment();
        newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startQuickBlock() {
        deactivateAllProfiles();
        selectedQuickBlockProfile.setBlockActive(true);
        timerViewModel.updateProfile(selectedQuickBlockProfile);
        startCountdown();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startCountdown() {
        mEndTime = timerViewModel.getEndTime();
        long currentTime = System.currentTimeMillis();
        long durationTime = mEndTime - currentTime;

        countdownIntent = new Intent(getActivity(), CountdownTimerService.class);
        countdownIntent.putExtra("durationTime", durationTime);
        getActivity().startService(countdownIntent);
        Intent intent = new Intent("EndAlarm");
        intent.putExtra("profileId", selectedQuickBlockProfile.getProfileId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 9, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, mEndTime, pendingIntent);
    }

    //Inner broadcast receiver class to change isActive boolean of profile when time starts/ends
    BroadcastReceiver startAlarmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int profileId = intent.getIntExtra("profileId", 0);
            deactivateAllProfiles();
            for (Profile profile : mProfileList) {
                if (profile.getProfileId() == profileId) {
                    profile.setBlockActive(true);
                    activeProfile = profile;
                    timerViewModel.updateProfile(profile);
                }
            }
            startActivity(new Intent(getContext(), MainActivity.class));

        }

    };

    BroadcastReceiver endAlarmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int profileId = intent.getIntExtra("profileId", 0);
            toggleAppMonitoringService(false);
            deactivateAllProfiles();
        }

    };

    BroadcastReceiver countdownServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long millisUntilFinished = intent.getLongExtra("countdown", 0);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String formattedString = sdf.format(new Date(millisUntilFinished));
            timerCountdown.setText(formattedString);
            updateWidget(context, millisUntilFinished);
        }
    };

    public void updateWidget(Context context, long millisUntilFinished) {
        Intent intent = new Intent(context.getApplicationContext(), CountdownTimerWidget.class);
        intent.setAction(UPDATE_WIDGET);
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        int[] ids = widgetManager.getAppWidgetIds(new ComponentName(context,
                CountdownTimerWidget.class));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            widgetManager.notifyAppWidgetViewDataChanged(ids, android.R.id.list);

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        intent.putExtra("countdown", millisUntilFinished);
        context.sendBroadcast(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter startFilter = new IntentFilter("StartAlarm");
        getContext().registerReceiver(startAlarmReceiver, startFilter);
        IntentFilter endFilter = new IntentFilter("EndAlarm");
        getContext().registerReceiver(endAlarmReceiver, endFilter);
        getContext().registerReceiver(countdownServiceReceiver, new IntentFilter(COUNTDOWN_BR));
        if (prefs != null) {
            isQuickBlockActive = prefs.getBoolean("quickBlockKey", false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean("quickBlockKey", isQuickBlockActive);
        Gson gson = new Gson();
        String json = gson.toJson(selectedQuickBlockProfile);
        prefsEditor.putString("selectedQuickBlockProfile", json);
        prefsEditor.commit();
        prefsEditor.apply();
    }

    private void deactivateAllProfiles() {
        for (Profile profile : mProfileList) {
            profile.setBlockActive(false);
            timerViewModel.updateProfile(profile);
        }
    }
}