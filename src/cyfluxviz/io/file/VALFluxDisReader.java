package cyfluxviz.io.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import cyfluxviz.FluxDis;
import cyfluxviz.util.CyNetworkUtils;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

public class VALFluxDisReader extends AbstractFluxDisReader {
	public static final String SEPARATOR = "\t";
	
	
	public VALFluxDisReader() {
		super();
		extension = "val";
	}
	

	/** Reads the val files. Only one FluxDistribution is stored per val file. */
	@Override
	public Collection<FluxDis> read(File file) throws IOException {
		Collection<FluxDis> fds = new LinkedList<FluxDis>();
		
		// TODO: Only the current network is tested !!! -> can generate problems with
		// 			multiple networks : discrepancy between the networks in the val
		//			files and the existing networks in Cytoscape.		
		if (! CyNetworkUtils.existsCurrentNetwork()){
			System.out.println("CyFluxViz[WARNING]: Current Network does not exist. Val files are not loaded.");
			return fds;
		}
		CyNetwork network = Cytoscape.getCurrentNetwork();
		
		// TODO: has to be tested for all file importers !! 
		if (! CyNetworkUtils.isNetworkCyFluxVizCompatible(network)){
			return fds;
        }
		
		String name = getFluxDistributionNameFromFile(file);
	    String networkId = network.getIdentifier(); 	//TODO ? title
		
		HashMap<String, Double> nodeFluxes = readNodeFluxesFromFile(file);
		FluxDis fd = new FluxDis(name, networkId, nodeFluxes);
		fds.add(fd);
		
		return fds;
	}
	
	
	/** Gets the name of the FluxDistribution from the filename.
	 * Removes the extension (.val).*/
	private String getFluxDistributionNameFromFile(File file){
		String name = file.getName();
		return  name.substring(0, name.length()-4);
	}

	/** Reads all the node value pairs from file. */
	private static HashMap<String, Double> readNodeFluxesFromFile(File file){
		HashMap<String, Double> fluxes = new HashMap<String, Double> ();
		String line;
	    try {
	      BufferedReader br = new BufferedReader(new FileReader(file));
	      while ( (line = br.readLine()) != null){
	    	  parseLineAndAddToMap(line, fluxes);
	      } 
	      br.close();
	    } catch (FileNotFoundException e) {
	    	e.printStackTrace();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
		return fluxes;
	}
	
	/** Parses the (id, value) pair from given line. */
	private static void parseLineAndAddToMap(String line, HashMap<String, Double> fluxes) {
		line = line.trim();
		if (line.startsWith("#") || line.length() == 0) {
			return;
		}

		Double value = 0.0;
		String[] idvalue = line.split(SEPARATOR);
		String id = idvalue[0];
		try {
			value = Double.valueOf(idvalue[1]).doubleValue();
			fluxes.put(id, value);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

}
