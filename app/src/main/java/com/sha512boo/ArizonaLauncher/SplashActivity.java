package com.sha512boo.ArizonaLauncher;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sha512boo.ArizonaLauncher.utils.NetworkCheck;
import com.sha512boo.ArizonaLauncher.utils.PermissionUtil;
import com.sha512boo.ArizonaLauncher.utils.Tools;

public class SplashActivity extends AppCompatActivity {

    private boolean on_permission_result = false;


    SharedPreferences sharedPreferences;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();

        if (Tools.needRequestPermission() && !on_permission_result) {
            String[] permission = PermissionUtil.getDeniedPermission(this);
            if (permission.length != 0) {
                requestPermissions(permission, 200);
            }else {
                startProcess();
            }
        } else {
            startProcess();
        }
    }

    private void startProcess() {
        if (!NetworkCheck.isConnect(this)) {
            final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
            View mView = this.getLayoutInflater().inflate(R.layout.no_internet, null);
            Button mOk = (Button) mView.findViewById(R.id.ok);
            TextView mCancel = (TextView) mView.findViewById(R.id.cancel);

            mBuilder.setView(mView);
            final AlertDialog sampDialog = mBuilder.create();

            sampDialog.setCancelable(false);
            mOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startProcess();
                }
            });
            mCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            sampDialog.show();
        } else {
            Intent i = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }
}
