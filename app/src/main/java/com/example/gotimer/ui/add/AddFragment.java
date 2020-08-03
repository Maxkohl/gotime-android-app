package com.example.gotimer.ui.add;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.dpro.widgets.OnWeekdaysChangeListener;
import com.dpro.widgets.WeekdaysPicker;
import com.example.gotimer.MainActivity;
import com.example.gotimer.R;
import com.example.gotimer.entity.Profile;
import com.example.gotimer.ui.appselector.AppSelectorActivity;
import com.example.gotimer.util.TimePickerFragment;

import java.util.List;

public class AddFragment extends Fragment {

    private AddViewModel addViewModel;

    private Context mContext;
    private EditText mNameEditText;
    private String mProfileName;
    private Button mStartButton;
    private long mStartTime;
    private Button mEndButton;
    private long mEndTime;
    private Button mSaveButton;
    private WeekdaysPicker mDayPicker;
    private List<String> mSelectedDays;
    private Button mSelectAppsButton;
    private Button mSelectLocationButton;
    boolean quickBlockStarted = false;

    private List<String> mAppList;
    private List<String> mProcessList;
    private static final int APP_REQUEST = 0;

    private List<Profile> mProfileList;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addViewModel = new ViewModelProvider(requireActivity()).get(AddViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_add, container, false);
        mNameEditText = root.findViewById(R.id.profilename_input);

        mStartButton = root.findViewById(R.id.starttime_button);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(view);
            }
        });

        mEndButton = root.findViewById(R.id.endtime_button);
        mEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(view);
            }
        });

        mSaveButton = root.findViewById(R.id.save_profile_button);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                createTimerProfile();
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        mDayPicker = root.findViewById(R.id.weekdays);
        mDayPicker.setOnWeekdaysChangeListener(new OnWeekdaysChangeListener() {
            @Override
            public void onChange(View view, int clickedDayOfWeek, List<Integer> selectedDays) {

            }
        });
        mSelectAppsButton = root.findViewById(R.id.selectapps_button);
        mSelectAppsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchAppSelectorActivity();
            }
        });
        mSelectLocationButton = root.findViewById(R.id.location_button);
        mSelectLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });

        addViewModel.getAllProfiles().observe(getViewLifecycleOwner(), profiles -> {
            mProfileList = profiles;
        });


        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getArguments() != null) {
            mAppList = getArguments().getStringArrayList("appList");
            mProcessList = getArguments().getStringArrayList("processList");
        }
    }

    private void launchAppSelectorActivity() {
        Intent intent = new Intent(getContext(), AppSelectorActivity.class);
        startActivityForResult(intent, APP_REQUEST);
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void createTimerProfile() {
        mProfileName = mNameEditText.getText().toString();
        mStartTime = addViewModel.getStartTime();
        mEndTime = addViewModel.getEndTime();
        Profile newProfile = new Profile(mProfileName, mStartTime, mEndTime);
        mSelectedDays = mDayPicker.getSelectedDaysText();
        if (mSelectedDays != null) {
            newProfile.setDaysActiveString(mSelectedDays);
        }
        if (mAppList != null) {
            newProfile.setBlockedAppsString(mAppList);
        }
        if (mProcessList != null) {
            newProfile.setBlockedProcessNamesString(mProcessList);
        }
        addViewModel.insertNewTimerProfile(newProfile);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                mAppList = data.getStringArrayListExtra("appList");
                mProcessList = data.getStringArrayListExtra("processList");
            }
        }
    }


}