package cyfluxviz;

import cyfluxviz.gui.PanelDialogs;

public class FluxStatistics {
	private FluxDistribution fluxDistribution;
	
	// statistical values
	private double min = 0.0;		
	private double max = 0.0;
	private double absMin = 0.0;
	private double absMax = 0.0;
	private double absMean = 0.0;
	private double fluxFraction = 0.0;
	private int reactions = 0;
	private int zeroReactions = 0;

	public FluxStatistics(FluxDistribution fluxDistribution){
		this.fluxDistribution = fluxDistribution;
		calculateStatistics();
	}
	
	private void calculateStatistics(){
		System.out.println("calculateStatistics not implemented !");
	}
	
	/*
	private void calculateStatistics(){					
		// over all nodes (Flux statistics is based on the edge flux)
		for (Iterator<CyNode> i = nodeList.iterator(); i.hasNext();){
			node = i.next();
			nodeId = node.getIdentifier();
			if (nodeAttributes.getAttribute(nodeId, CySBMLConstants.ATT_TYPE).equals(CySBMLConstants.NODETYPE_REACTION)){
				
				// count reactions and reactions with zero flux
				reactions++;
				flux = nodeAttributes.getDoubleAttribute(nodeId, attributeName);
				if (flux == 0.0){ 
					zeroReactions++;
					continue;
				}
				
				// adjacent edges
				CyNetwork network = Cytoscape.getCurrentNetwork();
				for (int index: network.getAdjacentEdgeIndicesArray(node.getRootGraphIndex(), true, true, true)){
					edgesNum++;
					edge = (CyEdge) network.getEdge(index);
					try{
						stoichiometry = (Double) edgeAttributes.getAttribute(edge.getIdentifier(), CySBMLConstants.ATT_STOICHIOMETRY);
					}
					catch (Exception e){
						// Stoichiometry information is not available
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
		
	}
	*/
	
	public double getMin() {
		return min;
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
	
	public String toHTML(){
		String out = String.format(
				PanelDialogs.getHTMLHeader() +
				"<b>Properties of selected flux distribution</b> <br>" +
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
	
	public String toString(){
		String output = String.format(
				"min: %s\n" +
				"max: %s\n" +
				"absMin: %s\n" +
				"absMax: %s\n" +
				"absMean: %s\n" +
				"fluxFraction: [%s/%s] %s",
				min, max, absMin, absMax, absMean, fluxFraction,
				reactions-zeroReactions, reactions, fluxFraction);
		return output;
	}
}
