package com.westwood.parkerguide;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
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
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MapsActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMapLongClickListener {


    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */
    private int BOUND_PADDING = 100;
    private ArrayList<LatLng> list;

    /*
     * Define a request code to send to Google Play services This code is
     * returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap));
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    list = new ArrayList<LatLng>();
                    //Toast.makeText(getApplicationContext(), "list created", Toast.LENGTH_SHORT).show();
                    //generateList();
                    //Toast.makeText(getApplicationContext(), "points being added", Toast.LENGTH_SHORT).show();
                    loadMap(map);
                    //map.setInfoWindowAdapter(new CustomWindowAdapter(getLayoutInflater()));
                }
            });
        } else {
            Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
        }

    }

    private void generateList() {
        String[] values = new String[] {
                "34.138602", "-118.11881820000002",
                "34.1391019", "-118.11882630000002",
                "34.1396018", "-118.11883439999997",
                "34.1407749", "-118.11897929999998",
                "34.1419179", "-118.11900179999998",
                "34.1434407", "-118.11932189999999",
                "34.1407651", "-118.11850090000002",
                "34.1419199", "-118.11850179999999",
                "34.139784", "-118.11799810000002",
                "34.1407669", "-118.11800089999997",
                "34.1419189", "-118.11799289999999",
                "34.1434681", "-118.11725669999998",
                "34.1386024", "-118.11715670000001",
                "34.1391018", "-118.11715979999997",
                "34.1397804", "-118.11749810000003",
                "34.1401069", "-118.11717529999999",
                "34.1407688", "-118.11750089999998",
                "34.1416019", "-118.11720379999997",
                "34.1428425", "-118.1169266",
                "34.14077109999999", "-118.11700159999998",
                "34.1388437", "-118.11650179999998",
                "34.1397893", "-118.11650470000001",
                "34.1407743", "-118.11650159999999",
                "34.1419043", "-118.1164996",
                "34.1390971", "-118.1156406",
                "34.1397979", "-118.11600499999997",
                "34.1407775", "-118.1160016",
                "34.1411009", "-118.11559390000002",
                "34.1419039", "-118.11599960000001",
                "34.142793", "-118.1160099",
                "34.1401004", "-118.11559699999998",
                "34.1415998", "-118.11559549999998",
                "34.1430972", "-118.1156259",
                "34.1386025", "-118.11463090000001",
                "34.139796", "-118.1149969",
                "34.1407848", "-118.11500219999999",
                "34.141921", "-118.11500519999998",
                "34.1426057", "-118.11446280000001",
                "34.1431056", "-118.11447049999998",
                "34.1391236", "-118.11457089999999",
                "34.1396007", "-118.1144253",
                "34.1401011", "-118.1144357",
                "34.1406009", "-118.1144481",
                "34.1411", "-118.11445270000002",
                "34.1416", "-118.1144524",
                "34.1421005", "-118.11445500000002"
        };
        for (int i = 0; i < (values.length/2); i++) {
            if ((i%2) == 0) {
                //double latitude = Double.parseDouble(values[i]);
                //double longitude = Double.parseDouble(values[i + 1]);
                //LatLng location = new LatLng(latitude, longitude);
                list.add(new LatLng(Double.parseDouble(values[i]), Double.parseDouble(values[i + 1])));
                //Toast.makeText(this, (new LatLng(Double.parseDouble(values[i]), Double.parseDouble(values[i + 1]))).toString(), Toast.LENGTH_LONG).show();
                BitmapDescriptor defaultMarker =
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                // Extract content from alert dialog
                String title = "Parking Available:";
                String snippet = "8AM - 10 PM, M - F";
                Marker marker = map.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(values[i]), Double.parseDouble(values[i + 1])))
                        .title(title)
                        .snippet(snippet)
                        .icon(defaultMarker));
                dropPinEffect(marker);
            }
            else {
                continue;
            }
        }

        for (int i = (values.length/2); i < values.length; i++) {
            if ((i%2) == 0) {
                //double latitude = Double.parseDouble(values[i]);
                //double longitude = Double.parseDouble(values[i + 1]);
                //LatLng location = new LatLng(latitude, longitude);
                list.add(new LatLng(Double.parseDouble(values[i]), Double.parseDouble(values[i + 1])));
                //Toast.makeText(this, (new LatLng(Double.parseDouble(values[i]), Double.parseDouble(values[i + 1]))).toString(), Toast.LENGTH_LONG).show();
                BitmapDescriptor defaultMarker =
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                // Extract content from alert dialog
                String title = "Parking Available:";
                String snippet = "10AM - 5 PM, M - W";
                Marker marker = map.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(values[i]), Double.parseDouble(values[i + 1])))
                        .title(title)
                        .snippet(snippet)
                        .icon(defaultMarker));
                dropPinEffect(marker);
            }
            else {
                continue;
            }
        }
    }

    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready
            Toast.makeText(this, "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();
            MapsActivityPermissionsDispatcher.getMyLocationWithCheck(this);
            generateList();
            //Toast.makeText(this, "Drawing Polylines", Toast.LENGTH_SHORT).show();
            //drawPolyLineOnMap(list);
            // Attach long click listener to the map here
            map.setOnMapLongClickListener(this);

        } else {
            Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MapsActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @SuppressWarnings("all")
    @NeedsPermission({android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION})
    void getMyLocation() {
        if (map != null) {
            // Now that map has loaded, let's get our location!
            map.setMyLocationEnabled(true);
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            connectClient();
        }
    }

    protected void connectClient() {
        // Connect the client.
        if (isGooglePlayServicesAvailable() && mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    /*
     * Called when the Activity becomes visible.
    */
    @Override
    protected void onStart() {
        super.onStart();
        connectClient();
    }

    /*
     * Called when the Activity is no longer visible.
     */
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    /*
     * Handle results returned to the FragmentActivity by Google Play services
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {

            case CONNECTION_FAILURE_RESOLUTION_REQUEST:
			/*
			 * If the result code is Activity.RESULT_OK, try to connect again
			 */
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        mGoogleApiClient.connect();
                        break;
                }

        }
    }

    private boolean isGooglePlayServicesAvailable() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(errorDialog);
                errorFragment.show(getSupportFragmentManager(), "Location Updates");
            }

            return false;
        }
    }

    /*
     * Called by Location Services when the request to connect the client
     * finishes successfully. At this point, you can request the current
     * location or start periodic updates
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
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
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null) {
            Toast.makeText(this, "GPS location was found!", Toast.LENGTH_SHORT).show();
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            map.animateCamera(cameraUpdate);
        } else {
            Toast.makeText(this, "Current location was null, enable GPS on emulator!", Toast.LENGTH_SHORT).show();
        }
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
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
        //LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
        //        mLocationRequest, (com.google.android.gms.location.LocationListener) this);
    }

    public void onLocationChanged(Location location) {
        // Report to the UI that the location was updated
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /*
     * Called by Location Services if the connection to the location client
     * drops because of an error.
     */
    @Override
    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(this, "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * Called by Location Services if the attempt to Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry. Location services not available to you", Toast.LENGTH_LONG).show();
        }
    }

    // Fires when a long press happens on the map
    @Override
    public void onMapLongClick(final LatLng point) {
        Toast.makeText(this, "Long Press", Toast.LENGTH_LONG).show();

        // Display the alert dialog
        showAlertDialogForPoint(point);
    }

    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

    // Display the alert that adds the marker
    private void showAlertDialogForPoint(final LatLng point) {
        // inflate message_item.xml view
        View messageView = LayoutInflater.from(MapsActivity.this).
                inflate(R.layout.message_item, null);
        // Create alert dialog builder
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set message_item.xml to AlertDialog builder
        alertDialogBuilder.setView(messageView);

        // Create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        // Configure dialog button (OK)
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Define color of marker icon
                        BitmapDescriptor defaultMarker =
                                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                        // Extract content from alert dialog
                        String title = ((EditText) alertDialog.findViewById(R.id.etTitle)).
                                getText().toString();
                        String snippet = ((EditText) alertDialog.findViewById(R.id.etSnippet)).
                                getText().toString();
                        // Creates and adds marker to the map
                        Marker marker = map.addMarker(new MarkerOptions()
                                .position(point)
                                .title(title)
                                .snippet(snippet)
                                .icon(defaultMarker));
                        //list.add(point);
                        //drawPolyLineOnMap(list);
                        Toast.makeText(getApplicationContext(), point.toString(), Toast.LENGTH_LONG).show();
                        // Animate marker using drop effect
                        // --> Call the dropPinEffect method here
                        dropPinEffect(marker);
                    }
                });

        // Configure dialog button (Cancel)
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { dialog.cancel(); }
                });

        // Display the dialog
        alertDialog.show();
    }

    private void dropPinEffect(final Marker marker) {
        // Handler allows us to repeat a code block after a specified delay
        final android.os.Handler handler = new android.os.Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1500;

        // Use the bounce interpolator
        final android.view.animation.Interpolator interpolator =
                new BounceInterpolator();

        // Animate marker with a bounce updating its position every 15ms
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                // Calculate t for bounce based on elapsed time
                float t = Math.max(
                        1 - interpolator.getInterpolation((float) elapsed
                                / duration), 0);
                // Set the anchor
                marker.setAnchor(0.5f, 1.0f + 14 * t);

                if (t > 0.0) {
                    // Post this event again 15ms from now.
                    handler.postDelayed(this, 15);
                } else { // done elapsing, show window
                    marker.showInfoWindow();
                }
            }
        });
    }

    public void drawPolyLineOnMap(ArrayList<LatLng> list) {
        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(Color.GREEN);
        polyOptions.width(5);
        polyOptions.addAll(list);
        //map.clear();
        map.addPolyline(polyOptions);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : list) {
            builder.include(latLng);
        }
        //final LatLngBounds bounds = builder.build();
        //BOUND_PADDING is an int to specify padding of bound.. try 100.
        //CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, BOUND_PADDING);
        //map.animateCamera(cu);
    }
}
