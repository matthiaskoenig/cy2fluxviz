package cyfluxviz;

import java.util.HashMap;
import java.util.UUID;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

/* A FluxDistribution is HashMap of Double Values for the Edges in the Network.
 * Fluxes are > 0 if the flux is in direction of definition of the edge, < 0 
 * otherwise. 
 * 
 * Flux Distribution has to have a name and must be associated to a network;
 * 
 * Different importers of Fluxes which all generate the same type of Flux Distribution.
 * - import general flux distribution
 * - import metabolic network flux distribution (using given stoichiometry for the network).
 * 
 * - test methods for the flux distributions 
 *
 * Only in the importers the 
 * 
 * */
public class FluxDis {
	public final static String ATT_STOICHIOMETRY = "sbml stoichiometry";
	public final static String ATT_TYPE = "sbml type";
	public final static String NODE_TYPE_REACTION = "reaction";
	
	private UUID id;
	private String name;
	private String networkId;
	private HashMap<String, Double> edgeFluxes;
	private HashMap<String, FluxDirection> edgeDirections;
	private HashMap<String, Double> nodeFluxes;
	
	private FluxDisStatistics fluxStatistics;
	
	public FluxDis(String name, String networkId, 
							HashMap<String, Double> nodeFluxes,
							HashMap<String, Double> edgeFluxes,
							HashMap<String, FluxDirection> edgeDirections
							){
		id = UUID.randomUUID();
		this.name = name;
		this.networkId = networkId;
		if (hasValidNetworkId()){
			
			this.nodeFluxes = nodeFluxes;
			this.edgeFluxes = edgeFluxes;
			this.edgeDirections = edgeDirections;
			fluxStatistics = new FluxDisStatistics(this);
		} else {
			reset();
		}
		System.out.println("CyFluxViz[INFO] -> FluxDistribution created");
		System.out.println(this.toString());
	}
	
	public String toString(){
		String info = String.format(
				"id : %s\n" +
				"name : %s\n" +
				"networkId : %s\n" +
				"nodeFluxes : %s\n" +
				"edgeFluxes : %s\n" +
				"edgeDirections : %s\n", 
				id, name, networkId, nodeFluxes.size(), edgeFluxes.size(), edgeDirections.size());
		
		return info;
	}
	
	public String toHTML(){
		String info = String.format(
				  "<b>Flux Distribution</b> <br>"
				+ "<table>"
				+ "<tr><td><i>Id</i></td>           		<td>%s</td></tr>"
				+ "<tr><td><i>Name</i></td>           		<td>%s</td></tr>"
				+ "<tr><td><i>Network Id</i></td>           <td>%s</td></tr>"
				+ "<tr><td><i>Node/Edge Fluxes</i></td>        <td>%d/%d</td></tr>"
				+ "</table>", 
				id, name, networkId, nodeFluxes.size(), edgeFluxes.size(), edgeDirections.size());
		return info;
	}
	
	
	private void reset(){
		id = null;
		name = null;
		networkId = null;
		nodeFluxes = null;
		edgeFluxes = null;
		fluxStatistics = null;
	}
	
	public String getId(){
		return id.toString();
	}
	public boolean isSetId(){
		return (id != null);
	}
	
	public String getName(){
		return name;
	}
	public boolean isSetName(){
		return (name != null);
	}
	
	public String getNetworkId(){
		return networkId;
	}
	
	public FluxDisStatistics getFluxStatistics(){
		return fluxStatistics;
	}
	
	public HashMap<String, Double> getEdgeFluxes(){
		return edgeFluxes;
	}
	
	public HashMap<String, FluxDirection> getEdgeDirections(){
		return edgeDirections;
	}
	
	public HashMap<String, Double> getNodeFluxes(){
		return nodeFluxes;
	}
	
	public boolean edgeHasFlux(Integer edgeId){
		return (getFluxForEdge(edgeId) != 0.0);
	}
	public boolean nodeHasFlux(Integer nodeId){
		return (getFluxForNode(nodeId) != 0.0);
	}
	
	public double getFluxForEdge(Integer edgeId){
		double flux = 0.0;
		if (edgeFluxes.containsKey(edgeId)){
			flux = edgeFluxes.get(edgeId);
		}
		return flux;
	}
	public double getFluxForNode(Integer nodeId){
		double flux = 0.0;
		if (nodeFluxes.containsKey(nodeId)){
			flux = nodeFluxes.get(nodeId);
		}
		return flux;
	}
		
	public boolean isValid(){
		boolean valid = true;
		// id is set
		if (id == null){
			valid = false;
		}
		// networkId corresponds to existing Network
		if (! hasValidNetworkId()){
			valid = false;
		}
		return valid;
	}
	
	private boolean hasValidNetworkId(){
		return ( Cytoscape.getNetwork(networkId) != null);
	}
	public CyNetwork getCyNetwork(){
		return Cytoscape.getNetwork(networkId);
	}
	
}
