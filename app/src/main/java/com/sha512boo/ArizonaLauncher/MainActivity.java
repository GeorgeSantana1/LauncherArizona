package com.sha512boo.ArizonaLauncher;

import android.app.DownloadManager;

import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.RequiresApi;

import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;


import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.sha512boo.ArizonaLauncher.fragment.FavoriteServersFragment;
import com.sha512boo.ArizonaLauncher.fragment.HostedServersFragment;
import com.sha512boo.ArizonaLauncher.fragment.OfficialServersFragment;
import com.sha512boo.ArizonaLauncher.utils.Tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener,
        ServersFragment.OnFragmentInteractionListener,
        ToolsFragment.OnFragmentInteractionListener,
        FavoriteServersFragment.OnFragmentInteractionListener,
        OfficialServersFragment.OnFragmentInteractionListener,
        HostedServersFragment.OnFragmentInteractionListener {
    Uri uri;
    File game, cache_game;
    AlertDialog.Builder alertDialog;
    NavController navController;
    Context context;

    View brd0, brd1, brd2;

    File sampfile;
    File settingfile;
    File fontfile;
    ProgressBar mProgressBar;
    String[] arrayServers;
    String utf8text;
    static DownloadManager downloadManager;
    SharedPreferences sharedPreferences;
    final String LOG_TAG = getClass().getSimpleName();
    private TabLayout tabLayout;
    private ViewPager viewPager;
    LayoutInflater inflater;
    View serversView;
    static NetworkInfo.State mobile;

    public static NetworkInfo.State getMobileState() {
        return mobile;
    }

    public static DownloadManager getDownloadManager() {
        return downloadManager;

    }

    public void InstallFile(String fileuri, String filedirectory, String filename) {

        Uri Download_Uri = Uri.parse(fileuri);

        DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(false);
        request.setTitle(filename);
        request.setDescription(filename);
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir(filedirectory, filename);
        final long refid = downloadManager.enqueue(request);
    }

    private BottomNavigationViewEx.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationViewEx.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    navController.navigate(R.id.homeFragment);
                    brd0.setVisibility(View.VISIBLE);
                    brd1.setVisibility(View.INVISIBLE);
                    brd2.setVisibility(View.INVISIBLE);
                    return true;
                case R.id.navigation_dashboard:
                    navController.navigate(R.id.serversFragment);
                    brd0.setVisibility(View.INVISIBLE);
                    brd1.setVisibility(View.VISIBLE);
                    brd2.setVisibility(View.INVISIBLE);
                    return true;
                case R.id.navigation_notifications:
                    navController.navigate(R.id.toolsFragment);
                    brd0.setVisibility(View.INVISIBLE);
                    brd1.setVisibility(View.INVISIBLE);
                    brd2.setVisibility(View.VISIBLE);
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationViewEx navView = (BottomNavigationViewEx) findViewById(R.id.nav_view);
        navView.enableAnimation(false);
        navView.enableShiftingMode(false);
        navView.enableItemShiftingMode(false);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mobile = conMan.getNetworkInfo(0).getState();
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        alertDialog = new Builder(this);
        generalData gd = new generalData();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        DataViewModel model = ViewModelProviders.of(this).get(DataViewModel.class);

        sampfile = new File(this.getExternalCacheDir(), "samp_favorite.txt");
        settingfile = new File(this.getExternalCacheDir(), "settings.ini");
        fontfile = new File(this.getExternalCacheDir(), "fonts/arial.ttf");

        brd0 = (View) findViewById(R.id.brdItm0);
        brd1 = (View) findViewById(R.id.brdItm1);
        brd2 = (View) findViewById(R.id.brdItm2);

//        inflater = getLayoutInflater();
//        serversView = inflater.inflate(R.layout.layout_home, null, false);
//        test = serversView.findViewById(R.id.titleDescription);


//        cache_game = new File(Environment.getExternalStorageDirectory().getPath()+"/data/сom.x4soft.sampclient" );
//        if (!cache_game.exists()) {
//            alertDialog.setTitle("Error");
//            alertDialog.setMessage("сom.x4soft.sampclient didn't find");
//            AlertDialog alert = alertDialog.create();
//            alert.show();
//        }

        Tools.needAppDialog(MainActivity.this);
        loadFile();
        loadTools();


    }

    @Override
    public void onBackPressed() {
        doExitApp();
    }


    public void loadTools() {


    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    private long exitTime = 0;

    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, R.string.press_again, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    public void loadFile() {
        if (!sampfile.exists()) {
            try {
                sampfile.createNewFile();
                BufferedWriter fav = new BufferedWriter(new FileWriter(sampfile, true));
                fav.write(Tools.fav);
                fav.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!settingfile.exists()) {
            try {
                settingfile.createNewFile();
                BufferedWriter set = new BufferedWriter(new FileWriter(settingfile, true));
                set.write(Tools.set);
                set.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!fontfile.exists()) {
            Tools.downloadFile(this, "http://vhost34882.cpsite.ru/Arial.ttf");
            sharedPreferences = this.getSharedPreferences("settings", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("font", "arial.ttf");
            editor.apply();
        }
    }

}
