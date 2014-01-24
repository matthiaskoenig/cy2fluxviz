package cyfluxviz;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import java.util.List;

import browser.AttributeBrowser;
import browser.AttributeBrowserPlugin;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

/** FluxDisCyAttributes handles the changes in the 
 * Cytoscape node and edge attributes when the active FD change.
 * When the selection changes, the respective attributes are set
 * from the selected FluxDistribution.
 * @author mkoenig
 *
 */
public class FluxDisCyAttributes implements Observer {
	
	private static FluxDisCyAttributes uniqueInstance;
	
	public static synchronized FluxDisCyAttributes getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new FluxDisCyAttributes();
		}
		return uniqueInstance;
	}
	
	private FluxDisCyAttributes() {}
	
	@Override
	public void update(Observable obs, Object arg) {
		if (obs instanceof FluxDisCollection) {
			// No active FluxDis
			if (arg == null) {
				deleteCyFluxVizCytoscapeAttributes();
			} else {
				FluxDis fd = (FluxDis) arg;
				setCytoscapeAttributesForFluxDistribution(fd);
			}
		}
	}
	
	/** Deletes the Cytoscape attributes. */
	private static void deleteCyFluxVizCytoscapeAttributes(){
		CyAttributes nAtts = Cytoscape.getNodeAttributes();
		nAtts.deleteAttribute(CyFluxVizPlugin.NODE_FLUX_ATTRIBUTE);
		nAtts.deleteAttribute(CyFluxVizPlugin.NODE_CONCENTRATION_ATTRIBUTE);
		
		CyAttributes eAtt = Cytoscape.getEdgeAttributes();
		eAtt.deleteAttribute(CyFluxVizPlugin.EDGE_FLUX_ATTRIBUTE);
		eAtt.deleteAttribute(CyFluxVizPlugin.EDGE_DIRECTION_ATTRIBUTE);
	}
	
	/** Updates the node and edge attributes to the given FluxDis values. */
	private static void setCytoscapeAttributesForFluxDistribution(FluxDis fd){
		setNodeFluxAttribute(fd);
		setEdgeFluxAttribute(fd);
		setEdgeDirectionAttribute(fd);
		setNodeConcentrationAttribute(fd);
	}
	
	
	private static void setNodeFluxAttribute(FluxDis fluxDistribution){
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		String attName = CyFluxVizPlugin.NODE_FLUX_ATTRIBUTE;
		HashMap<String, Double> data = fluxDistribution.getNodeFluxes();
		nodeAttributes.deleteAttribute(attName);
		for (String id: data.keySet()){
			nodeAttributes.setAttribute(id, attName, data.get(id));
		}
	}
	
	private static void setNodeConcentrationAttribute(FluxDis fluxDistribution){
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		String attName = CyFluxVizPlugin.NODE_CONCENTRATION_ATTRIBUTE;
		HashMap<String, Double> data = fluxDistribution.getNodeConcentrations();
		
		nodeAttributes.deleteAttribute(attName);
		for (String id: data.keySet()){
			nodeAttributes.setAttribute(id, attName, data.get(id));
		}
	}
	
	private static void setEdgeFluxAttribute(FluxDis fluxDistribution){
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
		String attName = CyFluxVizPlugin.EDGE_FLUX_ATTRIBUTE;
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
		String attName = CyFluxVizPlugin.EDGE_DIRECTION_ATTRIBUTE;
		HashMap<String, FluxDirection> data = fluxDistribution.getEdgeDirections();
		
		edgeAttributes.deleteAttribute(attName);
		for (String id: data.keySet()){
			edgeAttributes.setAttribute(id, attName, data.get(id).toInt());
		}
	}
	
	
	/** Initial selection of the CyFluxViz table attributes. 
	 * TODO: possible bug in CySBML (overwriting already selected attributes) 
	 * TODO: what happens to the deleted attributes ? are the names still available.
	 * 			This could explain the problems with the visibility after every deletion.
	 */
	public static void selectCyFluxVizTableAttributes(){
		AttributeBrowser nodeAttributeBrowser = AttributeBrowserPlugin.getAttributeBrowser(browser.DataObjectType.NODES);
		List<String> selectedNodeAtts = nodeAttributeBrowser.getSelectedAttributes();
		
		if (! selectedNodeAtts.contains(CyFluxVizPlugin.NODE_FLUX_ATTRIBUTE)){
			selectedNodeAtts.add(CyFluxVizPlugin.NODE_FLUX_ATTRIBUTE);
			
		}
		if (! selectedNodeAtts.contains(CyFluxVizPlugin.NODE_CONCENTRATION_ATTRIBUTE)){
			selectedNodeAtts.add(CyFluxVizPlugin.NODE_CONCENTRATION_ATTRIBUTE);
		}
		nodeAttributeBrowser.setSelectedAttributes(selectedNodeAtts);
			
		AttributeBrowser edgeAttributeBrowser = AttributeBrowserPlugin.getAttributeBrowser(browser.DataObjectType.EDGES);
		List<String> selectedEdgeAtts = edgeAttributeBrowser.getSelectedAttributes();
		if (! selectedNodeAtts.contains(CyFluxVizPlugin.EDGE_DIRECTION_ATTRIBUTE)){
			selectedEdgeAtts.add(CyFluxVizPlugin.EDGE_DIRECTION_ATTRIBUTE);
		}
		if (! selectedNodeAtts.contains(CyFluxVizPlugin.EDGE_FLUX_ATTRIBUTE)){
			selectedEdgeAtts.add(CyFluxVizPlugin.EDGE_FLUX_ATTRIBUTE);
		}
		edgeAttributeBrowser.setSelectedAttributes(selectedEdgeAtts);
	}

}