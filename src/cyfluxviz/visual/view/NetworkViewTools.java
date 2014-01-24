package cyfluxviz.visual.view;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;

/** Set of helper functions to change the visibility of nodes
 * and edges in the NetworkView. 
 * Helping in generating the various views depending on the
 * selected settings.
 * @author mkoenig
 *
 */
public class NetworkViewTools {
  
	/** Get the views associated to the network.
	 * Normally only one view is associated with a network.
	 * But child views can be generated, which are associated with the current network.
	 * @param networkId
	 * @return
	 */
    public static List<CyNetworkView> getViewsForNetwork(final String networkId){
    	List<CyNetworkView> views = new LinkedList<CyNetworkView> ();
    	HashMap<String, CyNetworkView> netViewMap = (HashMap<String, CyNetworkView>) 
    														Cytoscape.getNetworkViewMap();
    	
    	// Search all views for derived networks from the core network
    	String coreId = getParentNetworkPrefix(networkId);
    	for (CyNetworkView view: netViewMap.values()){
    		String viewNetId = view.getNetwork().getTitle();
    		if (viewNetId.startsWith(coreId)){
    			views.add(view);
    		}
    	}
    	return views;
    }
    
    /** Removes all the '--child*' parts of the network id. 
     * Necessary if the currently selected network is a child network.
     * @param networkId
     * @return
     */
    private static String getParentNetworkPrefix(final String networkId){
    	String coreId = networkId;
    	String pattern = "--child";
    	int index = networkId.indexOf(pattern);
    	if (index != -1){
    		coreId = coreId.substring(0, index);
    		// System.out.println("CyFluxViz[INFO]: core network : " + networkId + " -> " + coreId);
    	}
    	return coreId;
    }
    
	
    public static void showNodesInView(Collection<CyNode> nodes, CyNetworkView view){
    	for (CyNode node: nodes){
    		view.showGraphObject(view.getNodeView(node));
    	}
    }
    public static void showEdgesInView(Collection<CyEdge> edges, CyNetworkView view){
    	for (CyEdge edge: edges){
    		view.showGraphObject(view.getEdgeView(edge));
    	}
    }
    public static void showEdgesBetweenNodesInView(Collection<CyNode> nodes, CyNetworkView view){
    	for (CyEdge edge: getEdgesBetweenNodes(nodes)){
    		view.showGraphObject(view.getEdgeView(edge));
    	}
	}	
    
    public static void showAllNodesAndEdgesInCurrentView(){
    	showAllNodesAndEdgesInView(Cytoscape.getCurrentNetworkView());
    }
    
	@SuppressWarnings("unchecked")
	public static void showAllNodesAndEdgesInView(CyNetworkView view){
        List<CyNode> nodeList = Cytoscape.getCyNodesList();
        List<CyEdge> edgeList = Cytoscape.getCyEdgesList();
        for (CyNode node: nodeList){
            view.showGraphObject(view.getNodeView(node));
        }
        for (CyEdge edge: edgeList){
        	view.showGraphObject(view.getEdgeView(edge));
        }
    }
	
	public static void hideAllNodesAndEdgesInCurrentView(){
		hideAllNodesAndEdgesInView(Cytoscape.getCurrentNetworkView());
	}
	
	@SuppressWarnings("unchecked")
	public static void hideAllNodesAndEdgesInView(CyNetworkView view){
        List<CyNode> nodeList = Cytoscape.getCyNodesList();
        List<CyEdge> edgeList = Cytoscape.getCyEdgesList();
        for (CyNode node: nodeList){
            view.hideGraphObject(view.getNodeView(node));
        }
        for (CyEdge edge: edgeList){
        	view.hideGraphObject(view.getEdgeView(edge));
        }
    }
    
    public static Set<CyEdge> getEdgesBetweenNodes(Collection<CyNode> nodes){
    	@SuppressWarnings("unchecked")
    	List<CyEdge> edgesInCytoscape = Cytoscape.getCyEdgesList();
    	Set<CyEdge> edges = new HashSet<CyEdge>();
    	for (CyEdge edge: edgesInCytoscape){
    		if (nodes.contains((CyNode)edge.getSource()) &&
				nodes.contains((CyNode)edge.getTarget()) ){
    			edges.add(edge);
    		}
    	}
    	return edges;
    }
    
    public static void main(String[] args){
    	System.out.println("Test getting the core parent Id:");
    	String id = "network";
    	String cid = getParentNetworkPrefix(id);
    	System.out.println(id + " -> " + cid);
    	
    	id = "network--child";
    	cid = getParentNetworkPrefix(id);
    	System.out.println(id + " -> " + cid);
    }
    
}
