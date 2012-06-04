package fluxviz;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

import fluxviz.statistics.FluxStatistics;


/*
 * Class handles the FluxAttributes.
 * FluxAttributes is an abstract class, use the ValAttributes from fasimu as implentation.
 * Flux attributes are node attributes of type double with value for all nodes 
 * of the 'sbml type' reaction.
 */
public abstract class FluxAttributes {
	private Set<String> attributeNames;
	
	public FluxAttributes(){
		updateFluxAttributes();
	}
		
	public void updateFluxAttributes(){
		attributeNames = getFluxAttributeNames();
	}
	
	public abstract Set<String> getFluxAttributeNames();
	
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
