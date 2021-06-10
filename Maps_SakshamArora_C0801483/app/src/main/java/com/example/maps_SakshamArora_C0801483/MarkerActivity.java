package com.example.maps_SakshamArora_C0801483;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MarkerActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQUEST_CODE = 1;
    private static final int TOTAL_MARKERS = 4;
    final List<Marker> markers = new ArrayList<>();
    final String[] markersName = {"A", "B", "C", "D"};
    private int currentMarker = 0;
    private GoogleMap map;
    private Marker destinationMarker;
    private Marker polygonMarker;
    private final List<Polyline> polylines = new ArrayList<>();
    private final ArrayList<LatLng> markerLocations = new ArrayList<>();
    private Polygon polygon;
    private Location currentLocation, endLocation, aLocation, bLocation, cLocation, dLocation;
    private DecimalFormat decimalFormat;
    private String streetName, postalCode, city, province;
    private LatLng selectedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        decimalFormat = new DecimalFormat("#.##");
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                setDestination(latLng);
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //int position = (int)(marker.getTag());
                if (Objects.requireNonNull(marker.getSnippet()).equals("center")) {
                    aLocation = new Location("locationA");
                    aLocation.setLatitude(markers.get(0).getPosition().latitude);
                    aLocation.setLongitude(markers.get(0).getPosition().longitude);
                    bLocation = new Location("locationB");
                    bLocation.setLatitude(markers.get(1).getPosition().latitude);
                    bLocation.setLongitude(markers.get(1).getPosition().longitude);
                    cLocation = new Location("locationC");
                    cLocation.setLatitude(markers.get(2).getPosition().latitude);
                    cLocation.setLongitude(markers.get(2).getPosition().longitude);
                    dLocation = new Location("locationD");
                    dLocation.setLatitude(markers.get(3).getPosition().latitude);
                    dLocation.setLongitude(markers.get(3).getPosition().longitude);
                    double distance = aLocation.distanceTo(bLocation) + bLocation.distanceTo(cLocation) + cLocation.distanceTo(dLocation);
                    showAlert("Total Distance " + decimalFormat.format(distance / 1000) + " km");
                } else {
                    endLocation = new Location("endLocation");
                    endLocation.setLatitude(marker.getPosition().latitude);
                    endLocation.setLongitude(marker.getPosition().longitude);

                    if (currentLocation != null) {
                        double distance = currentLocation.distanceTo(endLocation);

                        marker.setSnippet(decimalFormat.format(distance / 1000) + " km");

                        Toast.makeText(MarkerActivity.this, getCompleteAddress(marker.getPosition().latitude, marker.getPosition().longitude), Toast.LENGTH_LONG).show();
                        showAlert("Street Name : " + streetName + "\n" + "Postal Code : " + postalCode + "\n" + "City : " + city + "\n" + "Province : " + province);
                    } else {
                        Toast.makeText(MarkerActivity.this, "", Toast.LENGTH_LONG).show();
                    }
                }

                return false;
            }
        });

        map.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                aLocation = new Location("locationA");
                aLocation.setLatitude(polyline.getPoints().get(0).latitude);
                aLocation.setLongitude(polyline.getPoints().get(0).longitude);
                bLocation = new Location("locationB");
                bLocation.setLatitude(polyline.getPoints().get(1).latitude);
                bLocation.setLongitude(polyline.getPoints().get(1).longitude);

                Toast.makeText(MarkerActivity.this, decimalFormat.format((aLocation.distanceTo(bLocation)) / 1000) + " km", Toast.LENGTH_LONG).show();
                showAlert(decimalFormat.format((aLocation.distanceTo(bLocation)) / 1000) + " km");
            }
        });


        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker arg0) {
                // TODO Auto-generated method stub
                Log.d("System out", "onMarkerDragStart..." + arg0.getPosition().latitude + "..." + arg0.getPosition().longitude);
            }

            @Override
            public void onMarkerDragEnd(Marker arg0) {
                // TODO Auto-generated method stub
                Log.d("System out", "onMarkerDragEnd..." + arg0.getPosition().latitude + "..." + arg0.getPosition().longitude);

                map.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));
                polygon.remove();
                for (Polyline line : polylines) {
                    line.remove();
                }
                polylines.clear();
                if (markers.size() > 3) {
                    drawPolyLine(markers.get(0).getPosition(), markers.get(1).getPosition());
                    drawPolyLine(markers.get(1).getPosition(), markers.get(2).getPosition());
                    drawPolyLine(markers.get(2).getPosition(), markers.get(3).getPosition());
                    drawPolyLine(markers.get(3).getPosition(), markers.get(0).getPosition());
                }
                drawPolygon();
            }

            @Override
            public void onMarkerDrag(Marker arg0) {
                // TODO Auto-generated method stub
                Log.i("System out", "onMarkerDrag...");
            }
        });

        if (checkPermission())
            requestPermission();
        else {
            getLocation();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (selectedLocation != null)
                        setCurrent(selectedLocation);
                    else {
                        selectedLocation = new LatLng(51.213890, -102.462776);
                        setCurrent(selectedLocation);
                    }
                }
            }, 2000);
        }

    }

    private String getCompleteAddress(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();
                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    streetName = returnedAddress.getFeatureName();
                    postalCode = returnedAddress.getPostalCode();
                    province = returnedAddress.getCountryName();
                    city = returnedAddress.getLocality();
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();

                Log.w("location", strReturnedAddress.toString());
            } else {
                Log.w("location", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("location", "Cann't get Address!");
        }
        return strAdd;
    }

    private boolean checkPermission() {
        int isGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return isGranted != PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (checkPermission())
                requestPermission();
            else {
                getLocation();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (selectedLocation != null) {
                            setCurrent(selectedLocation);
                        }
                    }
                }, 2000);
            }
        }
    }

    private void setCurrent(LatLng location) {
        currentLocation = new Location("myLocation");
        currentLocation.setLatitude(location.latitude);
        currentLocation.setLongitude(location.longitude);
        MarkerOptions markerOptions = new MarkerOptions().position(location)
                .title("Your Location")
                .icon(makeCustomMarker(R.drawable.my_location, 0))
                .snippet("You are here");
        map.addMarker(markerOptions);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 8));
    }

    private void setDestination(LatLng location) {
        markerLocations.add(location);
        String label = markersName[currentMarker];
        if (currentMarker == 3) {
            currentMarker = 0;
        } else {
            currentMarker++;
        }
        MarkerOptions markerOptions = new MarkerOptions().position(location)
                .title(label)
                .snippet(label)
                .icon(makeCustomMarker(R.drawable.marker,0))
                .draggable(true);

        if (markers.size() == TOTAL_MARKERS) {
            for (Polyline line : polylines) {
                line.remove();
            }
            polylines.clear();
            resetMap();
        }

        markers.add(map.addMarker(markerOptions));

        if (markers.size() == TOTAL_MARKERS) {
            drawPolygon();
            if (markers.size() > 3) {
                drawPolyLine(markers.get(0).getPosition(), markers.get(1).getPosition());
                drawPolyLine(markers.get(1).getPosition(), markers.get(2).getPosition());
                drawPolyLine(markers.get(2).getPosition(), markers.get(3).getPosition());
                drawPolyLine(markers.get(3).getPosition(), markers.get(0).getPosition());
            }
        }
    }

    private void resetMap() {
        polygonMarker.remove();
        if (destinationMarker != null) {
            destinationMarker.remove();
            destinationMarker = null;
        }
        for (int i = 0; i < TOTAL_MARKERS; i++) {
            markers.get(i).remove();
        }
        markers.clear();
        polygon.remove();
    }

    private void drawPolyLine(LatLng home, LatLng dest) {
        PolylineOptions polylineOptions = new PolylineOptions()
                .add(home, dest)
                .clickable(true)
                .color(Color.RED)
                .width(20)
                .visible(true);
        polylines.add(map.addPolyline(polylineOptions));
    }

    private void drawPolygon() {
        if (TOTAL_MARKERS > markers.size()){
            return;
        }
        PolygonOptions polygonOptions = new PolygonOptions()
                .clickable(true)
                .strokeColor(R.color.red)
                .strokeWidth(20)
                .visible(true);

        for (int i = 0; i < TOTAL_MARKERS; i++) {
            polygonOptions.add(markers.get(i).getPosition());
        }

        MarkerOptions markerOptions = new MarkerOptions().position(getPolygonCenter(markerLocations))
                .icon(makeCustomMarker(R.drawable.ic_launcher_background, Color.GREEN)).snippet("center");
        polygonMarker = map.addMarker(markerOptions);

        polygon = map.addPolygon(polygonOptions);
        polygon.setFillColor(Color.parseColor("#5900FF00"));
    }

    @SuppressWarnings("deprecation")
    public void getLocation() {
        try {
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            LocationCallback mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    for (Location location : locationResult.getLocations()) {
                        setLocation(location);
                    }
                }
            };
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(2000);
            mLocationRequest.setFastestInterval(2000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            fusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        } catch (Exception e) {
            Log.d("error", e.getMessage());
            e.printStackTrace();
        }

    }

    private void setLocation(Location loc) {
        selectedLocation = new LatLng(loc.getLatitude(), loc.getLongitude());
    }

    @SuppressWarnings("deprecation")
    private void showAlert(String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(
                MarkerActivity.this).create();
        alertDialog.setTitle("");
        alertDialog.setMessage(message);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Showing Alert
        alertDialog.show();
    }

    private LatLng getPolygonCenter(ArrayList<LatLng> polygonPointsList) {
        LatLng centerLatLng;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < polygonPointsList.size(); i++) {
            builder.include(polygonPointsList.get(i));
        }
        LatLngBounds bounds = builder.build();
        centerLatLng = bounds.getCenter();

        return centerLatLng;
    }

    private BitmapDescriptor makeCustomMarker(@DrawableRes int id, @ColorInt int color) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
        assert vectorDrawable != null;
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        if (color != 0){
            DrawableCompat.setTint(vectorDrawable, color);
        }
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}
