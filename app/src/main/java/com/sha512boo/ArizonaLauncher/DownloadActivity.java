package com.sha512boo.ArizonaLauncher;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sha512boo.ArizonaLauncher.utils.Tools;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DownloadActivity extends AppCompatActivity {

    private DownloadManager dm;
    static String uri, directory, file, typefile;
    static int dl_progress = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Bundle arguments = getIntent().getExtras();
        uri = arguments.get("uri").toString();
        file = arguments.get("file").toString();
        directory = arguments.get("directory").toString();
        typefile = arguments.get("type").toString();
        this.setFinishOnTouchOutside(false);


        new Thread(new Runnable() {

            @Override
            public void run() {
                if (!Tools.needInstallFiles(file, directory)) {
                    final Uri Download_Uri = Uri.parse(uri);
                    DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                    request.setAllowedOverRoaming(false);
                    request.setTitle(file);
                    request.setDescription(file);
                    request.setVisibleInDownloadsUi(true);
                    request.setDestinationInExternalPublicDir(directory, file);
                    final long refid = dm.enqueue(request);
                    final ProgressBar mProgressBar = (ProgressBar) findViewById(R.id.progress_download);
                    final TextView download = (TextView) findViewById(R.id.download);
                    final TextView Mb = (TextView) findViewById(R.id.Mb);
                    download.setText(download.getText().toString() + " " + file);

                    boolean downloading = true;

                    while (downloading) {

                        DownloadManager.Query q = new DownloadManager.Query();
                        q.setFilterById(refid);

                        Cursor cursor = dm.query(q);
                        cursor.moveToFirst();
                        final int bytes_downloaded = cursor.getInt(cursor
                                .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                        final int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                        if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                            downloading = false;
                        }
                        dl_progress = (int) ((bytes_downloaded * 100l) / bytes_total);

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                mProgressBar.setProgress((int) dl_progress);
                                Mb.setText(Long.toString(bytes_downloaded / 1024 / 1024) + "/" + Long.toString(bytes_total / 1024 / 1024) + " Mb");
                            }
                        });
                        cursor.close();
                    }
                }
                if (typefile.equals("apk")) {
                    ApkInstaller.installApplication(DownloadActivity.this, "storage/emulated/0/arizona_test.apk");
                    finish();
                }
                else if(typefile.equals("zip_obb")){
                    String file_zip = file;
                    file = "";
                    for(int i = 0; i<file_zip.length()-3; i++){
                        file = file + file_zip.charAt(i);
                    }
                    file = file + "obb";
                    try {
                        unzip(directory+file_zip, directory+file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                Intent intent = new Intent(DownloadActivity.this, MainActivity.class);
                startActivity(intent);
                DownloadActivity.this.finish();
            }
        }).start();

    }
    public void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
                byte[] bytesIn = new byte[4096];
                int read = 0;
                while ((read = zipIn.read(bytesIn)) != -1) {
                    bos.write(bytesIn, 0, read);
                }
                bos.close();
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdir();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }
}
