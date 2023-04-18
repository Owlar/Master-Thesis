package firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import com.google.gson.Gson;
import constants.Constants;
import influx.InfluxDB;
import model.Data;

import java.io.FileInputStream;
import java.io.IOException;

public class FirebaseDB {
    private final InfluxDB influxDB;
    private DatabaseReference reference = null;

    public FirebaseDB() {
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
        // Database to write to
        influxDB = new InfluxDB(
                Constants.TOKEN.toString(),
                Constants.BUCKET.toString(),
                Constants.ORG.toString(),
                Constants.URL.toString()
        );
    }

    public void getData() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("Number of clients with known position in realtime database: " + dataSnapshot.getChildrenCount());
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String json = new Gson().toJson(snapshot.getValue(Object.class));
                    Data data = new Gson().fromJson(json, Data.class);

                    influxDB.insertDataPoint(data);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Could not retrieve data from realtime database.");
            }
        });
    }

}
