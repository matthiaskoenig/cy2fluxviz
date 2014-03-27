package cyfluxviz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cyfluxviz.util.CyNetworkUtils;
import cysbml.CySBMLConstants;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

/** Main data structure storing the information or flux distributions.
 * Ids are generated via an static id counter. 
 * Flux Distributions have to be associated with networks. This is done via the networkId. 
 * 
 * The actual numerical values of the fluxes and concentrations for the node
 * and edges are stored in HashMaps which are accessed via the node and edge ids.
 * */
public class FluxDis implements Comparable<FluxDis>{
	private static int idCounter = 1;
	
	private Integer id;
	private String name;
	private String networkId;
	
	private HashMap<String, Double> edgeFluxes;
	private HashMap<String, FluxDirection> edgeDirections;
	private HashMap<String, Double> nodeFluxes;
	private HashMap<String, Double> nodeConcentrations;
	
	private FluxDisStatistics fluxStatistics;
	
	//////////////// CONSTRUCTORS ////////////////////////////////////////////////////
	
	/** Minimalistic Constructor. */
	private FluxDis(String name, String networkId){
		id = generateId();
		this.name = name;
		this.networkId = networkId;
	}
	
	
	/** Full constructor providing all the informations. */
	public FluxDis(String name, String networkId, 
							HashMap<String, Double> nodeFluxes,
							HashMap<String, Double> edgeFluxes,
							HashMap<String, Double> nodeConcentrations){
	
		this(name, networkId);
		
		this.nodeFluxes = nodeFluxes;
		this.edgeFluxes = edgeFluxes;
		this.nodeConcentrations = nodeConcentrations;
		
		doPostProcessing();
	}
	
	
	/** Minimalistic Constructor only based on nodeFluxes (based
	 * on the former val files.
	 * @param name
	 * @param networkId
	 * @param nodeFluxes
	 */
	public FluxDis(String name, String networkId, HashMap<String, Double> nodeFluxes){
		this(name, networkId);
		this.nodeFluxes = nodeFluxes;
		doPostProcessing();
	}

	////////////////////////////////////////////////////////////////////
	
	/** Generates a unique ID for the FluxDistribution based on 
	 * a static counter.
	 * @return
	 */
	private static Integer generateId(){
		Integer id = idCounter;
		idCounter++;
		return id;
	}
	
