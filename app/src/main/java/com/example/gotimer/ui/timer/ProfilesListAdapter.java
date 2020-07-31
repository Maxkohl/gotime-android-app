package com.example.gotimer.ui.timer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gotimer.R;
import com.example.gotimer.entity.Profile;
import com.example.gotimer.interfaces.OnDeleteClickListener;
import com.example.gotimer.interfaces.OnSwitchChange;

import org.w3c.dom.Text;

import java.util.List;

public class ProfilesListAdapter extends RecyclerView.Adapter<com.example.gotimer.ui.timer.ProfilesListAdapter.ProfilesViewHolder> {

    private LayoutInflater inflater;
    private List<Profile> mProfiles;
    private Context mContext;
    private int activePosition;
    private boolean profileChanged = false;

    private OnSwitchChange mSwitchListener;
    private OnDeleteClickListener mDeleteClickListener;

    public ProfilesListAdapter(Context context, OnSwitchChange switchListener,
                               OnDeleteClickListener deleteClickListener) {
        inflater = LayoutInflater.from(context);
        this.mContext = mContext;
        mSwitchListener = switchListener;
        mDeleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public ProfilesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = inflater.inflate(R.layout.recyclerview_item, parent, false);
        return new ProfilesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfilesViewHolder holder, int position) {
        if (mProfiles != null) {
            Profile current = mProfiles.get(position);
            holder.mProfileName.setText(current.getProfileName());
            holder.mStartTime.setText(current.getStartTimeString());
            holder.mEndTime.setText(current.getEndTimeString());
            holder.mDaysActive.setText(current.getDaysActive());
            holder.mBlockedApps.setText(current.getBlockedApps());
            holder.mIsActive.setChecked(current.isOn());
            //Using OnClickListener instead of onCheckedChangeListener so that infinite loop of
            // switch changing doesn't occur
            holder.mIsActive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSwitchListener.onSwitchChange(mProfiles, position);
                }
            });
            holder.isOn.setText("Active: " + current.isOn());
            holder.mBlockedProcesses.setText(current.getBlockedAppsProcessNames());
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDeleteClickListener.OnDeleteClickListener(current.getProfileId());
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if (mProfiles != null) {
            return mProfiles.size();
        }
        return 0;
    }

    public void setProfiles(List<Profile> profiles) {
        mProfiles = profiles;
        notifyDataSetChanged();
    }

    public class ProfilesViewHolder extends RecyclerView.ViewHolder {
        private final TextView mProfileName;
        private final TextView mStartTime;
        private final TextView mEndTime;
        private final TextView mDaysActive;
        private final TextView mBlockedApps;
        private final Switch mIsActive;
        private final TextView isOn;
        private final TextView mBlockedProcesses;
        private final ImageButton deleteButton;

        public ProfilesViewHolder(@NonNull View itemView) {
            super(itemView);
            mProfileName = itemView.findViewById(R.id.profileName);
            mStartTime = itemView.findViewById(R.id.profileStartTime);
            mEndTime = itemView.findViewById(R.id.profileEndTime);
            mDaysActive = itemView.findViewById(R.id.profileDaysActive);
            mBlockedApps = itemView.findViewById(R.id.blockedApps);
            mIsActive = itemView.findViewById(R.id.isActive);
            isOn = itemView.findViewById(R.id.isOnText);
            mBlockedProcesses = itemView.findViewById(R.id.processesBlocked);
            deleteButton = itemView.findViewById(R.id.delete_imagebutton);
        }
    }


    public boolean getProfileChanged() {
        return profileChanged;
    }

    public void setProfileChanged(boolean isChanged) {
        profileChanged = isChanged;
    }

    public int getPositionOfUpdatedProfile() {
        return activePosition;
    }

    public Profile getProfileAtPosition(int position) {
        return mProfiles.get(position);
    }

}
