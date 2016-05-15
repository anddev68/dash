package com.example.chrome.helloworld;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by chrome on 16/05/14.
 */
public class AttackActivity extends AppCompatActivity {

    TextView textView;
    Handler handler;
    ServerThread thread;
    Timer timer;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_attack);
        handler = new Handler();

        //textView = (TextView) findViewById(R.id.textView);
        //textView.setText("attack machi");


        SharedPreferences sp = AttackActivity.this.getSharedPreferences("data",MODE_PRIVATE);
        String name = sp.getString("name","");

        //thread = new ServerThread();
        //thread.run();
        MyTimerTask task = new MyTimerTask(name);
        timer = new Timer();
        timer.schedule(task,10,1000);

    }

    /**
     * inquire loop
     * INQR
     */
    class ServerThread implements Runnable{

        @Override
        public void run() {
            SharedPreferences sp = AttackActivity.this.getSharedPreferences("data",MODE_PRIVATE);
            String name = sp.getString("name","");
            String uuid = getIntent().getStringExtra("uuid");

            while(true){
                InqrTask task = new InqrTask(name);
                task.execute((Void)null);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }


    }

    /**
     * inqurie timeri
     */
    class MyTimerTask extends TimerTask {

        String mName;
        public MyTimerTask(String name) {
            mName = name;
        }

        @Override
        public void run() {
            int result = HttpUtil.inquireRequest(mName);
            Log.d("result",""+result);

            switch(result){
                case 0:
                    return;
                case 1:
                    openDialog("Alert","Your attack was blocked.");
                    timer.cancel();
                    timer = null;
                    break;
                case 2:
                    openDialog("Congraturation","Your attack was successed.");
                    timer.cancel();
                    timer = null;
            }

        }
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */

    class InqrTask extends AsyncTask<Void, Void, Integer> {

        private final String mName;

        InqrTask(String name){
            mName =name;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            // Simulate network acces
            int result = HttpUtil.inquireRequest(mName);
            Log.d("result",""+result);

            return result;
        }

        @Override
        protected void onPostExecute(final Integer result) {

            switch(result){
                case 0:
                    return;
                case 1:
                    openDialog("Alert","Your attack was blocked.");
                    timer.cancel();
                    timer = null;
                    break;
                case 2:
                    openDialog("Congraturation","Your attack was successed.");
                    timer.cancel();
                    timer = null;
            }
        }

        @Override
        protected void onCancelled() {
        }
    }

    private void end(){
        Intent intent = new Intent(AttackActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }




    /**
     * open dialog
     */
    private void openDialog(final String title,final String mes){
        handler.post(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(AttackActivity.this)
                        .setTitle(title)
                        .setMessage(mes)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                end();
                            }
                        })
                        .show();
            }
        });
    }


}
