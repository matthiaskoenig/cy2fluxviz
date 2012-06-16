package cyfluxviz.attributes;
import java.io.*;
import java.util.*;

import cyfluxviz.FluxDistribution;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

/* Val file importer (simple key/value pairs for reactions). */
public class FluxDistributionImporter {
	
	private String networkId;
	private String name;
	private HashMap<String, Double> nodeFluxes = new HashMap<String, Double>();
	private HashMap<String, Double> edgeFluxes = new HashMap<String, Double>();
	private FluxDistribution fluxDistribution;
	
	
	public FluxDistributionImporter(String filename){
		importFromFile(filename);
	}
	
	public FluxDistribution getFluxDistribution(){
		return fluxDistribution;
	}
	
	private void importFromFile(String filename){
		name = filename;
		networkId = getCurrentNetworkId();
		nodeFluxes = getNodeFluxesFromFile(filename);
		// Reduce to nodes currently in Cytoscape
		nodeFluxes = filterNodeFluxesInCytoscape(nodeFluxes);
		
		edgeFluxes = getEdgeFluxesFromNodeFluxes(nodeFluxes);
		fluxDistribution = new FluxDistribution(name, networkId, edgeFluxes, nodeFluxes);
	}
	
	public static HashMap<String, Double> filterNodeFluxesInCytoscape(HashMap<String, Double> nFluxes){
		HashMap<String, Double> filteredFluxes = new HashMap<String, Double>();
		for (String id : nFluxes.keySet()){
			if (Cytoscape.getCyNode(id, false) != null){
				filteredFluxes.put(id, nFluxes.get(id));
			}else{
				System.out.println("Id in flux mapping, but not in Cytoscape : " +  id);
			}
		}
		return filteredFluxes;
	}
	
	public static HashMap<String, Double> filterNodeFluxesInCurrentNetwork(HashMap<String, Double> nFluxes){
		HashMap<String, Double> filteredFluxes = new HashMap<String, Double>();
		String netId = getCurrentNetworkId();
		if (netId != null){
			CyNetwork network = Cytoscape.getNetwork(netId);
			for (String id : nFluxes.keySet()){
				CyNode node = Cytoscape.getCyNode(id, false);
				if (node != null && network.containsNode(node) == true){
					filteredFluxes.put(id, nFluxes.get(id));
				} else {
					System.out.println("Id in flux mapping, but not in current Network  : " +  id);
				}
			}
		}
		return filteredFluxes;
	}
	
	private static String getCurrentNetworkId(){
		String id = null;
		CyNetwork network = Cytoscape.getCurrentNetwork();
		if (network != null){
			id = network.getIdentifier();
		}
		return id;
	}
	
	private static HashMap<String, Double> getNodeFluxesFromFile(String filename){
		HashMap<String, Double> fluxes = new HashMap<String, Double> ();
		String line;
	    try {
	      BufferedReader br = new BufferedReader(new FileReader(filename));
	      // dis.available() returns 0 if the file does not have more lines.
	      while ( (line = br.readLine()) != null){
	    	  parseLineAndAddToMap(line, fluxes);
	      } 
	      br.close();
	    } catch (FileNotFoundException e) {
	    	System.err.println("Error: " + e);
	    	e.printStackTrace();
	    } catch (IOException e) {
	    	System.err.println("Error: " + e);
	    	e.printStackTrace();
	    }
		return fluxes;
	}
	
	/* Parses the (id, value) pair from given line. */
	private static void parseLineAndAddToMap(String line, HashMap<String, Double> fluxes){
	  String id;
	  Double value = 0.0;
	  line = line.trim();	// remove newline
	  if (line.length() != 0) {
	  	  String[] idvalue = line.split("\t");
	  	  id = idvalue[0];
	  	  try {
	  		  value = Double.valueOf(idvalue[1]).doubleValue();
	  		  fluxes.put(id, value);
	  	  } catch (NumberFormatException e) {
	  		 e.printStackTrace();
	  	  }
	  } 
	}
	
	
	private static HashMap<String, Integer> getEdgeDirectionsFromEdgeFluxes(HashMap<String, Double> eFluxes){
		// set the direction of the edges
		direction = 1;
		
		// here problems with the edge direction
		// TODO: test if interaction is available
		// for edges to reactants the direction has to be reversed
		edgeType = edge_attrs.getStringAttribute(edgeId, "interaction"); 
		if (edgeType.equals("reaction-reactant")){
			direction = - direction;
		}
		
		// TODO: outsource in the SBML | XGMML generation 
		// HepatoCore networks handle
		if (edgeType.equals("pp")){
			String objectType = edge_attrs.getStringAttribute(edgeId, "object_type");
			if (objectType.equals("sink_in_event") || objectType.equals("product_in_event")){
				direction = - direction;
			}
		}
		
		// if the flux is negative the edges have to be reversed
		if (flux < 0){
			direction = -direction;
		}
		edge_attrs.setAttribute(edgeId, attEdgeDirName, direction);
		
	}
	
	private static HashMap<String, Double> getEdgeFluxesFromNodeFluxes(){
		HashMap<String, Double> eFluxes = new HashMap<String, Double>();
					
		// Create the edge attributes
		// Attribute for value and direction is necessary
		CyNetwork network = Cytoscape.getCurrentNetwork();
		double flux;
		double stoichiometry;
		int direction;
		String edgeId;
		String edgeType;
		
		@SuppressWarnings("unchecked")
		List<CyNode> cyNodes = Cytoscape.getCyNodesList(); 
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		String nodeId = null;
		for (CyNode node : cyNodes){
			nodeId = node.getIdentifier();
			
			if (nodeAttributes.getAttribute(nodeId, "sbml type").equals("reaction")){
				
				// get all adjacent edges and set the flux value and the direction
				List<CyEdge> adjEdges = network.getAdjacentEdgesList(node, true, true, true);
				for (CyEdge edge: adjEdges){
					flux = node_attrs.getDoubleAttribute(nodeId, attName);
					// set the flux of the edges
					edgeId = edge.getIdentifier();
					if (edge_attrs.getAttribute(edgeId, "stoichiometry") == null){
						stoichiometry = 1.0;
					}
					else {
						stoichiometry = (Double) edge_attrs.getAttribute(edgeId, "stoichiometry");
					}
					//System.out.println("Stoichiometry: " + stoichiometry);
					flux = stoichiometry * flux;
					edge_attrs.setAttribute(edgeId, attEdgeName, Math.abs(flux));
										
					
				}
			}
		}	
		
		return eFluxes;
	}
	
}
