maven-code-gen-plugin
=====================

An OWL to Java code generation plugin for maven.


Declare the runtime dependency in your main project 

	<dependency>
			<groupId>edu.stanford.protege</groupId>
			<artifactId>org.protege.editor.owl.codegeneration</artifactId>
			<version>1.0.3-SNAPSHOT</version>
	</dependency>
	
Configure the compile time plugin to work as a maven goal 

	<plugins>
	...
	<plugin>
		<groupId>edu.stanford.protege</groupId>
		<artifactId>org.protege.editor.owl.codegeneration</artifactId>
		<version>1.0.3-SNAPSHOT</version>
		<executions>
			<execution>
				<id>owl-codegen</id>
				<goals>
					<goal>owl-codegen</goal>
				</goals>
				<configuration>
					<inputOntologyFile>${basedir}/src/main/resources/CatalogueScheme.owl</inputOntologyFile>
					<packageName>com.example.owl</packageName>
					<factoryName>factory.CatalogueFactory</factoryName> 
						<!-- N.B. this will end up in the sub package as com.example.owl.factory.CatalgoueFactory-->
					<outputDirectory>${basedir}/target/generated-sources/protege</outputDirectory>
				</configuration>
			</execution>
		</executions>
	</plugin>
	...
	<!-- add in your generated sources directory -->
	<plugin>
		<groupId>org.codehaus.mojo</groupId>
		<artifactId>build-helper-maven-plugin</artifactId>
		<executions>
			<execution>
				<id>add-source</id>
				<goals>
					<goal>add-source</goal>
				</goals>
				<configuration>
					<sources>
						<source>${basedir}/target/generated-sources/protege</source>
					</sources>
				</configuration>
			</execution> 
		</executions>
	</plugin>
	...
	</plugins>
	
run a mvn generate-sources and your generated java code should magically appear in a new source folder
Eclipse may need to be F5  refreshed to make the new code visible.
