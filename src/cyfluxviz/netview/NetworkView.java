package cyfluxviz.netview;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import cyfluxviz.FluxDis;
import cyfluxviz.gui.CyFluxVizPanel;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;

public class NetworkView {
	
	private static FluxDis fd;
	private static Set<CyNode> visibleNodes;
	private static Set<CyEdge> visibleEdges;
	
	private static boolean isFluxSubnet;
	private static boolean isAttributeSubnet;
	
    public static void updateNetworkViewsForFluxDistribution(FluxDis fluxDistribution){
    	fd = fluxDistribution;
    	isFluxSubnet = isFluxSubNetwork();
    	isAttributeSubnet = isAttributeSubNetwork();
    	
    	if (fd != null){
    		String networkId = fd.getNetworkId();
    		CyNetwork network = Cytoscape.getNetwork(networkId);
    		if (network != null){
    			List<CyNetworkView> views = FluxDistributionView.getCyNetworkViewsForNetworkId(networkId);
    			for (CyNetworkView view: views){
    				updateNetworkView(view);		
    			}
    		}
    	} else {
    		CyNetworkView view = Cytoscape.getCurrentNetworkView();
    		NetworkViewTools.showAllNodesAndEdgesInView(view);
    		view.updateView();
    	}
    }
    
    //// Decide which view is generated depending on the settings ////
	public static void updateNetworkView(CyNetworkView view){
    	// [1] Calculate the visible nodes and edges
		if (isFluxSubnet && !isAttributeSubnet){
			calculateFluxSubnet();
			updateVisibiltyInView(view);
		}else if (!isFluxSubnet && isAttributeSubnet){
			calculateAttributeSubnet();
			updateVisibiltyInView(view);
		}else if (isFluxSubnet && isAttributeSubnet){
			calculateAttributeFluxSubnet();
			updateVisibiltyInView(view);
		}else {
			NetworkViewTools.showAllNodesAndEdgesInView(view);
		}		
		view.updateView();
	}
    public static boolean isFluxSubNetwork(){
    	 CyFluxVizPanel panel = CyFluxVizPanel.getInstance();
    	 return panel.getFluxSubnetCheckbox().isSelected();
    }
    public static boolean isAttributeSubNetwork(){
   	 CyFluxVizPanel panel = CyFluxVizPanel.getInstance();
   	 return panel.getAttributeSubnetCheckbox().isSelected();
    }
    
    // Visibility of nodes is updated
    public static void updateVisibiltyInView(CyNetworkView view){
    	NetworkViewTools.hideAllNodesAndEdgesInView(view);
    	NetworkViewTools.showNodesInView(visibleNodes, view);
    	NetworkViewTools.showEdgesInView(visibleEdges, view);
    }
    
    // Calculation of Flux subnetwork
	public static void calculateFluxSubnet(){	
    	HashMap<String, Double> edgeFluxes = fd.getEdgeFluxes();
    	
    	Set<CyEdge> visEdges = new HashSet<CyEdge>();
    	Set<CyNode> visNodes = new HashSet<CyNode>();
        
    	@SuppressWarnings("unchecked")
		List<CyEdge> edges = Cytoscape.getCyEdgesList();
		for (CyEdge edge: edges){
			String id = edge.getIdentifier();
			if (edgeFluxes.containsKey(id) && edgeFluxes.get(id) != 0.0){
				visEdges.add(edge);
				visNodes.add((CyNode) edge.getSource());
				visNodes.add((CyNode) edge.getTarget());
			}
		}
		visibleNodes = visNodes;
		visibleEdges = visEdges;
    }
	
	
	// Calculation of attribute subnetwork
	public static void calculateAttributeSubnet(){	
		CyFluxVizPanel panel = CyFluxVizPanel.getInstance();
		String selectedAttribute = getSelectedAttributeInPanel(panel);
		
    	Set<CyNode> visNodes = new HashSet<CyNode>();
        if (selectedAttribute != null){
        	visNodes = getVisibleNodesBasedOnAttribute(selectedAttribute,
        								 getSelectedAttributeValuesInPanel(panel), 
        								 isNullAttributeVisibleInPanel(panel));
        }
        visibleNodes = visNodes;
        visibleEdges = NetworkViewTools.getEdgesBetweenNodes(visNodes);
    }
	
	private static String getSelectedAttributeInPanel(CyFluxVizPanel panel){
		return (String) panel.getNodeAttributeComboBox().getSelectedItem();
	}
	
	private static Set<Object> getSelectedAttributeValuesInPanel(CyFluxVizPanel panel){
		Set<Object> selectedValues = new HashSet<Object>();
		for (Object obj: panel.getNodeAttributeList().getSelectedValues()){
	            selectedValues.add(obj);
	     }
		return selectedValues;
	}
	
	private static boolean isNullAttributeVisibleInPanel(CyFluxVizPanel panel){
		return panel.getNullVisibleCheckbox().isSelected();
	}
	
    public static Set<CyNode> getVisibleNodesBasedOnAttribute(String attributeName, 
    														 Set selected, Boolean nullVisible){
    	Set<CyNode> visNodes = new HashSet<CyNode>();
    	CyAttributes nodeAttrs = Cytoscape.getNodeAttributes();
        @SuppressWarnings("unchecked")
		List<CyNode> nodeList = Cytoscape.getCyNodesList();
		for (CyNode node: nodeList){
			String id = node.getIdentifier();
			if (selected.contains(nodeAttrs.getAttribute(id, attributeName))){
				visNodes.add(node);
			}
			else if (nullVisible && nodeAttrs.getAttribute(id, attributeName)==null){
				visNodes.add(node);
			}
		}
		return visNodes;
    }
	
	public static void calculateAttributeFluxSubnet(){
		calculateFluxSubnet();
		
		CyFluxVizPanel panel = CyFluxVizPanel.getInstance();
		String selectedAttribute = getSelectedAttributeInPanel(panel);
	    Set<CyNode> visNodes = new HashSet<CyNode>();
	    Set<CyEdge> visEdges = new HashSet<CyEdge>();
	    if (selectedAttribute != null){
	        visNodes = getVisibleNodesBasedOnAttribute(selectedAttribute,
	        								 getSelectedAttributeValuesInPanel(panel), 
	        								 isNullAttributeVisibleInPanel(panel));
	        visEdges = NetworkViewTools.getEdgesBetweenNodes(visNodes);
	    }
	    // Calculate the intersection
	    visibleNodes.retainAll(visNodes);
	    visibleEdges.retainAll(visEdges);
	}
}
