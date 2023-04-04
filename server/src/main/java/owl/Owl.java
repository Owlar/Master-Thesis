package owl;

import constants.Constants;
import model.Area;
import model.Data;
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
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;

public class Owl {

    public static void addIndividuals(Map<Integer, Data> dataList) throws OWLOntologyCreationIOException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        File file = new File(Constants.ONTOLOGYFILEPATH.toString());
        IRI ontologyIRI = IRI.create(Constants.ONTOLOGY.toString());
        try {
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);

            IRI docIRI = manager.getOntologyDocumentIRI(ontology);

            OWLDataFactory factory = manager.getOWLDataFactory();
            OWLClass movableEntity = factory.getOWLClass(ontologyIRI + Constants.MOVABLEENTITY.toString());

            for (Data data : dataList.values()) {
                OWLIndividual smartphone = factory.getOWLNamedIndividual(ontologyIRI + "smartphone" + data.id);

                OWLDataProperty idProperty = factory.getOWLDataProperty(ontologyIRI + Constants.MOVABLEENTITYID.toString());
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

    public static ArrayList<Area> getAreasFromAssetModel() {
        ArrayList<Area> res = new ArrayList<>();

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        File file = new File(Constants.ONTOLOGYFILEPATH.toString());
        IRI ontologyIRI = IRI.create(Constants.ONTOLOGY.toString());
        try {
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
            OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
            OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(ontology);

            OWLDataFactory factory = manager.getOWLDataFactory();
            OWLClass owlClass = factory.getOWLClass(ontologyIRI + Constants.AREA.toString());

            NodeSet<OWLNamedIndividual> instances = reasoner.getInstances(owlClass, false);
            for (OWLNamedIndividual individual : instances.getFlattened()) {
                OWLDataPropertyExpression areaId = factory.getOWLDataProperty(ontologyIRI + Constants.AREAID.toString());
                OWLDataPropertyExpression name = factory.getOWLDataProperty(ontologyIRI + Constants.NAME.toString());
                OWLDataPropertyExpression isCriticalArea = factory.getOWLDataProperty(ontologyIRI + Constants.ISCRITICALAREA.toString());
                OWLDataPropertyExpression latitude1 = factory.getOWLDataProperty(ontologyIRI + Constants.LATITUDE.toString() + 1);
                OWLDataPropertyExpression latitude2 = factory.getOWLDataProperty(ontologyIRI + Constants.LATITUDE.toString() + 2);
                OWLDataPropertyExpression longitude1 = factory.getOWLDataProperty(ontologyIRI + Constants.LONGITUDE.toString() + 1);
                OWLDataPropertyExpression longitude2 = factory.getOWLDataProperty(ontologyIRI + Constants.LONGITUDE.toString() + 2);

                Area area = new Area();
                area.areaId = EntitySearcher.getDataPropertyValues(individual, areaId, ontology).iterator().next().getLiteral();
                area.name = EntitySearcher.getDataPropertyValues(individual, name, ontology).iterator().next().getLiteral();
                area.isCriticalArea = Boolean.parseBoolean(EntitySearcher.getDataPropertyValues(individual, isCriticalArea, ontology).iterator().next().getLiteral());
                area.latitude1 = Double.parseDouble(EntitySearcher.getDataPropertyValues(individual, latitude1, ontology).iterator().next().getLiteral());
                area.latitude2 = Double.parseDouble(EntitySearcher.getDataPropertyValues(individual, latitude2, ontology).iterator().next().getLiteral());
                area.longitude1 = Double.parseDouble(EntitySearcher.getDataPropertyValues(individual, longitude1, ontology).iterator().next().getLiteral());
                area.longitude2 = Double.parseDouble(EntitySearcher.getDataPropertyValues(individual, longitude2, ontology).iterator().next().getLiteral());
                area.instant = Instant.now();

                res.add(area);
            }


        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }

        return res;
    }

}
