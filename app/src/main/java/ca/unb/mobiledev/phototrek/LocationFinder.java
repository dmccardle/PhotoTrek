package ca.unb.mobiledev.phototrek;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationFinder {
    private FusedLocationProviderClient fusedLocationProviderClient;
    private double longitude;
    private double latitude;
    private Context context;
    private Activity activity;

    public LocationFinder(Context contextIn, Activity activityIn) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(contextIn);
        context = contextIn;
        activity = activityIn;
    }

    public double[] getLocation() {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
                        try {
                            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                            List<Address> addresses = geocoder.getFromLocation(
                                    location.getLatitude(), location.getLongitude(), 1
                            );
                            longitude = location.getLongitude();
                            latitude = location.getLatitude();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
        return new double[]{latitude, longitude};
    }

    public void setPhotoLocation(Photo photo, DataManager dataManager) {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if(location != null){
//                        try {
//                            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
//                            List<Address> addresses = geocoder.getFromLocation(
//                                    location.getLatitude(), location.getLongitude(), 1
//                            );
                            longitude = location.getLongitude();
                            latitude = location.getLatitude();
                            LatLng latlong = new LatLng(latitude, longitude);
                            photo.setCoordinates(latlong);
                            dataManager.addPhoto(photo);
                            Log.i("PHOTO", photo.getCoordinates().latitude + " ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~LOCATIONFINDER~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ " + photo.getCoordinates().longitude);
//                        } catch (IOException e){
//                            e.printStackTrace();
//                        }
                    }
                }
            });
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

}
