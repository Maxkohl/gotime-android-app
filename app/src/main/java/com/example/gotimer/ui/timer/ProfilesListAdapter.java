package com.example.gotimer.ui.timer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gotimer.R;
import com.example.gotimer.entity.Profile;

import org.w3c.dom.Text;

import java.util.List;

public class ProfilesListAdapter extends RecyclerView.Adapter<com.example.gotimer.ui.timer.ProfilesListAdapter.ProfilesViewHolder> {

    private LayoutInflater inflater;
    private List<Profile> mProfiles;
    private Context mContext;

    public ProfilesListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.mContext = mContext;
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

        public ProfilesViewHolder(@NonNull View itemView) {
            super(itemView);
            mProfileName = itemView.findViewById(R.id.profileName);
            mStartTime = itemView.findViewById(R.id.profileStartTime);
            mEndTime = itemView.findViewById(R.id.profileEndTime);
            mDaysActive = itemView.findViewById(R.id.profileDaysActive);
            mBlockedApps = itemView.findViewById(R.id.blockedApps);
        }
    }

    public Profile getProfileAtPosition(int position) {
        return mProfiles.get(position);
    }
}
