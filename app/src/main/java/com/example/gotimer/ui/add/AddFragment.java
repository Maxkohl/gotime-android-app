package com.example.gotimer.ui.add;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.dpro.widgets.OnWeekdaysChangeListener;
import com.dpro.widgets.WeekdaysPicker;
import com.example.gotimer.R;
import com.example.gotimer.entity.Profile;
import com.example.gotimer.ui.appselector.AppSelectorActivity;
import com.example.gotimer.ui.timer.TimerFragment;

import org.w3c.dom.Text;

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

        return root;
    }

    private void launchAppSelectorActivity() {
        Intent intent = new Intent(getContext(), AppSelectorActivity.class);
        startActivityForResult(intent, 0);
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void createTimerProfile() {
        mProfileName = mNameEditText.getText().toString();
        mStartTime = addViewModel.getTime();
        Profile newProfile = new Profile(mProfileName, mStartTime, mStartTime);
        mSelectedDays = mDayPicker.getSelectedDaysText();
        if (mSelectedDays != null) {
            newProfile.setDaysActiveString(mSelectedDays);
        }
        addViewModel.insertNewTimerProfile(newProfile);
    }


}