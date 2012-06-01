package fluxviz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Handles the Fasimu Flux information for the given flux distributions.
 * @author mkoenig
 *
 */
public class FluxInformation {
	
	/** Set of the flux attribute names */
	public Set<String> attributeNames;
	public Map<String, FluxInfo> attributeInformation;
	
	/** 
	 * Creates the FluxInformation based on the simulation file.
	 * @param simFile simulation file  
	 */
	public FluxInformation(File simFile){
		parseSimFile(simFile);
	}
	
	/**
	 * Parse the simulation file and create the information from them.
	 * @param filename
	 */
	public void parseSimFile(File filename){
		attributeNames = new HashSet<String>();
		attributeInformation = new HashMap<String, FluxInfo>();
		String line;
		String name;
		FluxInfo fluxInfo;
	    try {
	      BufferedReader br = new BufferedReader(new FileReader(filename));
	      while ( (line = br.readLine()) != null){
	    	  fluxInfo = parseLine(line);
	    	  if (fluxInfo != null){
	    		  //Store the fluxInfo in the map
	    		  System.out.println(fluxInfo.toString());
	    		  name = fluxInfo.getName();
	    		  attributeNames.add(name);
	    		  attributeInformation.put(name, fluxInfo);
	    	  }
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
	}
	
	/**
	 * Parses line from the simulation file and creates the FluxInfo from the line.
	 * Returns null if no FluxInfo could be created from the line (for example 
	 * comment lines) or returns the created FluxInfo object.
	 * 
	 * Comment lines and empty lines are excluded.
	 * 
	 * @param line line from the simulation file
	 */
	public FluxInfo parseLine(String line){
		FluxInfo fluxInfo = null;
		//Create the FluxInformation object from the given line and return it
		if (line == null){
			return null;
		}
		if (line.startsWith("#")){
			return null;
		}
		if (line == "\n"){
			return null;
		}
		fluxInfo = new FluxInfo(line);
		return fluxInfo;
	}
	
	
}
