package com.example.telefonmonitruygulamasi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private SQLiteDatabase sensorData;
    private Sensor sensor;
    private int battaryLevel;
    private double sensorDeger;
    private Runnable zamanlayici;
    private ListView holder;
    private ArrayList<String> List;
    private android.os.Handler handler;
    private BroadcastReceiver battaryReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            battaryLevel=intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorData=this.openOrCreateDatabase("SensorData",MODE_PRIVATE,null);
        sensorData.execSQL("CREATE TABLE IF NOT EXISTS sensorler(id INTEGER PRIMARY KEY,sicaklik INTEGER NOT NULL,bataryaseviyesi INTEGER NOT NULL,zaman DATETIME DEFAULT CURRENT_TIMESTAMP)");
        Cursor cursor=sensorData.rawQuery("SELECT * FROM sensorler",null);
        holder=this.findViewById(R.id.listview1);
        List=new ArrayList<>();
        List.add("battary");
        List.add("temp");
        handler=new Handler();
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor=sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        holder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position==0)
                {
                    Intent intent=new Intent(MainActivity.this,BatteryActivity.class);
                    MainActivity.this.startActivity(intent);
                }
                else
                {
                    Intent intent=new Intent(MainActivity.this,TempActivity.class);
                    MainActivity.this.startActivity(intent);
                }
            }
        });
        zamanlayici=new Runnable() {
            @Override
            public void run() {
                MainActivity.this.registerReceiver(battaryReceiver,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                List.set(0,"Battary "+String.valueOf(battaryLevel)+"%");//BATARYA Vüzde verisi
                List.set(1,"Temp "+String.valueOf((int) sensorDeger)+"°C");
                sensorData.execSQL("INSERT INTO sensorler(sicaklik,bataryaseviyesi) VALUES("+(int)sensorDeger+","+List.get(0).replace("Battary ","").replace("%","")+")");
                final ArrayAdapter<String> Adapter=new ArrayAdapter<String>(MainActivity.this.getApplicationContext(),android.R.layout.simple_list_item_1,List);
                if (sensorDeger<=-29 || sensorDeger>=49)
                {
                    Intent intent1=new Intent(MainActivity.this,TempServis.class);
                    MainActivity.this.startService(intent1);
                }
                if (battaryLevel<=10)
                {
                    Intent intent2=new Intent(MainActivity.this,BattaryServis.class);
                    MainActivity.this.startService(intent2);
                }

                holder.setAdapter(Adapter);
                handler.postDelayed(zamanlayici,10000);

            }
        };
        handler.postDelayed(zamanlayici,100);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        sensorDeger=event.values[0];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
