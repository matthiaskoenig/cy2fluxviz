package fluxviz.nodes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CyNodeView;
import fluxviz.view.Position;
import fluxviz.view.PositionUtil;

/**
 * Class for seclude nodes based on attributes and
 * and merge the secluded nodes back to a single entity.
 * This feature is needed in metabolic networks due to the high
 * conectivity of some compounds (especially the cofactors).
 * 
 * On highly connected comound is split into multiple compounds
 * where the newly generated child nodes are located near the reactions
 * to which they belong.
 * 
 * The location of the split node is stored so that the splitting is reversible. 
 * Also the position of the splitted nodes is stored so that an generated splitting with
 * its positions can be reused.
 * 
 * Splitted nodes are a selection of nodes which should be split.
 * 
 * Handle as singleton class.
 * @author mkoenig
 *
 */
public class SecludeNodes{
	// handle the state of the seclusion
	private boolean secluded = false;
	
	// List of original nodes
	private static Map<String, CyNode> nodes = new HashMap<String, CyNode>();
	private static Map<String, CyEdge> edges = new HashMap<String, CyEdge>();
	
	// Storage of positions of original nodes (identifier and position)
	private static Map<String,Position> positions = new HashMap<String, Position>();
	// Storage of positions of secluded nodes
	private static Map<String, Map<String, Position>> positionsSecluded = new HashMap<String, Map<String, Position>>();
	
	// Key are identifier of original nodes (stored are the set of nodes wich are generated
	private static Map<String, Set<CyNode>> nodesSecluded = new HashMap<String, Set<CyNode>>();
	private static Map<String, Set<CyEdge>> edgesSecluded = new HashMap<String, Set<CyEdge>>();
	
	// Singleton pattern
	private static SecludeNodes instance = null;
	protected SecludeNodes() {
	      // Exists only to defeat instantiation.
	   }
	public static SecludeNodes getInstance() {
		  if(instance == null) {
		     instance = new SecludeNodes();
		  }
		  return instance;
	}
	
	/** Returns the state of the seclusion */
	public boolean getState(){
		return secluded;
	}

	
	/**
	 * Secludes all the given nodes. 
	 * The seclusion process is performed for every node in the given
	 * selection.
	 * @param nodes
	 */
	public void secludeNodes(Set<CyNode> nodes){
		Object[] nodesArray = nodes.toArray();
		CyNode node;
		System.out.println("secludeNodes");
		// previous seclusion has to be made back
		if (secluded == true){ unitNodes();}
		
		// calculate new seclusion
		for (int i=0; i<nodesArray.length; ++i){
			node = (CyNode) nodesArray[i];			System.out.println(node.getIdentifier());
			secludeNode(node);
		}
		secluded = true;

		//update seclusion attribute
		updateSeclusionTypeAttribute();
		
		//update the view
		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		view.updateView();
		view.redrawGraph(true,true);
		
		// select all secluded nodes
		CyNetwork network = Cytoscape.getCurrentNetwork();
		network.unselectAllNodes();
		for (Set<CyNode> secNodes : nodesSecluded.values()){
			network.setSelectedNodeState(secNodes, true);
		}
	}
	
	
	/** Recalculate the 'seclusion type' attribute based on the 
	 * current seclusion setting.
	 * The seclusion type can be used for visualization instead of the 'sbml type'
	 * to separate species (compounds) and cofactors.
	 */
	public static void updateSeclusionTypeAttribute(){
		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyAttributes attrs = Cytoscape.getNodeAttributes();
		String name = "seclusion type";
		String sbmlType = "sbml type";
		String value;
		for (int index: network.getNodeIndicesArray()){
			String id = network.getNode(index).getIdentifier();
			value = attrs.getStringAttribute(id, sbmlType);
			
			// ! necessary that complete sbml type available
			if (getSecludedNodesSet().contains(id))
				attrs.setAttribute(id, name, "cofactor");
			else{
				if (value != null)
					attrs.setAttribute(id, name, value);
			}
		}
		
	}
	
