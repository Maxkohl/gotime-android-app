package com.example.gotimer.ui.timer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gotimer.entity.Profile;

import java.util.List;

public class ProfilesListAdapter extends RecyclerView.Adapter<com.example.gotimer.ui.timer.ProfilesListAdapter.ProfilesViewHolder> {

    private LayoutInflater inflater;
    private List<Profile> mProfiles;
    private Context mContext;

    public ProfilesListAdapter(Context context) {
        inflater =LayoutInflater.from(context);
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ProfilesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = inflater.inflate();
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ProfilesViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ProfilesViewHolder extends RecyclerView.ViewHolder {
    }
}
