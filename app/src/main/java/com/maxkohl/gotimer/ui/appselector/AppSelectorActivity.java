package com.maxkohl.gotimer.ui.appselector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.maxkohl.gotimer.R;
import com.maxkohl.gotimer.entity.Application;

import java.util.ArrayList;
import java.util.List;

public class AppSelectorActivity extends AppCompatActivity {

    private List<Application> allUserApps = new ArrayList<>();
    private RecyclerView appRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_selector);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        allUserApps = getUserApps();

        appRecycler = findViewById(R.id.applicationRecycler);
        final AppListAdapter adapter = new AppListAdapter(getApplicationContext());
        adapter.setApps(allUserApps);
        appRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        appRecycler.setAdapter(adapter);
    }

    public List<Application> getUserApps() {
        final PackageManager pm = getPackageManager();
        List<Application> resultList =  new ArrayList<>();
        //get a list of installed apps.
        //TODO Change this back to ApplicationInfo to remove a step and cleaner code?
        List<PackageInfo> allApps = pm.getInstalledPackages(PackageManager.GET_META_DATA);
        //        return pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (PackageInfo app : allApps) {
            if (!isSystemPackage(app) && !app.applicationInfo.processName.equals("com.example.gotimer")) {
                String appName = app.applicationInfo.loadLabel(getPackageManager()).toString();
                Drawable appIconRes = app.applicationInfo.loadIcon(getPackageManager());
                String processName = app.applicationInfo.processName;
                resultList.add(new Application(appName, appIconRes, processName));
            }
        }
        return resultList;
    }

    private boolean isSystemPackage(PackageInfo packageInfo) {
        //TODO This is only showing one app on emulator for some reason. Works on real phone
        return ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.saveapp_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            ArrayList<String> appList = getAllSelectedApps();
            ArrayList<String> processList = getAllSelectedProcesses();
            Intent intent = new Intent();
            intent.putStringArrayListExtra("appList", appList);
            intent.putStringArrayListExtra("processList", processList);
            setResult(RESULT_OK, intent);
            finish();

        }
        return super.onOptionsItemSelected(item);
    }

    public ArrayList<String> getAllSelectedApps() {
        ArrayList<String> resultList = new ArrayList<>();
        for (Application app : allUserApps) {
            if (app.isSelected()) {
                resultList.add(app.getAppName());
            }
        }
        return resultList;
    }

    public ArrayList<String> getAllSelectedProcesses() {
        ArrayList<String> resultList = new ArrayList<>();
        for (Application app : allUserApps) {
            if (app.isSelected()) {
                resultList.add(app.getProcessName());
            }
        }
        return resultList;
    }
}