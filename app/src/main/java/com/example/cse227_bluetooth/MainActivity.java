package com.example.cse227_bluetooth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    
    BluetoothAdapter bluetoothAdapter;

    private ListView listview;
    private ArrayAdapter Adapter;
    boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void enalbleBluetooth(View view) {
        
        if (bluetoothAdapter!= null)
        {
            Toast.makeText(this, "Bluetooth Supported", Toast.LENGTH_SHORT).show();
            if (!bluetoothAdapter.isEnabled())
            {

                Intent i = new Intent();
                i.setAction(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(i,1);
            }
        }
        else
            Toast.makeText(this, "Device not support bluetooth", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode==1) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Bluetooth Turned onn", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Bluetooth turn onn cancelled by user", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode==3)
        {
            if (resultCode == 100)
            {
                Toast.makeText(this, "Bluetooth Turned onn", Toast.LENGTH_SHORT).show();
            }
        else if (resultCode== RESULT_CANCELED)
            {
                Toast.makeText(this, "Bluetooth turn onn cancelled by user", Toast.LENGTH_SHORT).show();
            } }
    }

    public void diableBluetooth(View view) {
        if (bluetoothAdapter != null)
        {
            if (bluetoothAdapter.isEnabled()){
                if (bluetoothAdapter.disable()){
                    Toast.makeText(this, "Bluetooth Turned Off ", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter stateFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(stateReceiver,stateFilter);

        IntentFilter foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(foundReciever,foundFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(stateReceiver);
        unregisterReceiver(foundReciever);
        bluetoothAdapter.cancelDiscovery();
    }

    BroadcastReceiver foundReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            ArrayList list = new ArrayList();

            String action = intent.getAction();
            list.clear();
            Adapter.notifyDataSetChanged();
            if (action.equals(BluetoothDevice.ACTION_FOUND))
            {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String name = device.getName();
                String macAddress = device.getAddress();

                list.add("Name: "+name+"MAC Address: "+macAddress);

            }

            Adapter.notifyDataSetChanged();

        }
    };
    BroadcastReceiver stateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED))
            {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,BluetoothAdapter.ERROR);
                switch (state)
                {
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Toast.makeText(context, "TURNING Onn", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Toast.makeText(context, "ONN", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Toast.makeText(context, "Turning Off", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        Toast.makeText(context, "Off", Toast.LENGTH_SHORT).show();


                }
            }

        }
    };

    public void getPairedDevices(View view) {

        ArrayList list = new ArrayList();

        if (bluetoothAdapter != null)
        {
            if (bluetoothAdapter.isEnabled())
            {
                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                if (pairedDevices.size() > 0)
                {
                    for (BluetoothDevice device : pairedDevices)
                    {
                        String name = device.getName();
                        String macAddress = device.getAddress();

                        list.add("Name: "+name+"MAC Address: "+macAddress);
                        //Toast.makeText(this, name+" "+macAddress, Toast.LENGTH_SHORT).show();
                    }

                    listview = (ListView) findViewById(R.id.list);
                    Adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, list);
                    listview.setAdapter(Adapter);

                }
            }
        }
    }

    void checkLocationPermission()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
        {
            flag = true;
        }
        else
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},2);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 2)
        {
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                flag = true;
            }
        }
    }

    public void discoverDevice(View view) {

        checkLocationPermission();

        if (bluetoothAdapter != null)
        {
            if (bluetoothAdapter.isEnabled())
            {
                if (flag==true) {

                    boolean dis = bluetoothAdapter.startDiscovery();
                    Toast.makeText(this, "Start Discovery " + dis, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void makediscoverable(View view) {
        if (bluetoothAdapter != null)
        {
            Intent i =new Intent();
            i.setAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            i.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,100);
            startActivityForResult(i,3);
        }
    }
}