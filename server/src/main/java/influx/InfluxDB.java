package influx;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;

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

    public void insertDataPoint(Object object) {
        WriteApiBlocking writeApi = db.getWriteApiBlocking();
        writeApi.writeMeasurement(bucket, org, WritePrecision.MS, object);
    }


}
