package model;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;

import java.time.Instant;

@Measurement(name = "data")
public class Data {

    @Column(tag = true)
    public String id;

    @Column(name = "latitude")
    public Double latitude;

    @Column(name = "longitude")
    public Double longitude;

    // It's used even though it says it isn't
    @Column(timestamp = true)
    public Instant instant = Instant.now();
}
