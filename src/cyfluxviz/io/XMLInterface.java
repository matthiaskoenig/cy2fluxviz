package cyfluxviz.io;

import java.util.HashMap;
import java.util.UUID;

import cyfluxviz.FluxDis;
import cyfluxviz.FluxDisStatistics;

public class XMLInterface {

	public void importXML(){
		// id
		// name
		// networkId
		
		// NodeFlux
		// NodeConcentrations
	}
	
	/* Export a whole list/set of xml */
	public void exportXML(FluxDis fd){
		// Reads 
		// EdgeFlux
		// EdgeDirection
		// NodeFlux
		String id = fd.getId();
		String name = fd.getName();
		String networkId = fd.getNetworkId();
		HashMap<String, Double> nodeFluxes = fd.getNodeFluxes();
		
	}
}
