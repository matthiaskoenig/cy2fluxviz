package fluxviz.util;

import java.util.Iterator;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;

/**
 * Collection of functions which are used multiple times in Cytoscape.
 * For example the test if a network is currently available or if a network
 * view is currently available.
 * @author mkoenig
 *
 */
public class FluxVizUtil {
	/**
	 * Test if network is available.
	 */
	public static boolean availableNetwork() {
		CyNetwork n = Cytoscape.getCurrentNetwork();
		if ( n == null || n == Cytoscape.getNullNetwork() ) 
			return false;
		else
			return true;
	}

	/**
	 * Test if network and view is available.
	 */
	public static boolean availableNetworkAndView() {
		CyNetwork n = Cytoscape.getCurrentNetwork();
		if ( n == null || n == Cytoscape.getNullNetwork() ) {
			return false;
		}
		
		CyNetworkView v = Cytoscape.getCurrentNetworkView();
		if ( v == null || v == Cytoscape.getNullNetworkView() )
			return false;
		else
			return true;
	}
	
	/**
	 * Test if every network node has a 'sbml type' attribute. 
	 * The 'sbml type' attribute is used for the visualisation of the fluxes 
	 * (descrimination between 'reaction' and 'species' necessary). 
	 * 
	 * @return has every node an sbml type
	 */
	@SuppressWarnings("unchecked")
	public static boolean hasCompleteSBMLTypeAttribute() {
    	CyAttributes node_attrs = Cytoscape.getNodeAttributes();
		String nodeId;
		for (Iterator<CyNode> i = Cytoscape.getCyNodesList().iterator(); i.hasNext();){
			nodeId = i.next().getIdentifier();
			// missing attribute for node
			if (node_attrs.getAttribute(nodeId, "sbml type") == null){
				return false;
			}
			// test if 'reaction' or 'species'
			if (node_attrs.getAttribute(nodeId, "sbml type").equals("reaction")){
				continue;
			}
			if (node_attrs.getAttribute(nodeId, "sbml type").equals("species")){
				continue;
			}
			System.out.println("NodeID without 'sbml type': " + nodeId);
			return false;
		}
		return true;
	}
	
	/**
	 * Test if every network edge has a 'stoichiometry' attribute. 
	 * The 'stoichiometry' attribute is used for the visualisation of the fluxes 
	 * (reaction flux is multiplied with the stoichiometric coefficient of the edge
	 * in the reaction). 
	 * 
	 * @return has every edge a stoichiometric coefficient.
	 */
	@SuppressWarnings("unchecked")
	public static boolean hasCompleteStoichiometryAttribute(){
    	CyAttributes attrs = Cytoscape.getEdgeAttributes();
    	String name = "stoichiometry";
		String edgeId;
		for (Iterator<CyEdge> i = Cytoscape.getCyEdgesList().iterator(); i.hasNext();){
			edgeId = i.next().getIdentifier();
			if (attrs.getAttribute(edgeId, name) != null ){
				continue;
			}
			return false;
		}
		return true;
	}
	
	
}
