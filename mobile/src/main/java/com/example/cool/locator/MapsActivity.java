package com.example.cool.locator;

import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private static final LatLng FidCenter = new LatLng(40.7064, -74.0094);
    private static final LatLng MyLocation = new LatLng(40.71, -73.998585);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
//        setUpMapIfNeeded();
//        onMapReady(mMap);


    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        System.out.println("In Setup Map");
       /* Location points = mMap.getMyLocation();
        System.out.println("My Location"+ points); */
        CameraPosition w = mMap.getCameraPosition();
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        LatLng fidelity_center = new LatLng(40.7064, -74.0094);
        CameraPosition INIT = new CameraPosition.Builder().target(MyLocation).zoom( 17.5F ).bearing(300F).tilt(50F).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(INIT));
        String url = getMapsApiDirectionsUrl();
        System.out.println("Direction Url" + url);
        mMap.addMarker(new MarkerOptions().position(FidCenter).title("My Location"));
        mMap.addMarker(new MarkerOptions().position(MyLocation).title("Fidelity Center"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fidelity_center, 13));
        //mMap.animateCamera(CameraUpdateFactory.newCameraPosition(INIT) );
        mMap.addMarker( new MarkerOptions().position(MyLocation ).snippet("Radius: " + "1 mi") ).showInfoWindow();
        CircleOptions circleOptions = new CircleOptions().center( MyLocation ).radius(1500).fillColor(0x40ff0000).strokeColor(Color.TRANSPARENT).strokeWidth(2);

        Circle circle = mMap.addCircle(circleOptions);
        ReadTask downloadTask = new ReadTask();
        downloadTask.execute(url);
    }

    private String getMapsApiDirectionsUrl() {
        String origin = "origin="+ MyLocation.latitude + "," + MyLocation.longitude;
        String destination = "destination="+ FidCenter.latitude + "," + FidCenter.longitude;
        String waypoints = "waypoints=optimize:true|"
                + MyLocation.latitude + "," + MyLocation.longitude + "|" + FidCenter.latitude + "," + FidCenter.longitude;

        String sensor = "sensor=false";
        String params = origin + "&" + destination + "&" + waypoints + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params;
        return url;
    }


    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            System.out.println("In Read Task-------------->");
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
                //System.out.println("In Read Task-------------->: data"+ data);
            } catch (Exception e) {
                System.out.println("Background Task" + e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
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

            // traversing through routes
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
                polyLineOptions.width(2);
                polyLineOptions.color(Color.BLUE);
            }

            mMap.addPolyline(polyLineOptions);
        }
    }
}
