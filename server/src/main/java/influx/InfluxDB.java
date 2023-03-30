package influx;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import model.CriticalArea;
import model.Data;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.*;

import java.io.FileInputStream;
import java.util.ArrayList;

import static org.apache.commons.codec.CharEncoding.UTF_8;

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

    //TODO: Make Object instead of "Data" ?
    public void insertDataPoint(Data data) {
        WriteApiBlocking writeApi = db.getWriteApiBlocking();
        writeApi.writeMeasurement(bucket, org, WritePrecision.MS, data);
    }

    public void closeInfluxClient() {
        db.close();
    }


}
