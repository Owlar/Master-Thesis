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
import java.util.ArrayList;

public class Main {

    private DatabaseReference reference = null;
    private InfluxDB influxDB = null;


    public static void main(String[] args) {
        Main main = new Main();
        main.initialize();
    }



    public void initialize() {
        System.out.println("Server has started! Getting clients from realtime database.");

        setupFirebase();
        setupInflux();

        System.out.println("Running..");

        while (true) {
            new Worker().start();
        }
    }

    class Worker extends Thread {

        @Override
        public void run() {
            prepareData();
            ArrayList<Integer> res = Jena.getEndangeredSmartphones();
            // TODO: Update Firebase with endangered smartphones
        }

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



    public void prepareData() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String json = new Gson().toJson(snapshot.getValue(Object.class));
                    Data data = new Gson().fromJson(json, Data.class);
                    influxDB.insertDataPoint(data);
                    try {
                        Owl.addIndividual(data);
                    } catch (OWLOntologyCreationIOException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Could not retrieve data from realtime database.");
            }
        });
    }
}