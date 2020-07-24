package com.example.gotimer.ui.appselector;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gotimer.R;
import com.example.gotimer.entity.Profile;

import org.w3c.dom.Text;

import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<com.example.gotimer.ui.appselector.AppListAdapter.AppsViewHolder> {

    private LayoutInflater inflater;
    private List<ApplicationInfo> mAppsInfo;
    private Context mContext;

    public AppListAdapter(Context mContext) {
        inflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public AppsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.apprecycler_item, parent, false);
        return new AppsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AppsViewHolder holder, int position) {
        if (mAppsInfo != null) {
            ApplicationInfo current = mAppsInfo.get(position);
            holder.mAppIcon.setImageResource(current.icon);
            holder.mAppName.setText(current.packageName);
        }

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class AppsViewHolder extends RecyclerView.ViewHolder {
        private ImageView mAppIcon;
        private TextView mAppName;
        private Switch mBlockAppSwitch;

        public AppsViewHolder(@NonNull View itemView) {
            super(itemView);
            mAppIcon = itemView.findViewById(R.id.appIcon);
            mAppName = itemView.findViewById(R.id.appName);
            mBlockAppSwitch = itemView.findViewById(R.id.appBlockSwitch);
        }
    }
}
