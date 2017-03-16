package com.websmithing.gpstracker;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WarningActivity extends AppCompatActivity implements View.OnClickListener {
    Button checkNetworkB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning);
        checkNetworkB=(Button)findViewById(R.id.button2);
        checkNetworkB.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.button2) {
            Intent callGPSSettingIntent = new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS);
            callGPSSettingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(callGPSSettingIntent);
        }
    }
}
