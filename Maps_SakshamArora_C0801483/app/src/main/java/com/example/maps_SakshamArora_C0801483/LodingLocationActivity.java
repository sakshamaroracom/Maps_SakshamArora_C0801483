package com.example.maps_SakshamArora_C0801483;


import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;

import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

@SuppressWarnings("deprecation")
public class LodingLocationActivity extends AppCompatActivity {

    final int REQUEST_LOCATION = 1;
    boolean gpsStatus;
    private GoogleApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_location);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    LocationManager manager = (LocationManager) LodingLocationActivity.this.getSystemService(Context.LOCATION_SERVICE);
                    gpsStatus = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    if (!gpsStatus) {
                        locationRequest();
                    } else {
                        Intent intent = new Intent(LodingLocationActivity.this, MarkerActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },2000);

    }

    private void locationRequest() {
        if (apiClient == null) {
            apiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {
                            Log.d("Location error ", connectionResult.getErrorCode() + "");
                        }
                    }).build();
            apiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(apiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(@NonNull LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    if (status.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                        try {
                            status.startResolutionForResult(LodingLocationActivity.this, REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {

                            Toast.makeText(LodingLocationActivity.this, "Errror", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        } else {
            Toast.makeText(this, "Enable location manually", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOCATION) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(LodingLocationActivity.this, MarkerActivity.class);
                startActivity(intent);
                finish();
            } else {
                finish();
            }
        }

    }

}