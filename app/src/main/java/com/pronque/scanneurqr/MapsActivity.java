package com.pronque.scanneurqr;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.pronque.scanneurqr.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    // Déclaration des variables

    // Code de la requête pour la permission
    private static final int REQUEST_CODE = 101;
    // Variables pour la localisation
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    // Point d'entrée pour gérer les données et les éléments géographiques sous-jacents de la carte
    private GoogleMap mMap;
    // Point d'entrée pour les services de localisation
    private ActivityMapsBinding binding;
    // Fragment pour gérer le cycle de vie d'un objet GoogleMap
    private SupportMapFragment mapFragment;
    // Fragment pour gérer le cycle de vie d'un objet smsFragment
    private smsFragment smsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialise le services de localisation
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialise le fragment de la carte
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        // Initialise le fragment du sms
        smsFragment = new smsFragment();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Appelle la méthode pour récupérer la position actuelle
        getCurrentLocation();
    }

    /**
     * Récupère la position actuelle
     */
    private void getCurrentLocation() {
        // Vérifie si l'application a la permission de localisation
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Demande la permission de localisation
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        // Récupère la position actuelle
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            // Vérifie si la position n'est pas nulle
            if (location != null) {
                currentLocation = location;
                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                assert supportMapFragment != null;
                supportMapFragment.getMapAsync(MapsActivity.this);
            }
        });
    }

    /**
     * Appelé lorsque la carte est prête à être utilisée.
     *
     * @param googleMap Objet GoogleMap qui est prêt à être utilisé.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Récupère les extras de l'intent
        Bundle extras = getIntent().getExtras();

        // Récupère la latitude et la longitude
        String result = extras.getString("result");
        double latitude = Double.parseDouble(result.split(",")[0]);
        double longitude = Double.parseDouble(result.split(",")[1]);

        // Envoi au fragment les données pour le message
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("latitude", String.valueOf(latitude));
        bundle.putString("longitude", String.valueOf(longitude));
        bundle.putString("result", result);

        // Ajoute les arguments au fragment
        smsFragment.setArguments(bundle);
        transaction.replace(R.id.sms, smsFragment).commit();

        // Initialise la carte
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // La position actuelle
        //LatLng current = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        // La position du QR Code
        LatLng scan = new LatLng(latitude, longitude);
        // Ajoute un marqueur
        MarkerOptions markerOptions = new MarkerOptions().position(scan);
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(scan));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(scan, 12));
        googleMap.addMarker(markerOptions);
    }

    /**
     * Appelé lorsque la demande de permission est terminée.
     *
     * @param requestCode  Le code de demande envoyé avec la demande.
     * @param permissions  Les tableaux de chaînes de caractères des permissions demandées.
     * @param grantResults Les tableaux d'entiers des résultats de la demande associée à chaque permission demandée.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Vérifie si la permission a été acceptée
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Appelle la méthode pour récupérer la position actuelle
                getCurrentLocation();
            }
        }
    }
}