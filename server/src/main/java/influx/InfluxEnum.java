package influx;

import io.reactivex.rxjava3.annotations.NonNull;

public enum InfluxEnum {
    TOKEN("PH5eHWPsLQ0o008nzOxT_nO8Nahi_oj6IoEdKIKqiIB44QX57UYDrjyU8gUI3SFB87vYX7xA6y8FrOuEhJUMwA=="),
    BUCKET("Data"),
    ORG("Dev"),
    URL("https://europe-west1-1.gcp.cloud2.influxdata.com");

    private final String text;

    InfluxEnum(String text) {
        this.text = text;
    }

    @NonNull
    @Override
    public String toString() {
        return text;
    }
}
