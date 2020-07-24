package com.example.gotimer.ui.appselector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.example.gotimer.R;
import com.example.gotimer.ui.timer.ProfilesListAdapter;

import java.util.ArrayList;
import java.util.List;

public class AppSelectorActivity extends AppCompatActivity {

    private List<PackageInfo> allUserApps;
    private RecyclerView appRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_selector);

        allUserApps = getUserApps();

        appRecycler = findViewById(R.id.applicationRecycler);
        final AppListAdapter adapter = new AppListAdapter(getApplicationContext());
        adapter.setApps(allUserApps);
        appRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        appRecycler.setAdapter(adapter);
    }

    public List<PackageInfo> getUserApps() {
        final PackageManager pm = getPackageManager();
        List<PackageInfo> resultList =  new ArrayList<>();
        //get a list of installed apps.
        //TODO Change this back to ApplicationInfo to remove a step and cleaner code?
        List<PackageInfo> allApps = pm.getInstalledPackages(PackageManager.GET_META_DATA);
        //        return pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (PackageInfo app : allApps) {
            if (!isSystemPackage(app)) {
                resultList.add(app);
            }
        }
        return resultList;
    }

    private boolean isSystemPackage(PackageInfo packageInfo) {
        //TODO This is only showing one app for some reason
        return ((packageInfo.applicationInfo.flags & packageInfo.applicationInfo.FLAG_SYSTEM) != 0);
    }
}