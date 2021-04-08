package com.example.maps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.BreakIterator;


public class MainActivity extends AppCompatActivity {

    private boolean mLocationPermissionGranted;
    private LocationRequest mLocationRequest;
    private TextView longitude;
    private TextView latitude;
    private Button myLocationBtn;
    private TextView latInstantanee;
    private TextView lgInstantanee;


    public static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static int REQUEST_CHECK_SETTINGS = 0;
    private FusedLocationProviderClient mFusedLocationClient;

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {
                Log.i("XXXX", "Lat " + location.getLatitude() + " long : " + location.getLongitude());
            }
        }

        ;
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myLocationBtn = findViewById(R.id.myLocation_btn);
        myLocationBtn.setOnClickListener(this::seeMyLocation);

        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        latInstantanee = findViewById(R.id.lat_instantane);
        lgInstantanee = findViewById(R.id.lg_instantane);


        getLocationPermission();

        if (mLocationPermissionGranted) {

            retrieveLocation();

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest
                    .Builder()
                    .addLocationRequest(createLocationRequest());

            SettingsClient client = LocationServices.getSettingsClient(this);
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

            task.addOnSuccessListener(this,
                    response -> {
                        startLocationUpdates();
                    });


            task.addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    if (e instanceof ResolvableApiException) {
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(MainActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                        }
                    }
                }
            });


        }
    }

    private void seeMyLocation(View view) {
        if (!mLocationPermissionGranted)
            getLocationPermission();
        else
            retrieveLocation();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    private void stopLocationUpdate() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    protected LocationRequest createLocationRequest() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;

    }


    protected void retrieveLocation() {

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    205);
            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {

                longitude.setText("Longitude : " + location.getLongitude());
                latitude.setText("Latitude : " + location.getLatitude());

                Log.i("xxx", " Latitude : " + location.getLatitude() + "\tLongitude : " + location.getLongitude());
            }
        });
        }


    private void getLocationPermission() {

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mLocationPermissionGranted = true;

        }
        else{
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String permissions[], @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case 205: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    retrieveLocation();
                }
            }
        }
    }

}