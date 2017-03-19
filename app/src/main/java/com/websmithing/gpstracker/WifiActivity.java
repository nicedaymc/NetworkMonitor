package com.websmithing.gpstracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Activity;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class WifiActivity extends Activity implements View.OnClickListener
{
    private static final String TAG = "WifiActivity";
    WifiManager wifi;
    ListView lv, selectedlv;
    TextView textStatus;
    Button buttonScan, buttonAddWiFi;
    int size = 0;
    List<ScanResult> results;
    int selectedPosition=-1;
    int selectedToRemove=-1;


    ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> selectedArraylist = new ArrayList<HashMap<String, String>>();
    SimpleAdapter adapter, selectedAdapter;




    /* Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        textStatus = (TextView) findViewById(R.id.wifiTextView);
        buttonScan = (Button) findViewById(R.id.wifi_scan_button);
        buttonScan.setOnClickListener(this);
        buttonAddWiFi=(Button) findViewById(R.id.select_ap_button);
        buttonAddWiFi.setOnClickListener(this);
        lv = (ListView)findViewById(R.id.wifi_listView);
        selectedlv=(ListView)findViewById(R.id.selected_WiFi);

        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled() == false)
        {
            Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }

        String[] from =new String[] { "SSID","BSSID" };
        int [] to=new int[] { R.id.griditem1,R.id.griditem2};

        this.adapter = new SimpleAdapter(this, arraylist, R.layout.grid_item, from, to) ;  //R.id.list_value });
        lv.setAdapter(this.adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                selectedPosition=position;
               // Toast.makeText(WifiActivity.this, "" + position, Toast.LENGTH_SHORT).show();
            }
        });
        this.selectedAdapter=new SimpleAdapter(this, arraylist, R.layout.grid_item, from, to) ;
        selectedlv.setAdapter(this.adapter);
        selectedlv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                selectedToRemove=position;
            }
        });

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

    public void onClick(View v)
    {
        if (v.getId() == R.id.wifi_scan_button) {

            //while (size<=0) {
            arraylist.clear();
            wifi.startScan();
            Log.e(TAG, "Scanned Wifi:" + size);

            Toast.makeText(this, "Scanning...." + size, Toast.LENGTH_SHORT).show();
            try {
                size = size - 1;
                while (size >= 0) {

                    checkIfExist(results.get(size).SSID);

                    if (!checkIfExist(results.get(size).SSID)) {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("SSID", results.get(size).SSID);
                        hashMap.put("BSSID", results.get(size).BSSID);
                        arraylist.add(hashMap);
                        adapter.notifyDataSetChanged();
                    }
                    size--;

                }
            } catch (Exception e) {

            }
        }else if (v.getId()==R.id.select_ap_button){

        }

    }

    public boolean checkIfExist(String ssid){
        for (HashMap item:arraylist){
            if (item.get("SSID").equals(ssid)){
                return true;
            }
        }
        return false;

    }

    private boolean saveUserSettings() {

        return false;

    }


}