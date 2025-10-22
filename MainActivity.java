package com.example.delhimapapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import org.json.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EditText sourceEditText, destinationEditText;
    private TextView resultTextView;
    private Button findRouteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sourceEditText = findViewById(R.id.sourceEditText);
        destinationEditText = findViewById(R.id.destinationEditText);
        resultTextView = findViewById(R.id.resultTextView);
        findRouteButton = findViewById(R.id.findRouteButton);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        findRouteButton.setOnClickListener(v -> {
            String source = sourceEditText.getText().toString();
            String destination = destinationEditText.getText().toString();
            getRouteInfo(source, destination);
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng delhi = new LatLng(28.6139, 77.2090);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(delhi, 12));
    }

    private void getRouteInfo(String source, String destination) {
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" +
                source + "&destination=" + destination + "&key=YOUR_API_KEY_HERE";

        new Thread(() -> {
            try {
                URL directionUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) directionUrl.openConnection();
                conn.setRequestMethod("GET");
                InputStream in = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONObject jsonObject = new JSONObject(result.toString());
                JSONArray routes = jsonObject.getJSONArray("routes");
                JSONObject route = routes.getJSONObject(0);
                JSONObject leg = route.getJSONArray("legs").getJSONObject(0);
                String distance = leg.getJSONObject("distance").getString("text");
                String duration = leg.getJSONObject("duration").getString("text");

                runOnUiThread(() -> resultTextView.setText("Distance: " + distance + "\nTime: " + duration));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
