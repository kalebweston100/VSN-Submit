package com.production.vsn;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            runService();
        }
    }

    public void secureStart(View view) {
        Intent intent = VpnService.prepare(this);
        if (intent == null) {
            runService();
        }
        else {
            startActivityForResult(intent, 0);
        }
    }

    public void runService() {
        Intent service = new Intent(this, SecureService.class);
        startForegroundService(service);
    }

    public void secureStop(View view) {
        Intent intent = new Intent(this, SecureService.class);
        stopService(intent);
    }
}