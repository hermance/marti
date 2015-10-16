package fr.martigne_le_logis.hermancedornier.martimenu;

import android.app.Fragment;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Hermance Dornier on 28/09/2015.
 */
public class LocationFragment extends Fragment {
    private static View view;
    // Initializing
    ArrayList<LatLng> markerPoints = new ArrayList<LatLng>();
    MapView mMapView;
    private static GoogleMap googleMap;
    private LocationManager locationManager;
    private Button locate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //ArrayList<LocationProvider> providers = new ArrayList<LocationProvider>();
        //ArrayList<String> names = locationManager.getProviders(true);
        View v = inflater.inflate(R.layout.map_layout, container,
                false);
        mMapView = (MapView) v.findViewById(R.id.map_layout);
        locate = (Button)v.findViewById(R.id.locate);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        googleMap.setMyLocationEnabled(true);
        googleMap.addMarker(new MarkerOptions().position(new LatLng(47.93969,-0.254394)).title("Le Logis de Martigne"));
        //47.93969,-0.254394,17
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(47.93969,-0.254394))      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);


        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
/*
                // Already 10 locations with 8 waypoints and 1 start location and 1 end location.
                // Upto 8 waypoints are allowed in a query for non-business users
                if (markerPoints.size() >= 10) {
                    return;
                }

                // Adding new item to the ArrayList
                markerPoints.add(point);

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(point);

                /**
                 * For the start location, the color of marker is GREEN and
                 * for the end location, the color of marker is RED and
                 * for the rest of markers, the color is AZURE

                if (markerPoints.size() == 1) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else if (markerPoints.size() == 2) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                } else {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                }

                // Add new marker to the Google Map Android API V2
                googleMap.addMarker(options);*/
            }
        });

        // The map will be cleared on long click


        locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* if (markerPoints.size() >= 2) {
                    LatLng origin = markerPoints.get(0);
                    LatLng dest = markerPoints.get(1);
*/
                  // Getting URL to the Google Directions API
                if(googleMap.getMyLocation()!= null)
                {
                    LatLng origin = new LatLng(googleMap.getMyLocation().getLatitude(),googleMap.getMyLocation().getLongitude());
                LatLng dest = new LatLng(47.93969,-0.254394);

                    String url = getDirectionsUrl(origin, dest);

                    DownloadTask downloadTask = new DownloadTask();

                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);
                }
                else{
                    Toast.makeText(getActivity().getApplicationContext(),"Autorisez la géolocalisation!", Toast.LENGTH_LONG).show();

                }
            }
        });
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng point) {



                // Removes all the points from Google Map
                 googleMap.clear();

                // Removes all the points in the ArrayList
                 markerPoints.clear();
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }



    private String getDirectionsUrl(LatLng origin,LatLng dest){
       /* if(googleMap.getMyLocation()!= null)
        {
            origin = new LatLng(googleMap.getMyLocation().getLatitude(),googleMap.getMyLocation().getLongitude());
            dest = new LatLng(47.93969,-0.254394);*/
            // Origin of route
            String str_origin = "origin="+origin.latitude+","+origin.longitude;

            // Destination of route
            String str_dest = "destination="+dest.latitude+","+dest.longitude;

            // Sensor enabled
            String sensor = "sensor=false";

            // Waypoints
            String waypoints = "";
            for(int i=2;i<markerPoints.size();i++){
                LatLng point  = (LatLng) markerPoints.get(i);
                if(i==2)
                    waypoints = "waypoints=";
                waypoints += point.latitude + "," + point.longitude + "|";
            }

            // Building the parameters to the web service
            String parameters = str_origin+"&"+str_dest+"&"+sensor+"&"+waypoints;

            // Output format
            String output = "json";

            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

            return url;
        /*}
        else{
            Toast.makeText(getActivity().getApplicationContext(),"Autorisez la géolocalisation!", Toast.LENGTH_LONG).show();
            return null;
        }*/

    }



    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service

            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            List<String> data = new ArrayList<String>();
            //String distance = "";

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                //data = parser.getData(jObject);
               // googleMap.addMarker(new MarkerOptions().position(new LatLng(47.93969,-0.254394)).title("distance : " + data ));

                /*for(int i=0; i<data.size();i++) {
                    googleMap.addMarker(new MarkerOptions().position(new LatLng(47.93969,-0.254394)).title("distance : " + data[i]));

                }*/
                //googleMap.addMarker(new MarkerOptions().position(new LatLng(47.93969,-0.254394)).title("distance : " + distance));
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }


        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(Color.RED);
            }

            // Drawing polyline in the Google Map for the i-th route
            googleMap.addPolyline(lineOptions);
        }
    }
}
