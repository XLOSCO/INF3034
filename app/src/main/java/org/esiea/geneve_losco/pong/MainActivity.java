package org.esiea.geneve_losco.pong;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    int affichage;
    int hour;
    int minute;
    private static Timer timer;
    Calendar rightNow = Calendar.getInstance();
    int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
    int currentMinute = rightNow.get(Calendar.MINUTE);
    int currentSecond = rightNow.get(Calendar.SECOND);
    SharedPreferences.Editor editor;
    RecyclerView rv;

    public JSONArray getBeersFromFiles(){
        try{
            InputStream is = new FileInputStream(getCacheDir()+"/"+"bieres.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            return new JSONArray(new String(buffer,"UTF-8"));
        }catch (IOException e){
            e.printStackTrace();
            return new JSONArray();
        } catch (JSONException e) {
            e.printStackTrace();
        } return new JSONArray();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sp = getSharedPreferences("Global", Activity.MODE_PRIVATE);
        editor = sp.edit();

        hour = sp.getInt("hour", 12);
        minute = sp.getInt("minute", 0);

        long notif_pop = ((hour - currentHour)*3600 + (minute - currentMinute)*60 - currentSecond)*1000; // calcul du delai
        if (notif_pop < 0) {
            notif_pop = 86400000 + notif_pop;
        }

        timer = new Timer();
        timer.schedule(new MaTask(), notif_pop);



        affichage=0;
        findViewById(R.id.reglageszone).setVisibility(View.GONE);
        findViewById(R.id.favoriszone).setVisibility(View.GONE);
        findViewById(R.id.listezone).setVisibility(View.VISIBLE);

        final TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        final Button listebutton = (Button) findViewById(R.id.listebutton);
        listebutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                affichage=0;
                findViewById(R.id.reglageszone).setVisibility(View.GONE);
                findViewById(R.id.listezone).setVisibility(View.VISIBLE);
                findViewById(R.id.favoriszone).setVisibility(View.GONE);
            }
        });

        final Button favorisbutton = (Button) findViewById(R.id.favorisbutton);
        favorisbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                affichage=1;
                findViewById(R.id.reglageszone).setVisibility(View.GONE);
                findViewById(R.id.listezone).setVisibility(View.GONE);
                findViewById(R.id.favoriszone).setVisibility(View.VISIBLE);
            }
        });

        final Button reglagesbutton = (Button) findViewById(R.id.reglagesbutton);
        reglagesbutton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onClick(View v) {
                affichage=2;
                findViewById(R.id.reglageszone).setVisibility(View.VISIBLE);
                findViewById(R.id.listezone).setVisibility(View.GONE);
                findViewById(R.id.favoriszone).setVisibility(View.GONE);
                timePicker.setHour(hour);
                timePicker.setMinute(minute);
            }
        });

        final Button enregbutton = (Button) findViewById(R.id.enregbutton);
        enregbutton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onClick(View v) {
                rightNow = Calendar.getInstance();
                currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
                currentMinute = rightNow.get(Calendar.MINUTE);
                currentSecond = rightNow.get(Calendar.SECOND);

                hour=timePicker.getHour();
                minute=timePicker.getMinute();

                editor.putInt("hour", hour);
                editor.putInt("minute", minute);
                editor.commit();
                timer.purge();
                long notif_pop = ((hour - currentHour)*3600 + (minute - currentMinute)*60 - currentSecond)*1000;
                if(notif_pop < 0) {
                    notif_pop = 86400000 + notif_pop;
                }
                timer = new Timer();
                timer.schedule(new MaTask(), notif_pop);
                affichage=0;
                findViewById(R.id.reglageszone).setVisibility(View.GONE);
                findViewById(R.id.listezone).setVisibility(View.VISIBLE);
                findViewById(R.id.favoriszone).setVisibility(View.GONE);
            }
        });

    }

    public void notification_test() {
        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this).setContentText("Voici la bière du jour").setContentTitle("Bière du jour").setSmallIcon(R.mipmap.ic_launcher);
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1, builder.build());
    }

    public class MaTask extends TimerTask {
        @Override
        public void run() {
            rightNow = Calendar.getInstance();
            currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
            currentMinute = rightNow.get(Calendar.MINUTE);
            currentSecond = rightNow.get(Calendar.SECOND);
            long notif_pop = ((hour - currentHour)*3600 + (minute - currentMinute)*60 - currentSecond)*1000;
            if(notif_pop != 0) {
                return;
            }
            notification_test();
        }
    }

}

