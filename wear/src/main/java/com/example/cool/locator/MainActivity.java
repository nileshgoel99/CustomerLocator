package com.example.cool.locator;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.TextView;

import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;

public class MainActivity extends Activity {

    int notificationId = 1;
    NotificationManager notificationManager = null;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MapsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
      //  setContentView(R.layout.activity_main);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("Fidelity Offers");
        builder.setContentText("Retirement Plan Just For You within a mile!!");
        builder.setSmallIcon(R.drawable.location, 5);
        builder.setTicker("Fidelity Investment Center");
        builder.setContentIntent(pendingIntent);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.map));
        //builder.extend(new NotificationCompat.WearableExtender().setBackground(bitmap));
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

       // final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        notificationManager.notify(notificationId, builder.build());

      /*  stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        }); */
    }

    public void getMap(View view){
        System.out.println("CliCk Get map-------------->");
        Intent intent;
        intent = new Intent(this, MapsNew.class);
    }
}
