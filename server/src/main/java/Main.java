import com.google.common.base.Charsets;
import db.InfluxDB;
import db.InfluxEnum;
import org.apache.commons.io.IOUtils;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {
        Socket socket;
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println("Server has started! Now waiting for a client.");

            socket = serverSocket.accept();
            System.out.println("Client has been accepted!");

            InfluxDB influxDB = new InfluxDB(
                    InfluxEnum.TOKEN.toString(),
                    InfluxEnum.BUCKET.toString(),
                    InfluxEnum.ORG.toString(),
                    InfluxEnum.URL.toString()
            );

            while (true) {
                try {
                    String data = IOUtils.toString(socket.getInputStream(), Charsets.UTF_8);

                    if (!data.isEmpty()) {
                        influxDB.insertDataPoint(data);
                        influxDB.printFluxRecords();
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