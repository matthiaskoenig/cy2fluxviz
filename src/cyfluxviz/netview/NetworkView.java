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
    
	public static void updateNetworkView(CyNetworkView view){
    	// [1] Calculate the visible nodes and edges
		if (isFluxSubnet && !isAttributeSubnet){
			calculateFluxSubnet();
			updateVisibiltyInView(view);
		}else if (!isFluxSubnet && isAttributeSubnet){
			calculateAttributeSubnet();
			updateVisibiltyInView(view);
		}else if (isFluxSubnet && isAttributeSubnet){
			calculateFluxSubnet();
			calculateAttributeSubnet();
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
    
    public static void updateVisibiltyInView(CyNetworkView view){
    	NetworkViewTools.hideAllNodesAndEdgesInView(view);
    	NetworkViewTools.showNodesInView(visibleNodes, view);
    	NetworkViewTools.showEdgesInView(visibleEdges, view);
    }
    
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
	
	public static void calculateAttributeSubnet(){	
		// Get attribute information from the panel
		CyFluxVizPanel panel = CyFluxVizPanel.getInstance();
		boolean isNullVisible = panel.getNullVisibleCheckbox().isSelected();
		
		// Selected attribute
		String nodeAttribute = (String) panel.getNodeAttributeComboBox().getSelectedItem();
	    // Selected values
        Set<Object> selectedValues = new HashSet<Object>();
        for (Object obj: panel.getNodeAttributeList().getSelectedValues()){
            selectedValues.add(obj);
        }
        if (nodeAttribute != null){
        	calculateNodeAttributeSubnet(nodeAttribute, selectedValues, isNullVisible);
        }
    }
	
    
    public static void viewNodeAttributeSubnet(String attributeName, Set selected, Boolean nullVisible){
    	CyAttributes nodeAttrs = Cytoscape.getNodeAttributes();
    	byte attrType = nodeAttrs.getType(attributeName);
    	if (attrType != CyAttributes.TYPE_STRING){
    		System.out.println("CyFluxViz[INFO] -> Subnetworks only based on String attributes possible");
    		return;
    	}
    	
        //Calculate the set of visible nodes based on the attribute
        // and the set of selected values which should be displayed
        Set<CyNode> visibleNodes = new HashSet<CyNode>(); 
        List<CyNode> nodeList = Cytoscape.getCyNodesList();
		for (CyNode node: nodeList){
			String id = node.getIdentifier();
			if (selected.contains(nodeAttrs.getAttribute(id, attributeName))){
				visibleNodes.add(node);
			}
			else if (nullVisible==true && nodeAttrs.getAttribute(id, attributeName)==null){
				visibleNodes.add(node);
			}
		}
		
		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		NetworkViewTools.hideAllNodesAndEdgesInView(view);
		NetworkViewTools.showNodesInView(visibleNodes, view);
		NetworkViewTools.showEdgesBetweenNodesInView(visibleNodes, view);
		
		view.updateView();
		
		
		
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
	
	
	
    
    /*
    @SuppressWarnings("unchecked")
	public static void viewFluxAttributeSubnet(){
    	String edgeAttribute = CyFluxViz.EDGE_FLUX_ATTRIBUTE;
    	
    	CyNetworkView view = Cytoscape.getCurrentNetworkView();
        NetworkViewTools.hideAllNodesAndEdgesInView(view);
		
        List<CyNode> nodeList = Cytoscape.getCyNodesList();
    	List<CyEdge> edgeList = Cytoscape.getCyEdgesList();
    	
		//calculate flux subnetwork
        Set<CyEdge> fluxEdges = new HashSet<CyEdge>(); 
        CyAttributes edgeAttrs = Cytoscape.getEdgeAttributes();
		for (CyEdge edge: edgeList){
			if (edgeAttrs.getDoubleAttribute(edge.getIdentifier(), edgeAttribute) != 0.0){
				fluxEdges.add(edge);
			}
		}
        // calculate the set of visible nodes based on the attribute condition
    	CyFluxVizPanel panel = CyFluxViz.getFvPanel();
        String attribute = (String) panel.getNodeAttributeComboBox().getSelectedItem();
        
    	boolean nullVisible = panel.getNullVisibleCheckbox().isSelected();
        Set<Object> selected = new HashSet<Object>();
        for (Object obj: panel.getNodeAttributeList().getSelectedValues()){
            selected.add(obj);
        }
        CyAttributes nodeAttrs = Cytoscape.getNodeAttributes();
    	
        Set<CyNode> visibleNodes = new HashSet<CyNode>(); 
		for (CyNode node: nodeList){
			String id = node.getIdentifier();
			
			if (selected.contains(nodeAttrs.getAttribute(id, attribute))){
				visibleNodes.add(node);
			}
			else if (nullVisible==true && nodeAttrs.getAttribute(id, attribute)==null){
				visibleNodes.add(node);
			}
		}

		//2.2 all nodes and edges of the flux subnetwork visible
		CyNode node;
		boolean source_visible;
		boolean target_visible;
		for (CyEdge edge: fluxEdges){
			source_visible = false;
			target_visible = false;
			//show source and target nodes if attribute fullfilled
			node = (CyNode)edge.getSource();
			if (visibleNodes.contains(node)){
				view.showGraphObject(view.getNodeView(node));
				source_visible = true;
			}
			node = (CyNode)edge.getTarget();
			if (visibleNodes.contains(node)){
				view.showGraphObject(view.getNodeView(node));
				target_visible = true;
			}
			//show flux edge
			if (source_visible && target_visible){
				view.showGraphObject(view.getEdgeView(edge));
			}	
		}
		view.updateView();		
    }
    */
}
