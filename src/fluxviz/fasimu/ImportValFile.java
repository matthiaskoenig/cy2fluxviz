package fluxviz.fasimu;
import java.io.*;
import java.util.*;

/**
 * Importer for the val files.
 * The key, value pairs of the flux are stored in a hashmap.
 */
public class ImportValFile {
	
	// Create a hashmap for the values of the form [ID] = flux_value
	private String name;
	private HashMap<String, Double> hm = new HashMap<String, Double>();
	
	public ImportValFile(String filename){
		name = filename;
		String line;
	    try {
	      BufferedReader br = new BufferedReader(new FileReader(filename));
	      // dis.available() returns 0 if the file does not have more lines.
	      while ( (line = br.readLine()) != null){
	    	  this.parseLine(line);
	      } 
	      // dispose all the resources after using them.
	      br.close();
	    } catch (FileNotFoundException e) {
	    	System.err.println("Error: " + e);
	    	e.printStackTrace();
	    } catch (IOException e) {
	    	System.err.println("Error: " + e);
	    	e.printStackTrace();
	    }
	    // TODO: catch corrupt val file exception
	}
	
	/*
	 * Parses the identifier / value pair from the float file
	 */
	public void parseLine(String line){
	  String id;
	  Double value = 0.0;
	  // remove newline
	  line = line.trim();
	  // if line content add the entry to the hashmap
	  // TODO: test if identifier already in the network
	  if (line.length() != 0) {
		  //System.out.println(line);
	  	  String[] idvalue = line.split("\t");
	  	  // TODO: test length
	  	  id = idvalue[0];
	  	  try {
	  		  //value = Float.valueOf(idvalue[1]).floatValue();
	  		  value = Double.valueOf(idvalue[1]).doubleValue();
	  	  }
	  	  catch (NumberFormatException e)
	  	  {
	  		 System.out.println("NumberFormatException: " + e.getMessage());
	  		 e.printStackTrace();
	  	  }
	  	  this.getHm().put(id, value);
	  } 
	}
	
	/*
	public static void main(String[] args){
		String filename = "/home/mkoenig/Desktop/Cytoscape_v2.6.3/examples/val/02_atp_cyto.val";
		ImportValFile importval = new ImportValFile(filename);
		
		// iterate over the hashmap and print the data
		Set set = importval.getHm().entrySet();
		Iterator i = set.iterator();
		while (i.hasNext()){
			Map.Entry<String, Double> me = (Map.Entry<String, Double>) i.next();
			System.out.println(me.getKey() + " : " + me.getValue() );
		}
	}*/
	public void setHm(HashMap<String, Double> hm) {
		this.hm = hm;
	}
	public HashMap<String, Double> getHm() {
		return hm;
	}

	public String getName() {
		return name;
	}
}
