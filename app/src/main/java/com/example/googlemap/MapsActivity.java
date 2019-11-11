package com.example.googlemap;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;

    private Context context;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;


    Location mLastLocation;

    Marker mCurrLocationMarker;

    GoogleApiClient mGoogleApiClient;

    LocationRequest mLocationRequest;

    SearchView searchView;

    private int PROXIMITY_RADIUS = 5000;
    private double latitude, longitude;
    private StringBuilder googlePlacesUrl;
    private static final String GOOGLE_API_KEY = "AIzaSyAZokiEvuoHfvUBdPL2OltBq3780BFdzyw";
    private ArrayList<Place> placeArrayListFinal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        context = MapsActivity.this;

        searchView = (SearchView) findViewById(R.id.searchView);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);


        Log.e("check ", String.valueOf(isLocationEnabled(context)));


        verifyPermission();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Toast.makeText(context, query, Toast.LENGTH_LONG).show();

                findplace(query)
                ;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //    adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private void findplace(String query) {
        placeArrayListFinal = new ArrayList<>();

        buildURL(query);

        if (Globle.checkInternetConnection(context)) {
            GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
            googlePlacesReadTask.execute();
        } else {
            new Globle().alertDialog(context, "No internet Found, Please Turn-on your network settings");

            finish();
        }
    }


    private void buildURL(String query) {
        googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&types=" + query);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + GOOGLE_API_KEY);
    }


    public class GooglePlacesReadTask extends AsyncTask<Void, Void, String> {
        String googlePlacesData = null;

        @Override
        protected String doInBackground(Void... inputObj) {
            try {
                Http http = new Http();

                Log.e("api  ", googlePlacesUrl.toString());
                googlePlacesData = http.read(googlePlacesUrl.toString());
            } catch (Exception e) {
            }
            return googlePlacesData;
        }

        @Override
        protected void onPostExecute(String result) {
            PlacesParser placeJsonParser = new PlacesParser();
            JSONObject googlePlacesJson = null;
            try {
                googlePlacesJson = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final ArrayList<Place> placeArrayList = placeJsonParser.parse(googlePlacesJson);
            if (placeArrayList != null && placeArrayList.size() > 0) {

                for (int i = 0; i < placeArrayList.size(); i++) {

                    mMap.addMarker(new MarkerOptions().
                            position(new LatLng(placeArrayList.get(i).getLatitude()
                                    , placeArrayList.get(i).getLongitude()
                            )).title(placeArrayList.get(i).getPlaceName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.defaultmarker)));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));


                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {

                            final Dialog dialog = new Dialog(context);
                            dialog.setContentView(R.layout.custom_dialog_place);
                            dialog.setCancelable(true);
                            dialog.setCanceledOnTouchOutside(true);

                            ImageView imageView = dialog.findViewById(R.id.iv);
                            TextView address = dialog.findViewById(R.id.tv_address);
                            TextView distance = dialog.findViewById(R.id.tv_distance);
                            TextView name = dialog.findViewById(R.id.tv_name);
                            name.setText(marker.getTitle());


                            for (int a = 0; a < placeArrayList.size(); a++) {

                                String name1 = placeArrayList.get(a).getPlaceName();

                                if (name1.equals(marker.getTitle())) {

                                    Picasso.with(context)
                                            .load(placeArrayList.get(a).getIcon())
                                            .into(imageView);


                                    address.setText(placeArrayList.get(a).getAddress());

                                    String distancea = String.valueOf(distanceBetweenTwo(latitude, longitude, placeArrayList.get(a).getLatitude(), placeArrayList.get(a).getLongitude()));

                                    distance.setText(distancea);

                                }

                            }



//                            Picasso.with(context)
//                                    .load(placeArrayList.get(i).getIcon())
//                                    .into(imageView);


//                            address.setText(placeArrayList.get(i).getAddress());
//
//                            String distancea = String.valueOf(distanceBetweenTwo(latitude, longitude, placeArrayList.get(i).getLatitude(), placeArrayList.get(i).getLongitude()));
//
//                            distance.setText(distancea);


                            dialog.show();
                            return false;
                        }
                    });
