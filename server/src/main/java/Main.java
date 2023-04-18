import firebase.FirebaseDB;
import influx.InfluxDB;
import jena.Jena;
import model.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    private Map<Integer,Data> dataList = new HashMap<>();
    private List<Worker> workers = new ArrayList<>();
    private FirebaseDB firebaseDB = null;
    private InfluxDB influxDB = null;


    public static void main(String[] args) {
        Main main = new Main();
        main.initialize();
    }


    public void initialize() {
        System.out.println("Server has started! Now waiting for clients from realtime database.");

        firebaseDB = new FirebaseDB();
        while (true) {
            new Worker().start();
        }
    }


    private void printDataList() {
        System.out.println("Number of records: " + dataList.size());
        int i = 1;
        for (Data d : dataList.values()) {
            System.out.println("   Record " + i + ": Client " + d.id + " has data [(" + d.latitude + ", " + d.longitude + ")");
            i++;
        }
    }


    class Worker extends Thread {

        public Worker() {
            workers.add(this);
        }


        private void warnEndangeredSmartphone(int id) {
            ArrayList<Integer> resSmartphones = Jena.getEndangeredSmartphones();
            if (!resSmartphones.isEmpty()) {
                for (Integer integer : resSmartphones) {
                    if (id == integer) {
                        System.out.println("Warning client: " + id);
                        //writer.println(-1);
                    }
                }
                return;
            }
            System.out.println("Run the Digital Twin again and make sure to use 'dump' to update knowledge graph, then recheck position in client!");
        }


        @Override
        public void run() {
            firebaseDB.getData();



            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            workers.remove(this);


            /*
            try {

                // Is true when a client sends its latest position
                if (!message.isEmpty()) {
                    String[] parts = message.split(";");

                    // Identify client
                    if (String.valueOf(id).equals(parts[0].trim())) {
                        Data data = getData(parts);
                        dataList.put(id, data);
                        printDataList();

                        Owl.addIndividuals(dataList);
                        influxDB.insertDataPoint(data);

                        warnEndangeredSmartphone(id);

                        workers.remove(this);
                    }

                } else {
                    influxDB.closeInfluxClient();
                }
            } catch (IOException | OWLOntologyCreationIOException e) {
                e.printStackTrace();
            }*/
        }

    }
}