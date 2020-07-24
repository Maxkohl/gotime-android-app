package com.example.gotimer.ui.appselector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.example.gotimer.R;
import com.example.gotimer.ui.timer.ProfilesListAdapter;

import java.util.List;

public class AppSelectorActivity extends AppCompatActivity {

    private List<ApplicationInfo> appList;
    private RecyclerView appRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_selector);

        appList = getAllApps();

        appRecycler = findViewById(R.id.applicationRecycler);
        final AppListAdapter adapter = new AppListAdapter(getApplicationContext());
        adapter.setApps(appList);
        appRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        appRecycler.setAdapter(adapter);
    }

    public List<ApplicationInfo> getAllApps() {
        final PackageManager pm = getPackageManager();
        //get a list of installed apps.
        return pm.getInstalledApplications(PackageManager.GET_META_DATA);
    }
}