	/** Does the post processing after the Constructor call. 
	 * Calculated derived data like the EdgeDirections from the given 
	 * EdgeFluxes.
	 */
	public void doPostProcessing(){
		// Recalculate the nodeFluxes
		nodeFluxes = testNodeFluxesInCytoscape(nodeFluxes);
		
		// Calculate the EdgeFluxes if necessary
		if (edgeFluxes == null || edgeFluxes.size() == 0){
			edgeFluxes = getEdgeFluxesFromNodeFluxes(nodeFluxes);
		}
		
		// Calculate the EdgeDirections
		edgeDirections = getEdgeDirectionsFromEdgeFluxes(edgeFluxes);
		
		// Set the nodeConcentrations if necessary
		if (nodeConcentrations == null){
			nodeConcentrations = new HashMap<String, Double>();
		}
		
		// Calculate the fluxStatistics
		fluxStatistics = new FluxDisStatistics(this);
		
		// Print postprocessing results
		System.out.println("CyFluxViz[INFO]: PostProcessing of FluxDistribution finished:");
		System.out.println(this);
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
			
			if (nFluxes.containsKey(nodeId) && nodeType != null && nodeType.equals(CySBMLConstants.NODETYPE_REACTION)){
				
				int[] edgeInts = network.getAdjacentEdgeIndicesArray(node.getRootGraphIndex(), true, true, true);
				for (int edgeInt: edgeInts){
					
					edgeId = network.getEdge(edgeInt).getIdentifier();
					
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
	
	
	/** Calculate the edge direction attribute based on the SBML interactions which are written 
	 * in the interaction file.
	 * @param eFluxes
	 * @return
	 */
	public static HashMap<String, FluxDirection> getEdgeDirectionsFromEdgeFluxes(HashMap<String, Double> eFluxes){
		HashMap<String, FluxDirection> directionMap = new HashMap<String, FluxDirection>();
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
		
		for (String edgeId : eFluxes.keySet()){
			int direction = 1;
			// reverse direction for reaction-reactant edges
			String edgeType = edgeAttributes.getStringAttribute(edgeId, "interaction");
			if (
					(edgeType.equals(CySBMLConstants.EDGETYPE_REACTION_REACTANT)) ||
					(edgeType.equals(CySBMLConstants.EDGETYPE_REACTION_SIDEREACTANT))
				){
				direction = - direction;
			}
			// reverse direction for negative fluxes
			double flux = eFluxes.get(edgeId);
			if (flux < 0){
				direction = -direction;
			}
			
			// is forward or backward attribute
			FluxDirection fluxDirection = FluxDirection.FORWARD;
			if (direction < 0){ 
				fluxDirection = FluxDirection.REVERSE;
			}
			directionMap.put(edgeId, fluxDirection);
		}
		return directionMap;
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
	
	/** Reduce the node fluxes to nodes in the current network. */
	public static HashMap<String, Double> filterNodeFluxesInCurrentNetwork(HashMap<String, Double> nFluxes) {
		HashMap<String, Double> filteredFluxes = new HashMap<String, Double>();
		String netId = CyNetworkUtils.getCurrentNetworkId();
		if (netId == null) {
			return filteredFluxes;
		}

		CyNetwork network = Cytoscape.getNetwork(netId);
		for (String id : nFluxes.keySet()) {
			CyNode node = Cytoscape.getCyNode(id, false);
			if (node != null && network.containsNode(node) == true) {
				filteredFluxes.put(id, nFluxes.get(id));
			} else {
				System.out.println("Id in flux mapping, but not in current Network : " + id);
			}
		}
		return filteredFluxes;
	}
	
	
	/** Comparison is done via networkId and name. */
	@Override
	public int compareTo(FluxDis o) {
		int res = networkId.compareTo(o.networkId);
		if (res == 0){
			res = name.compareTo(o.name);
		}
		return res;
	}
		
	public Integer getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
		
	public String getNetworkId(){
		return networkId;
	}
	
	public FluxDisStatistics getFluxStatistics(){
		return fluxStatistics;
	}
	
	// getter for the dictionaries
	public HashMap<String, Double> getEdgeFluxes(){
		return edgeFluxes;
	}
	
	public HashMap<String, FluxDirection> getEdgeDirections(){
		return edgeDirections;
	}
	
	public HashMap<String, Double> getNodeFluxes(){
		return nodeFluxes;
	}
	
	public HashMap<String, Double> getNodeConcentrations(){
		return nodeConcentrations;
	}
	
	private String mapToString(Map<?,?> m){
		String t = "null";
		if(m != null){
			t = ((Integer) m.size()).toString();
		}
		return t;
	}
	
	public String toString(){
		
		String info = String.format(
				"---------------------------\n" + 
				"id : %d\n" +
				"name : %s\n" +
				"networkId : %s\n" +
				"nodeFluxes : %s\n" +
				"edgeFluxes : %s\n" +
				"edgeDirections : %s\n" +
				"nodeConcentrations : %s\n" +
				"---------------------------", 
				id, name, networkId, mapToString(nodeFluxes),
									 mapToString(edgeFluxes),
									 mapToString(edgeDirections),
									 mapToString(nodeConcentrations));
		return info;
	}
	
	public String toHTML(){
		String info = String.format(
				  "<b>Flux Distribution</b> <br>"
				+ "<table>"
				+ "<tr><td><i>Id</i></td>           		<td>%d</td></tr>"
				+ "<tr><td><i>Name</i></td>           		<td>%s</td></tr>"
				+ "<tr><td><i>Network Id</i></td>           <td>%s</td></tr>"
				+ "<tr><td><i>Node/Edge Fluxes</i></td>        <td>%s/%s</td></tr>"
				+ "<tr><td><i>Node Concentrations</i></td>        <td>%s</td></tr>"
				+ "</table>", 
				id, name, networkId, mapToString(nodeFluxes),
				 					 mapToString(edgeFluxes),
				 					 mapToString(edgeDirections),
				 					 mapToString(nodeConcentrations));
		return info;
	}
	
	/*
	@Deprecated
	// Set all fields to null.
	public void reset(){
		id = null;
		name = null;
		networkId = null;
		nodeFluxes = null;
		edgeFluxes = null;
		fluxStatistics = null;
		nodeFluxes = null;
	}
	
	
	// edges and nodes have the respective value if it is found in the mapping
	@Deprecated
	public boolean edgeHasFlux(String edgeId){
		return edgeFluxes.containsKey(edgeId);
	}
	@Deprecated
	public boolean edgeHasDirection(String edgeId){
		return edgeDirections.containsKey(edgeId);
	}
	@Deprecated
	public boolean nodeHasFlux(String nodeId){
		return nodeFluxes.containsKey(nodeId);
	}
	@Deprecated
	public boolean nodeHasConcentration(String nodeId){
		return nodeConcentrations.containsKey(nodeId);
	}
	
	// the get methods return the Double value or null if not available
	// The null exceptions have to be handled.
	@Deprecated
	public Double getFluxForEdge(String edgeId){
		if (edgeHasFlux(edgeId)){
			return edgeFluxes.get(edgeId);
		}
		return null;
	}
	
	@Deprecated
	public FluxDirection getFluxDirectionForEdge(String edgeId){
		if (edgeHasDirection(edgeId)){
			return edgeDirections.get(edgeId);
		}
		return null;
	}
	
	@Deprecated
	public Double getFluxForNode(String nodeId){
		if (nodeHasFlux(nodeId)){
			return nodeFluxes.get(nodeId);
		}
		return null;
	}
	
	@Deprecated
	public Double getConcentrationForNode(String nodeId){
		if (nodeHasConcentration(nodeId)){
			return nodeConcentrations.get(nodeId);
		}
		return null;
	}
	*/
}