package fluxviz.fluxanalysis;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

/**
 * Calculation of FluxStatistics for a given Flux Distribution.
 * Additional information for the visualization.
 * 
 * Flux Statistics is based on the edge fluxes
 * 
 * AbsMean and AbsVar are calculated based on the flux caryying 
 */
public class FluxStatistics {
	/** identifier for the flux distribution */
	private String attributeName;
	private CyAttributes nodeAttributes;
	private CyAttributes edgeAttributes;
	
	// statistical values
	private double min = 0.0;		
	private double max = 0.0;
	private double absMin = 0.0;
	private double absMax = 0.0;
	private double absMean = 0.0;
	private double fluxFraction = 0.0;
	private int reactions = 0;
	private int edgesNum = 0;
	private int zeroReactions = 0;
	
	// set of all edge fluxes
	private Set<Double> edgeFluxes;
	private Histogram fHistogram;
	

	public FluxStatistics(CyAttributes nodeAttributes, CyAttributes edgeAttributes, String attributeName){
		this.nodeAttributes = nodeAttributes;
		this.edgeAttributes = edgeAttributes;
		this.attributeName = attributeName;	
		init();
	}
	
	@SuppressWarnings("unchecked")
	public void init(){	
		// Initialisation
		double absSum = 0.0;
		edgeFluxes = new HashSet<Double>();
		
		// Calculate the statistics for the flux attribute
		List<CyNode> nodeList = Cytoscape.getCyNodesList();
		CyNode node;
		CyEdge edge;
		String nodeId;
		double flux;
		double edgeFlux;
		double stoichiometry;
		
		
		// over all nodes (Flux statistics is based on the edge flux)
		for (Iterator<CyNode> i = nodeList.iterator(); i.hasNext();){
			node = i.next();
			nodeId = node.getIdentifier();
			if (nodeAttributes.getAttribute(nodeId, "sbml type").equals("reaction")){
				// count the reactions
				reactions++;
				// count the zero reactions
				flux = nodeAttributes.getDoubleAttribute(nodeId, attributeName);
				if (flux == 0.0){ 
					zeroReactions++;
					continue;
				}
				
				// over all the adjacent edges
				CyNetwork network = Cytoscape.getCurrentNetwork();
				for (int index: network.getAdjacentEdgeIndicesArray(node.getRootGraphIndex(), true, true, true)){
					edgesNum++;
					edge = (CyEdge) network.getEdge(index);
					try{
						stoichiometry = (Double) edgeAttributes.getAttribute(edge.getIdentifier(), "stoichiometry");
					}
					catch (Exception e){
						//TODO: handle if the stoichiometry information is not available
						//System.out.println("stoichiometry attribute not available");
						stoichiometry = 1.0;
					}
					
					edgeFlux = flux * stoichiometry;
					edgeFluxes.add(edgeFlux);
					
					absSum += Math.abs(edgeFlux);
					if (absMin == 0.0 && edgeFlux != 0.0 ){
						absMin = Math.abs(edgeFlux);
					}
					if (edgeFlux > max){
						max = edgeFlux;
					}
					if (edgeFlux < min){
						min = edgeFlux;
					}
					if (edgeFlux != 0.0 && Math.abs(edgeFlux) < absMin){
						absMin = Math.abs(edgeFlux);
					}
					if (Math.abs(edgeFlux) > absMax){
						absMax = edgeFlux;
					}
				}
			}
		}

		fluxFraction = 1.0 * (reactions - zeroReactions)/reactions;
		absMean = absSum/edgesNum;		
		
		// Generate the Histogramm data
		makeHistogramm();
		
	}
	
	/** 
	 * Generate a new Histogramm for the current FluxStatistics.
	 * TODO: Problems with multiple different localisations for storage of 
	 * the edge flux values can arise. Better to only store the flux values in
	 * one place in the attributes (-> differences between the absolute and the
	 * directed values)
	 */
	public void makeHistogramm(){
		System.out.println("makeHistogramm");
		fHistogram = new Histogram ("Edge Flux Distribution", attributeName, 
				20, Math.floor(min), Math.ceil(max));
		
		// Fill histogram with Edge values
		for (Double val: edgeFluxes) {
			fHistogram.add (val);
		}
	}
	
	
	/** Create HTML output of the statistics. */
	public String toHTML(){
		String out = String.format(
				"<b>[FluxStatistics]</b> <br>" +
				"<table>" +
				"<tr><td><i>min</i></td>           <td>%.3f</td></tr>" +
				"<tr><td><i>max</i></td>           <td>%.3f</td></tr>" +
				"<tr><td><i>absMin</i></td>        <td>%.3f</td></tr>" +
				"<tr><td><i>absMax</i></td>        <td>%.3f</td></tr>" +
				"<tr><td><i>absMean</i></td>       <td>%.3f</td></tr>" +
				"<tr><td><i>fluxFraction</i></td>  <td>%.3f [%d/%d] </td></tr>" +
				"</table>",
				min, max, absMin, absMax, absMean, fluxFraction,
				reactions-zeroReactions, reactions);
		return out;
	}
	
	/**
	 * Print the statistics.
	 */
	public void print(){
		String out = "min: " + min + "\n";
		out += "max: " + max + "\n";
		out += "absMin: " + absMin + "\n";
		out += "absMax: " + absMax + "\n";
		out += "absMean: " + absMean + "\n";
		out += "fluxFraction: [" + (reactions-zeroReactions) + "/"+ reactions + "] "+ fluxFraction +"\n";
		System.out.println(out);
	}
	public void setMin(double min) {
		this.min = min;
	}
	public double getMin() {
		return min;
	}
	public void setMax(double max) {
		this.max = max;
	}
	public double getMax() {
		return max;
	}
	public double getAbsMin(){
		return absMin;
	}
	public double getAbsMax(){
		return absMax;
	}
	public Histogram getFHistogram(){
		return fHistogram;	
	}
}
