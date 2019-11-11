//package cloudzon.com.googlecurrentlocation;
//
//import android.Manifest;
//import android.app.IntentService;
//import android.app.Service;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.os.Bundle;
//import android.os.IBinder;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.ActivityCompat;
//import android.util.Log;
//
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.location.LocationListener;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.location.LocationSettingsRequest;
//
//
///**
// * Created by lg on 5/9/2017.
// */
//
//public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {
//    private final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
//    private final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
//
//    public LocationRequest mLocationRequest;
//    private LocationSettingsRequest.Builder locationSettingBuilder;
//    public GoogleApiClient mGoogleApiClient;
//    @Override
//    public void onCreate() {
//        super.onCreate();
//       // Log.e("Oncreate","locationservice;-");
//        buildGoogleApiClient();
//        createLocationRequest();
//    }
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        return START_STICKY;
//    }
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
////    @Override
////    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
////        Log.e("onStartCommand","1");
////        return START_STICKY;
////    }
//
//    private void sendUpdate(double latitude,double longitude){
//        Intent intent = new Intent("cloudzon.com.googlecurrentlocation.SEND_UPDATES");
//        intent.putExtra(LocationFetcher.LATITUDE,latitude);
//        intent.putExtra(LocationFetcher.LONGITUDE,longitude);
//        getApplicationContext().sendBroadcast(intent);
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//        if(location!=null){
//            sendUpdate(location.getLatitude(),location.getLongitude());
//        }
//    }
//    private synchronized void buildGoogleApiClient() {
//        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext()).addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
//        mGoogleApiClient.connect();
//    }
//    private void createLocationRequest() {
//        mLocationRequest = new LocationRequest();
//
//        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
//
//        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
//
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//    }
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            stopSelf();
//            return;
//        }
//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }
//}
