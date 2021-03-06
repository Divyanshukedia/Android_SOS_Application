package com.example.sos;

import static android.Manifest.permission.CALL_PHONE;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaParser;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationSettingsRequest;

import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {

    Button b1,b2;
    private FusedLocationProviderClient client;
    Database myDB;
    private final int REQUEST_CHECK_CODE =8989;
    private LocationSettingsRequest.Builder builder;
    String x = "";
    String y = "";
    private static final int REQUEST_LOCATION = 1;

    LocationManager locationManager;
    Intent mIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        b1 = findViewById(R.id.button);
        b2 = findViewById(R.id.button2);
        myDB = new Database(this);
        final MediaPlayer mp = MediaPlayer.create(getApplicationContext(),R.raw.sound);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            onGPS();
        }

        else{
            startTrack();
        }
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),Register.class);
                startActivity(i);
            }
        });

        b2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mp.start();
                Toast.makeText(MainActivity2.this, "PANIC BUTTON STARTED", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();

            }
        });

        }

    private void loadData() {

        ArrayList<String> thelist = new ArrayList<>( );
        Cursor data = myDB.getListContents();
        if (data.getCount()==0){
            Toast.makeText(this, "no content to show", Toast.LENGTH_SHORT).show();
        }
        else{
            String msg = "I NEED HELP LATITUDE:" +x+"LONGITUDE:"+y;
            String number = "";

            while(data.moveToNext()){
                thelist.add(data.getString(1));
                number = number+data.getString(1)+(data.isLast()?"":";");
                call();
            }
            if(! thelist.isEmpty()){
                sendSms(number,msg,true);
            }

        }
    }

    private void sendSms(String number, String msg, boolean b) {
        Intent smsIntent = new Intent((Intent.ACTION_SENDTO) );
        Uri.parse("smsto:"+number);
        smsIntent.putExtra("smsbody",msg);
        startActivity(smsIntent);
    }

    private void call() {
        Intent i = new Intent(Intent.ACTION_CALL);
        i.setData(Uri.parse("tel:1000"));

        if (ContextCompat.checkSelfPermission(getApplicationContext(),CALL_PHONE)== PackageManager.PERMISSION_GRANTED)
        {
            startActivity( i );
        }

        else
        {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M);{
                requestPermissions(new String[]{CALL_PHONE},1);
            }
        }

    }


    private void startTrack() {
        if (ActivityCompat.checkSelfPermission(MainActivity2.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (MainActivity2.this,Manifest.permission.ACCESS_COARSE_LOCATION )!=PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);

        }
        else{
            Location locationgps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(locationgps != null){
                double lat = locationgps.getLatitude();
                double lon = locationgps.getLongitude();
                x = String.valueOf(lat);
                y = String.valueOf(lon);
            }
            else{
                Toast.makeText(this, "UNABLE TO FIND LOCATION", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}