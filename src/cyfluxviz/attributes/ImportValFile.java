package cyfluxviz.attributes;
import java.io.*;
import java.util.*;

/* Val file importer (simple key/value pairs for reactions). */
public class FluxDistributionImporter {
	
	private String networkId;
	private String name;
	private HashMap<String, Double> nodeFluxes = new HashMap<String, Double>();
	private HashMap<String, Double> edgeFluxes = new HashMap<String, Double>();

	public FluxDistributionImporter(String filename){
		importFromFile(filename);
	}
	
	public HashMap<String, Double> getHm() {
		return nodeFluxes;
	}

	public String getName() {
		return name;
	}

	public void importFromFile(String filename){
		name = filename;
		//TODO: set network id
		
		String line;
	    try {
	      BufferedReader br = new BufferedReader(new FileReader(filename));
	      // dis.available() returns 0 if the file does not have more lines.
	      while ( (line = br.readLine()) != null){
	    	  this.parseLine(line);
	      } 
	      br.close();
	    } catch (FileNotFoundException e) {
	    	System.err.println("Error: " + e);
	    	e.printStackTrace();
	    } catch (IOException e) {
	    	System.err.println("Error: " + e);
	    	e.printStackTrace();
	    }
	}
	
	/* Parses the (id, value) pair from given line. */
	public void parseLine(String line){
	  String id;
	  Double value = 0.0;
	  line = line.trim();	// remove newline
	  if (line.length() != 0) {
	  	  String[] idvalue = line.split("\t");
	  	  id = idvalue[0];
	  	  try {
	  		  value = Double.valueOf(idvalue[1]).doubleValue();
	  	  }
	  	  catch (NumberFormatException e)
	  	  {
	  		 e.printStackTrace();
	  	  }
	  	  nodeFluxes.put(id, value);
	  } 
	}
}
