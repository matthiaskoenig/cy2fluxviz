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
	
    public static void updateNetworkViewsForFluxDistribution(FluxDis fluxDistribution){
    	fd = fluxDistribution;
    	
    	if (fd != null){
    		String networkId = fd.getNetworkId();
    		CyNetwork network = Cytoscape.getNetwork(networkId);
    		if (network != null){
    			List<CyNetworkView> views = NetworkViewTools.getCyNetworkViewsForNetworkId(networkId);
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
    
	public static void updateNetworkView(CyNetworkView view){
    	calculateFluxSubnet();
		if (isAttributeSubNetwork()){
			calculateAttributeSubnet();
		}
		updateVisibiltyInView(view);		
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
    
    public static void updateVisibiltyInView(CyNetworkView view){
    	NetworkViewTools.hideAllNodesAndEdgesInView(view);
    	NetworkViewTools.showNodesInView(visibleNodes, view);
    	NetworkViewTools.showEdgesInView(visibleEdges, view);
    }
    
	public static void calculateFluxSubnet(){	
    	HashMap<String, Double> edgeFluxes = fd.getEdgeFluxes();
    	visibleEdges = new HashSet<CyEdge>();
    	visibleNodes = new HashSet<CyNode>();
        
    	@SuppressWarnings("unchecked")
		List<CyEdge> edges = Cytoscape.getCyEdgesList();
		for (CyEdge edge: edges){
			String id = edge.getIdentifier();
			if (!isFluxSubNetwork()){
				addEdgeWithNodesToVisible(edge);
			}else{ 
				if (edgeFluxes.containsKey(id) && edgeFluxes.get(id) != 0.0){
				addEdgeWithNodesToVisible(edge);
				}
			}
		}
    }
	
	private static void addEdgeWithNodesToVisible(CyEdge edge){
		visibleEdges.add(edge);
		visibleNodes.add((CyNode) edge.getSource());
		visibleNodes.add((CyNode) edge.getTarget());
	}
	
	public static void calculateAttributeSubnet(){	
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
	
}
