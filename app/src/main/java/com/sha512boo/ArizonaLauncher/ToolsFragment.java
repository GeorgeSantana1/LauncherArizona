package com.sha512boo.ArizonaLauncher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sha512boo.ArizonaLauncher.utils.Tools;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class ToolsFragment extends Fragment {
    private static final int PICKFILE_RESULT_CODE = 8778;
    private static final int CHOOSE_FILE_REQUESTCODE = 8777;
    Context context;
    Button btnSetFonts;
    Button buttonAccept;
    Uri uri;
    TextView progress_status_50,progress_status_100,progress_status_200;
    EditText userLogin;
    SharedPreferences sharedPreferences;
    private SeekBar guiScaleBar;
    final String TAG = getClass().getSimpleName();

    private OnFragmentInteractionListener mListener;

    public ToolsFragment() {
    }

    public static ToolsFragment newInstance() {
        ToolsFragment fragment = new ToolsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences("settings",Context.MODE_PRIVATE);
        String savedLogin = sharedPreferences.getString("login", "");
        String savedFont = sharedPreferences.getString("font", "");
        int savedScale = sharedPreferences.getInt("gui_scale",0);
        Log.i(TAG,"SavedLogin: "+ savedLogin);
        Log.i(TAG,"savedFont: "+ savedFont);
        Log.i(TAG,"savedScale: "+ savedScale);
        Log.i(TAG,Environment.getExternalStorageDirectory()+"");

        View toolsView = inflater.inflate(R.layout.fragment_tools, container, false);

        buttonAccept = toolsView.findViewById(R.id.buttonAccept);
        userLogin = toolsView.findViewById(R.id.inputUserName);
        userLogin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                    if(!hasFocus) {
                        generalData.hideKeyboard(getActivity());

                    }

            }
        });

        progress_status_50 = toolsView.findViewById(R.id.progress_status_50);
        progress_status_100 = toolsView.findViewById(R.id.progress_status_100);
        progress_status_200 = toolsView.findViewById(R.id.progress_status_200);
        guiScaleBar = toolsView.findViewById(R.id.GuiScale);
        btnSetFonts = toolsView.findViewById(R.id.pathToFonts);
        btnSetFonts.setText(savedFont);
        userLogin.setText(savedLogin);
        guiScaleBar.setProgress(savedScale);
        if(savedScale == 0){
            progress_status_50.setTextColor(getResources().getColor(R.color.colorOrangeBrand));
        }
        else if(savedScale == 1){
            progress_status_100.setTextColor(getResources().getColor(R.color.colorOrangeBrand));
        }
        else if(savedScale == 2){
            progress_status_200.setTextColor(getResources().getColor(R.color.colorOrangeBrand));
        }
        guiScaleBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                Toast.makeText(getActivity(), "Current value is " + seekBar.getProgress(), Toast.LENGTH_SHORT).show();
                if(seekBar.getProgress() == 0){
                    progress_status_50.setTextColor(getResources().getColor(R.color.colorOrangeBrand));
                    progress_status_100.setTextColor(getResources().getColor(R.color.colorGrayBrand));
                    progress_status_200.setTextColor(getResources().getColor(R.color.colorGrayBrand));

                }
                else if(seekBar.getProgress() == 1){
                    progress_status_50.setTextColor(getResources().getColor(R.color.colorGrayBrand));
                    progress_status_100.setTextColor(getResources().getColor(R.color.colorOrangeBrand));
                    progress_status_200.setTextColor(getResources().getColor(R.color.colorGrayBrand));
                }
                else if(seekBar.getProgress() == 2){
                    progress_status_50.setTextColor(getResources().getColor(R.color.colorGrayBrand));
                    progress_status_100.setTextColor(getResources().getColor(R.color.colorGrayBrand));
                    progress_status_200.setTextColor(getResources().getColor(R.color.colorOrangeBrand));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnSetFonts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File directoryFonts = new File(getActivity().getExternalCacheDir(),"/fonts");
                /*try {
                    File arialFont = new File(getActivity().getExternalCacheDir()+"/fonts","Arial.ttf");
                    if(!arialFont.exists()){
                        boolean isArialFontCreated = arialFont.createNewFile();
                        Log.i(TAG,"Состояние isArialFontCreated: " + isArialFontCreated);
                    }
                }
                catch(IOException e){
                    Log.i(TAG,"Ошибка при создании файла: " + e);
                }*/

                if(!directoryFonts.exists()){
                    boolean isDirectoryFontsCreated = directoryFonts.mkdir();
                    Log.i(TAG,"Состояние isDirectoryFontsCreated: " + isDirectoryFontsCreated);
                }
                Log.i(TAG,"getExternalCacheDir is: " + getActivity().getExternalCacheDir());

                uri = Uri.parse(getActivity().getExternalCacheDir()+"/fonts");
                Intent intentTools = new Intent(Intent.ACTION_GET_CONTENT);
                intentTools.setDataAndType(uri,"application/x-font-ttf");
                intentTools.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                Log.i(TAG,"Uri is: " + uri + " intentTools is: " + intentTools);

                startActivityForResult(intentTools,PICKFILE_RESULT_CODE);


            }
        });
        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                File directorySettings = new File(getActivity().getExternalCacheDir(),"/settings");
