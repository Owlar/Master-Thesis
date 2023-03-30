package model;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;

@Measurement(name = "criticalArea")
public class CriticalArea {

    @Column(tag = true)
    public int areaId;

    @Column(name = "isCriticalArea")
    public Boolean isCriticalArea;

    // To make it easy to access in SMOL from InfluDB
    @Column(name = "latitude1")
    public double latitude1;

    @Column(name = "latitude2")
    public double latitude2;

    @Column(name = "longitude1")
    public double longitude1;

    @Column(name = "longitude2")
    public double longitude2;
}
