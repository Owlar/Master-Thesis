package owl;

import model.Data;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.io.OWLOntologyCreationIOException;
import org.semanticweb.owlapi.model.*;

import java.io.File;

public class Owl {

    public static void addIndividual(Data data) throws OWLOntologyCreationIOException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        File file = new File("../twin/building.owl");
        IRI ontologyIRI = IRI.create("http://www.semanticweb.org/oscarlr/ontologies/2023/2/building#");
        try {
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
            IRI docIRI = manager.getOntologyDocumentIRI(ontology);

            OWLDataFactory factory = manager.getOWLDataFactory();
            OWLClass movableEntity = factory.getOWLClass(ontologyIRI + "MovableEntity");

            OWLIndividual smartphone = factory.getOWLNamedIndividual(ontologyIRI + "smartphone" + data.id);

            OWLDataProperty idProperty = factory.getOWLDataProperty(ontologyIRI + "movableEntityId");
            OWLDataPropertyAssertionAxiom idAssertion = factory.getOWLDataPropertyAssertionAxiom(idProperty, smartphone, data.id);
            manager.addAxiom(ontology, idAssertion);

            OWLDataProperty latitudeProperty = factory.getOWLDataProperty(ontologyIRI + "latitude");
            OWLDataPropertyAssertionAxiom latitudeAssertion = factory.getOWLDataPropertyAssertionAxiom(latitudeProperty, smartphone, data.latitude);
            manager.addAxiom(ontology, latitudeAssertion);

            OWLDataProperty longitudeProperty = factory.getOWLDataProperty(ontologyIRI + "longitude");
            OWLDataPropertyAssertionAxiom longitudeAssertion = factory.getOWLDataPropertyAssertionAxiom(longitudeProperty, smartphone, data.longitude);
            manager.addAxiom(ontology, longitudeAssertion);

            OWLClassAssertionAxiom assertion = factory.getOWLClassAssertionAxiom(movableEntity, smartphone);
            manager.addAxiom(ontology, assertion);

            manager.saveOntology(ontology, new TurtleDocumentFormat(), docIRI);
        } catch (OWLOntologyCreationException | OWLOntologyStorageException e) {
            e.printStackTrace();
        }

    }
}
