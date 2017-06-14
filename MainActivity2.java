package com.example.mareeswaran_m.bixolonconnectsample;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_REQUEST_CONSTANT =1 ;
    private static final int REQUEST_ENABLE_BT=2;
    private ListView listView;
    private ArrayList<String> mDeviceList = new ArrayList<String>();
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //since i was working with appcompat, i used ActivityCompat method, but this method can be called easily from Activity subclassess.
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
                 ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSION_REQUEST_CONSTANT);

        listView = (ListView) findViewById(R.id.pairedList);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter == null){
            Toast.makeText(this, "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
            finish();
        }

        if(!mBluetoothAdapter.isEnabled()){
            //   Toast.makeText(this, "Please enable Bluetooth and retry", Toast.LENGTH_SHORT).show();
            //  finish();
            Toast.makeText(this, "Please enable Bluetooth and retry", Toast.LENGTH_SHORT).show();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }


        if(mBluetoothAdapter != null) {
            mBluetoothAdapter.startDiscovery();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopScan();
                }
            }, 15000);
        }

        //mBluetoothAdapter.startDiscovery();

        //Add the filter for possible event trigred by broadcast
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction("android.bluetooth.device.action.PAIRING_REQUEST");
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);


       // IntentFilter filter2 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(mReceiver, filter);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_ENABLE_BT)
        {
            if(resultCode==RESULT_OK)
                Toast.makeText(MainActivity.this,"BT Enabled",Toast.LENGTH_LONG).show();
            else
                Toast.makeText(MainActivity.this,"BT Not Enabled",Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(MainActivity.this,"BT Not Enabled > ERROR",Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }


    public void stopScan(){
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
        }
    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            Log.i("BT1", "Action is "+action);

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action))
            {
                Toast.makeText(context,"Discovery Started",Toast.LENGTH_SHORT).show();
            }
            else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Toast.makeText(context,"Device Found",Toast.LENGTH_SHORT).show();
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mDeviceList.add(device.getName() + "\n" + device.getAddress());
                Log.i("BT2", device.getName() + "\n" + device.getAddress());
                listView.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, mDeviceList));
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
                Toast.makeText(context,"DISCOVERY_FINISHED",Toast.LENGTH_SHORT).show();
            }
        }
    };

}