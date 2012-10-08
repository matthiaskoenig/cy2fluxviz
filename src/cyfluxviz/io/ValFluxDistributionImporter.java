package cyfluxviz.io;

import java.io.*;
import java.util.*;

import cysbml.CySBMLConstants;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.util.CyFileFilter;
import cytoscape.util.FileUtil;
import cytoscape.view.CyNetworkView;

import cyfluxviz.FluxDirection;
import cyfluxviz.FluxDis;
import cyfluxviz.gui.CyFluxVizPanel;
import cyfluxviz.gui.PanelText;
import cyfluxviz.util.FluxVizFileUtil;
import cyfluxviz.util.FluxVizUtil;

/* Val file importer (simple key/value pairs for reactions). */
public class ValFluxDistributionImporter {
	
	private String networkId;
	private String name;
	private HashMap<String, Double> nodeFluxes = new HashMap<String, Double>();
	private HashMap<String, Double> edgeFluxes = new HashMap<String, Double>();
	private HashMap<String, FluxDirection> edgeDirections = new HashMap<String, FluxDirection>();
	private FluxDis fluxDistribution;
	
	public ValFluxDistributionImporter(File file){
		importFromFile(file);
	}
	
	public ValFluxDistributionImporter(String id, String name, String networkId, HashMap<String, Double> nodeFluxes){
		this.name = name;
		this.networkId = networkId;
		this.nodeFluxes = nodeFluxes;
		// Reduce to nodes currently in Cytoscape
		nodeFluxes = testNodeFluxesInCytoscape(nodeFluxes);
		edgeFluxes = getEdgeFluxesFromNodeFluxes(nodeFluxes);
		edgeDirections = getEdgeDirectionsFromEdgeFluxes(edgeFluxes);
		fluxDistribution = new FluxDis(name, networkId, nodeFluxes, edgeFluxes, edgeDirections);
		fluxDistribution.setIdFromString(id);
	}
	
	public FluxDis getFluxDistribution(){
		return fluxDistribution;
	}
	
	private void importFromFile(File file){
		name = getFluxDistributionNameFromFile(file);
		networkId = getCurrentNetworkId();
		nodeFluxes = getNodeFluxesFromFile(file);
		// Reduce to nodes currently in Cytoscape
		nodeFluxes = testNodeFluxesInCytoscape(nodeFluxes);
		edgeFluxes = getEdgeFluxesFromNodeFluxes(nodeFluxes);
		edgeDirections = getEdgeDirectionsFromEdgeFluxes(edgeFluxes);
		fluxDistribution = new FluxDis(name, networkId, nodeFluxes, edgeFluxes, edgeDirections);
	}
	

	
	
	public String getFluxDistributionNameFromFile(File file){
		String name = file.getName();
		// remove the .val ending
		name = name.substring(0, name.length()-4);
		return name;
	}
	
	public static HashMap<String, Double> testNodeFluxesInCytoscape(HashMap<String, Double> nFluxes){
		HashMap<String, Double> filteredFluxes = new HashMap<String, Double>();
		for (String id : nFluxes.keySet()){
			double flux = nFluxes.get(id);
			// only the reaction with non-zero flux are used
			if (Cytoscape.getCyNode(id, false) == null){
				System.out.println("CyFluxViz[WARNING] -> Val Import : Id in flux mapping not in Cytoscape : " +  id);
			}else{
				
			}
			filteredFluxes.put(id, flux);
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
		} else {
			System.out.println("CyFluxViz[INFO] -> No Network associated with FluxDistribution");
		}
		return id;
	}
	
