package cyfluxviz;

import java.util.Collection;
import java.util.HashMap;
import java.util.Observable;
import java.util.Set;

import cyfluxviz.gui.CyFluxVizPanel;
import cyfluxviz.util.CyNetworkUtils;


/** Singleton class storing Fluxdistributions. 
 * Extends Observable to inform other instances about changes
 * in the active FluxDistribution.
 * TODO: The changing of FluxDistribution content and the 
 * 		 active FluxDistribution has to inform the panel !
 */
public class FluxDisCollection extends Observable {
	
	private static FluxDisCollection uniqueInstance;
	
	private HashMap<Integer, FluxDis> fluxDistributions;
	private FluxDis activeFD;

	private Double globalAbsMin;
	private Double globalAbsMax;

	
	public static synchronized FluxDisCollection getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new FluxDisCollection();
		}
		return uniqueInstance;
	}
	private FluxDisCollection() {
		fluxDistributions = new HashMap<Integer, FluxDis>();
	}

	public Set<Integer> getIdSet() {
		return fluxDistributions.keySet();
	}
	
	public int size(){
		return fluxDistributions.size();
	}
	
	
	public Collection<FluxDis> getFluxDistributions(){
		return fluxDistributions.values();
	}

	public boolean containsFluxDistribution(Integer fdId) {
		return fluxDistributions.containsKey(fdId);
	}
	
	public FluxDis getFluxDistribution(Integer fdId) {
		if (containsFluxDistribution(fdId)) {
			return fluxDistributions.get(fdId);
		}
		return null;
	}
	
	/** Only can be added if the respective network is available. 
	 * Returns "" if all FluxDistributions were added, or an 
	 * information message if some FDs could not be added (network is not
	 * existing). */
	public String addFluxDistributions(Collection<FluxDis> fds) {
		String msg = "";
		for (FluxDis fd: fds){
			msg += addFluxDistribution(fd);
		}
		
		System.out.println("Message adding FluxDistributions: '" + msg + "'");
		if (!msg.equals("")){
			msg = "Networks for the following FluxDistributions could not be found:\n" +
				  "----------------------------------------------------------------\n" +
				  msg +
				  "----------------------------------------------------------------\n" + 
				  "Load networks first and control network names in FluxDistributions.\n";
		}
		//TODO: all importers have to go via the same interface.
		
		// TODO: handle better to avoid the strong dependencies 
		// between the classes 
		// -> better observer pattern;
		CyFluxVizPanel.getInstance().updateFluxDistributionTable();
		return msg;
	}
	
	/** Adds flux distributions to the the FluxDistribution 
	 * collection. Returns error messages and warnings if FluxDistribution
	 * could not be added. Returns "" otherwise. */ 
	private String addFluxDistribution(FluxDis fd) {
		String msg = "";
		// only add if network is available
		if (CyNetworkUtils.existsNetwork(fd)){
			fluxDistributions.put(fd.getId(), fd);
			updateGlobalAbsValues(fd);
			System.out.println( String.format("CyFluxViz[INFO] -> Flux Distribution added: %s | %s | %s",
				fd.getId(), fd.getName(), fd.getNetworkId()) );
		} else {
			msg = String.format("%s | %s -> %s\n", fd.getId(), fd.getName(), fd.getNetworkId());
			System.out.print("CyFluxViz[WARNING]: " + msg);
		}
		return msg;
	}
	
	public void removeFluxDistributions(Collection<FluxDis> fds) {
		for (FluxDis fd: fds){
			removeFluxDistribution(fd);
		}
		CyFluxVizPanel.getInstance().updateFluxDistributionTable();
	}

	public void removeFluxDistribution(FluxDis fd) {
		int fdId = fd.getId();
		if (containsFluxDistribution(fdId)) {
			if (activeFD == fd){
				deactivateFluxDistribution();
			}
			fluxDistributions.remove(fdId);
			updateGlobalAbsValues();
		}
		System.out.println( String.format("CyFluxViz[INFO] -> Flux Distribution removed: %s | %s | %s",
						fd.getId(), fd.getName(), fd.getNetworkId()) );
	}
	
	/** Removes all FluxDistributions and resets to the initial state. */
	public void reset() {
		uniqueInstance = null;
		fluxDistributions = null;
		activeFD = null;
		globalAbsMin = null;
		globalAbsMax = null;
	}
	

	// ACTIVATE/DEACTIVATE FD
	public boolean hasActiveFluxDistribution() {
		return (activeFD != null);
	}

	public FluxDis getActiveFluxDistribution() {
		return activeFD;
	}

	public void activateFluxDistribution(int fdId) {
		if (containsFluxDistribution(fdId)) {
			activateFluxDistribution(getFluxDistribution(fdId));
		}
	}

	public void activateFluxDistribution(FluxDis fluxDistribution) {
		activeFD = fluxDistribution;
		setChanged();
		notifyObservers(activeFD);
	}

	public void deactivateFluxDistribution() {
		activeFD = null; 
		setChanged();
		notifyObservers(activeFD);
	}
	

	// ABS VALUES FOR ALL FLUX DISTRIBUTIONS
	public double getGlobalAbsMin() {
		return globalAbsMin;
	}

	public double getGlobalAbsMax() {
		return globalAbsMax;
	}
	
	private void updateGlobalAbsValues() {
		resetGlobalAbsValues();
		for (FluxDis fd : getFluxDistributions()) {
			updateGlobalAbsValues(fd);
		}
	}
	
	private void resetGlobalAbsValues(){
		globalAbsMax = null;
		globalAbsMax = null;
	}
	
	private void updateGlobalAbsValues(FluxDis fluxDistribution) {
		FluxDisStatistics fluxStatistics = fluxDistribution.getFluxStatistics();
		updateGlobalAbsMin(fluxStatistics.getAbsMin());
		updateGlobalAbsMax(fluxStatistics.getAbsMax());
	}

	private void updateGlobalAbsMin(Double value) {
		if (globalAbsMin == null){
			globalAbsMin = value;
		} else if (value < globalAbsMin){
			globalAbsMin = value;
		}
	}

	private void updateGlobalAbsMax(Double value) {
		if (globalAbsMax == null){
			globalAbsMax = value;
		} else if (value > globalAbsMax){
			globalAbsMax = value;
		}
	}
}
