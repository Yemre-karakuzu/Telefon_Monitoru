package com.example.telefonmonitruygulamasi;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class BattaryServis extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationManager notif=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String kanalId="battaryNotif";
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            NotificationChannel battarykanal=new NotificationChannel(kanalId,"battarykanal",NotificationManager.IMPORTANCE_HIGH);
            notif.createNotificationChannel(battarykanal);
        }
        PendingIntent pending=PendingIntent.getActivity(this,0,new Intent(this,BatteryActivity.class),PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notifbuild=new NotificationCompat.Builder(this,"battaryNotif").setContentIntent(pending).setSmallIcon(R.drawable.ic_warning_black_24dp).setContentTitle("Batarya").setContentText("Bataryaniz %10 altına indi Lütfen Kontrol ediniz.");
        notif.notify(1,notifbuild.build());
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
