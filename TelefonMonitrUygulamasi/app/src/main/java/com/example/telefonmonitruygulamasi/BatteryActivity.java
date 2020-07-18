package com.example.telefonmonitruygulamasi;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class BatteryActivity extends AppCompatActivity {
    private SQLiteDatabase sorgu;
    private EditText baslangic;
    private EditText bitis;
    private GraphView graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery);
        baslangic = findViewById(R.id.baslangiceditText);
        bitis = findViewById(R.id.bitiseditText);
        sorgu = this.openOrCreateDatabase("SensorData", MODE_PRIVATE, null);
        graph = (GraphView) findViewById(R.id.graph);

    }

    public void getir(View saat) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String tarihBaslangic = baslangic.getText().toString();
        String tarihBitis = bitis.getText().toString();
        Cursor cursor = sorgu.rawQuery("SELECT * FROM sensorler Where zaman BETWEEN '" + tarihBaslangic + "' and '" + tarihBitis + "' ORDER BY zaman", null);
        if (cursor != null) {

            if (cursor.moveToFirst()) {

                LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
                ArrayList<DataPoint> veri = new ArrayList<>();
                do {
                    int bataryaSeviyesi = cursor.getInt(cursor.getColumnIndex("bataryaseviyesi"));
                    String tarih = cursor.getString(cursor.getColumnIndex("zaman"));
                    try {
                        Date tarihh = format.parse(tarih);
                        veri.add(new DataPoint(tarihh.getTime(),bataryaSeviyesi));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                } while (cursor.moveToNext());

                cursor.close();
                DataPoint[] veriler = new DataPoint[veri.size()];
                for (int i = 0; i < veri.size(); i++) {
                    veriler[i] = veri.get(i);
                }
                series = new LineGraphSeries<DataPoint>(veriler);
                graph.addSeries(series);
            }
        }
    }
}

