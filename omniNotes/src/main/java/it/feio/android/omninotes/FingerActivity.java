package it.feio.android.omninotes;

import android.os.Bundle;
//import android.os.CancellationSignal;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.core.os.CancellationSignal;


import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
//import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
//import android.support.v4.os.CancellationSignal;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;

import it.feio.android.omninotes.databinding.ActivityCategoryBinding;
import it.feio.android.omninotes.databinding.ActivityFingerBinding;

//这个可以的
public class FingerActivity extends AppCompatActivity implements CancellationSignal.OnCancelListener {
    CancellationSignal cancellationSignal;
    FingerprintManagerCompat fingerprintManagerCompat;

    ActivityFingerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        //setContentView(R.layout.activity_finger);

        binding = ActivityFingerBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        fingerprintManagerCompat = FingerprintManagerCompat.from(this);
        cancellationSignal = new CancellationSignal();
        cancellationSignal.setOnCancelListener(this);
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//startAuthenticate();
                startFinger();
            }
        });


    }

    void startAuthenticate(View v) {

        if (!fingerprintManagerCompat.isHardwareDetected()) {
            String tip = "设备不支持指纹识别";
            Toast.makeText(v.getContext(), "设备不支持指纹识别", Toast.LENGTH_SHORT).show();
            binding.tvTip.setText(tip);
            return;
        }

        if (!fingerprintManagerCompat.hasEnrolledFingerprints()) {//判断设备是否已经注册过指纹
            String tip = "设备尚未注册指纹";

            Toast.makeText(v.getContext(), "设备尚未注册指纹", Toast.LENGTH_SHORT).show();
            binding.tvTip.setText(tip);
            startActivity(new Intent(Settings.ACTION_SETTINGS));
            return;

        }

        fingerprintManagerCompat.authenticate(null, 0, cancellationSignal, callback, handler);
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private FingerprintManagerCompat.AuthenticationCallback callback =
            new FingerprintManagerCompat.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errMsgId, CharSequence errString) {
                    super.onAuthenticationError(errMsgId, errString);

                    Toast.makeText(FingerActivity.this, errString.toString(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                    super.onAuthenticationHelp(helpMsgId, helpString);
                }

                @Override
                public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    String tip = "指纹识别成功";
                    Toast.makeText(FingerActivity.this, "指纹识别成功", Toast.LENGTH_SHORT).show();
                    //成功后 释放识别事件
                    binding.tvTip.setText(tip);
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    String tip = "指纹识别失败";
                    Toast.makeText(FingerActivity.this, "指纹识别失败", Toast.LENGTH_SHORT).show();
                    //成功后 可继续验证指纹
                    binding.tvTip.setText(tip);
                }
            };

    @Override
    public void onCancel() {
        String tip = "取消指纹识别";
        Toast.makeText(FingerActivity.this, "取消指纹识别", Toast.LENGTH_SHORT).show();
        binding.tvTip.setText(tip);
    }

    private BiometricPrompt biometricPrompt;

    private void startFinger() {
        biometricPrompt = new BiometricPrompt(this, new Executor() {
            @Override
            public void execute(Runnable command) {
                command.run();
            }
        }, new FingerCallBack());
        biometricPrompt.authenticate(new BiometricPrompt.PromptInfo.Builder().setTitle("title")
                .setSubtitle("subTitle")
                .setDescription("description")
                .setDeviceCredentialAllowed(false)
                .setNegativeButtonText("button").build());
    }

    private void cancelFinger() {
        if (biometricPrompt != null) {
            biometricPrompt.cancelAuthentication();
        }
    }

    private class FingerCallBack extends BiometricPrompt.AuthenticationCallback {
        @Override
        public void onAuthenticationError(int errMsgId, CharSequence errString) {
            super.onAuthenticationError(errMsgId, errString);
            Log.e("fingers", "onAuthenticationError");

        }

        @Override
        public void onAuthenticationSucceeded(@NonNull @NotNull BiometricPrompt.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            cancelFinger();
            Log.e("fingers", "识别成功 onAuthenticationSucceeded");
            String tip = "识别成功";
            Toast.makeText(FingerActivity.this, tip, Toast.LENGTH_SHORT).show();
            //成功后 可继续验证指纹
            binding.tvTip.setText(tip);
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
            Log.e("fingers", "onAuthenticationFailed  ");
            String tip = "onAuthenticationFailed";
            Toast.makeText(FingerActivity.this, tip, Toast.LENGTH_SHORT).show();
            binding.tvTip.setText(tip);
        }
    }
}
