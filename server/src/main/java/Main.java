import com.google.common.base.Charsets;
import influx.InfluxDB;
import influx.InfluxEnum;
import org.apache.commons.io.IOUtils;

import java.io.EOFException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {
        Socket socket = null;
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println("Server has started! Now waiting for a client.");

            InfluxDB influxDB = new InfluxDB(
                    InfluxEnum.TOKEN.toString(),
                    InfluxEnum.BUCKET.toString(),
                    InfluxEnum.ORG.toString(),
                    InfluxEnum.URL.toString()
            );

            while (true) {
                try {
                    // First make sure client connects to server
                    // TODO: Use threads so multiple clients can connect
                    socket = serverSocket.accept();
                    System.out.println("Client has been accepted!");

                    // After client has connected, server tells client to send position
                    // TODO: Tell multiple clients to send their positions
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println(1);

                    String data = IOUtils.toString(socket.getInputStream(), Charsets.UTF_8);

                    if (!data.isEmpty()) {
                        influxDB.insertDataPoint(data);
                        influxDB.printFluxRecords();
                    } else {
                        influxDB.closeInfluxClient();
                        socket.close();
                        break;
                    }

                } catch (EOFException eofException) {
                    eofException.printStackTrace();
                }
            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