	private static HashMap<String, Double> getNodeFluxesFromFile(File file){
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
	
	private static HashMap<String, Double> getEdgeFluxesFromNodeFluxes(HashMap<String, Double> nFluxes){
		HashMap<String, Double> eFluxes = new HashMap<String, Double>();
							
		@SuppressWarnings("unchecked")
		List<CyNode> cyNodes = Cytoscape.getCyNodesList(); 
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
		CyNetwork network = Cytoscape.getCurrentNetwork();
		
		String nodeId;
		String nodeType;
		String edgeId;
		double flux;
		double stoichiometry;
		for (CyNode node : cyNodes){
			
			nodeId = node.getIdentifier();
			nodeType = (String) nodeAttributes.getAttribute(nodeId, CySBMLConstants.ATT_TYPE); 
			
			//TODO: change to support multiple networks at once
			if (nFluxes.containsKey(nodeId) && nodeType != null && nodeType.equals(CySBMLConstants.NODETYPE_REACTION)){
				@SuppressWarnings("unchecked")
				List<CyEdge> adjEdges = network.getAdjacentEdgesList(node, true, true, true);
				for (CyEdge edge: adjEdges){
					edgeId = edge.getIdentifier();
					
					stoichiometry = 1.0;
					if (edgeAttributes.getAttribute(edgeId, CySBMLConstants.ATT_STOICHIOMETRY) != null){
						stoichiometry = (Double) edgeAttributes.getAttribute(edgeId, CySBMLConstants.ATT_STOICHIOMETRY);
					}
					flux = 0.0;
					if (nFluxes.containsKey(nodeId)){
						flux = stoichiometry * nFluxes.get(nodeId);
					}
					eFluxes.put(edgeId, flux);
				}
			}
		}	
		return eFluxes;
	}
	
	public static HashMap<String, FluxDirection> getEdgeDirectionsFromEdgeFluxes(HashMap<String, Double> eFluxes){
		
		HashMap<String, FluxDirection> directionMap = new HashMap<String, FluxDirection>();
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
		
		for (String edgeId : eFluxes.keySet()){
			
			int direction = 1;
			
			
			
			// reverse direction for reaction-reactant edges
			String edgeType = edgeAttributes.getStringAttribute(edgeId, "interaction");
			if (edgeType.equals("reaction-reactant")){
				direction = - direction;
			}
			
			// reverse direction for negative fluxes
			double flux = eFluxes.get(edgeId);
			if (flux < 0){
				direction = -direction;
			}
			
			FluxDirection fluxDirection = FluxDirection.FORWARD;
			if (direction < 0){ 
				fluxDirection = FluxDirection.REVERSE;
			}
			directionMap.put(edgeId, fluxDirection);
		}
		return directionMap;
	}
	
	// TODO: Refactor all the tests
    public static void loadValFiles(){
        CyNetwork network = Cytoscape.getCurrentNetwork();
        CyNetworkView networkView = Cytoscape.getCurrentNetworkView();
        
    	if (FluxVizUtil.hasCompleteSBMLTypeAttribute() == false){
    		String title = "SBML type not complete.";
    		String msg = "No complete 'sbml type' attribute.\nEvery node has to be classified as " +
        			"either 'reaction' or 'species'.\nIf the network was not imported as SBML create attribute 'sbml type' manually\nand " +
        			"classify all nodes as either 'reaction' or 'species'.";
        	PanelText.showMessage(msg, title);
        	return;
        }
    	
    	if (FluxVizUtil.hasCompleteStoichiometryAttribute() == false){
    		String title = "Stoichiometry not complete.";
    		String msg = "Every edge should have stoichiometric information associated.\n" +
        			"Missing stoichiometric coefficients are handled as '1.0' in the visualisation.";
    		PanelText.showMessage(msg, title);
        }
    	
        // Test if network for val files available
        network.selectAllNodes();
        if (networkView.getSelectedNodes().size() == 0) {
        	String title = "No network warning.";
        	String msg = "No nodes in network. Network must be loaded and selected before loading of val files.";
        	PanelText.showMessage(msg, title);
        	return;
        }
        network.unselectAllNodes();
    	
	    // Select the val files and create the attributes
        CyFileFilter[] filter = { new CyFileFilter("val", "Val Flux Distributions")};
    	File[] valFiles = FileUtil.getFiles("Select val files for current network.", FileUtil.LOAD, filter);
    	if (valFiles != null){
    		for (int k=0; k<valFiles.length; ++k){
    			FluxVizFileUtil.createFluxDistributionFromValFile(valFiles[k]);
    		}
    		CyFluxVizPanel.getInstance().updateFluxDistributionTable();
    	}
    }
}