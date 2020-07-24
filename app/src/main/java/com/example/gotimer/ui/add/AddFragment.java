package com.example.gotimer.ui.add;

import android.app.Dialog;
import android.content.Context;
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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.example.gotimer.R;
import com.example.gotimer.entity.Profile;
import com.example.gotimer.ui.timer.TimerFragment;

import org.w3c.dom.Text;

public class AddFragment extends DialogFragment {

    private AddViewModel addViewModel;

    private Context mContext;
    private EditText mNameEditText;
    private String mProfileName;
    private Button mStartButton;
    private long mStartTime;
    private Button mEndButton;
    private long mEndTime;
    private Button mSaveButton;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addViewModel =
                ViewModelProviders.of(this).get(AddViewModel.class);
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
            @Override
            public void onClick(View view) {
                createTimerProfile();
            }
        });

        return root;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
    }

    public void createTimerProfile() {
        mProfileName = mNameEditText.getText().toString();
        mStartTime = addViewModel.getTime();
        Profile newProfile = new Profile("Test Name", 99999, 999999);
        addViewModel.insertNewTimerProfile(newProfile);

    }


}