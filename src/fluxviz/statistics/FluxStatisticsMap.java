package fluxviz.statistics;

import java.util.HashMap;
import java.util.Map;

import cytoscape.Cytoscape;
import fluxviz.CyFluxVizPlugin;

public class FluxStatisticsMap {

	public Map<String, FluxStatistics> statisticsMap = 
						new HashMap<String, FluxStatistics>();
	private double globalMinFlux = 0.0;
	private double globalMaxFlux = 0.0;
	
	public FluxStatisticsMap(){
		this.calculateMap();
	}
	
	private void calculateMap(){

		FluxStatistics fluxStat = null;
		
		for (String name : CyFluxVizPlugin.getFluxAttributes().getAttributeNames()){
			fluxStat = new FluxStatistics(Cytoscape.getNodeAttributes(),
										  Cytoscape.getEdgeAttributes(),
										  name);
			statisticsMap.put(name, fluxStat);
			
			// global maximum
			double tmp = fluxStat.getAbsMax(); 
			if (tmp > globalMaxFlux){
				globalMaxFlux = tmp;
			}
			//global minimum
			tmp = fluxStat.getAbsMin();
			if (globalMinFlux == 0.0){
				globalMinFlux = tmp;
			}
			else if (tmp < globalMinFlux){
				globalMinFlux = tmp;
			}
		}
	}
	
	public FluxStatistics getFluxStatistics(String attributeName){
		FluxStatistics res = null;
		if (statisticsMap.containsKey(attributeName)){
			res = statisticsMap.get(attributeName);
		}
		return res;
	}
	
	public double getGlobalMinFlux(){
		return globalMinFlux;
	}
	
	public double getGlobalMaxFlux(){
		return globalMaxFlux;
	}
}
