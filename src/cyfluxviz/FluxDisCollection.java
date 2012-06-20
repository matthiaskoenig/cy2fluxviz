package cyfluxviz;

import java.util.HashMap;
import java.util.Set;

import cyfluxviz.netview.NetworkViewTools;
import cyfluxviz.visualstyle.VisualStyleFactory;


public class FluxDisCollection {
	
	private static FluxDisCollection uniqueInstance;
	private HashMap<String, FluxDis> fluxDistributions;
	private FluxDis activeFD;
	
	private double globalAbsMin = 0.0;
	private double globalAbsMax = 0.0;
	
	private FluxDisCollection(){
		fluxDistributions = new HashMap<String, FluxDis>();
	}
	
	public static synchronized FluxDisCollection getInstance(){
		if (uniqueInstance == null){
			uniqueInstance = new FluxDisCollection();
		}
		return uniqueInstance;
	}
	
	public Set<String> getIdSet(){
		return fluxDistributions.keySet();
	}
	public double getGlobalAbsMin(){
		return globalAbsMin;
	}
	public double getGlobalAbsMax(){
		return globalAbsMin;
	}
		
	public FluxDis getFluxDistribution(String fdId){
		FluxDis fd = null;
		if (containsFluxDistribution(fdId)){
			fd = fluxDistributions.get(fdId);
		}
		return fd;
	}
	
	public boolean containsFluxDistribution(String fdId){
		return fluxDistributions.containsKey(fdId);
	}
	
	public void addFluxDistribution(FluxDis fluxDistribution){
		fluxDistributions.put(fluxDistribution.getId(), fluxDistribution);
		updateGlobalAbsValues(fluxDistribution);
	}
	
	public void removeFluxDistribution(FluxDis fluxDistribution){
		String fdId = fluxDistribution.getId();
		if (containsFluxDistribution(fdId)){
			fluxDistributions.remove(fdId);
			updateGlobalAbsValues();
		} else {
			String info = String.format("CyFluxViz[INFO] -> Collection does not contain FD %s",
										fdId);
			System.out.println(info);
		}
	}
	
	// ABS VALUES //
	
	private void updateGlobalAbsValues(){
		for (FluxDis fluxDistribution: fluxDistributions.values()){
			updateGlobalAbsValues(fluxDistribution);
		}
	}
	
	private void updateGlobalAbsValues(FluxDis fluxDistribution){
		FluxDisStatistics fluxStatistics = fluxDistribution.getFluxStatistics(); 
		updateGlobalAbsMin(fluxStatistics.getAbsMin());
		updateGlobalAbsMax(fluxStatistics.getAbsMax());
	}
	
	private void updateGlobalAbsMin(Double absMin){
		if (absMin < globalAbsMin){
			globalAbsMin = absMin;
		} else if (globalAbsMin == 0.0){
			globalAbsMin = absMin;
		}
	}
	private void updateGlobalAbsMax(Double absMax){
		if (absMax > globalAbsMax){
			globalAbsMax = absMax;
		}
	}
	
	
	// ACTIVATE/DEACTIVATE FD //
	public boolean hasActiveFluxDistribution(){
		return (activeFD != null);
	}
	
	public FluxDis getActiveFluxDistribution(){
		return activeFD;
	}
	
	
	public void setFluxDistributionActive(String fdId){
		if (containsFluxDistribution(fdId)){
			setFluxDistributionActive(getFluxDistribution(fdId));
		}
	}
	
	public void setFluxDistributionActive(FluxDis fluxDistribution){
		// Write the FluxDistribution to the CyFluxViz Attributes
		activeFD = fluxDistribution;
		FluxDisCyAttributes.setCytoscapeAttributesForFluxDistribution(activeFD);
		
		// Change graph
		String networkId = activeFD.getNetworkId();
        NetworkViewTools.applyFluxVizView(networkId);
	}
	
	public void deactivateFluxDistribution(){
		if (activeFD == null) {
			return;
		} else {
			activeFD = null;
			FluxDisCyAttributes.deleteFluxVizCytoscapeAttributes();
			// Redraw the graph -  make the changes to the VisualMapping
			
		}
	}
}
