package cyfluxviz;

import java.util.HashMap;
import java.util.Set;
import cyfluxviz.netview.NetworkViewTools;

public class FluxDisCollection {

	private static FluxDisCollection uniqueInstance;
	private HashMap<String, FluxDis> fluxDistributions;
	private FluxDis activeFD;

	private double globalAbsMin = 0.0;
	private double globalAbsMax = 0.0;

	private FluxDisCollection() {
		fluxDistributions = new HashMap<String, FluxDis>();
	}

	public static synchronized FluxDisCollection getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new FluxDisCollection();
		}
		return uniqueInstance;
	}

	public Set<String> getIdSet() {
		return fluxDistributions.keySet();
	}

	public double getGlobalAbsMin() {
		return globalAbsMin;
	}

	public double getGlobalAbsMax() {
		return globalAbsMin;
	}

	public FluxDis getFluxDistribution(String fdId) {
		FluxDis fd = null;
		if (containsFluxDistribution(fdId)) {
			fd = fluxDistributions.get(fdId);
		}
		return fd;
	}

	public boolean containsFluxDistribution(String fdId) {
		return fluxDistributions.containsKey(fdId);
	}

	public void addFluxDistribution(FluxDis fluxDistribution) {
		fluxDistributions.put(fluxDistribution.getId(), fluxDistribution);
		updateGlobalAbsValues(fluxDistribution);
	}

	public void removeFluxDistribution(FluxDis fd) {
		String fdId = fd.getId();
		if (containsFluxDistribution(fdId)) {
			if (activeFD == fd){
				deactivateFluxDistribution();
			}
			fluxDistributions.remove(fdId);
			updateGlobalAbsValues();
		}
	}

	// ACTIVATE/DEACTIVATE FD
	public boolean hasActiveFluxDistribution() {
		return (activeFD != null);
	}

	public FluxDis getActiveFluxDistribution() {
		return activeFD;
	}

	public void setFluxDistributionActive(String fdId) {
		if (containsFluxDistribution(fdId)) {
			setFluxDistributionActive(getFluxDistribution(fdId));
		}
	}

	public void setFluxDistributionActive(FluxDis fluxDistribution) {
		activeFD = fluxDistribution;
		FluxDisCyAttributes.setCytoscapeAttributesForFluxDistribution(activeFD);
		NetworkViewTools.applyFluxVizView(activeFD.getNetworkId());
	}

	public void deactivateFluxDistribution() {
		if (activeFD != null) {
			activeFD = null;
			FluxDisCyAttributes.deleteFluxVizCytoscapeAttributes();
		}
	}

	// ABS VALUES
	private void updateGlobalAbsValues() {
		for (FluxDis fluxDistribution : fluxDistributions.values()) {
			updateGlobalAbsValues(fluxDistribution);
		}
	}

	private void updateGlobalAbsValues(FluxDis fluxDistribution) {
		FluxDisStatistics fluxStatistics = fluxDistribution.getFluxStatistics();
		updateGlobalAbsMin(fluxStatistics.getAbsMin());
		updateGlobalAbsMax(fluxStatistics.getAbsMax());
	}

	private void updateGlobalAbsMin(Double absMin) {
		if (absMin < globalAbsMin) {
			globalAbsMin = absMin;
		} else if (globalAbsMin == 0.0) {
			globalAbsMin = absMin;
		}
	}

	private void updateGlobalAbsMax(Double absMax) {
		if (absMax > globalAbsMax) {
			globalAbsMax = absMax;
		}
	}
}
