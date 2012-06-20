package cyfluxviz.netview;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cyfluxviz.CyFluxViz;
import cyfluxviz.FluxDis;
import cyfluxviz.FluxDisCollection;
import cyfluxviz.FluxDisStatistics;
import cyfluxviz.gui.CyFluxVizPanel;
import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.VisualStyle;

public class NetworkViewTools {
	
	// CHANGE VISIBILITY OF NODES AND EDGES //
	
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
    
    public static boolean currentVisualStyleIsFluxVizVisualStyle(){
    	VisualStyle currentVS = Cytoscape.getVisualMappingManager().getVisualStyle();
    	VisualStyle fluxvizVS = CyFluxViz.getViStyle();
    	boolean equal = false;
    	if (fluxvizVS != null){
    		equal = fluxvizVS.getName().equals(currentVS.getName());
    	}
    	return equal;
    }
    
	public static void setFluxVizVisualStyleForCurrentView(){
		setFluxVizVisualStyleForView(Cytoscape.getCurrentNetworkView());
	}
	
    public static void setFluxVizVisualStyleForView(CyNetworkView view){
        if (!NetworkViewTools.currentVisualStyleIsFluxVizVisualStyle()){
        	view.setVisualStyle(CyFluxViz.getViStyle().getName());
            Cytoscape.getVisualMappingManager().setVisualStyle(CyFluxViz.getViStyle());	
        }
    }
    
    public static void updateFluxDistributionInformation(){
    	FluxDisCollection fdCollection = FluxDisCollection.getInstance();
    	FluxDis fd = fdCollection.getActiveFluxDistribution();
        FluxDisStatistics fdStatistics = fd.getFluxStatistics();
    	
        String info = fd.toHTML(); 
        info += fdStatistics.toHTML();
        
        CyFluxVizPanel panel = CyFluxVizPanel.getInstance();
        panel.updateInfoPaneHTMLText(info);
        panel.selectInfoPane();
    }
}
