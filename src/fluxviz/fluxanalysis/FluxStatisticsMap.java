package fluxviz.fluxanalysis;

import java.util.HashMap;
import java.util.Map;

import cytoscape.Cytoscape;
import fluxviz.CyFluxVizPlugin;

/**
 * Handles the flux statistics information for the different flux attributes.
 * TODO: if val files are loaded or flux attributes are changed also the 
 * 		fluxStatistics has to be recalculated
 * -> after loading of new attributes the Statistics is updated (but changes
 * 		in network attributes are not handled -> TODO: listen to changes in 
 * 		the attributes of the network
 * @author mkoenig
 *
 */
public class FluxStatisticsMap {
	/** Set of the flux attribute names */
	public Map<String, FluxStatistics> statisticsMap;
	
	// global values for the flux mapping
	private double absMin = 0.0;
	private double absMax = 0.0;
	
	
	/**
	 * Generates a new FluxStatisticsMap based on the current flux attributes.
	 */
	public FluxStatisticsMap(){
		this.init();
	}
	
	/**
	 * Calculate the flux statistics based on the current flux distributions.
	 */
	public void init(){
		// init empty structure
		statisticsMap = new HashMap<String, FluxStatistics>();
		
		// Get all attribute names and generate the Statistics data
		FluxStatistics fluxStat;
		double tmp;
		for (String name : CyFluxVizPlugin.getFluxAttributes().getAttributeNames()){
			fluxStat = new FluxStatistics(Cytoscape.getNodeAttributes(),
					Cytoscape.getEdgeAttributes(), name);
			statisticsMap.put(name, fluxStat);
			
			// update the global values if necessary
			tmp = fluxStat.getAbsMax(); 
			if (tmp > absMax) absMax = tmp;
			tmp = fluxStat.getAbsMin();
			if (absMin == 0.0) absMin = tmp;
			else if (tmp < absMin) absMin = tmp;
		}
	}
	
	/**
	 * Get single statistics based on attribute name
	 */
	public FluxStatistics get(String name){
		return statisticsMap.get(name);
	}
	
	public double getGlobalAbsMin(){
		return absMin;
	}
	public double getGlobalAbsMax(){
		return absMax;
	}
	
}
