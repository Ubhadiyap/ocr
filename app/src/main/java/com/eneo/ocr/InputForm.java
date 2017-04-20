package com.eneo.ocr;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.eneo.ocr.Model.Datalocal;
import com.eneo.ocr.Model.MyShortcuts;


import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by stephineosoro on 09/09/16.
 */
public class InputForm extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    @Bind(R.id.serial)
    TextView _serial;
    @Bind(R.id.index)
    TextView _index;
    @Bind(R.id.long_lat)
    TextView _long_lat;
    @Bind(R.id.date)
    TextView _date;


    @Bind(R.id.input_island)
    EditText _island;
    @Bind(R.id.input_zone)
    EditText _zone;
    @Bind(R.id.input_agency)
    EditText _agency;
    @Bind(R.id.input_agencyID)
    EditText _agencyID;
    private static final int REQUEST_FINE_LOCATION = 0;
    EditText input;
    @Bind(R.id.btn_save)
    Button _saveButton;
    private Location mlocation;
    private Criteria criteria;
    private String latitude = "", longitude = "";
    private LocationManager locationManager;
    LocationListener locationListener;
    private static Context ctx,context;
    private  GoogleApiClient googleApiClient;
    boolean gps_enabled = false;
    boolean network_enabled = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_input);
        ButterKnife.bind(this);
//        ctx=this;
        showPermissionDialog();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setActionBar(toolbar);
        setSupportActionBar(toolbar);
        setTitle("Input Details");
        context=this;
        /*if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(InputForm.this).build();
            googleApiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            // **************************
            builder.setAlwaysShow(true); // this is the key ingredient
            // **************************

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result
                            .getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can
                            // initialize location
                            // requests here.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
//                                 startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(InputForm.this, 1000);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have
                            // no way to fix the
                            // settings so we won't show the dialog.
//                            TODO asking the user for the location permission
                            Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(gpsOptionsIntent);
                            MyShortcuts.showToast("Please turn on the GPS/Location setting to record your location!",getBaseContext());
                            break;
                    }
                }
            });
        }*/
        _serial.setText("Meter Serial Number: "+MyShortcuts.getDefaults("serial", getBaseContext()));
        _index.setText("Meter Index Number: "+MyShortcuts.getDefaults("index", getBaseContext()));
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());



        /*if (!isGPSEnabled(this)){
//            turnGPSOn();
        }*/
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        _date.setText(formattedDate);
//        loadPermissions(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_FINE_LOCATION);
        _saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

//        TODO commented this get location code

       /* locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        Location gps_loc = null;
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mlocation = location;
                Log.e("Location Changes", location.toString());
                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());
*//*                latitude.setText(String.valueOf(location.getLatitude()));
                longitude.setText(String.valueOf(location.getLongitude()));*//*

                _long_lat.setText("Location ("+latitude + ", " + longitude+") ");
                Log.e("long lang", longitude+", "+latitude);
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


        };

        if (gps_enabled&&checkPermission(getBaseContext())) {
            gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (gps_loc!=null) {
                latitude = String.valueOf(gps_loc.getLatitude());
                longitude = String.valueOf(gps_loc.getLongitude());
                Log.e("inside gps enabled & cp","inside");
                Log.e("gps_loc get  last",longitude+","+latitude);
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        }*/




