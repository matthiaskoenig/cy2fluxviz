package cyfluxviz;

import java.util.HashMap;

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
}