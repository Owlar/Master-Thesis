import com.google.common.base.Charsets;
import constants.Constants;
import influx.InfluxDB;
import jena.Jena;
import model.Area;
import model.Data;
import org.apache.commons.io.IOUtils;
import org.semanticweb.owlapi.io.OWLOntologyCreationIOException;
import owl.Owl;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    private Map<Integer,Data> dataList = new HashMap<>();
    private List<Worker> workers = new ArrayList<>();
    private InfluxDB influxDB = null;


    public static void main(String[] args) {
        Main main = new Main();
        main.initialize();
    }


    public void initialize() {
        Socket socket;
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println("Server has started! Now waiting for clients.");
            influxDB = new InfluxDB(
                    Constants.TOKEN.toString(),
                    Constants.BUCKET.toString(),
                    Constants.ORG.toString(),
                    Constants.URL.toString()
            );
            getAreas();

            while (true) {
                // Let clients connect to server
                socket = serverSocket.accept();
                System.out.println("A client has been accepted!");
                new Worker(socket).start();
            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }


    private void printDataList() {
        System.out.println("Number of records: " + dataList.size());
        int i = 1;
        for (Data d : dataList.values()) {
            System.out.println("   Record " + i + ": Client " + d.id + " has data [(" + d.latitude + ", " + d.longitude + "), " + d.instant + "]");
            i++;
        }
    }


    private void getAreas() {
        ArrayList<Area> resAreas = Owl.getAreasFromAssetModel();
        if (!resAreas.isEmpty()) {
            for (Area area : resAreas)
                influxDB.insertDataPoint(area);
            return;
        }
        System.out.println("No areas in asset model! Add some areas to: " + Constants.ONTOLOGYFILEPATH);
    }


    /* Thread class to keep track of multiple clients */
    class Worker extends Thread {

        private Socket socket;
        private PrintWriter writer = null;


        public Worker(Socket socket) {
            this.socket = socket;
            workers.add(this);
        }


        private int assignClient() {
            int id = workers.size();
            writer.println(id);
            System.out.println("Client ID is " + id);
            return id;
        }


        private Data getData(String[] parts) {
            Data data = new Data();
            data.id = parts[0].trim();
            data.latitude = Double.parseDouble(parts[1].split(",")[0]);
            data.longitude = Double.parseDouble(parts[1].split(",")[1]);
            data.instant = Instant.now();

            System.out.println("Client " + data.id + " stopped sending position!");

            return data;
        }


        private void warnEndangeredSmartphone(int id) {
            ArrayList<Integer> resSmartphones = Jena.getEndangeredSmartphones();
            if (!resSmartphones.isEmpty()) {
                for (Integer integer : resSmartphones) {
                    if (id == integer) {
                        System.out.println("Warning client: " + id);
                        writer.println(-1);
                    }
                }
                return;
            }
            System.out.println("Run the Digital Twin again and make sure to use 'dump' to update knowledge graph, then recheck position in client!");
        }


        @Override
        public void run() {
            try {
                writer = new PrintWriter(socket.getOutputStream(), true);
                int id = assignClient();

                // The status message received from a client
                String message = IOUtils.toString(socket.getInputStream(), Charsets.UTF_8);

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
                    socket.close();
                }
            } catch (IOException | OWLOntologyCreationIOException e) {
                e.printStackTrace();
            }
        }

    }
}