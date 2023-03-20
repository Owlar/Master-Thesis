package owl;

import model.Data;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.io.OWLOntologyCreationIOException;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLEntityRemover;

import java.io.File;
import java.util.Map;

import static java.util.Collections.singleton;

public class Owl {

    private static OWLOntology ontology = null;
    private static OWLOntologyManager manager = null;

    public static void addIndividuals(Map<Integer, Data> dataList) throws OWLOntologyCreationIOException {
        manager = OWLManager.createOWLOntologyManager();
        File file = new File(OwlEnum.FILEPATH.toString());
        IRI ontologyIRI = IRI.create(OwlEnum.ONTOLOGY.toString());
        try {
            ontology = manager.loadOntologyFromOntologyDocument(file);

            IRI docIRI = manager.getOntologyDocumentIRI(ontology);

            OWLDataFactory factory = manager.getOWLDataFactory();
            OWLClass movableEntity = factory.getOWLClass(ontologyIRI + OwlEnum.MOVABLEENTITY.toString());

            reset();
            for (Data data : dataList.values()) {
                OWLIndividual smartphone = factory.getOWLNamedIndividual(ontologyIRI + "smartphone" + data.id);

                OWLDataProperty idProperty = factory.getOWLDataProperty(ontologyIRI + OwlEnum.MOVABLEENTITYID.toString());
                OWLDataPropertyAssertionAxiom idAssertion = factory.getOWLDataPropertyAssertionAxiom(idProperty, smartphone, data.id);
                manager.addAxiom(ontology, idAssertion);

                OWLDataProperty latitudeProperty = factory.getOWLDataProperty(ontologyIRI + OwlEnum.LATITUDE.toString());
                OWLDataPropertyAssertionAxiom latitudeAssertion = factory.getOWLDataPropertyAssertionAxiom(latitudeProperty, smartphone, data.latitude.toString());
                manager.addAxiom(ontology, latitudeAssertion);

                OWLDataProperty longitudeProperty = factory.getOWLDataProperty(ontologyIRI + OwlEnum.LONGITUDE.toString());
                OWLDataPropertyAssertionAxiom longitudeAssertion = factory.getOWLDataPropertyAssertionAxiom(longitudeProperty, smartphone, data.longitude.toString());
                manager.addAxiom(ontology, longitudeAssertion);

                OWLClassAssertionAxiom assertion = factory.getOWLClassAssertionAxiom(movableEntity, smartphone);
                manager.addAxiom(ontology, assertion);
            }

            manager.saveOntology(ontology, new TurtleDocumentFormat(), docIRI);
        } catch (OWLOntologyCreationException | OWLOntologyStorageException e) {
            e.printStackTrace();
        }

    }

    // Removes individuals from ontology
    public static void reset() {
        OWLEntityRemover remover = new OWLEntityRemover(singleton(ontology));
        for (OWLNamedIndividual individual : ontology.getIndividualsInSignature())
            individual.accept(remover);
        manager.applyChanges(remover.getChanges());
    }
}
