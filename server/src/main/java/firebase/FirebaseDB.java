package firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import model.Data;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class FirebaseDB {

    public FirebaseDB() {
        try {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(new FileInputStream("saf.json")))
                    .setDatabaseUrl("https://masterthesis-77b98-default-rtdb.europe-west1.firebasedatabase.app")
                    .build();
            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            System.out.println("Authentication to Firebase failed.");
            e.printStackTrace();
        }
    }

    public ArrayList<Data> getData() {
        ArrayList<Data> res = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Could not retrieve data from Firebase realtime database.");
            }
        });
        return res;
    }
}
