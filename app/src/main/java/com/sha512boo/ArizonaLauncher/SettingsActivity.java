package com.sha512boo.ArizonaLauncher;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.sha512boo.ArizonaLauncher.utils.Tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SettingsActivity extends AppCompatActivity {
    SharedPreferences sp;
    static String savedLogin, savedSettings, savedFont;
    static int savedScale;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
/*
    public void setSettings(){
        File directorySetting = new File(getExternalCacheDir(), "settings.ini");
        File directorySettingTemp = new File(getExternalCacheDir(),"settingsTemp.ini");

        if (!directorySetting.exists()) {
            try {
                BufferedWriter setcreate = new BufferedWriter(new FileWriter(directorySetting));
                setcreate.write(Tools.set);
                setcreate.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedReader set = new BufferedReader(new FileReader(directorySetting));
            BufferedWriter settemp = new BufferedWriter(new FileWriter(directorySettingTemp));

            String line;
            while ((line = set.readLine()) != null){
                if (line.contains("=")){
                    String[] part = line.split("=");
                    String jojo = part[part.length-2];
                    if (jojo.equals("name ")){
                        line = jojo + "= " + savedLogin;
                    }
                    if (jojo.equals("Font ")){
                        line = jojo + "= "  + savedFont;
                    }
                    if(jojo.equals("GUI ")){
                        line = jojo + "= " + savedScale;
                    }
                    settemp.write(line + System.getProperty("line.separator"));
                }else {
                    settemp.write(line + System.getProperty("line.separator"));
                }
            }
            set.close();
            settemp.close();
            directorySetting.delete();
            directorySettingTemp.renameTo(directorySetting);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.pref, rootKey);

        }

    }
}