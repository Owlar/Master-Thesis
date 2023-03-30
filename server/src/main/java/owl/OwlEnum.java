package owl;

import io.reactivex.rxjava3.annotations.NonNull;

public enum OwlEnum {
    FILEPATH("../twin/building.owl"),
    ONTOLOGY("http://www.semanticweb.org/oscarlr/ontologies/2023/2/building#"),
    MOVABLEENTITY("MovableEntity"),
    MOVABLEENTITYID("movableEntityId"),
    AREA("Area"),
    LATITUDE("latitude"),
    LONGITUDE("longitude");

    private final String text;

    OwlEnum(String text) {
        this.text = text;
    }

    @NonNull
    @Override
    public String toString() {
        return text;
    }
}
