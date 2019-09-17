package com.sha512boo.ArizonaLauncher.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import android.view.View;
import android.widget.Button;

import com.sha512boo.ArizonaLauncher.ApkInstaller;
import com.sha512boo.ArizonaLauncher.DownloadActivity;
import com.sha512boo.ArizonaLauncher.MainActivity;
import com.sha512boo.ArizonaLauncher.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Tools {
    Handler h;
    static String mainfile = "main.8.com.rockstargames.gtasa.obb";
    static String patchfile = "patch.8.com.rackstargames.gtasa.obb";
    static String mainzip = "main.8.com.rockstargames.gtasa.obb";
    static String patchzip = "patch.8.com.rockstargames.gtasa.obb";
    static String texdbfile = "texdb";
    static String SAMPfile = "SAMP";
    static String arizonafile = "arizona_test.apk";
    static String arizonauri = "http://arizona-download.react.group/arizona_test.apk";
    static String arizonadirectory = "/";
    static String obbdirectory = "/Android/obb/com.rockstargames.gtasa";
    static String SAMPdirectory = "/Android/data/com.rockstargames.gtasa/files";

    static String mainuri = "http://arizona-download.react.group/obb/main.8.com.rockstargames.gtasa.zip";
    static String patchuri = "http://arizona-download.react.group/obb/patch.8.com.rockstargames.gtasa.zip";

    static String SAMPuri = "http://arizona-download.react.group/files/SAMP/";
    static String texdburi = "http://arizona-download.react.group/files/texdb/";
    //static DownloadManager dm = MainActivity.getDownloadManager();


    public static boolean needRequestPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    public static boolean needInstallApk(Context ctx) {
        PackageManager pm = ctx.getPackageManager();
        try {
            pm.getApplicationInfo("com.rockstargames.gtasa", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean needInstallFiles(String namefile, String directoryfile) {
        String path = Environment.getExternalStorageDirectory().toString() + directoryfile;
        File directory = new File(path);
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().equals(namefile) && files[i].length() > 0) {
                return true;
            }
        }
        return false;
    }

    public static void checkLTE(final Activity act) {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(act, R.style.CustomAlertDialog);
        View mView = act.getLayoutInflater().inflate(R.layout.dialog_lte, null);
        Button mOk = (Button) mView.findViewById(R.id.ok);
        Button mNo = (Button) mView.findViewById(R.id.no);
        mBuilder.setView(mView);
        final AlertDialog sampDialog = mBuilder.create();

        sampDialog.setCancelable(false);
        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sampDialog.dismiss();
            }
        });
        mNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                act.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                sampDialog.dismiss();
            }
        });
        sampDialog.show();
    }

    public static void needAppDialog(final Activity act) {

        if (!needInstallApk(act)) {
            if (MainActivity.getMobileState() == NetworkInfo.State.CONNECTED || MainActivity.getMobileState() == NetworkInfo.State.CONNECTING) {
                checkLTE(act);
            }
                Intent intent = new Intent(act, DownloadActivity.class);
                intent.putExtra("file", arizonafile);
                intent.putExtra("uri", arizonauri);
                intent.putExtra("directory", "/");
                intent.putExtra("type", "apk");
                act.startActivity(intent);
                act.finish();

        } else {
            if (!needInstallFiles(patchfile, obbdirectory)) {
                if (MainActivity.getMobileState() == NetworkInfo.State.CONNECTED || MainActivity.getMobileState() == NetworkInfo.State.CONNECTING) {
                    checkLTE(act);
                }

                    Intent intent = new Intent(act, DownloadActivity.class);
                    intent.putExtra("file", patchzip);
                    intent.putExtra("uri", patchuri);
                    intent.putExtra("directory", obbdirectory);
                    intent.putExtra("type", "zip_obb");
                    act.startActivity(intent);
                    act.finish();

            }
            if (!needInstallFiles(mainfile, obbdirectory)) {
                if (MainActivity.getMobileState() == NetworkInfo.State.CONNECTED || MainActivity.getMobileState() == NetworkInfo.State.CONNECTING) {
                    checkLTE(act);
                }
                Intent intent = new Intent(act, DownloadActivity.class);
                intent.putExtra("file", mainzip);
                intent.putExtra("uri", mainuri);
                intent.putExtra("directory", obbdirectory);
                intent.putExtra("type", "zip_obb");
                act.startActivity(intent);
                act.finish();
            }


            if (!needInstallFiles(texdbfile, SAMPdirectory)) {
                Intent intent = new Intent(act, DownloadActivity.class);
                intent.putExtra("file", SAMPfile);
                intent.putExtra("uri", SAMPuri);
                intent.putExtra("directory", SAMPdirectory);
                intent.putExtra("type", "file");
                act.startActivity(intent);
                act.finish();


            }
            if( !needInstallFiles(SAMPfile, SAMPdirectory)){
                Intent intent = new Intent(act, DownloadActivity.class);
                intent.putExtra("file", texdbfile);
                intent.putExtra("uri", texdburi);
                intent.putExtra("directory", SAMPdirectory);
                intent.putExtra("type", "file");
                act.startActivity(intent);
                act.finish();
            }
        }
    }


    public static void setHostSetting(Activity act, int pass, String password, String host, String port) {
        File stngs = new File(act.getExternalCacheDir(), "settings.ini");
        File tempStngs = new File(act.getExternalCacheDir(), "settingsTemp.ini");

        try {
            BufferedReader settings = new BufferedReader(new FileReader(stngs));
            BufferedWriter settingstemp = new BufferedWriter(new FileWriter(tempStngs));
            String line;
            while ((line = settings.readLine()) != null) {
                if (line.contains("=")) {
                    String[] part = line.split("=");
                    String jojo = part[part.length - 2];
                    if (jojo.equals("host ")) {
                        line = jojo + "= " + host;
                    }
                    if (jojo.equals("port ")) {
                        line = jojo + "= " + port;
                    }
                    if (jojo.equals("#password ") || jojo.equals("password ")) {
                        if (pass == 1) {
                            line = "password = " + password;
                        } else {
                            line = "#password = pass";
                        }
                    }
                    settingstemp.write(line + System.getProperty("line.separator"));
                } else {
                    settingstemp.write(line + System.getProperty("line.separator"));
                }
            }
            settings.close();
            settingstemp.close();
            stngs.delete();
            tempStngs.renameTo(stngs);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void downloadFile(final Context context, String url) {

        new AsyncTask<String, Integer, File>() {
            private Exception m_error = null;

            @Override
            protected void onPreExecute() {

            }

            @Override
            protected File doInBackground(String... params) {
                URL url;
                HttpURLConnection urlConnection;
                InputStream inputStream;
                int totalSize;
                int downloadedSize;
                byte[] buffer;
                int bufferLength;

                File file = null;
                FileOutputStream fos = null;

                try {
                    url = new URL(params[0]);
                    urlConnection = (HttpURLConnection) url.openConnection();

                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoOutput(true);

                    urlConnection.connect();

                    File storagePath = new File(context.getExternalCacheDir() + File.separator + "/fonts/");
                    storagePath.mkdirs();
                    String finalName = Long.toString(System.currentTimeMillis());
                    file = new File(storagePath, "arial" + ".ttf");
                    fos = new FileOutputStream(file);
                    inputStream = urlConnection.getInputStream();

                    totalSize = urlConnection.getContentLength();
                    downloadedSize = 0;

                    buffer = new byte[1024];
                    bufferLength = 0;

                    // читаем со входа и пишем в выход,
                    // с каждой итерацией публикуем прогресс
                    while ((bufferLength = inputStream.read(buffer)) > 0) {
                        fos.write(buffer, 0, bufferLength);
                        downloadedSize += bufferLength;
                        publishProgress(downloadedSize, totalSize);
                    }

                    fos.close();
                    inputStream.close();

                    return file;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    m_error = e;
                } catch (IOException e) {
                    e.printStackTrace();
                    m_error = e;
                }

                return null;
            }


            @Override
            protected void onPostExecute(File file) {
                // отображаем сообщение, если возникла ошибка
                if (m_error != null) {
                    m_error.printStackTrace();
                    return;
                }
                // закрываем прогресс и удаляем временный файл

            }
        }.execute(url);
    }

    public static String fav = "185.169.134.3:7777\n" +
            "185.169.134.4:7777\n" +
            "185.169.134.43:7777\n" +
            "185.169.134.44:7777\n" +
            "185.169.134.45:7777\n" +
            "185.169.134.5:7777\n" +
            "185.169.134.59:7777\n" +
            "185.169.134.61:7777\n" +
            "185.169.134.107:7777\n";


    public static String set = "# Строки в начале которых стоит знак '#' - комментарии\n" +
            "# Для того, чтобы раскомментировать строку - удалите знак '#'\n" +
            "\n" +
            "[client]\n" +
            "# NickName / Имя игрока\n" +
            "name = 93.170.76.34:7778\n" +
            "host = 93.170.76.34\n" +
            "port = 7778\n" +
            "# Server password / Пароль сервера\n" +
            "#password = changeme\n" +
            "[debug]\n" +
            "debug = false\n" +
            "online = true\n" +
            "\n" +
            "[gui]\n" +
            "##### !!! ######\n" +
            "# Все координаты/размеры GUI элементов\n" +
            "# задаются относительно разрешения 1920x1080\n" +
            "################\n" +
            "\n" +
            "# Font / Шрифт\n" +
            "# Файл !обязательно! должен находится в папке fonts\n" +
            "Font = arial.ttf\n" +
            "\n" +
            "# Font Size / Размер шрифта\n" +
            "# Важный параметр. Многие GUI элементы масштабируются исходя\n" +
            "# из значения данного параметра\n" +
            "FontSize = 30.0\n" +
            "\n" +
            "# Размер обводки текста / Font outline size\n" +
            "FontOutline = 2\n" +
            "\n" +
            "# GUI Scale factor\n" +
            "GUI = 100\n" +
            "\n" +
            "# Chat window position / Позиция окна чата\n" +
            "ChatPosX = 325.0\n" +
            "ChatPosY = 25.0\n" +
            "\n" +
            "# Chat window size / размер окна чата\n" +
            "ChatSizeX = 1150.0\n" +
            "ChatSizeY = 220.0\n" +
            "\n" +
            "# 'samp' pagesize analog / кол-во строк выводимых на экран\n" +
            "ChatMaxMessages = 8\n" +
            "\n" +
            "# NameTag's HealthBar size / Размер полосы ХП других игроков\n" +
            "HealthBarWidth = 60.0\n" +
            "HealthBarHeight = 10.0";


}
