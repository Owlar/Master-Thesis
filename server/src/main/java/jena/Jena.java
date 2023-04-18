package jena;

import model.Data;
import org.apache.jena.rdf.model.*;

import java.util.HashMap;
import java.util.Map;

public class Jena {

    public static Map<String, Object> getEndangeredSmartphones() {
        Map<String, Object> res = new HashMap<>();

        Model model = ModelFactory.createDefaultModel();
        model.read("../twin/output.ttl");

        Property property = ResourceFactory.createProperty("https://github.com/Edkamb/SemanticObjects/Program#MovableEntity_endangered");
        Property id = ResourceFactory.createProperty("https://github.com/Edkamb/SemanticObjects/Program#MovableEntity_movableEntityId");
        ResIterator iterator = model.listSubjectsWithProperty(property);
        if (iterator.hasNext()) {
            while (iterator.hasNext()) {
                Resource current = iterator.nextResource();
                boolean isEndangered = Boolean.parseBoolean(current.getProperty(property).getString());
                if (isEndangered)
                    res.put(current.getProperty(id).getString(), new Data());
            }
        }
        return res;
    }
}
