package com.example.chrome.helloworld;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by chrome on 16/05/14.
 */
public class FetchService extends Service {

    Timer fetchTimer;
    FetchTask fetchTask;

    Timer limitTimer;
    LimitTask task;


    public final static int NOTIF_ID = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("FetchService","onCreate()");

        /* when created, start fetch thread */
        fetchTask = new FetchTask();
        fetchTimer = new Timer();
        fetchTimer.schedule(fetchTask,0,1000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /* notification */
    private void createNotification(){
        /* open block-activity */
        Intent intent = new Intent(this, BlockActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setContentIntent(contentIntent);
        builder.setContentTitle("abc");
        builder.setAutoCancel(true);
        builder.setTicker("abc");

        /*
        Intent intent = new Intent(FetchService.this,BlockActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(FetchService.this,0,intent,0);
        Notification notif = new NotificationCompat.Builder(FetchService.this)
                .setContentTitle("Attacked")
                .setContentText("want to block?")
                .addAction(android.R.drawable.ic_menu_save,"Block",pendingIntent)
                .setSmallIcon(android.R.drawable.alert_light_frame)
                .build();
        */

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIF_ID,builder.build());
    }





    /* fetch server task */
    class FetchTask extends TimerTask{

        @Override
        public void run() {
            /* server fetch */
            SharedPreferences sp = FetchService.this.getSharedPreferences("data",MODE_PRIVATE);
            String name = sp.getString("name","");
            int result = HttpUtil.attacked(name);
            Log.d("result",""+result);
            if( result == 1 ){
                /* we are attacked */
                /* create notification */
                //  createNotification();
                Intent intent = new Intent(FetchService.this,BlockActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                /* start timer */
                fetchTimer.cancel();
                fetchTimer = null;
                limitTimer = new Timer();
                limitTimer.schedule(new LimitTask(),0,1000);
            }
        }
    }


    class LimitTask extends TimerTask{

        int remainTime;

        public LimitTask(){
            remainTime = 10;
        }

        @Override
        public void run() {
            remainTime -=1;
            System.out.println("time"+remainTime+"[ms]");
            if(remainTime<=0){
                limitTimer.cancel();
                limitTimer = null;
                SharedPreferences sp = getSharedPreferences("data",MODE_PRIVATE);
                String name = sp.getString("name","");
                int result = HttpUtil.cantBlock(name);
                /* reboot */
                /* fetchTimer.schedule(fetchTask,0,1000); */

            }
        }
    }





}
