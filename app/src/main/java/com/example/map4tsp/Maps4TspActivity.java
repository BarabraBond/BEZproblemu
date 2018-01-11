package com.example.map4tsp;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
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

public class Maps4TspActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    private LocationManager lm;

    public android.location.LocationListener listener;

    private Polyline way = null;
    private Marker destinationMarker = null;

    private TextView title;
    private TextView gps;
    private Button home_route;
    private Button call;

    private Boolean is_home_route = false;

    private GoogleMap mMap;

    public LatLng dziki = new LatLng(54.361720, 18.578793);
    public LatLng niebezpieczne = new LatLng(54.360146, 18.580654);
    public LatLng budowa = new LatLng(54.358059, 18.588502);
    public LatLng pusty_plac = new LatLng(54.360763, 18.579426);
    public LatLng niebezpieczna_ulica = new LatLng(54.355375, 18.594665);
    public LatLng dom = new LatLng(54.358723, 18.598901);
    public LatLng sklep_nocny = new LatLng(54.362884, 18.611683);
    public LatLng obecna_pozycja = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drugi);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        call = (Button)findViewById(R.id.call);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:123"));
                startActivity(callIntent);
            }
        });
        home_route = (Button)findViewById(R.id.home_route);
        home_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                is_home_route = true;
                onMapClick(obecna_pozycja);
            }
        });
        title = (TextView)findViewById(R.id.title);
        title.setText(Html.fromHtml("<font color='#000000'>BEZ</font><font color='#FF0000'>problemu</font>"));
        gps = (TextView)findViewById(R.id.gps_status);

        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        //obecna_pozycja = niebezpieczna_ulica;
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                gps.setText("Znaleziono GPS");
                obecna_pozycja = new LatLng(location.getLatitude(),location.getLongitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }

        };

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(niebezpieczne, 16));

        PolylineOptions polilinia = new PolylineOptions().add(
                new LatLng(54.361923, 18.576396),
                new LatLng(54.361823, 18.578380),
                new LatLng(54.361702, 18.580128),
                new LatLng(54.362634, 18.580503),
                new LatLng(54.362994, 18.579583),
                new LatLng(54.363612, 18.578805),
                new LatLng(54.363118, 18.577111),
                new LatLng(54.361923, 18.576396)
        );
        googleMap.addPolyline(polilinia);
        googleMap.addMarker(new MarkerOptions().position(sklep_nocny).title("SKLEP NOCNY"));
        googleMap.addMarker(new MarkerOptions().position(niebezpieczne).title("ZAULEK"));
        googleMap.addMarker(new MarkerOptions().position(dom).title("DOM")).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        googleMap.addMarker(new MarkerOptions().position(dziki).title("DZIKIE ZWIERZETA"));
        googleMap.addMarker(new MarkerOptions().position(budowa).title("BUDOWA"));
        googleMap.addMarker(new MarkerOptions().position(niebezpieczna_ulica).title("NIEBEZPIECZNA ULICA"));
        googleMap.addMarker(new MarkerOptions().position(pusty_plac).title("PUSTY PLAC"));


        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {

                if(obecna_pozycja != null) {
                    if(way != null){
                        way.remove();
                    }
                    if(destinationMarker != null){
                        destinationMarker.remove();
                    }

                    LatLng origin = obecna_pozycja;
                    LatLng destination;
                    if(is_home_route) {
                        destination = dom;
                    }else{
                        destination = latLng;
                    }


                    is_home_route = false;

                    MarkerOptions options = new MarkerOptions();

                    options.position(latLng);
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                    destinationMarker = mMap.addMarker(options);
                    String url = getDirectionsUrl(origin, destination);
                    DownloadTask downloadTask = new DownloadTask();
                    downloadTask.execute(url);
                } else {
                    gps.setText("Brak sygnału!!!");
                }

    }

    @Override
    public void onMapLongClick(final LatLng latLng) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Wybierz zagrożenie")
                .setItems(R.array.niebezpieczenstwa, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        LatLng pos = latLng;
                        mMap.addMarker(new MarkerOptions().position(pos).title(getResources().getStringArray(R.array.niebezpieczenstwa)[which]));
                    }
                });
        builder.create().show();
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);

        }
    }


    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);
            }

// Drawing polyline in the Google Map for the i-th route
            way = mMap.addPolyline(lineOptions);
        }

    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=walking";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}
