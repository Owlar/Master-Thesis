package jena;

import org.apache.jena.rdf.model.*;

import java.util.ArrayList;

public class Jena {

    public static ArrayList<Integer> getEndangeredSmartphones() {
        ArrayList<Integer> res = new ArrayList<>();

        Model model = ModelFactory.createDefaultModel();
        model.read("../twin/output.ttl");

        Property endangered = ResourceFactory.createProperty("https://github.com/Edkamb/SemanticObjects/Program#MovableEntity_endangered");
        Property id = ResourceFactory.createProperty("https://github.com/Edkamb/SemanticObjects/Program#MovableEntity_movableEntityId");
        ResIterator iterator = model.listSubjectsWithProperty(endangered);
        if (iterator.hasNext()) {
            System.out.println("Knowledge graph contains endangered object(s):");
            while (iterator.hasNext()) {
                Resource current = iterator.nextResource();
                boolean isEndangered = Boolean.parseBoolean(current.getProperty(endangered).getString());
                if (isEndangered) {
                    System.out.println(current.getProperty(id).getInt() + ": " + current + " - " + true);
                    res.add(current.getProperty(id).getInt());
                }
            }
        }
        return res;
    }
}
