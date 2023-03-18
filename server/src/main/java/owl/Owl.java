package owl;

import model.Data;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.io.OWLOntologyCreationIOException;
import org.semanticweb.owlapi.model.*;

import java.io.File;

public class Owl {

    // TODO: Remove MovableEntity individuals in asset model each server run to deal with ID ordering?
    public static void addIndividual(Data data) throws OWLOntologyCreationIOException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        File file = new File(OwlEnum.FILEPATH.toString());
        IRI ontologyIRI = IRI.create(OwlEnum.ONTOLOGY.toString());
        try {
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
            IRI docIRI = manager.getOntologyDocumentIRI(ontology);

            OWLDataFactory factory = manager.getOWLDataFactory();
            OWLClass movableEntity = factory.getOWLClass(ontologyIRI + OwlEnum.MOVABLEENTITY.toString());

            OWLIndividual smartphone = factory.getOWLNamedIndividual(ontologyIRI + "smartphone" + data.id);

            OWLDataProperty idProperty = factory.getOWLDataProperty(ontologyIRI + OwlEnum.MOVABLEENTITYID.toString());
            OWLDataPropertyAssertionAxiom idAssertion = factory.getOWLDataPropertyAssertionAxiom(idProperty, smartphone, data.id);
            manager.addAxiom(ontology, idAssertion);

            OWLDataProperty latitudeProperty = factory.getOWLDataProperty(ontologyIRI + OwlEnum.LATITUDE.toString());
            OWLDataPropertyAssertionAxiom latitudeAssertion = factory.getOWLDataPropertyAssertionAxiom(latitudeProperty, smartphone, data.latitude);
            manager.addAxiom(ontology, latitudeAssertion);

            OWLDataProperty longitudeProperty = factory.getOWLDataProperty(ontologyIRI + OwlEnum.LONGITUDE.toString());
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
