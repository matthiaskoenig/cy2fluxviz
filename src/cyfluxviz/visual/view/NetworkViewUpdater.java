package cyfluxviz.visual.view;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cyfluxviz.CyFluxVizPlugin;
import cyfluxviz.FluxDis;
import cyfluxviz.gui.CyFluxVizPanel;
import cyfluxviz.util.CyNetworkUtils;
import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;

/** In this class the NetworkViews are updated based on the 
 * selected FluxDistribution and the settings in the CyFluxViz panel.
 * 
 * @author mkoenig
 *
 */
public class NetworkViewUpdater {
	
	private FluxDis fd;
	private Collection<CyNode> visibleNodes;
	private Collection<CyEdge> visibleEdges;
	
	
	public NetworkViewUpdater(FluxDis fd){
		this.fd = fd;
	}
	
	/** Updates all views associated with the network for the given FluxDistribution. */
    public void updateNetworkViewsForFluxDistribution(){
    	String networkId = null;
    	if (fd == null){
    		// Get the current network Id
    		networkId = CyNetworkUtils.getCurrentNetworkId();
    		if (networkId == null){
    			return;
    		}
    			
    	} else {
    		networkId = fd.getNetworkId();
        }
    	for (CyNetworkView view: NetworkViewTools.getViewsForNetwork(networkId)){
    		updateNetworkView(view);
    	}
    }
        
	private void updateNetworkView(CyNetworkView view){
    	calculateFluxSubnet();
		if (isAttributeSubNetwork()){
			calculateAttributeSubnet();
		}
		updateVisibiltyInView(view);		
		view.updateView();
	}
	

    /** Apply VisualStyle changes to view. */
	public static void applyVizMapChangesToView(CyNetworkView view){
		if (view == null){
			return;
		}
	    view.redrawGraph(false,true);
	    view.updateView();
	}
    
	
    private boolean isFluxSubNetwork(){
    	 CyFluxVizPanel panel = CyFluxVizPanel.getInstance();
    	 return panel.getFluxSubnetCheckbox().isSelected();
    }
    
    private boolean isAttributeSubNetwork(){
   	 CyFluxVizPanel panel = CyFluxVizPanel.getInstance();
   	 return panel.getAttributeSubnetCheckbox().isSelected();
    }
    
    private void updateVisibiltyInView(CyNetworkView view){
    	NetworkViewTools.hideAllNodesAndEdgesInView(view);
    	NetworkViewTools.showNodesInView(visibleNodes, view);
    	NetworkViewTools.showEdgesInView(visibleEdges, view);
    }
    
	private void calculateFluxSubnet(){	
    	if (fd == null || !isFluxSubNetwork()){
    		if (fd == null){
    			CyFluxVizPlugin.LOGGER.info("calculateFluxSubnet -> no FluxDistribution selected");
    		}
    		visibleNodes = Cytoscape.getCyNodesList();
    		visibleEdges = Cytoscape.getCyEdgesList();
    		return;
    	}
    	
    	visibleEdges = new HashSet<CyEdge>();
    	visibleNodes = new HashSet<CyNode>();
    	HashMap<String, Double> edgeFluxes = fd.getEdgeFluxes();
		List<CyEdge> edges = Cytoscape.getCyEdgesList();
		for (CyEdge edge: edges){
			String id = edge.getIdentifier();
			if (edgeFluxes.containsKey(id) && edgeFluxes.get(id) != 0.0){
				addEdgeWithNodesToVisible(edge);
			}
		}
    }
	
	private void addEdgeWithNodesToVisible(CyEdge edge){
		visibleEdges.add(edge);
		visibleNodes.add((CyNode) edge.getSource());
		visibleNodes.add((CyNode) edge.getTarget());
	}
	
	private void calculateAttributeSubnet(){	
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
		for (Object obj: panel.getNodeAttributeList().getSelectedValuesList()){
	            selectedValues.add(obj);
	     }
		return selectedValues;
	}
	
	private static boolean isNullAttributeVisibleInPanel(CyFluxVizPanel panel){
		return panel.getNullVisibleCheckbox().isSelected();
	}
	
    private static Set<CyNode> getVisibleNodesBasedOnAttribute(String attributeName, 
    														 Set<Object> selected, Boolean nullVisible){
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
