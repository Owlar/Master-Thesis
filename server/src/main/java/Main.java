import com.google.common.base.Charsets;
import influx.InfluxDB;
import influx.InfluxEnum;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

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
        }

        @Override
        public void run() {
            try {
                // Server tells client to send position
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println(1);

                // The position received from the client
                String data = IOUtils.toString(socket.getInputStream(), Charsets.UTF_8);

                if (!data.isEmpty()) {
                    influxDB.insertDataPoint(data);
                    influxDB.printFluxRecords();
                } else {
                    influxDB.closeInfluxClient();
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
