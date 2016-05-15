package com.example.chrome.helloworld;

import android.app.NotificationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by chrome on 16/05/14.
 */
public class BlockActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_block);
        deleteNotification();
    }

    /* delete notification */
    private void deleteNotification(){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(FetchService.NOTIF_ID);
    }




}
