package cyfluxviz;

import java.util.HashMap;

import java.util.List;

import browser.AttributeBrowser;
import browser.AttributeBrowserPlugin;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

public class FluxDisCyAttributes {
	
	public static void setCytoscapeAttributesForFluxDistribution(FluxDis fluxDistribution){
		setNodeFluxAttribute(fluxDistribution);
		setEdgeFluxAttribute(fluxDistribution);
		setEdgeDirectionAttribute(fluxDistribution);
	}
	
	public static void deleteFluxVizCytoscapeAttributes(){
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		nodeAttributes.deleteAttribute(CyFluxViz.NODE_FLUX_ATTRIBUTE);
		
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
		edgeAttributes.deleteAttribute(CyFluxViz.EDGE_FLUX_ATTRIBUTE);
		edgeAttributes.deleteAttribute(CyFluxViz.EDGE_DIRECTION_ATTRIBUTE);
	}
	
	private static void setNodeFluxAttribute(FluxDis fluxDistribution){
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		String attName = CyFluxViz.NODE_FLUX_ATTRIBUTE;
		HashMap<String, Double> data = fluxDistribution.getNodeFluxes();
		
		nodeAttributes.deleteAttribute(attName);
		for (String id: data.keySet()){
			nodeAttributes.setAttribute(id, attName, data.get(id));
		}
	}
	
	private static void setEdgeFluxAttribute(FluxDis fluxDistribution){
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
		String attName = CyFluxViz.EDGE_FLUX_ATTRIBUTE;
		HashMap<String, Double> data = fluxDistribution.getEdgeFluxes();	
		
		edgeAttributes.deleteAttribute(attName);
		for (String id: data.keySet()){
			// ! absolute values are set to the edges, direction information 
			//   stored separately - necessary for edge mapping ! 
			double flux = Math.abs(data.get(id));
			edgeAttributes.setAttribute(id, attName, flux);
		}
	}
	
	private static void setEdgeDirectionAttribute(FluxDis fluxDistribution){
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
		String attName = CyFluxViz.EDGE_DIRECTION_ATTRIBUTE;
		HashMap<String, FluxDirection> data = fluxDistribution.getEdgeDirections();
		
		edgeAttributes.deleteAttribute(attName);
		for (String id: data.keySet()){
			edgeAttributes.setAttribute(id, attName, data.get(id).toInt());
		}
	}
	
	public static void selectTableAttributes(){
		AttributeBrowser nodeAttributeBrowser = AttributeBrowserPlugin.getAttributeBrowser(browser.DataObjectType.NODES);
		List<String> selectedNodeAtts = nodeAttributeBrowser.getSelectedAttributes();
		if (! selectedNodeAtts.contains(CyFluxViz.NODE_FLUX_ATTRIBUTE)){
			selectedNodeAtts.add(CyFluxViz.NODE_FLUX_ATTRIBUTE);
			nodeAttributeBrowser.setSelectedAttributes(selectedNodeAtts);
		}
		
		AttributeBrowser edgeAttributeBrowser = AttributeBrowserPlugin.getAttributeBrowser(browser.DataObjectType.EDGES);
		List<String> selectedEdgeAtts = edgeAttributeBrowser.getSelectedAttributes();
		if (! selectedNodeAtts.contains(CyFluxViz.EDGE_DIRECTION_ATTRIBUTE)){
			selectedEdgeAtts.add(CyFluxViz.EDGE_DIRECTION_ATTRIBUTE);
		}
		if (! selectedNodeAtts.contains(CyFluxViz.EDGE_FLUX_ATTRIBUTE)){
			selectedEdgeAtts.add(CyFluxViz.EDGE_FLUX_ATTRIBUTE);
		}
		edgeAttributeBrowser.setSelectedAttributes(selectedEdgeAtts);
	}
}