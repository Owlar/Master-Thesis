package model;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;

import java.time.Instant;

@Measurement(name = "status")
public class Status {

    @Column(tag = true)
    public int id;

    @Column
    public String value;

    @Column(timestamp = true)
    public Instant instant;
}
