package fluxviz.fasimu;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import fluxviz.FluxAttributes;

/**
 * Handles the fasimu val attributes.
 * @author mkoenig
 *
 */

public class ValAttributes extends FluxAttributes{
	/*
	 * Filters the flux node attributes from all current node attributes.
	 * Flux attributes are calculated based on node attributes.
	 * 
	 * @ return set of node flux attributes.
	 */
	private CyAttributes cyAttributes;
	
	public Set<String> filterFluxAttributes(){
		cyAttributes = Cytoscape.getNodeAttributes();
		String[] allAttributes = cyAttributes.getAttributeNames();
		Set<String> fluxAttributes = new HashSet<String>();
		String name;
		for (int i=0; i<allAttributes.length; ++i){
			name = allAttributes[i];
			if (name.endsWith(".val")){
				fluxAttributes.add(name);
			}
		}
		//System.out.println("fluxAttributes: " + fluxAttributes);
		return fluxAttributes;
	}
	
	/**
	 *  Test if the attribute is valid.
	 *  Tests if all nodes in the Network have a value for the given attribute.
	 *  Returns True if a full mapping between node ids and attribute values exists.
	 * @return boolean Returns if the attribute is valide for the given network.
	 */
	@SuppressWarnings("unchecked")
	public boolean isValidAttribute(String attribute){
		List<CyNode> nodeList = Cytoscape.getCyNodesList();
		for (CyNode node : nodeList){
			if (cyAttributes.getAttribute(node.getIdentifier(), attribute) == null){
				return false;
			}
		}
		return true;
	}
	
}
