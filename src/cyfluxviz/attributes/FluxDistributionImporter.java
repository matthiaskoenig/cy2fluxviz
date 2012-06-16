package cyfluxviz.attributes;
import java.io.*;
import java.util.*;

import javax.swing.JCheckBox;

import cyfluxviz.CyFluxViz;
import cyfluxviz.FluxDirection;
import cyfluxviz.FluxDistribution;
import cyfluxviz.FluxDistributionCollection;
import cyfluxviz.gui.PanelDialogs;
import cyfluxviz.util.FileUtil;
import cyfluxviz.util.FluxVizUtil;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;

/* Val file importer (simple key/value pairs for reactions). */
public class FluxDistributionImporter {
	
	private String networkId;
	private String name;
	private HashMap<String, Double> nodeFluxes = new HashMap<String, Double>();
	private HashMap<String, Double> edgeFluxes = new HashMap<String, Double>();
	private HashMap<String, FluxDirection> edgeDirections = new HashMap<String, FluxDirection>();
	private FluxDistribution fluxDistribution;
	
	
	public FluxDistributionImporter(File file){
		importFromFile(file);
	}
	
	public FluxDistribution getFluxDistribution(){
		return fluxDistribution;
	}
	
	private void importFromFile(File file){
		name = file.getName();
		networkId = getCurrentNetworkId();
		nodeFluxes = getNodeFluxesFromFile(file);
		// Reduce to nodes currently in Cytoscape
		nodeFluxes = filterNodeFluxesInCytoscape(nodeFluxes);
		edgeFluxes = getEdgeFluxesFromNodeFluxes(nodeFluxes);
		edgeDirections = getEdgeDirectionsFromEdgeFluxes(edgeFluxes);
		fluxDistribution = new FluxDistribution(name, networkId, nodeFluxes, edgeFluxes, edgeDirections);
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
			nodeType = (String) nodeAttributes.getAttribute(nodeId, FluxDistribution.ATT_TYPE); 
			
			if (nodeType != null && nodeType.equals(FluxDistribution.NODE_TYPE_REACTION)){
				@SuppressWarnings("unchecked")
				List<CyEdge> adjEdges = network.getAdjacentEdgesList(node, true, true, true);
				for (CyEdge edge: adjEdges){
					edgeId = edge.getIdentifier();
					
					stoichiometry = 1.0;
					if (edgeAttributes.getAttribute(edgeId, FluxDistribution.ATT_STOICHIOMETRY) != null){
						stoichiometry = (Double) edgeAttributes.getAttribute(edgeId, FluxDistribution.ATT_STOICHIOMETRY);
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
	
	private static HashMap<String, FluxDirection> getEdgeDirectionsFromEdgeFluxes(HashMap<String, Double> eFluxes){
		
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
			
			FluxDirection fluxDirection = FluxDirection.NORMAL;
			if (direction < 0){ 
				fluxDirection = FluxDirection.REVERSE;
			}
			directionMap.put(edgeId, fluxDirection);
		}
		return directionMap;
	}
	
	// TODO: Refactor all the tests
    public static void loadValFiles(){
    	JCheckBox checkbox = CyFluxViz.getFvPanel().getFluxSubnetCheckbox(); 
    	if (checkbox.isSelected() == true){
    		checkbox.doClick();
    	}
    	
        CyNetwork network = Cytoscape.getCurrentNetwork();
        CyNetworkView networkView = Cytoscape.getCurrentNetworkView();
        
    	if (FluxVizUtil.hasCompleteSBMLTypeAttribute() == false){
    		String title = "SBML type not complete.";
    		String msg = "No complete 'sbml type' attribute.\nEvery node has to be classified as " +
        			"either 'reaction' or 'species'.\nIf the network was not imported as SBML create attribute 'sbml type' manually\nand " +
        			"classify all nodes as either 'reaction' or 'species'.";
        	PanelDialogs.showMessage(msg, title);
        	return;
        }
    	
    	if (FluxVizUtil.hasCompleteStoichiometryAttribute() == false){
    		String title = "Stoichiometry not complete.";
    		String msg = "Every edge should have stoichiometric information associated.\n" +
        			"Missing stoichiometric coefficients are handled as '1.0' in the visualisation.";
    		PanelDialogs.showMessage(msg, title);
        }
    	
        // Test if network for val files available
        network.selectAllNodes();
        if (networkView.getSelectedNodes().size() == 0) {
        	String title = "No network warning.";
        	String msg = "No nodes in network. Network must be loaded and selected before loading of val files.";
        	PanelDialogs.showMessage(msg, title);
        	return;
        }
        network.unselectAllNodes();
    	
	    // Select the val files and create the attributes
    	File[] valFiles = FileUtil.selectValFiles();
    	if (valFiles != null){
    		for (int k=0; k<valFiles.length; ++k){
    			FileUtil.createFluxDistributionFromValFile(valFiles[k]);
    		}
    		//Update the table with the new FluxDistributions
    		CyFluxViz.getFvPanel().updateTable();
    	}
    }
	
}
