package fluxviz.attributes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import fluxviz.FluxAttributes;

public class ValAttributes extends FluxAttributes{
	
	public Set<String> getFluxAttributeNames(){
		CyAttributes cyAttributes = Cytoscape.getNodeAttributes();
	
		Set<String> fluxAttributeNames = new HashSet<String>();
		for (String name : cyAttributes.getAttributeNames()){
			if (isFluxAttribute(name)){
				fluxAttributeNames.add(name);
			}
		}
		return fluxAttributeNames;
	}
	
	public boolean isValidFluxAttribute(String attributeName){
		if (!isFluxAttribute(attributeName)){
			return false;
		}
		return hasCompleteAttributeMapping(attributeName);
	}
	
	public static boolean isFluxAttribute(String attributeName){
		String suffix = ".val";
		return (attributeName.endsWith(suffix));
	}
	
	public boolean hasCompleteAttributeMapping(String attributeName){
		@SuppressWarnings("unchecked")
		List<CyNode> nodeList = Cytoscape.getCyNodesList();
		CyAttributes cyAttributes = Cytoscape.getNodeAttributes();
		for (CyNode node : nodeList){
			String id = node.getIdentifier();
			if (cyAttributes.getAttribute(id, attributeName) == null){
				return false;
			}
		}
		return true;
	}
}