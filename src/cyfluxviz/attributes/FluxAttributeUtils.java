package cyfluxviz.attributes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import cyfluxviz.CyFluxViz;
import cyfluxviz.gui.FluxVizPanel;
import cyfluxviz.gui.PanelDialogs;
import cyfluxviz.statistics.FluxStatisticsMap;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

public class FluxAttributeUtils {
    /** 
     * Updates the flux attributes based on the current network node attributes.
     * Changes the fluxAttributes in place and updates the list of available flux
     * attributes in the FluxViz panel.<br>
     * The edge attribute files are not updated.
     */
    public static void updateFluxAttributes(){
    	CyFluxViz.setFluxAttributes(new ValAttributes());
    	// Recalculate the statistics of the flux distributions
    	CyFluxViz.setFluxStatistics(new FluxStatisticsMap());
    	// If no flux statistics data -> reset the start information in the plugin
    	if (CyFluxViz.getFluxStatistics().statisticsMap.size() == 0){
    		PanelDialogs.setFluxVizInfo(CyFluxViz.getFvPanel());
    	}
    	// Update the table
    	CyFluxViz.getFvPanel().updateTable(CyFluxViz.getFluxAttributes().getAttributeNames());
    }
    
	/**
	 * Delete all loaded val attributes.
	 * Necessary because of problems with identifiers between different networks.
	 * Flux distributions always end with ".val".
	 */
    public static void removeValAttributes(){
    	CyAttributes cyAttributes = Cytoscape.getNodeAttributes(); 
    	String[] nodeAttr = cyAttributes.getAttributeNames();
    	for (int i=0; i<nodeAttr.length; ++i){
    		if (nodeAttr[i].endsWith(".val")){
    			cyAttributes.deleteAttribute(nodeAttr[i]);
    		}
    	}
    } 
    
	/**
	 * Get selected attributes from table model.
	 * @return array of selected attribute names
	 */
	public static String[] getSelectedAttributes(){
    	DefaultTableModel tableModel = CyFluxViz.getFvPanel().getTableModel();
    	JTable fluxTable = CyFluxViz.getFvPanel().getFluxTable();
    	
    	int[] selected = fluxTable.getSelectedRows();
    	String[] attributes = new String[selected.length];
    	for (int i=0; i<selected.length; ++i){
    		attributes[i] = (String) tableModel.getValueAt(selected[i], 0);
    	}
    	return attributes;
	}
	
	/**
	 * Get Node attributes from table model.
	 * @return array of string node attribute names
	 */
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
	/* Returns names of string NodeAttributes. */
	public static Set<String> getStringNodeAttributes(){
		return getTypeNodeAttributes(CyAttributes.TYPE_STRING);
	}
	
	/* Get the set of different values used in the attribute. 
	 * Identical function used for different types. */
	@SuppressWarnings("unchecked")
	public static Set getValueSet(String attributeName){
		Set valueSet = new HashSet();
    	if (attributeName == null){ return valueSet;}
		
		CyAttributes nodeAttrs = Cytoscape.getNodeAttributes();
		List<CyNode> nodeList = Cytoscape.getCyNodesList();
		for (CyNode node: nodeList){
			if (nodeAttrs.getAttribute(node.getIdentifier(), attributeName) != null){
				valueSet.add(nodeAttrs.getAttribute(node.getIdentifier(), attributeName));
			}
		}
		return valueSet;
	}
	
	
    public static void initNodeAttributeList(String attribute){    	
    	// 1. Get the possible values that can occur in the attribute
    	Object[] listObjects = getValueSet(attribute).toArray();
    	Arrays.sort(listObjects);
    	System.out.println("Values:");
    	for (Object obj: listObjects){
    		System.out.println(obj);
    	}
    	// Set the values in the list
    	
    	
		// 2.Set table model
        DefaultListModel model = new DefaultListModel();
        for (Object obj: listObjects){
        	model.addElement(obj);
        }
        // get the list and set the model
        FluxVizPanel panel = CyFluxViz.getFvPanel();
        JList list = panel.getNodeAttributeList();
        list.setModel(model);
	}
    
    /* Set the current NodeAttributes of type string in the ComboBox. */
    public static void initNodeAttributeComboBox(){
    	! BASED ON FLUXDISTRIBUTIONCOLLECTION
    	Object[] attributes = getStringNodeAttributes().toArray();
    	Arrays.sort(attributes);
    	
    	// get combo box and set the values
    	JComboBox box = CyFluxViz.getFvPanel().getNodeAttributeComboBox();
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
        	initNodeAttributeList(null);
        }	
    }
}