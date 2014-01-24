package cyfluxviz.io.socket.server;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;

/**
 * Takes the socket information and tries to perform the given socket commands.
 * @author mkoenig
 *
 */
public class SocketExecution {
	/** 
	 * Execute the given line of Client information in Cytoscape.
	 * @param line
	 */
	public static void execute(String line){	
		System.out.println(line);
		
		if (line != null){
			// select_nodes
			if (line.startsWith("select_nodes")){
				String[] tokens = line.split(" ");
				selectNodes(tokens);
			}	
		}
	}
	
	/**
	 * Select the nodes with the given node_ids in the current network.
	 * Unselects all nodes first, only selects the given node identifiers.
	 * 
	 * String[0] = "select_nodes";
	 * @param node_ids
	 */
	public static void selectNodes(String[] node_ids){
		// Unselect
		CyNetwork network = Cytoscape.getCurrentNetwork();
		network.unselectAllNodes();
		network.unselectAllEdges();
		
		// Select
		CyNode node;
		for (String id: node_ids){
			if (id.equals("select_nodes")){
				continue;
			}
			node = (CyNode) Cytoscape.getCyNode(id);
			if (node == null){
				System.out.println("Id not in network: " + id);
				continue;
			}
			network.setSelectedNodeState(node, true);
		}
		//Update view that new selection is visible
		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		view.updateView();
	}
	

}
