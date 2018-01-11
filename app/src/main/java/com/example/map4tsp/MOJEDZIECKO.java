package com.example.map4tsp;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Random;

public class MOJEDZIECKO extends Activity
        implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMapClickListener {


    TextView distView;
    TextView titleText;

    GoogleMap googleMap;
    public LatLng ETI = new LatLng(54.371732, 18.612349);
    private LocationManager lm;
    public android.location.LocationListener listener;
    public LatLng dziki = new LatLng(54.361720, 18.578793);
    public LatLng niebezpieczne = new LatLng(54.360146, 18.580654);
    public LatLng budowa = new LatLng(54.358059, 18.588502);
    public LatLng pusty_plac = new LatLng(54.360763, 18.579426);
    public LatLng niebezpieczna_ulica = new LatLng(54.355375, 18.594665);
    public LatLng zaulek2 = new LatLng(54.358723, 18.598901);
    public LatLng sklep_nocny = new LatLng(54.362884, 18.611683);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.rich_map);
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i("ta klasa","ZNALAZLAM LOKALIZACJE");
                LatLng pozycja = new LatLng(location.getLongitude(), location.getLatitude());
                googleMap.addMarker(new MarkerOptions().position(pozycja).title("pozycja"));
                distView.setText("tutaj"+pozycja);
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



        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        distView = (TextView) findViewById(R.id.distanceView);

        titleText = (TextView)findViewById(R.id.title);
        titleText.setText(Html.fromHtml("<font color='#FF0000'>BEZ</font>problemu"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        registerListener();
    }
    void registerListener() {
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setMyLocationEnabled(true);
        googleMap.addMarker(new MarkerOptions().position(niebezpieczne)
                .title("NIEBEZPIECZNE OSIEDLE"));
        googleMap.addCircle(new CircleOptions().center(niebezpieczne).radius(50));
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setOnMapClickListener(this);
        googleMap.setIndoorEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(niebezpieczne,15));

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
        googleMap.addMarker(new MarkerOptions().position(zaulek2).title("ZAULEK"));
        googleMap.addMarker(new MarkerOptions().position(dziki).title("DZIKIE ZWIERZETA"));
        googleMap.addMarker(new MarkerOptions().position(budowa).title("BUDOWA"));
        googleMap.addMarker(new MarkerOptions().position(niebezpieczna_ulica).title("NIEBEZPIECZNA ULICA"));
        googleMap.addMarker(new MarkerOptions().position(pusty_plac).title("PUSTY PLAC"));

    }

    private Polyline track;
    @Override
    public void onClick(View view) {
        if(track !=null) track.remove();
        PolylineOptions po = new PolylineOptions();
        Random rand = new Random();
        po.add(dziki);
        for (int i = 0; i < 7; i++){
            double dlat = (rand.nextDouble() - 0.5) / 100;
            double dlon = (rand.nextDouble() - 0.5) / 70;
            po.add(new LatLng(dziki.latitude + dlat , dziki.longitude + dlon));
        }
        po.add(dziki);
        po.color(Color.BLUE);
        track = googleMap.addPolyline(po);

        for (Marker m: all) m.remove();
        all.clear();
        overalDist =0;
        pointId = 1;
    }

    int pointId = 1;
    ArrayList<Marker> all = new ArrayList<>();
    double overalDist=0;



    @Override
    public void onMapClick(LatLng latLng) {
        Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng)
                .title("Punkt " + pointId++));
        all.add(marker);    //for future use
        float[] results = new float[3];
        Location.distanceBetween(marker.getPosition().latitude,marker.getPosition().longitude,ETI.latitude,ETI.longitude,results);
        overalDist+=results[0];
        distView.setText(""+overalDist);
    }

}