	/**
	 * Gives simple set of all secluded nodes.
	 * @return set of node identifiers which should be secluded
	 */
	public static Set<String> getSecludedNodesSet(){
		Set<CyNode> set = new HashSet<CyNode>();
		for (Set<CyNode> nodesSet : nodesSecluded.values()){
			set.addAll(nodesSet);
		}
		Set<String> idSet = new HashSet<String>();
		for (CyNode node: set){
			idSet.add(node.getIdentifier());
		}
		return idSet;
	}
	
	/**
	 * Unit all the secluded nodes.
	 */
	public void unitNodes(){
		System.out.println("unitNodes");
		if (secluded == false){ return;}
		for (String key: nodes.keySet()){
			System.out.println(key);
			unitNode(nodes.get(key));
		}
		secluded = false;
		
		// update seclusion attribute
		
		// update the view
		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		view.updateView();
		view.redrawGraph(true,true);
		
		// select the unified nodes (for visual feedback of action)
		CyNetwork network = Cytoscape.getCurrentNetwork();
		network.unselectAllNodes();
		network.setSelectedNodeState(nodes.values(), true);
	}
	
	/**
	 * Use the information stored in the global maps to reunite the secluded
	 * nodes. The original nodes and the position of these nodes are stored
	 * and can be used to reunit the original nodes.
	 * 
	 * Here the complete functionality form secludeNode has to be reversed.
	 * 
	 * TODO: problem with global storage
	 */
	public void unitNode(CyNode Node){
		// Use the stored information for the node to make the effects reversible
		
		// Secluded Edges and nodes have to be removed
		
		
	}
	
	
	/**
	 * Seclude a single node.
	 * Generate the secluded nodes from given nodes.
	 * Calculate the postions and set the
	 * 
	 *  TODO: How to handle the edges ?? The edge information should not be lost, but has to be 
	 *  restored if the seclusion is reverted.
	 *  
	 *  Add the data to the dictionaries.
	 *  
	 *  Returns true if succesful secluded nodes generated, returns false otherwise (in case only one edge).
	 */
	public void secludeNode(CyNode node){		
		// Get network and attributes
		CyNetwork network = Cytoscape.getCurrentNetwork();
		
		// Secluded nodes and edges and positions
		Set<CyNode> secNodes = new HashSet<CyNode>();
		Set<CyEdge> secEdges = new HashSet<CyEdge>();
		Map<String, Position> secPositions = new HashMap<String, Position>();
		
		// Get adjacent edges
		int[] edgeIndices = network.getAdjacentEdgeIndicesArray(node.getRootGraphIndex(), true, true, true);
		
		// Seclude node
		CyEdge edge;
		CyNode newNode;
		CyEdge newEdge;
		String suffix;
		for (int i=0; i<edgeIndices.length; ++i){
			// Get adjacent edge
			edge = (CyEdge) network.getEdge(edgeIndices[i]);
			
			// Get the id of the other node (reaction)
			if (isSourceNodeInEdge(edge, node))
				suffix = edge.getTarget().getIdentifier();
			else
				suffix = edge.getSource().getIdentifier();
			
			// Generate replacement node
			newNode = copyNode(node, node.getIdentifier() + "_" + suffix);
			network.addNode(newNode);
			
			// Generate replacement edge
			newEdge = copyEdge(edge, edge.getIdentifier() + "_" + suffix, node, newNode);
			network.addEdge(newEdge);
			
			// Move node to new position
			// TODO: position is not calculated correctly at the moment
			// Calculate the positions based on current layout
			PositionUtil.moveNode(newNode, PositionUtil.getPosition(node));
			
			// Remove old edge
			network.removeEdge(edge.getRootGraphIndex(), false);
			
			// Store all necessary information
			edges.put(edge.getIdentifier(), edge);
			secNodes.add(newNode);
			secEdges.add(newEdge);
			secPositions.put(newNode.getIdentifier(), PositionUtil.getPosition(newNode));
		}
		// remove old node
		network.removeNode(node.getRootGraphIndex(), false);
		
		// Store all necessary information
		nodes.put(node.getIdentifier(), node);
		positions.put(node.getIdentifier(), PositionUtil.getPosition(node));
		nodesSecluded.put(node.getIdentifier(), secNodes);
		edgesSecluded.put(node.getIdentifier(), secEdges);
		positionsSecluded.put(node.getIdentifier(), secPositions);

	}
	
