package org.protege.editor.owl.codegeneration.maven.test;

import org.protege.editor.owl.codegeneration.maven.test1.A;
import org.protege.editor.owl.codegeneration.maven.test1.AtomicFactory;
import org.semanticweb.owlapi.model.OWLOntology;

public class Test {
	OWLOntology ontology;
	
	public void setOntology(OWLOntology ontology) {
		this.ontology = ontology;
	}
	
	public A foo() {
		AtomicFactory factory = new AtomicFactory(ontology);
		return factory.createA("hello");
	}

}
