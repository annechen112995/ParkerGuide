package com.westwood.parkerguide;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, LocationListener {

    final private int REQUEST_CODE_START_UPDATES = 0;
    final private int REQUEST_CODE_STOP_UPDATES = 1;
    final private int REQUEST_CODE_LAST_KNOWN = 2;
    final private int UPDATE_MILLISECONDS = 0;
    final private int UPDATE_DISTANCE = 0;
    private static final int FIVE_MINUTES = 1000 * 60 * 5;

    private GoogleMap mMap;
    public static final String PREFS_NAME = "SharedPreferences";
    LocationManager locationManager;
    LatLngBounds.Builder builder;
    Location currentLocation;
    Location chosenLocation;
    int radius; //miles
    String locName; // name of chosen location, to display in location picker fragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        builder = new LatLngBounds.Builder();
    }

    public void setLocations(Location currentLocation, Location chosenLocation) {
        this.currentLocation = currentLocation;
        this.chosenLocation = chosenLocation;
        updateSharedPrefs();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle item selection
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {}
    public void onProviderEnabled(String provider) {}
    public void onProviderDisabled(String provider) {}
    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_START_UPDATES);
        }
        else {
            // Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, UPDATE_MILLISECONDS, UPDATE_DISTANCE, (android.location.LocationListener) this);
        }
    }

    public void stopLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_STOP_UPDATES);
        }
        else {
            locationManager.removeUpdates((android.location.LocationListener) this);
        }
    }

    public void requestLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LAST_KNOWN);
        }
        else {
            Location lastKnownLocation = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
            setLocations(lastKnownLocation, lastKnownLocation);
        }
    }

    // from android docs
    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > FIVE_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -FIVE_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }
        if (location.distanceTo(currentBestLocation) > mileToMeter(5)) {
            return true;
        }
        return false;

//        // Check whether the new location fix is more or less accurate
//        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
//        boolean isLessAccurate = accuracyDelta > 0;
//        boolean isMoreAccurate = accuracyDelta < 0;
//        boolean isSignificantlyLessAccurate = accuracyDelta > 200;
//
//        // Check if the old and new location are from the same provider
//        boolean isFromSameProvider = isSameProvider(location.getProvider(),
//                currentBestLocation.getProvider());
//
//        // Determine location quality using a combination of timeliness and accuracy
//        if (isMoreAccurate) {
//            return true;
//        } else if (isNewer && !isLessAccurate) {
//            return true;
//        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
//            return true;
//        }
//        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    public void updateSharedPrefs() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("chosen_latitude", Double.doubleToLongBits(chosenLocation.getLatitude()));
        editor.putLong("chosen_longitude", Double.doubleToLongBits(chosenLocation.getLongitude()));
        editor.putLong("current_latitude", Double.doubleToLongBits(currentLocation.getLatitude()));
        editor.putLong("current_longitude", Double.doubleToLongBits(currentLocation.getLongitude()));
        editor.putInt("radius", radius);
        editor.putString("location_name", locName);
        editor.apply();
    }

    public void setInitialLocations() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        double cachedChosenLatitude = Double.longBitsToDouble(settings.getLong("chosen_latitude", 0));
        double cachedChosenLongitude = Double.longBitsToDouble(settings.getLong("chosen_longitude", 0));
        double cachedCurrentLatitude = Double.longBitsToDouble(settings.getLong("current_latitude", 0));
        double cachedCurrentLongitude = Double.longBitsToDouble(settings.getLong("current_longitude", 0));
        locName = settings.getString("location_name", "Current Location");
        radius = settings.getInt("radius", 5);
        if (!(cachedChosenLatitude == 0 && cachedChosenLongitude == 0)) {
            Location lastChosenLocation = new Location("");
            lastChosenLocation.setLatitude(cachedChosenLatitude);
            lastChosenLocation.setLongitude(cachedChosenLongitude);
            Location lastCurrentLocation = new Location("");
            lastCurrentLocation.setLatitude(cachedCurrentLatitude);
            lastCurrentLocation.setLongitude(cachedCurrentLongitude);
            setLocations(lastCurrentLocation, lastChosenLocation);
            if (currentLocation.getLatitude() == chosenLocation.getLatitude() && currentLocation.getLongitude() == chosenLocation.getLongitude()) {
                startLocationUpdates();
            }
        }
        else {
            requestLastKnownLocation();
            startLocationUpdates();
        }
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

        SharedPreferences preferences = getSharedPreferences(this.PREFS_NAME, 0);
        double currentLat = Double.longBitsToDouble(preferences.getLong("current_latitude", 0));
        double currentLng = Double.longBitsToDouble(preferences.getLong("current_longitude", 0));

        LatLng current = new LatLng(currentLat, currentLng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 10f));

        // show current location (blue circle) on map
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
        mMap.setMyLocationEnabled(true);

        // set zoom in out buttons
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    public double mileToMeter(int mi) {
        return ((double) mi) * 1609.34;
    }
}
