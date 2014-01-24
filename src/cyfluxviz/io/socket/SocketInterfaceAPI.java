package cyfluxviz.io.socket;

import org.sbml.jsbml.SBMLDocument;


/** Defines the methods which can be called via the SocketConnection */
public class SocketInterfaceAPI {
	
	/** Loads an SBML model*/
	public static void loadSBML(String filename){
		
	}
	public static void loadSBML(SBMLDocument doc){
		
	}
	
	/** Loads a Layout for the model */
	public static void loadLayout(String filename){
		
	}
	
	//////////    Interaction with the Flux/Concentration Distributions    //// 
	
	/** Load FluxDistribution */
	public static void loadFluxDistributions(String filename){
		
		
	}
	
	/** Clears all FluxDistributions. */
	public static void clearFluxDistributions(){
		
	}
	
	/** Removes the FluxDistributions for the given network. */
	public static void clearFluxDistributionsForNetwork(String networkName){
			
	}
	
	
	
}
