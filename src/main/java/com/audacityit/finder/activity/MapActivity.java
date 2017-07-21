package com.audacityit.finder.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.audacityit.finder.R;
import com.audacityit.finder.util.Constants;
import com.audacityit.finder.util.PathJSONParser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.audacityit.finder.fragment.DetailViewFragment.itemDetails;
import static com.audacityit.finder.util.UtilMethods.isUserSignedIn;

/**
 * @author Audacity IT Solutions Ltd.
 * @class MapActivity
 * @brief Activity for showing user and business location with possible path to travel on map
 */

public class MapActivity extends FragmentActivity implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private Marker currentLocationMarker;
    private LatLng itemLocation;
    //    private Location lastLocation;
    private boolean isUpdated = false;
    private String distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ((TextView) findViewById(R.id.itemHeadingTV)).setText(itemDetails.getTitle());
        if (itemDetails != null) {
            itemLocation = new LatLng(Constants.DUMMY_LOCATION_LATITUDE,
                    Constants.DUMMY_LOCATION_LONGITUDE);
        }

        if (!isGooglePlayServicesAvailable()) {
            finish();
        }

        createLocationRequest();
        createGoogleApiClient();
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void createGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }


    private void setUpMapIfNeeded() {

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                .getMapAsync(this);

    }

    private void setUpMap() {
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(false);
    }


    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    private final int REQUEST_LOCATION = 200;

    private void startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= 23 && (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
// Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_LOCATION);

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_LOCATION);
            }
        } else {
            PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//        lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;

            updateMap();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    private void updateMap() {
        setUpMapIfNeeded();
        MarkerOptions options = new MarkerOptions();
        LatLng currentLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        options.position(currentLatLng);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_source));
        if (currentLocationMarker != null)
            currentLocationMarker.remove();
        currentLocationMarker = mMap.addMarker(options);

        if (isUserSignedIn(MapActivity.this)) {
            //set username
//            currentLocationMarker.setTitle(getResources().getString(R.string.you_string));
        } else
            currentLocationMarker.setTitle(getResources().getString(R.string.you_string));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(currentLatLng)
                .tilt(90)
                .zoom(17)
                .build();
if(mMap!=null)
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        addTargetMarker();
        new GetPathDefault().execute();
        //        getPathFromGoogle();

    }

    private void addTargetMarker() {
        if (mMap != null) {
            mMap.addMarker(new MarkerOptions().position(itemLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_destination))
                    .title(itemDetails.getTitle()))
                    .setSnippet(itemDetails.getAddress());
        }
    }

    private String getMapsApiDirectionsUrl() {

        String str_origin = "origin=" + mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude();
        String str_dest = "destination=" + itemLocation.latitude + "," + itemLocation.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        setUpMapIfNeeded();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mMap == null) {
            mMap = googleMap;
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private class GetPathDefault extends AsyncTask {

        URL url = null;
        BufferedReader reader = null;
        StringBuilder stringBuilder;

        @Override
        protected Object doInBackground(Object[] params) {

            try {
                url = new URL(getMapsApiDirectionsUrl());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
//                connection.setReadTimeout(15*1000);
                connection.connect();
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                stringBuilder = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }

            } catch (Exception e) {
                e.printStackTrace();
                try {
                    throw e;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            new ParserTask().execute(stringBuilder.toString());
        }
    }

    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                distance = jObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getString("text");
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            if(routes==null)
                return;

            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(10);
                polyLineOptions.color(getResources().getColor(R.color.map_polyline_color));

                if (isUserSignedIn(MapActivity.this)) {
                    //set username
                }

                ((TextView) findViewById(R.id.distanceTV)).setText(distance);
                findViewById(R.id.awayTV).setVisibility(View.VISIBLE);
            }
            if (mMap != null)
                mMap.addPolyline(polyLineOptions);
        }
    }

}
