package com.pronque.scanneurqr;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class MainActivity extends AppCompatActivity {
    // Déclaration du bouton pour lancer le scan
    private Button BT_scanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialisation du bouton pour lancer le scan
        BT_scanner = findViewById(R.id.scanner);

        // Au clic sur le bouton, lance le scanner
        BT_scanner.setOnClickListener(v -> {
            scanCode();
        });
    }

    /**
     * Permet de scanner lo code QR
     */
    private void scanCode() {
        // Déclare les options du scanner
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scannez le QR Code");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureScanActivity.class);
        // Lance le scanner
        scanCodeLauncher.launch(options);
    }

    /**
     * Permet de récupérer le résultat du scan
     */
    ActivityResultLauncher<ScanOptions> scanCodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        // Vérifie si le résultat n'est pas null
        if (result.getContents() != null) {
            // Lance l'activité MapsActivity avec le résultat du scan
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra("result", result.getContents());
            startActivity(intent);
        }
    });
}