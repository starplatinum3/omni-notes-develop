package it.feio.android.omninotes.utils;
//package com.leaveme.notebook.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
//import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

//import com.leaveme.notebook.ImageDialog;
//import com.leaveme.notebook.MainActivity;
//import com.leaveme.notebook.NoteActivity;
//import com.leaveme.notebook.R;
//import com.leaveme.notebook.ViewAdapter.NoteAdapter;
//import com.leaveme.notebook.ViewAdapter.SimpleItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.List;

public class FingerprintHelper {

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public FingerprintManager.AuthenticationCallback getSelfCancelled() {
        return selfCancelled;
    }

    public void setSelfCancelled(FingerprintManager.AuthenticationCallback selfCancelled) {
        this.selfCancelled = selfCancelled;
    }

    Context context;
    FingerprintManager.AuthenticationCallback selfCancelled;
    //回调方法
//    FingerprintManager.AuthenticationCallback mSelfCancelled;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void startListening(FingerprintManager.CryptoObject cryptoObject, Context context) {
        //android studio 上，没有这个会报错
//        mainActivity
//        FragmentActivity activity = getActivity();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "没有指纹识别权限", Toast.LENGTH_SHORT).show();
        }
        manager.authenticate(cryptoObject, mCancellationSignal, 0, selfCancelled, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void startListening(FingerprintManager.CryptoObject cryptoObject) {
        startListening(cryptoObject,context);

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void showAuthenticationScreen(Fragment fragment) {
        Intent intent = mKeyManager.createConfirmDeviceCredentialIntent("finger", "指纹识别失败次数过多，请输入锁屏密码");
        if (intent != null) {
            fragment. startActivityForResult(intent, REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS);
        }
//        showAuthenticationScreen((Activity)fragment);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void showAuthenticationScreen(Activity fragment) {
        Intent intent = mKeyManager.createConfirmDeviceCredentialIntent("finger", "指纹识别失败次数过多，请输入锁屏密码");
        if (intent != null) {
            fragment. startActivityForResult(intent, REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS);
        }
//        showAuthenticationScreen((Fragment)fragment);
    }


//    FingerprintManager.AuthenticationCallback mSelfCancelled = new FingerprintManager.AuthenticationCallback() {
//        @Override
//        public void onAuthenticationError(int errorCode, CharSequence errString) {
//            //但多次指纹密码验证错误后，进入此方法；并且，不能短时间内调用指纹验证
//            Toast.makeText(context, errString, Toast.LENGTH_SHORT).show();
//            showAuthenticationScreen();
//        }
//        @Override
//        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
//            //出现错误，可能是手指移动过快等问题　　　　　　　　Toast.makeText(MainActivity.this, helpString, Toast.LENGTH_SHORT).show();
//        }
//        @Override
//        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
//            //认证成功，可以进入加密文档
//            noteAdapter.openNote();
//        }
//        @Override
//        public void onAuthenticationFailed() {
//            Toast.makeText(context, "指纹识别失败", Toast.LENGTH_SHORT).show();
//        }
//    };
//
//
//
//    private NoteAdapter noteAdapter;
    private RecyclerView list;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    private ItemTouchHelper mItemTouchHelper;

    FingerprintManager manager;
    KeyguardManager mKeyManager;
    private final static int REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 0;

//    private ImageDialog imageDialog;
    private int position;



    //添加系统权限程序
    public boolean addPermission(List<String> permissionsList, String permission) {
        //判断该应用是否具备要请求的权限
        Log.i("context", "addPermission: context "+context);
        if (ContextCompat.checkSelfPermission(context,permission) != PackageManager.PERMISSION_GRANTED) {
            //没有该权限，则加入到请求列表
            permissionsList.add(permission);
            return ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission);
        }
        return true;
    }
    //创建内容显示对话框
    public void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    //权限请求程序
    public void permissionsInit(){
        //需要请求的权限请求字符串列表
        List<String> permissionsNeeded = new ArrayList<String>();
        //权限请求列表
        final List<String> permissionsList = new ArrayList<String>();
        //添加读写存储空间、读取手机状态、拨打电话、读取位置信息、读取精确位置信息这些权限到权限请求列表中
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("\n\r读存储空间");
        if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
            permissionsNeeded.add("\n\r写存储空间");
        if (!addPermission(permissionsList, Manifest.permission.INTERNET))
            permissionsNeeded.add("\n\r联网");
        if (!addPermission(permissionsList, Manifest.permission.USE_FINGERPRINT))
            permissionsNeeded.add("\n\r使用指纹识别");
        //如果权限请求列表中的内容大于0个，则开始请求权限
        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                //获取到第一个需要添加请求列表的权限
                StringBuilder message = new StringBuilder("你需要获取已下权限：" + permissionsNeeded.get(0));
                //循环将剩余需要请求的权限加入到请求列表
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message.append(", ").append(permissionsNeeded.get(i));
                showMessageOKCancel(message.toString(),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions((Activity)context,permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        });
                return;
            }
            //开始向系统请求权限
            ActivityCompat.requestPermissions((Activity) context,permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        }else {
            //程序初始化、加载数据库
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean isFinger() {
        //用来检查是否有指纹识别权限
        if(context.checkCallingOrSelfPermission(Manifest.permission.USE_FINGERPRINT) ==
                PackageManager.PERMISSION_GRANTED) {
            if (!manager.isHardwareDetected()) {
                Toast.makeText(context, "没有指纹识别模块", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!mKeyManager.isKeyguardSecure()) {
                Toast.makeText(context, "没有开启锁屏密码", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!manager.hasEnrolledFingerprints()) {
                Toast.makeText(context, "没有录入指纹", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    CancellationSignal mCancellationSignal = new CancellationSignal();
    //回调方法
//    FingerprintManager.AuthenticationCallback mSelfCancelled = new FingerprintManager.AuthenticationCallback() {
//        @Override
//        public void onAuthenticationError(int errorCode, CharSequence errString) {
//            //但多次指纹密码验证错误后，进入此方法；并且，不能短时间内调用指纹验证
//            if (imageDialog!=null){
//                imageDialog.dismiss();
//                imageDialog = null;
//            }
//            Toast.makeText(MainActivity.this, errString, Toast.LENGTH_SHORT).show();
//            showAuthenticationScreen();
//        }
//        @Override
//        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
//            Toast.makeText(MainActivity.this, helpString, Toast.LENGTH_SHORT).show();
//        }
//        @Override
//        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
//            if (imageDialog!=null){
//                imageDialog.dismiss();
//                imageDialog = null;
//            }
//            noteAdapter.openNote();
//        }
//        @Override
//        public void onAuthenticationFailed() {
//            if (imageDialog!=null){
//                imageDialog.dismiss();
//                imageDialog = null;
//            }
//            Toast.makeText(context, "指纹识别失败", Toast.LENGTH_SHORT).show();
//        }
//    };


//    public void startListening(FingerprintManager.CryptoObject cryptoObject) {
//        //android studio 上，没有这个会报错
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(this, "没有指纹识别权限", Toast.LENGTH_SHORT).show();
//            if (imageDialog!=null){
//                imageDialog.dismiss();
//                imageDialog = null;
//            }
//            return;
//        }
//        manager.authenticate(cryptoObject, mCancellationSignal, 0, mSelfCancelled, null);
//
//    }

//    private void showAuthenticationScreen() {
//        Intent intent = mKeyManager.createConfirmDeviceCredentialIntent("finger", "指纹识别失败次数过多，请输入锁屏密码");
//        if (intent != null) {
//            startActivityForResult(intent, REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS);
//        }
//    }
//
//

    private void Log(String tag, String msg) {
        Log.d(tag, msg);
    }




}
