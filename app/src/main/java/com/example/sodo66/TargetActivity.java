package com.example.sodo66;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;

import com.example.sodo.R;

public class TargetActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target);

        View targetLayout = findViewById(R.id.targetLayout);
         targetLayout.setBackgroundColor(Color.parseColor("#CC000000"));

        AppCompatButton button1 = findViewById(R.id.button);
        AppCompatButton button2 = findViewById(R.id.button2);
        AppCompatButton button3 = findViewById(R.id.button3);
        AppCompatImageView imageView = findViewById(R.id.appCompatImageView);
        button1.setOnClickListener(v -> openWebsite("https://www.google.com"));

        button2.setOnClickListener(v -> openWebsite("https://safety.google/"));

        button3.setOnClickListener(v -> openWebsite("https://www.google.com/doodles"));

        imageView.setOnClickListener(v -> openWebsite("https://play.google.com/store/apps"));
    }
    private void openWebsite(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}
