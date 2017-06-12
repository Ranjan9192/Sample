package com.example.mareeswaran_m.bixolonconnectsample;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import static android.R.attr.action;

public class MainActivity extends Activity {

    private ListView mPairedList;
    ArrayList<String> pairedDeviceList;
    private BluetoothAdapter mBluetoothAdapter;
    private TextView mPairedListStatus;
    private static final int REQUEST_ENABLE_BT=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPairedList = (ListView) findViewById(R.id.pairedList);
        mPairedListStatus = (TextView) findViewById(R.id.listStatus);
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

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction("android.bluetooth.device.action.PAIRING_REQUEST");
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        registerReceiver(mReceiver,filter);

        if(mBluetoothAdapter != null) {
            mBluetoothAdapter.startDiscovery();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //stopScan();
                }
            }, 15000);
        }

        //pairedDeviceList=
        pairedDeviceList = getPairedDeviceList();

        if(pairedDeviceList != null &&  pairedDeviceList.size()>0) {
            ArrayAdapter<String> pairedDeviceListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, pairedDeviceList);
            mPairedList.setAdapter(pairedDeviceListAdapter);

        }
        else{
            mPairedListStatus.setText("No paired bluetooth device is available");
            Toast.makeText(this, "No paired bluetooth device is available", Toast.LENGTH_SHORT).show();
        }

        mPairedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(pairedDeviceList!=null){
                    String bluetoothAddress = pairedDeviceList.get(position);
                    moveToStatusScreen(bluetoothAddress);
                }
            }
        });
    }

    private ArrayList<String> getPairedDeviceList(){
        ArrayList<String> pairedList = new ArrayList<String>();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices == null || pairedDevices.size() == 0) {
            return null;
        } else {
            Iterator<BluetoothDevice> devices = pairedDevices.iterator();
            while (devices.hasNext()) {
                BluetoothDevice device = devices.next();
                pairedList.add(device.getAddress());
            }
            return pairedList;
        }
    }

    private void moveToStatusScreen(String bluetoothAddress){
        Intent intent = new Intent(this, PrinterStatusActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("address", bluetoothAddress);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    public void stopScan(){
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
        }
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
                Toast.makeText(MainActivity.this,"Not OK ERROR",Toast.LENGTH_LONG).show();
            }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals("android.bluetooth.device.action.PAIRING_REQUEST"))
            {

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                Toast.makeText(MainActivity.this,"OnRecieve PAIRING_REQUEST",Toast.LENGTH_SHORT).show();
           /*     if (device.getAddress().equals(printerAddress)) {
                    //           setBluetoothPairingPin(device, printerPin);
                } else if (device.getAddress().equals(blackboxAddress)) {
                    //         setBluetoothPairingPin(device, blackboxPin);
                }*/

            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                /*if (device.getAddress().equalsIgnoreCase(printerAddress)) {

                } else*/ if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {

                    Toast.makeText(MainActivity.this,"OnRecieve ACTION_BOND_STATE_CHANGED",Toast.LENGTH_SHORT).show();

                    final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                    final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);
                    BluetoothDevice device1 = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                        // Pairing Success
                    } else if (state == BluetoothDevice.BOND_NONE) {
                        //Pairing Failure
                    }
                }
            }
        }
};


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mReceiver!=null)
        unregisterReceiver(mReceiver);
    }
}
