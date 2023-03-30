package owl;

import model.CriticalArea;
import model.Data;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.io.OWLOntologyCreationIOException;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.search.EntitySearcher;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class Owl {

    public static void addIndividuals(Map<Integer, Data> dataList) throws OWLOntologyCreationIOException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        File file = new File(OwlEnum.FILEPATH.toString());
        IRI ontologyIRI = IRI.create(OwlEnum.ONTOLOGY.toString());
        try {
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);

            IRI docIRI = manager.getOntologyDocumentIRI(ontology);

            OWLDataFactory factory = manager.getOWLDataFactory();
            OWLClass movableEntity = factory.getOWLClass(ontologyIRI + OwlEnum.MOVABLEENTITY.toString());

            for (Data data : dataList.values()) {
                OWLIndividual smartphone = factory.getOWLNamedIndividual(ontologyIRI + "smartphone" + data.id);

                OWLDataProperty idProperty = factory.getOWLDataProperty(ontologyIRI + OwlEnum.MOVABLEENTITYID.toString());
                OWLDataPropertyAssertionAxiom idAssertion = factory.getOWLDataPropertyAssertionAxiom(idProperty, smartphone, data.id);
                manager.addAxiom(ontology, idAssertion);

                OWLClassAssertionAxiom assertion = factory.getOWLClassAssertionAxiom(movableEntity, smartphone);
                manager.addAxiom(ontology, assertion);
            }

            manager.saveOntology(ontology, new TurtleDocumentFormat(), docIRI);
        } catch (OWLOntologyCreationException | OWLOntologyStorageException e) {
            e.printStackTrace();
        }
    }

    public static void insertCriticalAreas() {
        ArrayList<CriticalArea> res = getCriticalAreasFromAssetModel();
        for (CriticalArea criticalArea : res)
            System.out.println(criticalArea.areaId + ": " + criticalArea.isCriticalArea);

    }

    private static ArrayList<CriticalArea> getCriticalAreasFromAssetModel() {
        ArrayList<CriticalArea> res = new ArrayList<>();

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        File file = new File(OwlEnum.FILEPATH.toString());
        IRI ontologyIRI = IRI.create(OwlEnum.ONTOLOGY.toString());
        try {
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
            OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
            OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(ontology);

            OWLDataFactory factory = manager.getOWLDataFactory();
            OWLClass owlClass = factory.getOWLClass(ontologyIRI + OwlEnum.AREA.toString());

            NodeSet<OWLNamedIndividual> instances = reasoner.getInstances(owlClass, false);
            for (OWLNamedIndividual individual : instances.getFlattened()) {
                OWLDataPropertyExpression areaId = factory.getOWLDataProperty(ontologyIRI + "areaId");
                OWLDataPropertyExpression isCriticalArea = factory.getOWLDataProperty(ontologyIRI + "isCriticalArea");
                OWLDataPropertyExpression latitude1 = factory.getOWLDataProperty(ontologyIRI + "latitude1");
                OWLDataPropertyExpression latitude2 = factory.getOWLDataProperty(ontologyIRI + "latitude2");
                OWLDataPropertyExpression longitude1 = factory.getOWLDataProperty(ontologyIRI + "longitude1");
                OWLDataPropertyExpression longitude2 = factory.getOWLDataProperty(ontologyIRI + "longitude2");

                CriticalArea criticalArea = new CriticalArea();
                criticalArea.areaId = Integer.parseInt(EntitySearcher.getDataPropertyValues(individual, areaId, ontology).iterator().next().getLiteral());
                criticalArea.isCriticalArea = Boolean.parseBoolean(EntitySearcher.getDataPropertyValues(individual, isCriticalArea, ontology).iterator().next().getLiteral());
                criticalArea.latitude1 = Double.parseDouble(EntitySearcher.getDataPropertyValues(individual, latitude1, ontology).iterator().next().getLiteral());
                criticalArea.latitude2 = Double.parseDouble(EntitySearcher.getDataPropertyValues(individual, latitude2, ontology).iterator().next().getLiteral());
                criticalArea.longitude1 = Double.parseDouble(EntitySearcher.getDataPropertyValues(individual, longitude1, ontology).iterator().next().getLiteral());
                criticalArea.longitude2 = Double.parseDouble(EntitySearcher.getDataPropertyValues(individual, longitude2, ontology).iterator().next().getLiteral());

                res.add(criticalArea);
            }


        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }

        return res;
    }

    public static ArrayList<Integer> getEndangeredSmartphonesInKnowledgeGraph() {
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
                    System.out.println(current.getProperty(id).getInt() + ": " + current + " - " + isEndangered);
                    res.add(current.getProperty(id).getInt());
                }
            }
        }
        return res;
    }

}
