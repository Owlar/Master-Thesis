import com.google.common.base.Charsets;
import influx.InfluxDB;
import influx.InfluxEnum;
import model.Data;
import org.apache.commons.io.IOUtils;
import org.semanticweb.owlapi.io.OWLOntologyCreationIOException;
import owl.Owl;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.util.*;

public class Main {

    private Map<Integer,Data> dataList = new HashMap<>();
    private List<Worker> workers = new ArrayList<>();

    private void printDataList() {
        System.out.println("Number of records: " + dataList.size());
        int i = 1;
        for (Data d : dataList.values()) {
            System.out.println("   Record " + i + ": Client " + d.id + " has data [(" + d.latitude + ", " + d.longitude + "), " + d.instant + "]");
            i++;
        }
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.initialize();
    }

    public void initialize() {
        Socket socket;
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println("Server has started! Now waiting for clients.");
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



    /* Thread class to handle multiple clients connecting to the server */
    class Worker extends Thread {

        private Socket socket;
        private InfluxDB influxDB;

        public Worker(Socket socket) {
            this.socket = socket;
            this.influxDB = new InfluxDB(
                    InfluxEnum.TOKEN.toString(),
                    InfluxEnum.BUCKET.toString(),
                    InfluxEnum.ORG.toString(),
                    InfluxEnum.URL.toString()
            );
            workers.add(this);
        }

        @Override
        public void run() {
            try {
                // Server assigns ID to client and informs the client its position can be sent to server
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                int id = workers.size();
                writer.println(id);

                System.out.println("The client's ID is " + id + ".");

                // The message received from the client
                String message = IOUtils.toString(socket.getInputStream(), Charsets.UTF_8);

                // Is true when a client sends its latest position
                if (!message.isEmpty()) {
                    String[] parts = message.split(";");

                    // Identify client
                    if (String.valueOf(id).equals(parts[0].trim())) {
                        Data data = new Data();
                        data.id = parts[0].trim();
                        data.latitude = Double.parseDouble(parts[1].split(",")[0]);
                        data.longitude = Double.parseDouble(parts[1].split(",")[1]);
                        data.instant = Instant.now(); // TODO: Use received date

                        System.out.println("Client " + data.id + " stopped sending position!");

                        dataList.put(id, data);
                        printDataList();

                        Owl.addIndividuals(dataList);
                        influxDB.insertDataPoint(data);

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