	/**
	 * Copies a node with all its attributes and the view in the network. 
	 * Returns the copied node.
	 */
	public CyNode copyNode(CyNode node, String newIdentifier){
		// Create the new node
		CyNode newNode = Cytoscape.getCyNode(newIdentifier, true);
		
		// Copy all the information from the old node attributes
		copyAttributes(Cytoscape.getNodeAttributes(), node.getIdentifier(), newNode.getIdentifier());
		return newNode;
	}

	/**
	 * Copies a edge with all its attributes and views in the network.
	 * Returns the copied edge.
	 */
	public static CyEdge copyEdge(CyEdge edge, String newIdentifier, CyNode node, CyNode newNode){
		String interaction = Cytoscape.getEdgeAttributes().getStringAttribute(edge.getIdentifier(), "interaction");
		CyEdge newEdge;
		if (isSourceNodeInEdge(edge, node)){
			newEdge = Cytoscape.getCyEdge(newNode.getIdentifier(), newIdentifier, 
											edge.getTarget().getIdentifier(), interaction);	
		}
		else{
			newEdge = Cytoscape.getCyEdge(edge.getSource().getIdentifier(), newIdentifier,
											newNode.getIdentifier(), interaction);
		}
		
		// Copy all the information from the old edge attributes
		copyAttributes(Cytoscape.getEdgeAttributes(), edge.getIdentifier(), newEdge.getIdentifier());
	
		return newEdge;
	}
	
	/**
	 * Test is node is source in Edge.
	 * Comparison via identifier.
	 */
	public static boolean isSourceNodeInEdge(CyEdge edge, CyNode node){
		if (edge.getSource().getIdentifier().equals(node.getIdentifier())){
			return true;
		}
		return false;
	}
		
	public static boolean isTargetNodeInEdge(CyEdge edge, CyNode node){
		return (! isSourceNodeInEdge(edge, node));
	}
	
	
	/** Copies all attributes from oldId to newId */
	@SuppressWarnings("unchecked")
	public static void copyAttributes(CyAttributes attrs, String oldId, String newId){
		for (String name : attrs.getAttributeNames()){
			byte attrType = attrs.getType(name);
			switch (attrType) {
			case CyAttributes.TYPE_BOOLEAN:
				Boolean tmp_boolean = attrs.getBooleanAttribute(oldId, name);
				if (tmp_boolean != null)
					attrs.setAttribute(newId, name, tmp_boolean);
				else attrs.deleteAttribute(newId, name);
				break;
			case CyAttributes.TYPE_STRING:
				String tmp_string = attrs.getStringAttribute(oldId, name);
				if (tmp_string != null)
					attrs.setAttribute(newId, name, tmp_string);
				else attrs.deleteAttribute(newId, name);
				break;
			case CyAttributes.TYPE_FLOATING:
				Double tmp_double = attrs.getDoubleAttribute(oldId, name);
				if (tmp_double != null)
					attrs.setAttribute(newId, name, tmp_double);
				else attrs.deleteAttribute(newId, name);
				break;
			case CyAttributes.TYPE_INTEGER:
				Integer tmp_integer = attrs.getIntegerAttribute(oldId, name);
				if (tmp_integer != null)
					attrs.setAttribute(newId, name, tmp_integer);
				else
					attrs.deleteAttribute(newId, name);
				break;
			case CyAttributes.TYPE_SIMPLE_LIST:
				List tmp_list = attrs.getListAttribute(oldId, name);
				if (tmp_list != null)
					attrs.setListAttribute(newId, name, tmp_list);
				else
					attrs.deleteAttribute(newId, name);
				break;
			case CyAttributes.TYPE_SIMPLE_MAP:
				Map tmp_map = attrs.getMapAttribute(oldId, name);
				if (tmp_map != null)
					attrs.setMapAttribute(newId, name, attrs.getMapAttribute(oldId, name));
				else
					attrs.deleteAttribute(newId, name);
				break;
			}
		}
		
	}
	
	

	
	
