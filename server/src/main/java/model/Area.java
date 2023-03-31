package model;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;

import java.time.Instant;

@Measurement(name = "area")
public class Area {

    @Column(tag = true)
    public String areaId;

    @Column(name = "name")
    public String name;

    @Column(tag = true)
    public Boolean isCriticalArea;

    @Column(name = "latitude1")
    public double latitude1;

    @Column(name = "latitude2")
    public double latitude2;

    @Column(name = "longitude1")
    public double longitude1;

    @Column(name = "longitude2")
    public double longitude2;

    @Column(timestamp = true)
    public Instant instant;
}
