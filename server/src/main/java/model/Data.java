package model;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;

import java.time.Instant;

@Measurement(name = "data")
public class Data {

    @Column(tag = true)
    public int id;

    public String value;

    @Column(name = "latitude")
    public Double latitude;

    @Column(name = "longitude")
    public Double longitude;

    @Column(timestamp = true)
    public Instant instant;
}
