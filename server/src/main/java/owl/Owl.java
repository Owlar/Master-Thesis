package owl;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.io.OWLOntologyCreationIOException;
import org.semanticweb.owlapi.model.*;

import java.io.File;

public class Owl {

    public static void addIndividual(int id) throws OWLOntologyCreationIOException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        File file = new File("../twin/building.owl");
        IRI ontologyIRI = IRI.create("http://www.semanticweb.org/oscarlr/ontologies/2023/2/building#");
        try {
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
            IRI docIRI = manager.getOntologyDocumentIRI(ontology);

            OWLDataFactory factory = manager.getOWLDataFactory();
            OWLClass movableEntity = manager.getOWLDataFactory().getOWLClass(ontologyIRI + "MovableEntity");

            OWLIndividual smartphone1 = factory.getOWLNamedIndividual(ontologyIRI + "smartphone" + id);
            OWLIndividual smartphone2 = factory.getOWLNamedIndividual(ontologyIRI + "smartphone" + (id + 1));

            OWLClassAssertionAxiom assertion1 = factory.getOWLClassAssertionAxiom(movableEntity, smartphone1);
            OWLClassAssertionAxiom assertion2 = factory.getOWLClassAssertionAxiom(movableEntity, smartphone2);

            manager.addAxiom(ontology, assertion1);
            manager.addAxiom(ontology, assertion2);

            manager.saveOntology(ontology, new TurtleDocumentFormat(), docIRI);
        } catch (OWLOntologyCreationException | OWLOntologyStorageException e) {
            e.printStackTrace();
        }

    }
}
