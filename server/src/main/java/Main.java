import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import com.google.gson.Gson;
import constants.Constants;
import influx.InfluxDB;
import jena.Jena;
import model.Data;
import org.semanticweb.owlapi.io.OWLOntologyCreationIOException;
import owl.Owl;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class Main {

    private DatabaseReference reference = null;
    private InfluxDB influxDB = null;
    private boolean hasSensorData = false;
    private boolean hasWarned = false;


    public static void main(String[] args) {
        Main main = new Main();
        main.initialize();
    }



    public void initialize() {
        System.out.println("Server has started! Getting clients from realtime database.");

        setupFirebase();
        setupInflux();

        System.out.println("Running..");

        Forwarding forwarding = new Forwarding();
        Timer timer = new Timer();
        timer.schedule(forwarding, 0, 10000);
    }



    class Forwarding extends TimerTask {
        @Override
        public void run() {
            getSensorData();

            if (hasSensorData) {
                Map<String, String> res = Jena.getEndangeredSmartphones();
                if (res.size() != 0) {
                    warnEndangeredClients(res);
                }
            }
        }
    }



    private void warnEndangeredClients(Map<String, String> results) {
        reference = FirebaseDatabase.getInstance().getReference("endangered");
        DatabaseReference.CompletionListener completionListener = (databaseError, databaseReference) -> {
            System.out.println("Forwarded informed decision to PT!");
            hasWarned = true;
        };
        reference.setValue(results, completionListener);
    }



    public void setupFirebase() {
        System.out.println("Setting up real time database..");
        try {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(new FileInputStream(Constants.SERVICEACCOUNTFILE.toString())))
                    .setDatabaseUrl(Constants.FIREBASEDATABASEURL.toString())
                    .build();
            FirebaseApp.initializeApp(options);
            reference = FirebaseDatabase.getInstance().getReference("mobiles");
        } catch (IOException e) {
            System.out.println("Authentication to Firebase failed.");
            e.printStackTrace();
        }
    }



    public void setupInflux() {
        System.out.println("Setting up time series database..");
        influxDB = new InfluxDB(
                Constants.TOKEN.toString(),
                Constants.BUCKET.toString(),
                Constants.ORG.toString(),
                Constants.URL.toString()
        );
    }



    public void getSensorData() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String json = new Gson().toJson(snapshot.getValue(Object.class));
                    Data data = new Gson().fromJson(json, Data.class);
                    influxDB.insertDataPoint(data);
                    System.out.println("Forwarded sensor data of ID: " + data.id + " to DT!");
                    try {
                        Owl.addIndividual(data);
                    } catch (OWLOntologyCreationIOException e) {
                        e.printStackTrace();
                    }
                    hasSensorData = true;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Could not retrieve data from realtime database.");
            }
        });
    }



}