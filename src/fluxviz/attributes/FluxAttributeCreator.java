/** Copyright (c) 2010 Computational Systems Biochemistry - Charite Berlin
 * @author Matthias Koenig
 */

package fluxviz.attributes;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

/**
 * Creates the node and attribute files for given val file.
 * Node attribute 'sbml type' is needed for the creation of the attributes. 
 * 'sbml type' has to be set to 'species' or 'reaction' for all nodes in the 
 * network.
 */
public class FluxAttributeCreator {
	private ImportValFile importVF;
	
	/**
	 * Creates the node and edge attributes for given val file.
	 * The flux of reaction nodes with no mapping in the val file is set to 
	 * defaultValue (0.0). The edge flux attribute has suffix '_edge', the edge 
	 * flux direction attribute has the suffix '_edge_dir'.
	 * 
	 * The fluxes for the edges are multiplied with the stoichiometric coefficients
	 * defined in the edge attribute 'stoichiometry'. If no stoichiometric coefficient
	 * is defined for a given edge the stoichiometry '1.0' is used.
	 * 
	 * TODO: setUserEditable(String, boolean) for edge attributes
	 * TODO: setUserVisible(String, boolean) for edge direction
	 *  
	 * @param file *.val File which is used for the attribute generation
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public FluxAttributeCreator(File file){	
		// Import the val file and get the id, flux hashmap
		importVF = new ImportValFile(file.getAbsolutePath());	
		Set set = importVF.getHm().entrySet();
		
		// Define the attribute names
		String attName = file.getName();
		String attEdgeName = attName + "_edge";
		String attEdgeDirName = attName + "_edge_dir";
		
		// Delete old attributes
		CyAttributes node_attrs = Cytoscape.getNodeAttributes();
        CyAttributes edge_attrs = Cytoscape.getEdgeAttributes();
        
		node_attrs.deleteAttribute(attName);
		edge_attrs.deleteAttribute(attEdgeName);
		edge_attrs.deleteAttribute(attEdgeDirName);
		
		
		// Create node attribute
		List<CyNode> nodeList = Cytoscape.getCyNodesList();
		double defaultValue = 0.0;
		CyNode node;
		String nodeId;
		// Flux is set to default for all nodes of type reaction
		for (Iterator<CyNode> i = nodeList.iterator(); i.hasNext();){
			nodeId = i.next().getIdentifier();
			if (node_attrs.getAttribute(nodeId, "sbml type").equals("reaction")){
				node_attrs.setAttribute(nodeId, attName, defaultValue);
			}
		}
		// Flux is set to val values for ids in val file
		
		//TODO: THIS IS MAYOR BUG -> creates problems with other val files 
		boolean usePrefix = false;
		boolean removePrefix = false;
		int removeSize = 3;
		String prefix = "ID_";
		
		for (Iterator i = set.iterator(); i.hasNext();) {
			Map.Entry<String, Double> me = (Map.Entry<String, Double>) i.next();
			if (usePrefix){
				node_attrs.setAttribute(prefix + me.getKey(), attName, me.getValue());	
			}
			if (removePrefix){
				node_attrs.setAttribute(me.getKey().substring(removeSize), attName, me.getValue());	
			}
			else{
				node_attrs.setAttribute(me.getKey(), attName, me.getValue());
			}
		}
		
		// Create the edge attributes
		// Attribute for value and direction is necessary
		CyNetwork network = Cytoscape.getCurrentNetwork();
		double flux;
		double stoichiometry;
		int direction;
		String edgeId;
		String edgeType;
		
		for (Iterator<CyNode> i = nodeList.iterator(); i.hasNext();){
			node = i.next();
			nodeId = node.getIdentifier();
			
			if (node_attrs.getAttribute(nodeId, "sbml type").equals("reaction")){
				
				// get all adjacent edges and set the flux value and the direction
				// TODO: state of art implementation
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
			}
		}	
		
	}
}
