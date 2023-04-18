package constants;

import io.reactivex.rxjava3.annotations.NonNull;

public enum Constants {
    ONTOLOGYFILEPATH("../twin/building.owl"),
    ONTOLOGY("http://www.semanticweb.org/oscarlr/ontologies/2023/2/building#"),
    MOVABLEENTITY("MovableEntity"),
    MOVABLEENTITYID("movableEntityId"),
    TOKEN("PH5eHWPsLQ0o008nzOxT_nO8Nahi_oj6IoEdKIKqiIB44QX57UYDrjyU8gUI3SFB87vYX7xA6y8FrOuEhJUMwA=="),
    BUCKET("Data"),
    ORG("Dev"),
    URL("https://europe-west1-1.gcp.cloud2.influxdata.com"),
    FIREBASEDATABASEURL("https://masterthesis-77b98-default-rtdb.europe-west1.firebasedatabase.app"),
    SERVICEACCOUNTFILE("saf.json");

    private final String text;

    Constants(String text) {
        this.text = text;
    }

    @NonNull
    @Override
    public String toString() {
        return text;
    }
}
