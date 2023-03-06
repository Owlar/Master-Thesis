package influx;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import model.Data;

import java.time.Instant;
import java.util.List;

public class InfluxDB {

    private final String bucket;
    private final String org;
    private final InfluxDBClient db;

    public InfluxDB(String token, String bucket, String org, String url) {
        this.bucket = bucket;
        this.org = org;
        this.db = InfluxDBClientFactory.create(
                url,
                token.toCharArray()
        );
    }

    public void insertDataPoint(String string) {
        String[] parts = string.split(";");
        Data data = new Data();
        data.id = Integer.parseInt(parts[0]);
        data.latitude = Double.parseDouble(parts[1].split(",")[0]);
        data.longitude = Double.parseDouble(parts[1].split(",")[1]);
        // TODO: Use received date
        data.instant = Instant.now();

        WriteApiBlocking writeApi = db.getWriteApiBlocking();
        writeApi.writeMeasurement(bucket, org, WritePrecision.MS, data);
    }

    public void printFluxRecords() {
        String query = "from(bucket: \"" + bucket + "\") |> range(start: 0)";
        List<FluxTable> tables = db.getQueryApi().query(query, org);

        for (FluxTable fluxTable : tables) {
            System.out.println("Number of records: " + fluxTable.getRecords().size());
            for (FluxRecord record : fluxTable.getRecords()) {
                System.out.println(record.getTime() + ", value: " + record.getValue() + ", field: " + record.getField() + ", measurement: " + record.getMeasurement());
            }
        }
    }

    public void closeInfluxClient() {
        db.close();
    }


}
