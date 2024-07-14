package assignments.simpsons;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RiotNotFoundException;

public class Query {

	public static void main(String[] args) {
		
		String queryType = null;
		final String SELECT = "SELECT";
		final String CONSTRUCT = "CONSTRUCT";
		final String ASK = "ASK";
		
		try {
			// Get file paths from args
			String inputFilePath = args[0];
			String queryFilePath = args[1];
			
	        String queryString = Files.readString(Paths.get(queryFilePath));
	        
	        // Get query type
	        if(StringUtils.containsIgnoreCase(queryString, SELECT)) {
	        	queryType = SELECT;
	        }else if(StringUtils.containsIgnoreCase(queryString, CONSTRUCT)) {
	        	queryType = CONSTRUCT;
	        }else if(StringUtils.containsIgnoreCase(queryString, ASK)) {
	        	queryType = ASK;
	        }else {
	        	throw new InvalidSparqlQuery("Invalid query, please use SELECT, CONSTRUCT OR ASK");
	        }

			// Get rdf file extensions
			String inputFileExtension = FilenameUtils.getExtension(inputFilePath);

			// Get rdf languages from file extensions
			Lang inputFileRdfLang = RDFLanguages.fileExtToLang(inputFileExtension);

			// Create model and read from input file
			Model model = ModelFactory.createDefaultModel();
			RDFDataMgr.read(model, inputFilePath, inputFileRdfLang);
			
			// Creating query read from file
			org.apache.jena.query.Query q = QueryFactory.create(queryString);
			QueryExecution qe =	QueryExecutionFactory.create(q, model);
			String queryResultStr = null;
			Model constructedModel = ModelFactory.createDefaultModel();
			
			switch(queryType){
				case SELECT:
					queryResultStr = ResultSetFormatter.asText(qe.execSelect());
					break;
				case CONSTRUCT:
					constructedModel = qe.execConstruct();
					queryResultStr = constructedModel.toString();
					break;
				case ASK:
					queryResultStr = String.valueOf(qe.execAsk());
					break;
			}
			
			System.out.println(queryResultStr);

			if(queryType.equalsIgnoreCase(CONSTRUCT)) {
				FileWriter output = new FileWriter("src/main/output.ttl");
				constructedModel.write(output, Lang.TTL.getName());
				output.close();
			}else {
				File output = new File("src/main/output6.txt");
				FileWriter writer = new FileWriter(output);
				writer.write(queryResultStr);
				writer.flush();
				writer.close();
			}
		
			qe.close();
			
		} catch (IndexOutOfBoundsException e) {
			System.out.println("You need to add the RDF file and query file path in the run args");
			System.out.println(e.getMessage());
		} catch (RiotNotFoundException e) {
			System.out.println("File not found");
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	public static class InvalidSparqlQuery extends Exception{

		private static final long serialVersionUID = 1L;

		public InvalidSparqlQuery(String errorMessage) {
	        super(errorMessage);
	    }
	}

}