//                if(!directorySettings.exists()){
//                    boolean isDirectorySettingsCreated = directorySettings.mkdir();
//                    Log.i(TAG,"Состояние isDirectorySettingsCreated: " + isDirectorySettingsCreated);
//                }
//                try {
//                    File settingsIni = new File(getActivity().getExternalCacheDir()+"/settings","settings.ini");
//                    if(!settingsIni.exists()){
//                        boolean isSettingsIniCreated = settingsIni.createNewFile();
//                        Log.i(TAG,"Состояние isSettingsIniCreated: " + isSettingsIniCreated);
//                    }
//                }
//                catch(IOException e){
//                    Log.i(TAG,"Ошибка при создании файла: " + e);
//                }

                setSettings();

                btnSetFonts.getText();
                sharedPreferences = getActivity().getSharedPreferences("settings",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("login",userLogin.getText().toString());
                editor.putString("font",btnSetFonts.getText().toString());
                editor.putInt("gui_scale",guiScaleBar.getProgress());
                editor.apply();
                Log.i(TAG,btnSetFonts.getText() +" + " + userLogin.getText());
                Toast.makeText(getActivity(),R.string.data_save, Toast.LENGTH_SHORT).show();
            }
        });
        return toolsView;

    }

    public void setSettings(){
        File directorySetting = new File(getActivity().getExternalCacheDir(), "settings.ini");
        File directorySettingTemp = new File(getActivity().getExternalCacheDir(),"settingsTemp.ini");

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
                            line = jojo + "= " + userLogin.getText().toString();
                        }
                        if (jojo.equals("Font ")){
                            line = jojo + "= "  + btnSetFonts.getText().toString();
                        }
                        if(jojo.equals("GUI ")){
                            line = jojo + "= " + guiScaleBar.getProgress();
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
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKFILE_RESULT_CODE && resultCode == Activity.RESULT_OK)
        {
            Uri fontUri = data.getData();
            fontUri = Uri.parse(fontUri+"");
            String namePathToFont = fontUri.getLastPathSegment();

            if (namePathToFont.contains(".ttf")){
                Uri uriNamePathToFont = Uri.parse(namePathToFont);
                String LastNamePathToFont = uriNamePathToFont.getSchemeSpecificPart();
                Uri uriLastNamePathToFont = Uri.parse(LastNamePathToFont);
                String finalFontName = uriLastNamePathToFont.getLastPathSegment();
                Uri ftn;
                ftn = Uri.parse(getActivity().getExternalCacheDir()+"/fonts/" + finalFontName);

                String fontA = String.valueOf(fontUri).replaceAll("file:/", "");
                String fontB = String.valueOf(ftn);

                File sourceLocation= new File(fontA);
                File targetLocation = new File(fontB);

                try {
                    FileUtils.copyFile(sourceLocation, targetLocation);
                    Log.i(TAG, "File font is copied");
                } catch (IOException e) {
                    Log.i(TAG, "File font is not copied: " + e);
                    e.printStackTrace();
                }
                btnSetFonts.setText(finalFontName);
                Log.i(TAG,"finalFontName: "+  finalFontName);
            }else {
                Toast.makeText(getActivity(), R.string.invalid_select_font, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
