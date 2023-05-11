package jena;

import org.apache.jena.rdf.model.*;

import java.util.HashMap;
import java.util.Map;

public class Jena {

    public static Map<String, String> getEndangeredSmartphones() {
        Map<String, String> res = new HashMap<>();

        Model model = ModelFactory.createDefaultModel();
        model.read("../twin/output.ttl");

        Property property = ResourceFactory.createProperty("https://github.com/Edkamb/SemanticObjects/Program#MovableEntity_endangered");
        Property id = ResourceFactory.createProperty("https://github.com/Edkamb/SemanticObjects/Program#MovableEntity_movableEntityId");
        ResIterator iterator = model.listSubjectsWithProperty(property);
        while (iterator.hasNext()) {
            Resource current = iterator.nextResource();
            boolean isEndangered = Boolean.parseBoolean(current.getProperty(property).getString());
            if (isEndangered) {
                // To make it easier to retrieve from Firebase in client
                String currentPropertyId = current.getProperty(id).getString();
                res.put(currentPropertyId, currentPropertyId);
            }
        }
        return res;
    }
}
