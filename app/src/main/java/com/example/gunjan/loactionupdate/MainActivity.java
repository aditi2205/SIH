package com.example.gunjan.loactionupdate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.location.*;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.support.v4.content.ContextCompat;
import android.content.pm.PackageManager;
import android.widget.Toast;
import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.os.Handler;
import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements LocationListener {

    private TextView latitudePosition;
    private TextView longitudePosition;
    private TextView currentCity;
    private LocationManager locationManager;
    private Location location;
    private final int REQUEST_LOCATION = 200;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        latitudePosition = (TextView) findViewById(R.id.latitude);
        longitudePosition = (TextView) findViewById(R.id.longitude);
        currentCity = (TextView) findViewById(R.id.city);
        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           // ContextCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            Toast.makeText(MainActivity.this,"permission not given",Toast.LENGTH_SHORT);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 2, this);
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (location != null) {
                latitudePosition.setText(String.valueOf(location.getLatitude()));
                longitudePosition.setText(String.valueOf(location.getLongitude()));
                getAddressFromLocation(location, getApplicationContext(), new GeoCoderHandler());
            }
        } else {
           // showGPSDisabledAlertToUser();
            Toast.makeText(MainActivity.this,"provider is disabled",Toast.LENGTH_SHORT);

        }
    }
    @Override
    public void onProviderEnabled(String provider) {
    }
    @Override
    public void onProviderDisabled(String provider) {
        if (provider.equals(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(MainActivity.this,"provider is disabled",Toast.LENGTH_SHORT);
        }
    }
    @Override
    public void onLocationChanged(Location location) {
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    public static void getAddressFromLocation(final Location location, final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List<Address> list = geocoder.getFromLocation(
                            location.getLatitude(), location.getLongitude(), 1);
                    if (list != null && list.size() > 0) {
                        Address address = list.get(0);
                        // sending back first address line and locality
                        result = address.getAddressLine(0) + ", " + address.getLocality() + ", " +  address.getCountryName() ;
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Impossible to connect to Geocoder", e);
                } finally {
                    Message msg = Message.obtain();
                    msg.setTarget(handler);
                    if (result != null) {
                        msg.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("address", result);
                        msg.setData(bundle);
                    } else
                        msg.what = 0;
                    msg.sendToTarget();
                }
            }
        };
        thread.start();
    }
    private class GeoCoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String result;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    result = bundle.getString("address");
                    break;
                default:
                    result = null;
            }
           // currentCity.setText(result);
            Calendar calendar = Calendar.getInstance();
           // calendar.setTime(yourdate);
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            currentCity.setText("TIME: "+hours+" DATE: "+day+"/"+month+"/"+year);




        }
    }
}
