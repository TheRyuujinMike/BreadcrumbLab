package edu.temple.breadcrumblab;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    //private HashMap breadCrumbs;

    private ArrayList<Marker> breadCrumbs;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private boolean follow, laycrumbs, firstCurrentLocation;

    private Marker currentLocationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //breadCrumbs = new HashMap();
        breadCrumbs = new ArrayList<>();
        follow = false;
        laycrumbs = false;
        firstCurrentLocation = true;
        currentLocationMarker = null;

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                0);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //Marker lastCrumb = (Marker) breadCrumbs.get(breadCrumbs.size() - 1);

                LatLng lastCrumbLatlng;

                if (breadCrumbs.size() > 0) {
                    Marker lastCrumb = breadCrumbs.get(breadCrumbs.size() - 1);
                    lastCrumbLatlng = lastCrumb.getPosition();
                }
                else
                {
                    lastCrumbLatlng = new LatLng(0,0);
                }


                Location lastCrumbLoc = new Location("");
                Location currentLoc;

                String locationProvider = LocationManager.GPS_PROVIDER;

                if (ActivityCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                currentLoc = locationManager.getLastKnownLocation(locationProvider);

                lastCrumbLoc.setLatitude(lastCrumbLatlng.latitude);
                lastCrumbLoc.setLongitude(lastCrumbLatlng.longitude);

                float distanceInMeters = lastCrumbLoc.distanceTo(currentLoc);

                if ((distanceInMeters >= 5) && laycrumbs) {

                    //breadCrumbs.put(CurrentLocationMarker(), breadCrumbs.size());
                    breadCrumbs.add(BreadCrumbMarker());

                }

                if (follow) {
                    CurrentLocationMarker();
                }

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        Button dropButton = (Button) findViewById(R.id.location_button);
        Button clearButton = (Button) findViewById(R.id.clear_button);
        final ToggleButton followToggle = (ToggleButton) findViewById(R.id.toggle_follow);
        final ToggleButton breadCrumbToggle = (ToggleButton) findViewById(R.id.toggle_breadcrumbs);

        dropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //breadCrumbs.put(CurrentLocationMarker(), breadCrumbs.size());
                breadCrumbs.add(BreadCrumbMarker());

            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Marker marker;

                for (int x = breadCrumbs.size(); x > 0; x--) {
                    marker = breadCrumbs.get(x - 1);
                    marker.remove();
                    breadCrumbs.remove(x - 1);
                }

                breadCrumbs.clear();

            }
        });

        followToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked) {
                    follow = true;
                    if (firstCurrentLocation) {
                        currentLocationMarker = CurrentLocationMarker();
                    }
                }
                else {
                    follow = false;
                    currentLocationMarker.remove();
                    firstCurrentLocation = true;
                }

            }
        });

        breadCrumbToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked) {
                    laycrumbs = true;
                }
                else {
                    laycrumbs = false;
                }

            }
        });

    }

    @Override
    public void onDestroy() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(locationListener);

    }

    public Marker CurrentLocationMarker() {

        Location currentLocation;
        LatLng currentLatLng;

        // Add a marker at the current location and move the camera
        String locationProvider = LocationManager.GPS_PROVIDER;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        }
        currentLocation = locationManager.getLastKnownLocation(locationProvider);
        currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        if (currentLocationMarker != null) {
            currentLocationMarker.remove();
        }
        currentLocationMarker = mMap.addMarker(new MarkerOptions().position(currentLatLng));

        if (firstCurrentLocation) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17));
            firstCurrentLocation = false;
        }
        else {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
        }

        return currentLocationMarker;

    }

    public Marker BreadCrumbMarker() {

        Location currentLocation;
        LatLng currentLatLng;

        Marker marker;

        // Add a marker at the current location and move the camera
        String locationProvider = LocationManager.GPS_PROVIDER;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        }
        currentLocation = locationManager.getLastKnownLocation(locationProvider);
        currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        marker = mMap.addMarker(new MarkerOptions().position(currentLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        return marker;

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //breadCrumbs.put(CurrentLocationMarker(), breadCrumbs.size());
        //breadCrumbs.add(CurrentLocationMarker());

    }



}
