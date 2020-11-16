package com.fibocom.factorytest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,//定位权限
            Manifest.permission.ACCESS_FINE_LOCATION,//定位权限
            Manifest.permission.WRITE_EXTERNAL_STORAGE,//存储卡写入权限
            Manifest.permission.READ_EXTERNAL_STORAGE,//存储卡读取权限
            Manifest.permission.READ_PHONE_STATE//读取手机状态权限
    };
    private static final int PERMISSON_REQUESTCODE = 0;
    private boolean isNeedCheck = true;

    boolean checkDevice(String pathname, String token) {
        File file = new File(pathname);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("Version:") && line.contains(token)) {
                    return true;
                }
            }
            br.close();
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String rawStr = AESUtils.readShaderFromRawResource(this, R.raw.token);
        if (rawStr == null) {
            ((TextView) null).setText("");
        }
        String[] lines = rawStr.split("\n");
        if (lines.length != 2) finish();
        if (!checkDevice(lines[0].trim(), lines[1])) {
            ((TextView) null).setText("");
        }
        setContentView(R.layout.activity_main);

        if (!checkDevice(lines[0].trim(), lines[1]) || Build.VERSION.SDK_INT != Build.VERSION_CODES.Q) {
            ((TextView) null).setText("");
        }

        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
        NavController navController;
        if (navHostFragment != null) {
            navController = NavHostFragment.findNavController(navHostFragment);
            NavigationUI.setupActionBarWithNavController(this, navController);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
        if (fragment != null) {
            return NavHostFragment.findNavController(fragment).navigateUp();
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("checkPermissions", "onResume: " + isNeedCheck);
        if (isNeedCheck) {
            checkPermissions(needPermissions);
        }
    }

    private void checkPermissions(String... permissions) {
        List<String> needRequestPermissonList = findDeniedPermissions(permissions);
        if (needRequestPermissonList.size() > 0) {
            ActivityCompat.requestPermissions(this,
                    needRequestPermissonList.toArray(
                            new String[needRequestPermissonList.size()]),
                    PERMISSON_REQUESTCODE);
        }
    }

    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<String>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this,
                    perm) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    this, perm)) {
                needRequestPermissonList.add(perm);
            }
        }
        return needRequestPermissonList;
    }

    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] paramArrayOfInt) {
        if (requestCode == PERMISSON_REQUESTCODE) {
            if (!verifyPermissions(paramArrayOfInt)) {
                showMissingPermissionDialog();
                isNeedCheck = false;
            }
        }
    }

    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.notifyTitle);
        builder.setMessage(R.string.notifyMsg);
        // 拒绝授权 退出应用
        builder.setNegativeButton(R.string.cancel,
                (dialog, which) -> finish());
        //同意授权
        builder.setPositiveButton(R.string.setting,
                (dialog, which) -> startAppSettings());
        builder.setCancelable(false);
        builder.show();
    }

    private void startAppSettings() {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }
}