package adam.de.pickersdk.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import adam.de.pickersdk.R;
import adam.de.pickersdk.model.PlaceModel;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class PickerSdkMapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private static int RC_PERMISSION_LOCATION = 16;

    private GoogleMap mMap;

    PlaceModel placeModel;

    EditText typedMessage;

    Geocoder geocoder;

    public static final String PLACE_RESULTS = "place_results";

    public static Intent getAdamPlacePickerIntent(Context context) {
        return new Intent(context, PickerSdkMapActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pick_sdk_map_activity);


        if (!hasLocationPermissions() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION}, RC_PERMISSION_LOCATION);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button submit = findViewById(R.id.submit_result);
        Button search = findViewById(R.id.search);

        typedMessage = findViewById(R.id.address_search_field);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (placeModel == null) {
                    Toast.makeText(PickerSdkMapActivity.this, getString(R.string.pin_on_map_message), Toast.LENGTH_SHORT).show();
                } else {

                    /*
                        Send the results back to the previous activity using startActivityForResult
                        with onActivityResult
                    */

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(PLACE_RESULTS, placeModel);
                    setResult(Activity.RESULT_OK, returnIntent);

                    finish();
                }
            }
        });

    /*
        This will allow the user to type in a physical address and use geocoder to take the address
        and grab the latitude and longitude for it.
    */
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(typedMessage.getText())) {
                    Toast.makeText(PickerSdkMapActivity.this, getString(R.string.text_missing), Toast.LENGTH_LONG).show();
                } else {

                    List<Address> fromLocationName;
                    geocoder = new Geocoder(PickerSdkMapActivity.this, Locale.getDefault());

                    try {
                        fromLocationName = geocoder.getFromLocationName(typedMessage.getText().toString(), 1);

                        //user typed an address where there are no results
                        if (fromLocationName.size() <= 0) {
                            Toast.makeText(PickerSdkMapActivity.this, R.string.no_results, Toast.LENGTH_LONG).show();
                        } else {

                            LatLng latLng = new LatLng(fromLocationName.get(0).getLatitude(), fromLocationName.get(0).getLongitude());

                            displayMarkerOnMap(latLng.latitude, latLng.longitude, typedMessage.getText().toString());

                            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(latLng, 19);
                            mMap.animateCamera(yourLocation);
                        }

                    } catch (IOException e) {
                        Toast.makeText(PickerSdkMapActivity.this, R.string.error_message, Toast.LENGTH_LONG).show();
                    }
                }

                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
    }

    @Override
    public void onMapClick(LatLng latLng) {

        geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> fromLocation = new ArrayList<>();

        try {
            fromLocation = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            Toast.makeText(PickerSdkMapActivity.this, R.string.error_message, Toast.LENGTH_LONG).show();
        }

        //user clicked somewhere where there are no results
        if (fromLocation.size() <= 0) {
            Toast.makeText(PickerSdkMapActivity.this, R.string.no_results, Toast.LENGTH_LONG).show();
        } else {

            displayMarkerOnMap(latLng.latitude, latLng.longitude, fromLocation.get(0).getAddressLine(0));

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(this);
        findMyLocation();
    }

    /*
        only used to find the current location of the user and zoom in on google maps
    */

    public void findMyLocation() {
        if (mMap != null) {
            LatLng myPosition;

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            mMap.setMyLocationEnabled(true);

            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(provider);

            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                myPosition = new LatLng(latitude, longitude);

                CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(myPosition, 19);
                mMap.animateCamera(yourLocation);
            }
        }
    }

    /*
        used for displaying a marker on the map based on latitude and longitude that was clicked or typed
        a readable address will become the title of the marker
    */

    public void displayMarkerOnMap(Double latitude, Double longitude, String title) {
        placeModel = new PlaceModel();

        mMap.clear();

        mMap.addMarker(new MarkerOptions().position(
                new LatLng(latitude, longitude)).title(title)).showInfoWindow();

        if (placeModel != null) {
            placeModel.setLat(latitude);
            placeModel.setLng(longitude);
            placeModel.setAddress(title);
        }
    }

    public static PlaceModel getPlace(Intent bundle) {
        return (PlaceModel) bundle.getSerializableExtra(PLACE_RESULTS);
    }

    /*
    this is used to check if the user has accepted permissions for location.

    Please Note

    The library will still function fully if permissions are denied
    The only reason that we need these permissions is to pinpoint where the user is currently.

    If they deny the permissions, google maps will keep a top view, but once a user has typed an address
    the map will relocate to that destination on the map
     */

    public boolean hasLocationPermissions() {
        return ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_PERMISSION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (hasLocationPermissions()) {
                        findMyLocation();
                    } else {
                        requestPermissions(new String[]{ACCESS_FINE_LOCATION}, RC_PERMISSION_LOCATION);
                    }
                } else {
                    findMyLocation();
                }
            }
        }
    }
}
