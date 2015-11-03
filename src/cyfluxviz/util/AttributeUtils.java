package cyfluxviz.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JList;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cyfluxviz.CyFluxVizPlugin;
import cyfluxviz.gui.CyFluxVizPanel;

/** TODO: Rewrite, better handling of the attribute lists.
 * Collection of tools to handle the attribute list and work
 * with the selected attributes. 
 * These is the core functionality for the attribute subnetworks.
 */
public class AttributeUtils {

    /** Set the current NodeAttributes of type string in the ComboBox. 
     * Called when attributes are changing. */
    public static void initNodeAttributeComboBox(){
    	CyFluxVizPlugin.LOGGER.info("initNodeAttributeComboBox()");
    	Object[] attributes = getStringNodeAttributes().toArray();
    	Arrays.sort(attributes);
    	
    	// get combo box and set the values
    	JComboBox box = CyFluxVizPanel.getInstance().getNodeAttributeComboBox();
    	DefaultComboBoxModel model = new DefaultComboBoxModel(attributes);
    	box.setModel(model); 	
    	
    	// select value if value are available and update the corresponding list
    	String a=null;
    	if (model.getSize() != 0){
        	// try to find a compartment attribute
        	for (int i=0; i<attributes.length; i++){
        		a = (String) attributes[i];
        		if (a.contains("compartment")){
        			model.setSelectedItem(a);
        			initNodeAttributeList(a);
        			return;
        		}
        	}
        	// else take the first one
        	a = (String) attributes[0];
        	model.setSelectedItem(a);
        	initNodeAttributeList(a);
        }
        // if no string attributes are available initialize empty.
        else{
        	CyFluxVizPlugin.LOGGER.warning("empty attribute list initialized !");
        	initNodeAttributeList(null);
        }	
    }
	
	/** Initializes the list of possible values for the selected attributes. */
    public static void initNodeAttributeList(String attribute){    	
    	// 1. Get the possible values that can occur in the attribute
    	Object[] listObjects = getValueSet(attribute).toArray();
    	Arrays.sort(listObjects);
    	// Set the values in the list
		// 2.Set table model
        DefaultListModel<Object> model = new DefaultListModel<Object>();
        for (Object obj: listObjects){
        	model.addElement(obj);
        }
        // get the list and set the model
		JList<Object> list = CyFluxVizPanel.getInstance().getNodeAttributeList();
        list.setModel(model);
	}
    
	/** Get the set of different values used in the attribute. 
	 * Here for every attribute the values which can be used for the attribute subnetworks
	 * are defined.
	 * Identical function used for different types. */
	public static Set<Object> getValueSet(String attributeName){
		Set<Object> valueSet = new HashSet<Object>();
    	if (attributeName == null){ return valueSet;}
		
		CyAttributes nodeAttrs = Cytoscape.getNodeAttributes();
		@SuppressWarnings("unchecked")
		List<CyNode> nodeList = Cytoscape.getCyNodesList();
		for (CyNode node: nodeList){
			if (nodeAttrs.getAttribute(node.getIdentifier(), attributeName) != null){
				valueSet.add(nodeAttrs.getAttribute(node.getIdentifier(), attributeName));
			}
		}
		return valueSet;
	}
	
	public static Set<String> getStringNodeAttributes(){
		return getTypeNodeAttributes(CyAttributes.TYPE_STRING);
	}
	
	public static Set<String> getTypeNodeAttributes(byte attrType){
		Set<String> nameSet = new HashSet<String>();
		CyAttributes nodeAttrs = Cytoscape.getNodeAttributes();
		for (String name : nodeAttrs.getAttributeNames()){
			if (nodeAttrs.getType(name) == attrType){
				nameSet.add(name);
			}
		}
    	return nameSet;
	}

}