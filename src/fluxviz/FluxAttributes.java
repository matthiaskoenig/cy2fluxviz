package fluxviz;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

import fluxviz.fluxanalysis.FluxStatistics;


/*
 * Class handles the FluxAttributes.
 * FluxAttributes is an abstract class, use the ValAttributes from fasimu as implentation.
 * Flux attributes are node attributes of type double with value for all nodes 
 * of the 'sbml type' reaction.
 */

public abstract class FluxAttributes {
	/** Set of the flux attribute names */
	private Set<String> attributeNames;
	public Map<String, FluxStatistics> attributeStatistics;
	
	/** Creates FluxAttributes based on the current node attributes */
	public FluxAttributes(){
		update();
	}
		
	/**
	 * Calculates the valAttributes based on the current nodeAttributes.
	 * 
	 */
	public void update(){
		attributeNames = filterFluxAttributes();
		//TODO
		//attributeStatistics = updateAttributeStatistics();
		attributeStatistics = null;
	}
	
	/*
	 * Filters the flux node attributes from all current node attributes.
	 * Flux attributes are calculated based on node attributes.
	 * 
	 * @ return set of node flux attributes.
	 */
	public abstract Set<String> filterFluxAttributes();
	
	/*
	 * Calculates the Statistics for the flux attributes.
	 */
	public Map<String, FluxStatistics> updateAttributeStatistics(){
		Map<String, FluxStatistics> statSet = new HashMap<String, FluxStatistics>();
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes(); 
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes(); 
		String name;
		FluxStatistics fluxStat;
		for (Iterator<String> i = attributeNames.iterator(); i.hasNext();){
			name = i.next();
			fluxStat = new FluxStatistics(nodeAttributes, edgeAttributes, name);
			statSet.put(name, fluxStat);
		}
		return statSet;
	}
	
	/*
	 * Get the flux attribute names.
	 * @return list of sorted flux attributes
	 */
	public String[] getAttributeNames(){
		if (attributeNames == null){
			String[] res = {};
			return res;
		}
		String[] valNames = (String []) attributeNames.toArray(new String[attributeNames.size()]);
		Arrays.sort(valNames);
		return valNames;
	}
	
}
