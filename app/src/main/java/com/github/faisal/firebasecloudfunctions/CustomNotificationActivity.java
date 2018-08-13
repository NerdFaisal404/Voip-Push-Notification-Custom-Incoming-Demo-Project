package com.github.faisal.firebasecloudfunctions;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class CustomNotificationActivity extends AppCompatActivity {
    private int MIC_PERMISSION_REQUEST_CODE = 1;
    private KeyguardManager.KeyguardLock lock;
    private PowerManager.WakeLock wakeLock;
    private AudioManager audioManager;
    private int savedAudioMode = AudioManager.MODE_INVALID;
    private SoundPoolManager soundPoolManager;

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("faisald", "Test code");
        PowerManager pwm = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = pwm.newWakeLock(PowerManager.FULL_WAKE_LOCK, getClass().getSimpleName());
        wakeLock.acquire();
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        lock = keyguardManager.newKeyguardLock(getClass().getSimpleName());
        lock.disableKeyguard();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_custom_notification);
        soundPoolManager = SoundPoolManager.getInstance(this);
        /*
         * Needed for setting/abandoning audio focus during a call
         */
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(true);

        /*
         * Enable changing the volume using the up/down keys during a conversation
         */
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        if (!checkPermissionForMicrophone()) {
            requestPermissionForMicrophone();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        soundPoolManager.playRinging();
    }

    @Override
    public void onPause() {
        super.onPause();
        wakeLock.release();
        lock.reenableKeyguard();
    }


    private boolean checkPermissionForMicrophone() {
        int resultMic = ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO);
        return resultMic == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionForMicrophone() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.RECORD_AUDIO)) {

        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MIC_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        /*
         * Check if microphone permissions is granted
         */
        if (requestCode == MIC_PERMISSION_REQUEST_CODE && permissions.length > 0) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {

            } else {

            }
        }
    }
}