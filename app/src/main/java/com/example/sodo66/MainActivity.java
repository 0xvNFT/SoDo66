package com.example.sodo66;

import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.sodo.R;

public class MainActivity extends AppCompatActivity {
    private ImageView dot;
    private ImageView modeImageView;
    private ImageView onOffButton;
    private ImageView onOffButton2;
    private SeekBar sosSlider;
    private boolean isLightMode = true;
    private CameraManager cameraManager;
    private String cameraId;
    private boolean isFlashlightOn = false;
    private int sosFlickerSpeed = 1;
    private Handler sosFlickerHandler;
    private Runnable sosFlickerRunnable;
    private AppCompatButton navigateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dot = findViewById(R.id.dot);
        modeImageView = findViewById(R.id.modeImageView);
        onOffButton = findViewById(R.id.onOffButton);
        onOffButton2 = findViewById(R.id.onOffButton2);
        sosSlider = findViewById(R.id.sos_slider);
        sosFlickerHandler = new Handler();
        sosSlider.setVisibility(View.GONE);
        navigateButton = findViewById(R.id.navigateButton);

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }


        modeImageView.setOnClickListener(v -> switchMode());

        onOffButton.setOnClickListener(v -> toggleLight());

        onOffButton2.setOnClickListener(v -> toggleSOS());

        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the target layout
                Intent intent = new Intent(MainActivity.this, TargetActivity.class);
                startActivity(intent);
            }
        });

        sosSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                sosFlickerSpeed = sosSlider.getMax() - progress + 1;


                if (isFlashlightOn && !isLightMode) {
                    stopSOSFlicker();
                    startSOSFlicker();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void switchMode() {
        if (isFlashlightOn) {
            return;
        }

        isLightMode = !isLightMode;

        if (isLightMode) {
            modeImageView.setImageResource(R.drawable.light_mode);
            dot.setImageResource(R.drawable.green);
            onOffButton.setVisibility(View.VISIBLE);
            onOffButton2.setVisibility(View.GONE);
            stopSOSFlicker();
            sosSlider.setVisibility(View.GONE);
        } else {
            modeImageView.setImageResource(R.drawable.sos_mode);
            dot.setImageResource(R.drawable.red);
            onOffButton.setVisibility(View.GONE);
            onOffButton2.setVisibility(View.VISIBLE);
            onOffButton.setVisibility(View.GONE);
            sosSlider.setVisibility(View.VISIBLE);
            if (isFlashlightOn) {
                toggleLight();
            }
        }
    }
    private void toggleLight() {
        if (isLightMode) {
            try {
                boolean isFlashAvailable = Boolean.TRUE.equals(cameraManager.getCameraCharacteristics(cameraId)
                        .get(CameraCharacteristics.FLASH_INFO_AVAILABLE));

                if (isFlashAvailable) {
                    isFlashlightOn = !isFlashlightOn;
                    cameraManager.setTorchMode(cameraId, isFlashlightOn);
                    onOffButton.setImageResource(isFlashlightOn ? R.drawable.on_button : R.drawable.off_button);
                }
                else {
                    Toast.makeText(this, "Đèn pin không có sẵn", Toast.LENGTH_SHORT).show();
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }
    private void toggleSOS() {
        if (!isLightMode) {
            try {
                boolean isFlashAvailable = Boolean.TRUE.equals(cameraManager.getCameraCharacteristics(cameraId)
                        .get(CameraCharacteristics.FLASH_INFO_AVAILABLE));

                if (isFlashAvailable) {
                    isFlashlightOn = !isFlashlightOn;

                    if (isFlashlightOn) {
                        startSOSFlicker();
                    } else {
                        stopSOSFlicker();
                    }
                    onOffButton2.setImageResource(isFlashlightOn ? R.drawable.on_button : R.drawable.off_button);
                } else {
                    Toast.makeText(this, "Đèn pin không có sẵn", Toast.LENGTH_SHORT).show();
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }
    private void startSOSFlicker() {
        final long flickerInterval = (sosFlickerSpeed + 1) * 50L;

        sosFlickerRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    cameraManager.setTorchMode(cameraId, true);
                    Thread.sleep(50);
                    cameraManager.setTorchMode(cameraId, false);
                } catch (CameraAccessException | InterruptedException e) {
                    e.printStackTrace();
                }
                sosFlickerHandler.postDelayed(this, flickerInterval);
            }
        };
        sosFlickerHandler.post(sosFlickerRunnable);
    }
    private void stopSOSFlicker() {
        sosFlickerHandler.removeCallbacks(sosFlickerRunnable);
    }
}