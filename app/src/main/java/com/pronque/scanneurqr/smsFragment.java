package com.pronque.scanneurqr;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

/**
 * Une simple sous-classe de {@link Fragment}.
 */
public class smsFragment extends Fragment {
    // Déclaration des variables
    private EditText ED_num_tel;
    public EditText ED_message;
    private Button BT_envoyer;


    /**
     * Création du fragment
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Création de la vue
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_sms, container, false);

        // Initialisation des variables
        ED_num_tel = rootView.findViewById(R.id.numTel);
        ED_message = rootView.findViewById(R.id.message);
        BT_envoyer = rootView.findViewById(R.id.envoyer);

        // Récupère les arguments passés au fragment
        Bundle data = getArguments();

        // Si les arguments ne sont pas null
        if (data != null) {
            // Récupère la latitude, la longitude, le nom de la benne, le message et l'url Google Maps
            String result = data.getString("result");
            String latitude = data.getString("latitude");
            String longitude = data.getString("longitude");
            String benne = result.split(",")[2];
            String message = result.split(",")[3];
            String urlMaps = "https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude;

            // Met le message dans l'EditText
            ED_message.setText(benne + " : " + message + "\n " + urlMaps);
        }

        // Au clic sur le bouton, envoie le message
        BT_envoyer.setOnClickListener(v -> {
            // Vérifie si l'utilisateur a donné les permissions
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                // Appelle la méthode d'envoi du message
                sendMessage();
            } else {
                // Demande la permission d'envoyer un SMS
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.SEND_SMS}, 100);
            }
        });

        return rootView;
    }

    /**
     * Permet d'envoyer le message
     */
    public void sendMessage() {
        // Récupère le numéro de téléphone et le message
        String numTel = ED_num_tel.getText().toString();
        String message = ED_message.getText().toString();

        // Vérifie si le numéro de téléphone et le message ne sont pas vides
        if (numTel != null && message != null && !numTel.isEmpty() && !message.isEmpty()) {
            // Envoie le message et affiche une confirmation
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(numTel, null, message, null, null);
            Toast.makeText(getContext(), "Message envoyé", Toast.LENGTH_SHORT).show();
        } else {
            // Affiche un message d'erreur
            Toast.makeText(getContext(), "Entrez un numéro et/ou un le message", Toast.LENGTH_SHORT).show();
        }
    }
}