//                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//                        @Override
//                        public boolean onMarkerClick(Marker marker) {
//
//                            final Dialog dialog = new Dialog(context);
//                            dialog.setContentView(R.layout.custom_dialog_place);
//                            dialog.setTitle("Title...");
//
//                            ImageView imageView = dialog.findViewById(R.id.iv);
//
//                            Picasso.with(context)
//                                    .load(placeArrayList.get(finalI).getIcon())
//                                    .into(imageView);
//
//                            TextView name = dialog.findViewById(R.id.tv_name);
//                            name.setText(placeArrayList.get(finalI).getPlaceName());
//                            TextView address = dialog.findViewById(R.id.tv_address);
//                            address.setText(placeArrayList.get(finalI).getAddress());
//                            TextView distance = dialog.findViewById(R.id.tv_distance);
//
//                            String distancea = String.valueOf(distanceBetweenTwo(latitude, longitude, placeArrayList.get(finalI).getLatitude(), placeArrayList.get(finalI).getLongitude()));
//
//                            distance.setText(distancea);
//
//
//                            dialog.show();
//
//                        });
//                    });


                }

                placeArrayListFinal.clear();


            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Alert!");
                builder.setCancelable(false);
                builder.setMessage("No Data Found!");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
                Dialog dialog = builder.create();
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.show();
            }
        }
    }


    private double distanceBetweenTwo(double latA, double lngA, double latB, double lngB) {
        Location locationA = new Location("point A");
        locationA.setLatitude(latA);
        locationA.setLongitude(lngA);
        Location locationB = new Location("point B");
        locationB.setLatitude(latB);
        locationB.setLongitude(lngB);
        return Double.parseDouble(new DecimalFormat("##.##").format(locationA.distanceTo(locationB) / 1000));
    }

    @Override
    protected void onResume() {
        super.onResume();


//        if (android.os.Build.VERSION.SDK_INT >= 23) {
//            //            /*Ask  Permissions here*/
//
//            verifyPermission();
//
//        }

    }


    private void verifyPermission() {


        if (!isLocationEnabled(context)) {
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1000);
        } else {

            int permissionCheck = ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION);


            switch (permissionCheck) {

                case PackageManager.PERMISSION_GRANTED:

                    proceedAfterPermission();

                    break;

                case PackageManager.PERMISSION_DENIED:


                    if (ActivityCompat.shouldShowRequestPermissionRationale
                            ((Activity) context, Manifest.permission.ACCESS_FINE_LOCATION)) {

                        ActivityCompat.requestPermissions(this,
                                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                    } else {

                        ActivityCompat.requestPermissions(this,
                                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                    }

                    break;
            }
        }
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }

    private void proceedAfterPermission() {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();

            }
        } else {
            buildGoogleApiClient();
        }


    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        //proceedAfterPermission();

        if (mMap != null) {
            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location arg0) {
                    mMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
                }
            });
        }
        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

//        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//
//        LatLng current = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
//       mMap.addMarker(new MarkerOptions().position(current).title("current"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        if (!isLocationEnabled(context)) {
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1000);
        }

        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(20));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        switch (requestCode) {

            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED) {

                    proceedAfterPermission();
                    Log.e("check ", String.valueOf(isLocationEnabled(context)));

                } else {

                    // permission denied, boo!
                    //Disable the functionality that depends on this permission.

                    if (ActivityCompat.shouldShowRequestPermissionRationale
                            ((Activity) context, Manifest.permission.ACCESS_FINE_LOCATION)) {

                        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                        Log.e("check ", String.valueOf(isLocationEnabled(context)));

                    } else {

                        goToSettings("get location", 300);

                    }
                }


            }

        }

    }


    //grant permission of app needs in the phone setting

    private void goToSettings(final String title, final int per) {

        androidx.appcompat.app.AlertDialog.Builder builder = new
                androidx.appcompat.app.AlertDialog.Builder(MapsActivity.this);

        builder.setTitle("App Permission");

        builder.setMessage("This app needs " + title + " permission.");

        builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();

                Intent intent = new Intent
                        (Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

                Uri uri = Uri.fromParts("package", getPackageName(), null);

                intent.setData(uri);

                startActivityForResult(intent, per);
                Toast.makeText(getBaseContext(), "Go to Permissions to Grant Storage", Toast.LENGTH_LONG).show();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();

                goToSettings(title, per);

            }
        });

        builder.show();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 300) {

            if (ContextCompat.checkSelfPermission(MapsActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                proceedAfterPermission();

            } else {

                goToSettings("get location", 300);

            }

        } else if (requestCode == 1000) {
            verifyPermission();

            if (mMap != null) {
                mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location arg0) {
                        latitude = arg0.getLatitude();
                        longitude = arg0.getLongitude();
                        mMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));

                        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                    }
                });
            }
        }

    }


}
