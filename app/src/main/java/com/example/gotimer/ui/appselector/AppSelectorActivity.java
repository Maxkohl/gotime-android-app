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

    private List<ApplicationInfo> allAppsList;
    private List<ApplicationInfo> userApps;
    private RecyclerView appRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_selector);

        allAppsList = getAllApps();
        userApps = getUserApps(allAppsList);

        appRecycler = findViewById(R.id.applicationRecycler);
        final AppListAdapter adapter = new AppListAdapter(getApplicationContext());
        adapter.setApps(userApps);
        appRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        appRecycler.setAdapter(adapter);
    }

    public List<ApplicationInfo> getAllApps() {
        final PackageManager pm = getPackageManager();
        //get a list of installed apps.
        return pm.getInstalledApplications(PackageManager.GET_META_DATA);
    }

    public List<ApplicationInfo> getUserApps(List<ApplicationInfo> allApps) {
        List<ApplicationInfo> resultList =  new ArrayList<>();
        for (ApplicationInfo app : allApps) {
            if (!isSystemPackage(app)) {
                resultList.add(app);
            }
        }
        return resultList;
    }

    private boolean isSystemPackage(ApplicationInfo appInfo) {
        return ((appInfo.flags & appInfo.FLAG_SYSTEM) != 0);
    }
}