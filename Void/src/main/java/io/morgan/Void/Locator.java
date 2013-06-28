package io.morgan.Void;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mobrown on 6/16/13.
 *
 * References:
 * http://stackoverflow.com/questions/3145089/what-is-the-simplest-and-most-robust-way-to-get-the-users-current-location-in-a/3145655
 * http://stackoverflow.com/questions/12724533/how-to-get-current-location-in-android-application
 * http://stackoverflow.com/questions/1513485/how-do-i-get-the-current-gps-location-programmatically-in-android
 */
public class Locator {
    final int LOCATION_TIMEOUT = 20000;

    Timer timer;
    LocationManager locationManager;
    LocatorListener locatorListener;
    boolean gpsEnabled = false;
    boolean networkEnabled = false;

    public boolean getLocation(Context context, LocatorListener locatorListener) {

        //I use LocationResult callback class to pass location value from MyLocation to user code.
        this.locatorListener = locatorListener;

        if(locationManager == null) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }

        // Exceptions will be thrown if provider is not permitted.
        try{
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch(Exception ex) {
            Toast.makeText(context, "GPS is not available", Toast.LENGTH_LONG).show();
        }

        try{
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            Toast.makeText(context,"Network is not available", Toast.LENGTH_LONG).show();
        }

        //don't start listeners if no provider is enabled
        if(!gpsEnabled && !networkEnabled) {
            return false;
        }

        if(gpsEnabled) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGPS);
        }

        if(networkEnabled) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
        }

        timer = new Timer();
        timer.schedule(new GetLastLocation(), LOCATION_TIMEOUT);
        return true;
    }

    LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer.cancel();
            locatorListener.onLocationFound(location);
            locationManager.removeUpdates(this);
            locationManager.removeUpdates(locationListenerNetwork);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer.cancel();
            locatorListener.onLocationFound(location);
            locationManager.removeUpdates(this);
            locationManager.removeUpdates(locationListenerGPS);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    class GetLastLocation extends TimerTask {

        @Override
        public void run() {
            Location networkLocation = null;
            Location gpsLocation = null;

            locationManager.removeUpdates(locationListenerGPS);
            locationManager.removeUpdates(locationListenerNetwork);

            if(gpsEnabled) {
                gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

            if(networkEnabled) {
                networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            //if there are both values use the latest one
            if(gpsLocation != null && networkLocation != null) {
                if(gpsLocation.getTime() > networkLocation.getTime()) {
                    locatorListener.onLocationFound(gpsLocation);
                } else {
                    locatorListener.onLocationFound(networkLocation);
                }

                return;
            }

            if(gpsLocation != null) {
                locatorListener.onLocationFound(gpsLocation);
                return;
            }

            if(networkLocation != null) {
                locatorListener.onLocationFound(networkLocation);
                return;
            }

            locatorListener.onLocationFound(null);
        }
    }

    public static String getLocalityAndCountry(Context context, Location location) {
        String result = null;
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;

        if(location == null) {
            return result;
        }

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.size() > 0) {
                result = addresses.get(0).getLocality() + ", "+ addresses.get(0).getCountryName();
            }

        } catch (IOException e) {
            Toast.makeText(context, "Your location could not be determined", Toast.LENGTH_LONG).show();
            e.printStackTrace();

        }

        return result;
    }

    public static abstract class LocatorListener {
        public abstract void onLocationFound(Location location);
    }
}
