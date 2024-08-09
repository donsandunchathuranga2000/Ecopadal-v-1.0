package com.example.ecopadal;

import static com.example.ecopadal.R.*;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleSignInClient mGoogleSignInClient;
    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private FirebaseAuth mAuth;
    private ActivityResultLauncher<String[]> locationPermissionRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);//initialize the location client
        SupportMapFragment mapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }


        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) FloatingActionButton fab = findViewById(id.fab_button);
        fab.setOnClickListener(view -> getcurrentlocation());
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize the location permission request launcher
        locationPermissionRequest = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    Boolean fineLocationGranted = result.getOrDefault(
                            Manifest.permission.ACCESS_FINE_LOCATION, false);
                    Boolean coarseLocationGranted = result.getOrDefault(
                            Manifest.permission.ACCESS_COARSE_LOCATION, false);
                    if (fineLocationGranted != null && fineLocationGranted) {
                        // Precise location access granted.
                        handleFineLocationGranted();
                    } else if (coarseLocationGranted != null && coarseLocationGranted) {
                        // Only approximate location access granted.
                        handleCoarseLocationGranted();
                    } else {
                        // No location access granted.
                        handleNoLocationGranted();
                    }
                }
        );


        // Check for permissions and request them if not already granted
        checkAndRequestPermissions();



    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        // Enable the MyLocation layer if permission is granted
        enableMyLocation();
        addMarkers(googleMap);


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String markerTitle = marker.getTitle();

                Intent intent = new Intent(MainActivity.this, Mainmenue.class);
                intent.putExtra("markerTitle", markerTitle);
                startActivity(intent);
                return false;
            }
        });


    }


    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

    }

    private void getcurrentlocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                        mMap.addMarker(new MarkerOptions().position(currentLocation).title("You are here"));
                    } else {
                        Toast.makeText(MainActivity.this, "Unable to find location.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            // Permissions already granted, proceed with your logic
            handleFineLocationGranted();
        } else {
            // Request permissions
            locationPermissionRequest.launch(new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuth.getCurrentUser() != null) {
            mAuth.signOut();
            mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
                Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void signOutAndStartSignInActivity() {
        mAuth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void handleFineLocationGranted() {
        // Handle the case when precise location access is granted
        // Your logic here
    }

    private void handleCoarseLocationGranted() {
        // Handle the case when only approximate location access is granted
        // Your logic here
    }

    private void handleNoLocationGranted() {
        // Handle the case when no location access is granted
        Toast.makeText(this, "Location permissions are required to show the map.", Toast.LENGTH_SHORT).show();
    }


    private void addMarkers(GoogleMap googleMap) {
        LatLng colombo = new LatLng(6.899259277294395, 79.86072890544258);
        BitmapDescriptor customMarker = BitmapDescriptorFactory.fromResource(R.drawable.bikerent);
        googleMap.addMarker(new MarkerOptions().position(colombo).title("Uni junction").icon(customMarker));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(colombo, 18));


        LatLng colombo2 = new LatLng(6.8936427171307475, 79.86195465604716);
        BitmapDescriptor customMarker2 = BitmapDescriptorFactory.fromResource(R.drawable.bikerent);
        googleMap.addMarker(new MarkerOptions().position(colombo2).title("Police Park").icon(customMarker2));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(colombo2, 18));


        LatLng havlock = new LatLng(6.883286835958859, 79.86838573884928);
        BitmapDescriptor customMarker3= BitmapDescriptorFactory.fromResource(R.drawable.bikerent);
        googleMap.addMarker(new MarkerOptions().position(havlock).title("Havlock").icon(customMarker3));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(havlock, 18));

        LatLng kirulapone= new LatLng(6.878454218939701, 79.87547467099276);
        BitmapDescriptor customMarker4= BitmapDescriptorFactory.fromResource(R.drawable.bikerent);
        googleMap.addMarker(new MarkerOptions().position(kirulapone).title(" Kirulapone").icon(customMarker4));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kirulapone, 18));
    }

}
