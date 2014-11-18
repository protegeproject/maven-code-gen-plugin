package org.protege.editor.owl.codegeneration.maven.test;

import org.semanticweb.owlapi.model.OWLOntology;

public class Test {
	OWLOntology ontology;
	
	public void setOntology(OWLOntology ontology) {
		this.ontology = ontology;
	}
	
	public A foo() {
		CatalogueFactory factory = new CatalogueFactory(ontology);
		return factory.createB("test");
	}
	
}
