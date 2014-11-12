package org.protege.owl.codegeneration.maven;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.protege.owl.codegeneration.CodeGenerationOptions;
import org.protege.owl.codegeneration.DefaultWorker;
import org.protege.owl.codegeneration.inference.CodeGenerationInference;
import org.protege.owl.codegeneration.inference.ReasonerBasedInference;
import org.protege.owl.codegeneration.names.IriNames;
import org.semanticweb.HermiT.Reasoner.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;


/**
 * Rob Challen 2014-10-16
 * Goal which executes Protege codegen on an owl ontology to generate
 * java sources as part of the build process
 */
@Mojo( name = "owl-codegen", defaultPhase = LifecyclePhase.GENERATE_SOURCES )
public class ProtegeCodegenMojo
extends AbstractMojo
{
	/**
	 * The input file.
	 */
	@Parameter(required=true)
	private File inputOntologyFile;

	/**
	 * The output directory.
	 */
	@Parameter(required=true)
	private File outputDirectory;
	
	/**
	 * The output package name.
	 */
	@Parameter(required=true)
	private String packageName;

	/**
	 * The classname of a reasoner factory (must be declared as a dependency to the plugin) 
	 * 
	 */
	@Parameter(required=false)
	private String reasonerFactoryName;

	/**
	 * The classname of a reasoner factory (must be declared as a dependency to the plugin) 
	 * 
	 */
	@Parameter(required=false)
	private String factoryName;

	public void execute() throws MojoExecutionException, MojoFailureException {

		File outputBaseDirectory = outputDirectory;

		File outputFile = new File(outputBaseDirectory,".last");
		long outputModified = outputFile.lastModified();
		boolean stale = false;

		if (!inputOntologyFile.isFile())
			throw new MojoExecutionException("Input file " + inputOntologyFile.getAbsolutePath() + " does not exist as a file");
		if (inputOntologyFile.lastModified() > outputModified)
			stale = true;

		if (!stale) {
			getLog().info("Output is current, skipping OWL code generation invocation");
			return;
		}

		getLog().info("Executing OWL code gen");

		try {

			recursiveDelete(outputBaseDirectory);
			outputBaseDirectory.mkdir();

			generateSimpleJavaCode(
					inputOntologyFile,
					packageName,
					factoryName != null ? factoryName : "factory.Factory" ,
							reasonerFactoryName,
							outputBaseDirectory
					);
			getLog().info("OWL code generation complete");

			if (!outputFile.exists()) {
		            new FileOutputStream(outputFile).close();
			}

			outputFile.setLastModified(System.currentTimeMillis());


		} catch (Exception e) {
			e.printStackTrace();
			throw new MojoFailureException("Protege codegen error");
		}

	}

	private void recursiveDelete(File f) {
		try {

			for (File g :f.listFiles()) {
				if (g.isDirectory()) recursiveDelete(g);
				g.delete();
			}
			f.delete();
		} catch (NullPointerException e) {
			// there wasn;t anything to delete
			f.delete();
		}
	}

	private static void generateSimpleJavaCode(File ontologyLocation, 
			String packageName,
			String factoryName,
			String reasonerFactoryName, 
			File outputFolder) throws OWLOntologyCreationException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology owlOntology = manager.loadOntologyFromOntologyDocument(ontologyLocation);

		CodeGenerationOptions options = new CodeGenerationOptions();
		options.setPackage(packageName);
		options.setFactoryClassName(factoryName);
		options.setOutputFolder(outputFolder);

		CodeGenerationInference inference;
		if (reasonerFactoryName != null) {
			OWLReasonerFactory rFactory = (OWLReasonerFactory) Class.forName(reasonerFactoryName).newInstance();
			OWLReasoner reasoner = rFactory.createNonBufferingReasoner(owlOntology);
			inference = new ReasonerBasedInference(owlOntology, reasoner);
		} else {
			OWLReasonerFactory rFactory = new ReasonerFactory();
			OWLReasoner reasoner = rFactory.createReasoner(owlOntology);
			inference = new ReasonerBasedInference(owlOntology, reasoner);
		}
		// inference.preCompute();
		DefaultWorker.generateCode(owlOntology, options, new IriNames(owlOntology, options), inference);

	}
}
