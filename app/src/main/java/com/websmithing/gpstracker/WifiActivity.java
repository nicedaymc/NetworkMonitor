package com.websmithing.gpstracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WifiActivity extends Activity implements View.OnClickListener
{
    private static final String TAG = "WifiActivity";
    WifiManager wifi;
    ListView lv;
    TextView textStatus;
    Button buttonScan;
    int size = 0;
    List<ScanResult> results;

    String ITEM_KEY = "key";
    ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();
    SimpleAdapter adapter;

    /* Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        textStatus = (TextView) findViewById(R.id.wifiTextView);
        buttonScan = (Button) findViewById(R.id.wifi_scan_button);
        buttonScan.setOnClickListener(this);
        lv = (ListView)findViewById(R.id.wifi_listView);

        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled() == false)
        {
            Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }

        String[] from =new String[] { "SSID","BSSID" };
        int [] to=new int[] { R.id.griditem1,R.id.griditem2};

        this.adapter = new SimpleAdapter(this, arraylist, R.layout.grid_item, from, to) ;  //R.id.list_value });
        lv.setAdapter(this.adapter);

        registerReceiver(new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context c, Intent intent)
            {
                results = wifi.getScanResults();
                size = results.size();
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    public void onClick(View view)
    {
        arraylist.clear();
        wifi.startScan();

        Toast.makeText(this, "Scanning...." + size, Toast.LENGTH_SHORT).show();
        try
        {
            size = size - 1;
            while (size >= 0)
            {
                HashMap<String, String> item = new HashMap<String, String>();


                //String displayItem=results.get(size).SSID + "  " + results.get(size).capabilities;
                checkIfExist(results.get(size).SSID);
                item.put(results.get(size).SSID, results.get(size).BSSID);
                Log.d(TAG, results.get(size).BSSID);

                HashMap<String,String> hashMap=new HashMap<>();
                hashMap.put("SSID",results.get(size).SSID);
                hashMap.put("BSSID",results.get(size).BSSID);
                arraylist.add(hashMap);
                size--;
                adapter.notifyDataSetChanged();
            }
        }
        catch (Exception e)
        { }
    }

    public void checkIfExist(String ssid){

    }
}