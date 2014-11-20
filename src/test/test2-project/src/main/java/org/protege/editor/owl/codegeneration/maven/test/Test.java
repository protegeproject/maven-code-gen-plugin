package org.protege.editor.owl.codegeneration.maven.test;


import org.protege.editor.owl.codegeneration.maven.test2.A;
import org.protege.editor.owl.codegeneration.maven.test2.factory.test.CompoundFactory;
import org.semanticweb.owlapi.model.OWLOntology;

public class Test {
	OWLOntology ontology;
	
	public void setOntology(OWLOntology ontology) {
		this.ontology = ontology;
	}
	
	
	public A foo() {
		CompoundFactory factory = new CompoundFactory(ontology);
		return factory.createA("hello");
	}
	

}
