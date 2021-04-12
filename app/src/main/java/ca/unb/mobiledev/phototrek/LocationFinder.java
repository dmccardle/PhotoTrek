package ca.unb.mobiledev.phototrek;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationFinder {
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static LocationFinder instance;
    private LocationRequest mLocationRequest;
    private LatLng lastLocation;
    private Context context;
    private boolean requestingLocationRequest;

    public static LocationFinder getInstance(Context contextIn) {
        if (instance == null) {
            instance = new LocationFinder(contextIn);
            instance.lastLocation = new LatLng(1.0, 1.0);
        }
        return instance;
    }

    private LocationFinder(Context contextIn) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(contextIn);
        context = contextIn;
        setupLocationRequest(context);
    }

    public void setupLocationRequest(Context context) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(120000); // two minute interval
        mLocationRequest.setFastestInterval(120000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        startLocationUpdates();
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location currentLocation = locationResult.getLastLocation();
            lastLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        }
    };

    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && isLocationEnabled()) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    lastLocation = new LatLng(location.getLatitude(), location.getLongitude());
                }
            });
            fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            requestingLocationRequest = true;
        } else {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    public void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        requestingLocationRequest = false;
    }

    public LatLng getLocation() {
        return lastLocation;
    }

    public boolean isRequestingLocationRequest() {
        return requestingLocationRequest;
    }

    public boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    public double getLatitude() {
        return lastLocation.latitude;
    }

    public double getLongitude() {
        return lastLocation.longitude;
    }

}