	/** Returns the number of secluded nodes */
	public int getSecludedNodesCount(CyNode node){
		CyNetwork network = Cytoscape.getCurrentNetwork();
		// Get all the neighbor nodes and edges
		int[] edgeIndices = network.getAdjacentEdgeIndicesArray(node.getRootGraphIndex(), true, true, true);
		return edgeIndices.length;
	}
	
	
	/** 
	 * A node which should be secluded is given. 
	 * The positions of the new nodes are returned.
	 */
	public Position[] calculatePositions(CyNode node){
		Position[] positions = new Position[getSecludedNodesCount(node)];
		for (int i=0; i<positions.length; ++i){
			Position pos = PositionUtil.getPosition(node);
			PositionUtil.movePosition(pos, 0.0, 20.0);
			positions[i] = pos;
		}
		return positions;
	}
	
	/** Calculate the identifiers for the secluded nodes
	public calculateIdentifiers(node){
		
	}
	
	
	
	/**
	 * Returns list of nodes in the current network with 
	 * degree >= given degree. Degree is the number.
	 * 
	 * The degree has to be calculated via the NodeView of the node.
	 * 
	 * @param degree
	 * @return set of nodes in the current Cytoscape network with nodeDegree >= degree
	 */
	public static Set<CyNode> getNodesByDegree(int degree){
		//get all nodes in current network
		Set<CyNode> nodes = new HashSet<CyNode>();
		CyNode node;
		for (Object obj : Cytoscape.getCurrentNetwork().nodesList()){
			node = (CyNode) obj;
			CyNodeView nodeView = (CyNodeView) Cytoscape.getCurrentNetworkView().getNodeView(node.getRootGraphIndex());
			if (nodeView.getDegree() >= degree){
				nodes.add(node);
			}	
		}
		return nodes;
	}

	// Only returns the nodes which are compounds in the metabolic network
	// and fullfill the degree condition
	// Filter all nodes by degree with the attribute for compunds
	public static Set<CyNode> getSpeciesByDegree(int degree){
		Set<CyNode> nodes = getNodesByDegree(degree);
		Set<CyNode> species = new HashSet<CyNode>();
		// get the attribute sbmlType
		
		
		for (CyNode node : nodes){
			System.out.println(node);
			
		}
		return species;
	}
	
	
	//TODO: define in external tools
	//The type of the node is used from SBMLtype.
	//If no SBMLType attribute is available show message to create such
	// an attribute.
	
	
	/**
	 * Set all the attributes from one node for the other node.
	 * But not the identifier (the identifier has to be differnt.
	 */
	static boolean copyNodeAttributes(CyNode fromNode, CyNode toNode){
		//TODO: not implemented
		return false;
	}
	
	
	static boolean isSpecies(CyNode node){
		CyAttributes attributes = Cytoscape.getNodeAttributes();
		String sbmlType = attributes.getStringAttribute(node.getIdentifier(), "sbmlType");
		if (sbmlType.equals("species")){
			return true;
		}
		return false;
			
		/* TODO
		for (String name : nodeAttrs.getAttributeNames()){
			if (nodeAttrs.getType(name) == attrType){
				nameSet.add(name);
			}
		}
		CyAttributes attributes = C
		return true;
		*/
	}

	static boolean isReaction(CyNode node){
		return true;
	}


}
