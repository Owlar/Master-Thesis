package owl;

import constants.Constants;
import model.Data;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.io.OWLOntologyCreationIOException;
import org.semanticweb.owlapi.model.*;

import java.io.File;
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

}
