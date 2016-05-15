package com.example.chrome.helloworld;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;

/**
 * Created by chrome on 16/05/14.
 */
public class BluetoothUtil {

    public static String getUUID(){
        String mac = BluetoothAdapter.getDefaultAdapter().getAddress();
        return mac;
    }



}
