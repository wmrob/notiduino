package com.example.doohans.notifytest26_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    public static final String APP_PACKAGE_NAME = "CHECK_PACKAGE";

    private RelativeLayout mRelativeLayout;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<AppInfo> installedApps;

    private AppsManager appManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean isPermissionAllowed = isNotiPermissionAllowed();

        if(!isPermissionAllowed) {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        }

        installedApps = new ArrayList<AppInfo>();
        mRecyclerView = (RecyclerView) findViewById(R.id.recycleView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        appManager = new AppsManager(this);
        installedApps = appManager.getApps();

        final SharedPreferences settings = getSharedPreferences(APP_PACKAGE_NAME, 0);

        if(settings != null) {
            for (AppInfo app : installedApps) {
                app.setSelected(settings.getBoolean(app.getAppPackage(), false));
            }
        }
        // Initialize a new adapter for RecyclerView
        mAdapter = new InstalledAppsAdapter(
                getApplicationContext(),
                installedApps,
                new InstalledAppsAdapter.OnItemClickListener(){

                    @Override
                    public void onItemClick(AppInfo item) {
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean(item.getAppPackage(),item.isSelected());
                        editor.commit();

                        Log.i("MainActivity", "[doohans] item:" + item.getAppPackage());
                        Log.i("MainActivity", "[doohans] item:" + item.isSelected());
                    }
                }
        );


        mRecyclerView.setAdapter(mAdapter);

    }


    private boolean isNotiPermissionAllowed() {
        Set<String> notiListenerSet = NotificationManagerCompat.getEnabledListenerPackages(this);
        String myPackageName = getPackageName();

        for(String packageName : notiListenerSet) {
            if(packageName == null) {
                continue;
            }
            if(packageName.equals(myPackageName)) {
                return true;
            }
        }

        return false;
    }

}
