package assignments.simpsons;

import java.io.FileWriter;

import org.apache.commons.io.FilenameUtils;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RiotNotFoundException;

public class Simpsons {

	public static void main(String[] args) {
		try {
			// Get file paths from args
			String inputFilePath = args[0];
			String outputFile = args[1];

			// Get file extensions
			String inputFileExtension = FilenameUtils.getExtension(inputFilePath);
			String outputFileExtension = FilenameUtils.getExtension(outputFile);

			// Get rdf languages from file extensions
			Lang inputFileRdfLang = RDFLanguages.fileExtToLang(inputFileExtension);
			Lang outputFileRdfLang = RDFLanguages.fileExtToLang(outputFileExtension);

			// Create model and read from input file
			Model model = ModelFactory.createDefaultModel();
			RDFDataMgr.read(model, inputFilePath, inputFileRdfLang);

			// Get prefixes URIs
			String simPrefix = model.getNsPrefixURI("sim");
			String rdfPrefix = model.getNsPrefixURI("rdf");
			String foafPrefix = model.getNsPrefixURI("foaf");
			String famPrefix = model.getNsPrefixURI("fam");

			// Create properties
			Property typeProp = model.createProperty(rdfPrefix + "type");
			Property nameProp = model.createProperty(foafPrefix + "name");
			Property ageProp = model.createProperty(foafPrefix + "age");
			Property hasSpouseProp = model.createProperty(famPrefix + "hasSpouse");
			Property hasParent = model.createProperty(famPrefix + "hasParent");
			Property hasFather = model.createProperty(famPrefix + "hasFather");
			
			// Create resource
			Resource personRes = model.createResource(foafPrefix + "Person");

			// Create datatype 
			RDFDatatype typeInt = XSDDatatype.XSDint;

			// Add Maggie as a person with name Maggie Simpson and age 1
			Resource Maggie = model.createResource(simPrefix + "Maggie");
			Maggie.addProperty(typeProp, personRes);
			Literal maggiesName = model.createLiteral("Maggie Simpson");
			Literal maggiesAge = model.createTypedLiteral("1", typeInt);
			Maggie.addLiteral(nameProp, maggiesName);
			Maggie.addLiteral(ageProp, maggiesAge);

			// Add Mona as a person with name Mona Simpson and age 70
			Resource Mona = model.createResource(simPrefix + "Mona");
			Mona.addProperty(typeProp, personRes);
			Literal monasName = model.createLiteral("Mona Simpson");
			Literal monasAge = model.createTypedLiteral("70", typeInt);
			Mona.addLiteral(nameProp, monasName);
			Mona.addLiteral(ageProp, monasAge);

			// Add Abraham as a person with name Abraham Simpson and age 78
			Resource Abraham = model.createResource(simPrefix + "Abraham");
			Abraham.addProperty(typeProp, personRes);
			Literal abrahamsName = model.createLiteral("Abraham Simpson");
			Literal abrahamsAge = model.createTypedLiteral("78", typeInt);
			Abraham.addLiteral(nameProp, abrahamsName);
			Abraham.addLiteral(ageProp, abrahamsAge);

			// Add Abraham as the spouse of Mona, and Mona as the spouse of Abraham
			Abraham.addProperty(hasSpouseProp, Mona);
			Mona.addProperty(hasSpouseProp, Abraham);

			// Add Herb as a person and with a unknown father
			Resource Herb = model.createResource(simPrefix + "Herb");
			Resource herbsFather = model.createResource();
			Herb.addProperty(typeProp, personRes);
			Herb.addProperty(hasParent, herbsFather);
			Herb.addProperty(hasFather, herbsFather);

			// Add age category to all resources type Person
			ResIterator peopleIterator = model.listSubjectsWithProperty(typeProp, personRes);
			Resource infantRes = model.createResource(famPrefix + "Infant");
			Resource minorRes = model.createResource(famPrefix + "Minor");
			Resource oldRes = model.createResource(famPrefix + "Old");
			while (peopleIterator.hasNext()) {
				Resource person = peopleIterator.next();
				Statement ageStatement = person.getProperty(ageProp);
				if(ageStatement != null) {	
					int age = ageStatement.getInt();
					if (age < 2) {
						person.addProperty(typeProp, infantRes);
						person.addProperty(typeProp, minorRes);
						continue;
					}
					if (age < 18) {
						person.addProperty(typeProp, minorRes);
						continue;
					}
					if (age > 70) {
						person.addProperty(typeProp, oldRes);
					}
				}
			}
			
			// Write model to output file
			FileWriter out = new FileWriter(outputFile);
			try {
			    model.write(out, outputFileRdfLang.getName());
			} catch(Exception e) {
				System.out.println(e.getMessage());
			}
			finally {
			    out.close();
			}

		} catch (IndexOutOfBoundsException e) {
			System.out.println("You need to add the RDF file path in the run args");
			System.out.println(e.getMessage());
		} catch (RiotNotFoundException e) {
			System.out.println("File not found");
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
