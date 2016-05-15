package com.example.chrome.helloworld;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ArrayAdapter<String> arrayAdapter;

    IntentFilter filter;
    BluetoothAdapter adapter;
    ArrayList<String> devices;
    Handler handler;

    @Override
    protected void onCreate(final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent2 = new Intent(this,FetchService.class);
        startService(intent2);

        listView = (ListView) findViewById(R.id.listView);

        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);

        adapter = BluetoothAdapter.getDefaultAdapter();
        devices = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,devices);
        handler = new Handler();
        listView.setAdapter(arrayAdapter);

        if(adapter==null){
            Toast.makeText(this,"No bluetooth",Toast.LENGTH_SHORT).show();
        }
        if(!adapter.isEnabled()){
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent,1);
        }
        if(adapter.isDiscovering()){
            adapter.cancelDiscovery();
        }


        registerReceiver(mReciever,filter);
        adapter.startDiscovery();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String uuid = devices.get((int) id);
                SharedPreferences sp = MainActivity.this.getSharedPreferences("data",MODE_PRIVATE);
                String myName = sp.getString("name",null);
                if(myName==null){
                    Toast.makeText(MainActivity.this,"MY NAME NULL",Toast.LENGTH_SHORT).show();
                }else{
                    AttackTask task = new AttackTask(myName,uuid);
                    task.execute((Void) null);
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Log.d("Main","Bluetooth turned on");
                adapter.startDiscovery();
            }
        }
    }

    @Override
    public void onDestroy(){
        this.unregisterReceiver(mReciever);
        adapter.cancelDiscovery();
        super.onDestroy();
    }



    private final BroadcastReceiver mReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("device","scand");
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                System.out.println(device);
                handler.post(new Runnable(){
                    @Override
                    public void run() {
                        devices.add(device.toString());
                        arrayAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
    };

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    class AttackTask extends AsyncTask<Void, Void, Boolean> {

        private final String mName;
        private final String mUuid;

        AttackTask(String name,String uuid){
            mName =name;
            mUuid = uuid;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            // Simulate network acces
            int result = HttpUtil.attack(mName,mUuid);
            Log.d("result",""+result);
            if(result==1) {
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                Intent intent = new Intent(MainActivity.this,AttackActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(MainActivity.this,"Attack Failed!",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
        }
    }



}