/*
        // this is done to save the battery life of the device

        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);



        Looper looper = null;
        if (checkPermission(this)) {
            locationManager.requestSingleUpdate(criteria, locationListener, looper);
        }
*/
/*


        Datalocal dataLocal = new Datalocal();

        dataLocal.setMeter_index(MyShortcuts.getDefaults("index", getBaseContext()));
        dataLocal.setMeter_serial(MyShortcuts.getDefaults("serial", getBaseContext()));

        dataLocal.setLatitude(latitude);
        dataLocal.setLongitude(longitude);
*/




    }


    public void save() {
        Log.d("", "saving");

        if (!validate()) {
            onsaveFailed();
            return;
        }

        _saveButton.setEnabled(false);


        savedetail();
    }


    public void onsaveSuccess() {
        _saveButton.setEnabled(true);
        finish();
    }

    public void onsaveFailed() {
        Toast.makeText(getBaseContext(), "saving the details failed! ", Toast.LENGTH_LONG).show();

        _saveButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;


        String island = _island.getText().toString();
        String zone = _zone.getText().toString();
        String agency = _agency.getText().toString();
        String agencyID = _agencyID.getText().toString();


        if (island.isEmpty()) {
            _island.setError("Island is empty");
            valid = false;
        } else {
            _island.setError(null);
        }
        if (zone.isEmpty()) {
            _zone.setError("Zone is empty");
            valid = false;
        } else {
            _zone.setError(null);
        }
        if (agency.isEmpty()) {
            _agency.setError("Agency is empty");
            valid = false;
        } else {
            _agency.setError(null);
        }
        if (agencyID.isEmpty()) {
            _agencyID.setError("Agency ID is empty");
            valid = false;
        } else {
            _agencyID.setError(null);
        }


        return valid;
    }

    private void savedetail() {
        String island = _island.getText().toString();
        String zone = _zone.getText().toString();
        String agency = _agency.getText().toString();
        String agencyID = _agencyID.getText().toString();

        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());

        Datalocal datalocal = new Datalocal();
        datalocal.setAgency(agency);
        datalocal.setAgencyID(agencyID);
        datalocal.setDatetime(formattedDate);
        datalocal.setMeter_index(MyShortcuts.getDefaults("index", getBaseContext()));
        datalocal.setMeter_serial(MyShortcuts.getDefaults("serial", getBaseContext()));
        datalocal.setIsland(island);
        datalocal.setZone(zone);
        datalocal.setLatitude(MyShortcuts.getDefaults("latitude", getBaseContext()));
        datalocal.setLongitude(MyShortcuts.getDefaults("longitude", getBaseContext()));
        datalocal.setLocationName(MyShortcuts.getDefaults("locationname", getBaseContext()));

//        TODO Reverse Geocode this to get the location
//        datalocal.setLocationName();

        datalocal.save();

        MyShortcuts.showToast("Saved Successfully!", getBaseContext());
        Intent intent = new Intent(getBaseContext(),CapturedList.class);
        startActivity(intent);
    }

    private void loadPermissions(String perm, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                ActivityCompat.requestPermissions(this, new String[]{perm}, requestCode);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // granted

                    Looper looper = null;
                    if (checkPermission(getBaseContext())) {

//                        TODO Commented this location code
//                        locationManager.requestSingleUpdate(criteria, locationListener, looper);
                    }
                    Log.e("inside", "inside");
                } else {
                    // not granted
                    MyShortcuts.showToast("You cannot read meter details without accepting this permission!", getBaseContext());
                    showPermissionDialog();
                }
                return;
            }

        }

    }

    private void showPermissionDialog() {
        if (!checkPermission(this)) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        }
    }

    public static boolean checkPermission(final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean isGPSEnabled (Context mContext){
        LocationManager locationManager = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    /**
     * Used to check the result of the check of the user location settings
     *
     * @param requestCode code of the request made
     * @param resultCode code of the result of that request
     * @param intent intent with further information
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(intent);
        switch (requestCode) {
            case 1000:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        if ( googleApiClient.isConnected()) {
//                            startLocationUpdates();
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        break;
                    default:
                        break;
                }
                break;
        }
    }


   /* public void turnGPSOn()
    {
        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", true);
        this.ctx.sendBroadcast(intent);

        String provider = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(!provider.contains("gps")){ //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            this.ctx.sendBroadcast(poke);


        }
    }
    // automatic turn off the gps
    public void turnGPSOff()
    {
        String provider = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(provider.contains("gps")){ //if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            this.ctx.sendBroadcast(poke);
        }
    }*/

}
