package cyfluxviz.util;

import java.util.List;

import cyfluxviz.CyFluxVizPlugin;
import cyfluxviz.FluxDis;
import cyfluxviz.gui.PanelText;
import cysbml.CySBMLConstants;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;

/** Helper classes to work with CyNetworks. Mainly getting information from the 
 * network and testing for the existence of certain aspects in the network. 
 *
 * @author mkoenig
 * @date 2013-07-12
 */
public class CyNetworkUtils {
	
	/////// EXISTENCE TESTS ////////////
	
	/** Does the network with the given networkId exist in Cytoscape ? */
	public static boolean existsNetwork(String networkId){
		CyNetwork n = Cytoscape.getNetwork(networkId);
		if ( n == null || n == Cytoscape.getNullNetwork() ){
			return false;
		}
		return true;
	}
	
	public static boolean existsNetwork(FluxDis fd){
		String networkId = fd.getNetworkId();
		return existsNetwork(networkId);
	}
	
	/** Test if a current network is available in Cytoscape. */ 
	public static boolean existsCurrentNetwork() {
		CyNetwork n = Cytoscape.getCurrentNetwork();
		return existsNetwork(n.getIdentifier());
	}
	
	/** Test if a current view is available in Cytoscape. */ 
	public static boolean existsCurrentView() {
		CyNetworkView v = Cytoscape.getCurrentNetworkView();
		if (v == null || v == Cytoscape.getNullNetworkView()){
			return false;
		}
		return true;
	}
	
	/** Test if current network and view are available in Cytoscape. */
	public static boolean existsCurrentNetworkAndView() {
		return (existsCurrentNetwork() && existsCurrentView());
	}
	
	
	/////// GET NETWORK INFORMATION ////////////
	
	public static String getCurrentNetworkId(){
		String id = null;
		CyNetwork network = Cytoscape.getCurrentNetwork();
		if (network != null){
			id = network.getIdentifier();
		} else {
			CyFluxVizPlugin.LOGGER.info("No current network");
		}
		return id;
	}
	
	
	/** Get the number of edges for the network. 
	 * Returns -1 if the network does not exist. */
	public static int getNetworkEdgeCount(String networkId) {
		if (existsNetwork(networkId)){
			return Cytoscape.getNetwork(networkId).getEdgeCount();
		}
		return -1;
	}
	
	/** Get the number of nodes for the network. 
	 *  Returns -1 if the network does not exist. */
	public static int getNetworkNodeCount(String networkId) {
		if (existsNetwork(networkId)){
			return Cytoscape.getNetwork(networkId).getNodeCount();
		}
		return -1;
	}

	
	/////// TEST NETWORK FOR CYFLUXVIZ COMPATIBILITY ////////////
	
	/** Check if the network is compatible with CyFluxViz. 
	 * TODO: test only the nodes and edges which are affected by the 
	 * 		 loaded flux distribution, meaning in the subnetwork should be tested 
	 * 
	 */
	public static boolean isNetworkCyFluxVizCompatible(CyNetwork network){
        List<?> nodeList = Cytoscape.getCyNodesList();
        List<?> edgeList = Cytoscape.getCyEdgesList();
        
        // Get the edges between the nodes in the network
    	if (CyNetworkUtils.hasCompleteSBMLTypeAttribute(nodeList) == false){
    		String title = "SBML type not complete.";
    		String msg = "No complete 'sbml type' attribute.\nEvery node has to be classified as " +
        			"either 'reaction' or 'species'.\nIf the network was not imported as SBML create attribute 'sbml type' manually\nand " +
        			"classify all nodes as either 'reaction' or 'species'.";
        	PanelText.showMessage(msg, title);
        	return false;
        }
    	
    	if (CyNetworkUtils.hasCompleteStoichiometryAttribute(edgeList) == false){
    		String title = "Stoichiometry not complete.";
    		String msg = "Every edge should have stoichiometric information associated.\n" +
        			"Missing stoichiometric coefficients are handled as '1.0' in the visualisation.";
    		PanelText.showMessage(msg, title);
        }
    	return true;
	}

	
	/** Test if all nodes in Cytoscape have the 'sbml type' attribute. 
	 * The 'sbml type' attribute is necessary for the distinction of
	 * 'reaction' and 'species' in the network, according to the SBML 
	 * specification. 
	 * TODO: support the qual extension -> currently only tested for the
	 * 		 basic SBML types.
	 * TODO: properly to harsh condition testing all the nodes in Cytoscape. 
	 */
	public static boolean hasCompleteSBMLTypeAttribute(List<?> nodeList) {
    	CyAttributes attrs = Cytoscape.getNodeAttributes();
    	String type = CySBMLConstants.ATT_TYPE;
    	
    	for (Object obj : nodeList){
    		CyNode node = (CyNode) obj;
    		String id = node.getIdentifier();
    		String nodeType = (String) attrs.getAttribute(id, type);
    		
			if (nodeType == null){
				return false;
			}
			
			if (nodeType.equals(CySBMLConstants.NODETYPE_REACTION)){
				continue;
			}
			if (nodeType.equals(CySBMLConstants.NODETYPE_SPECIES)){
				continue;
			}
			
			System.out.println("CyFluxViz[WARNING]: Node type not supported: " + id + " -> " + nodeType);
			return false;
    	}
		return true;
	}
	
	/** Test if all edges have a 'stoichiometry' attribute. 
	 * The 'stoichiometry' attribute is used for the visualisation of the fluxes 
	 * (reaction flux is multiplied with the stoichiometric coefficient of the edge
	 * in the reaction). 
	 * Properly to harsh condition testing all the edges in Cytoscape.
	 */
	public static boolean hasCompleteStoichiometryAttribute(List<?> edgeList){
    	CyAttributes attrs = Cytoscape.getEdgeAttributes();
		for (Object obj : edgeList){
			CyEdge edge = (CyEdge) obj;
			if (attrs.getAttribute(edge.getIdentifier(), CySBMLConstants.ATT_STOICHIOMETRY) != null ){
				continue;
			}
			return false;		
		}
		return true;
	}
}
