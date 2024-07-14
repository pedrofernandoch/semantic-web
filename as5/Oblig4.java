import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

class Oblig4{

    /*
    * args[0] = RDFS Model (family.ttl)
    * args[1] = SPARQL query (Oblig4.rq)
    * args[2] = Result file to write results from SPARQL query (results.ttl)
    */    
    public static void main (String[] args){
        try{
            Model model = ModelFactory.createDefaultModel();
            Model urlModel = ModelFactory.createDefaultModel();

            System.out.println("Reading file.....\n");
            readfilesAndCombine(model, urlModel, args[0], args[1], args[2]);
            System.out.println("Making model.....\n");
            System.out.println("Creating output.....\n");
            System.out.println("Finished!\n");   
        
        } catch (Exception e) {
            System.out.println("ERROR, try again!\n");
            System.exit(0);
        }
    }

    private static void readfilesAndCombine(Model model, Model model2, String filename, String queryfile, String combinedFile) throws IOException{
        
        //Read file from ulr and save locally
        URL website = new URL("https://www.uio.no/studier/emner/matnat/ifi/IN3060/v21/obliger/simpsons.ttl");
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream("simpsons.ttl");
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        
        //Read models
        model.read(filename);
        model2.read("simpsons.ttl");
        fos.close();

        //Combine models into one
        Model combinationModel = ModelFactory.createRDFSModel(model, model2);

        //Read query
        Query query = QueryFactory.read(queryfile);

        //Execute query and save results in new model
        QueryExecution execution = QueryExecutionFactory.create(query, combinationModel);
        Model newModel = execution.execConstruct();
        
        //Add the new information to the combined model
        combinationModel.add(newModel);
        execution.close();

        makeOutput(combinationModel, combinedFile);
    }

    private static void makeOutput(Model model, String filename){
        try{
            FileOutputStream w = new FileOutputStream(filename);
            String placeholder = filename.toString();
            int index = placeholder.lastIndexOf('.');
            String extension = filename.substring(index + 1);
            model.write(w, extension);
            
        } catch (Exception e){
            System.out.println("Error! No file created\n");
            e.printStackTrace();
        }
    }
    
}