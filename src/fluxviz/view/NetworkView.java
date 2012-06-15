package fluxviz.view;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.ArrowShape;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.PassThroughMapping;

import fluxviz.FluxInfo;
import fluxviz.CyFluxVizPlugin;
import fluxviz.gui.FluxVizPanel;
import fluxviz.statistics.FluxStatistics;
import fluxviz.statistics.FluxStatisticsMap;
import fluxviz.util.FileUtil;

/* Class managing the distinct NetworkViews based on selected criteria, like
 * for instance the FluxSubnetworkView or AttributeSubnetworkView. */
public class NetworkView {
	
    /* Handles the action which is performed when the flux subnetwork selection box
     * is selected or deselected.
     * Here the mapping of the flux distributions is performed. Set the mapping
     * and apply the subnetwork view depending on the fluxes.
     */
    public static void changeSubnetView(){
    	FluxVizPanel panel = CyFluxVizPlugin.getFvPanel();
    	JCheckBox fluxBox = panel.getFluxSubnetCheckbox(); 
    	JCheckBox attributeBox = panel.getAttributeSubnetCheckbox();
    	JTable table = panel.getFluxTable();
    	DefaultTableModel model = panel.getTableModel();
    	
    	if (fluxBox.isSelected() == true ){ 
    		String fluxAttribute = (String)model.getValueAt(table.getSelectedRow(), 0);
    		if (attributeBox.isSelected() == false){
    			viewFluxSubnet(fluxAttribute);
    		} else {
    			viewFluxAttributeSubnet(fluxAttribute);
    		}
    	}
    	if (fluxBox.isSelected() == false ) { //&& table.getSelectedRow() != -1
    		if (attributeBox.isSelected() == false){
    			NetworkViewTools.showAllNodesAndEdgesInCurrentView();	
    		}else{
    			viewAttributeSubnet();
    		}		
    	}
    }
    
	/*Generate flux attribute subnetwork view for given flux attribute
	 * combination and selected flux distribution.
	 * TODO: handling of the edge attributes in better way
	 */
    @SuppressWarnings("unchecked")
	public static void viewFluxAttributeSubnet(String nodeAttribute){
    	String edgeAttribute = nodeAttribute + "_edge";
    	
    	CyNetworkView view = Cytoscape.getCurrentNetworkView();
        NetworkViewTools.hideAllNodesAndEdgesInView(view);
		
        List<CyNode> nodeList = Cytoscape.getCyNodesList();
    	List<CyEdge> edgeList = Cytoscape.getCyEdgesList();
    	
		//calculate flux subnetwork
        Set<CyEdge> fluxEdges = new HashSet<CyEdge>(); 
        CyAttributes edgeAttrs = Cytoscape.getEdgeAttributes();
		for (CyEdge edge: edgeList){
			if (edgeAttrs.getDoubleAttribute(edge.getIdentifier(), edgeAttribute) != 0.0){
				fluxEdges.add(edge);
			}
		}
		
        // calculate the set of visible nodes based on the attribute condition
    	FluxVizPanel panel = CyFluxVizPlugin.getFvPanel();
        String attribute = (String) panel.getNodeAttributeComboBox().getSelectedItem();
        
    	boolean nullVisible = panel.getNullVisibleCheckbox().isSelected();
        Set<Object> selected = new HashSet<Object>();
        for (Object obj: panel.getNodeAttributeList().getSelectedValues()){
            selected.add(obj);
        }
        CyAttributes nodeAttrs = Cytoscape.getNodeAttributes();
    	
        Set<CyNode> visibleNodes = new HashSet<CyNode>(); 
		for (CyNode node: nodeList){
			String id = node.getIdentifier();
			
			if (selected.contains(nodeAttrs.getAttribute(id, attribute))){
				visibleNodes.add(node);
			}
			else if (nullVisible==true && nodeAttrs.getAttribute(id, attribute)==null){
				visibleNodes.add(node);
			}
		}

		//2.2 all nodes and edges of the flux subnetwork visible
		CyNode node;
		boolean source_visible;
		boolean target_visible;
		for (CyEdge edge: fluxEdges){
			source_visible = false;
			target_visible = false;
			//show source and target nodes if attribute fullfilled
			node = (CyNode)edge.getSource();
			if (visibleNodes.contains(node)){
				view.showGraphObject(view.getNodeView(node));
				source_visible = true;
			}
			node = (CyNode)edge.getTarget();
			if (visibleNodes.contains(node)){
				view.showGraphObject(view.getNodeView(node));
				target_visible = true;
			}
			//show flux edge
			if (source_visible && target_visible){
				view.showGraphObject(view.getEdgeView(edge));
			}	
		}
		view.updateView();		
    }
    
    /* Generate flux subnetwork view for given flux attribute. */
    @SuppressWarnings("unchecked")
	public static void viewFluxSubnet(String attribute){    	
    	String edgeAttribute = attribute + "_edge";
    	
    	//2.1. calculate flux subnetwork
    	List<CyEdge> edgeList = Cytoscape.getCyEdgesList();    			
        Set<CyEdge> fluxEdges = new HashSet<CyEdge>(); 
        CyAttributes edgeAttrs = Cytoscape.getEdgeAttributes();
		for (CyEdge edge: edgeList){
			if (edgeAttrs.getDoubleAttribute(edge.getIdentifier(), edgeAttribute) != 0.0){
				fluxEdges.add(edge);
			}
		}
		//2.2 make flux subnetwork visible
    	CyNetworkView view = Cytoscape.getCurrentNetworkView();
    	NetworkViewTools.hideAllNodesAndEdgesInView(view);
		CyNode node;
		for (CyEdge edge: fluxEdges){
			//show source and target nodes
			node = (CyNode)edge.getSource();
			view.showGraphObject(view.getNodeView(node));
			node = (CyNode)edge.getTarget();
			view.showGraphObject(view.getNodeView(node));
			//show flux edge
			view.showGraphObject(view.getEdgeView(edge));
		}
		view.updateView();
    }
    
    /* View the attribute based subnetwork. */
    public static void viewAttributeSubnet(){
    	FluxVizPanel panel = CyFluxVizPlugin.getFvPanel();
        String attribute = (String) panel.getNodeAttributeComboBox().getSelectedItem();
        boolean nullVisible = panel.getNullVisibleCheckbox().isSelected();
        Set<Object> selected = new HashSet<Object>();
        for (Object obj: panel.getNodeAttributeList().getSelectedValues()){
            selected.add(obj);
        }
        if (attribute != null){
        	NetworkView.viewNodeAttributeSubnet(attribute, selected, nullVisible);
        }
    }
    
    /**
     * Generate subnetwork view based on attributes in the network.
     * Hide all nodes and edges which are not in the selected Set.
     * Set has to be of type of the attributeName (in the simplest case
     * set of Strings).
     * 
     * if attribute == null -> no val file is flux distribution is selected, only the
     * subnetwork view is generated.
     * 
     * nullVisible decides if the nodes without mapping are visible or not. 
     */
    @SuppressWarnings("unchecked")
	public static void viewNodeAttributeSubnet(String attributeName, Set selected, Boolean nullVisible){
    	CyAttributes nodeAttrs = Cytoscape.getNodeAttributes();
    	byte attrType = nodeAttrs.getType(attributeName);
    	if (attrType != CyAttributes.TYPE_STRING){
    		System.out.println("CyFluxViz[INFO] -> Subnetworks only based on String attributes possible");
    		return;
    	}
    	
    	CyNetworkView view = Cytoscape.getCurrentNetworkView();
    	List<CyNode> nodeList = Cytoscape.getCyNodesList();
    	List<CyEdge> edgeList = Cytoscape.getCyEdgesList();
    	
		// set all nodes and edges invisible
        for (CyNode node: nodeList){
        	view.hideGraphObject(view.getNodeView(node));
        }
        //1.1 set all edges invisible
        for (CyEdge edge: edgeList){
        	view.hideGraphObject(view.getEdgeView(edge));
        }
		
        //2.1 calculate the set of visible nodes based on the attribute
        // and the set of selected values which should be displayed
        Set<CyNode> visibleNodes = new HashSet<CyNode>(); 
		for (CyNode node: nodeList){
			String id = node.getIdentifier();
			if (selected.contains(nodeAttrs.getAttribute(id, attributeName))){
				visibleNodes.add(node);
			}
			else if (nullVisible==true && nodeAttrs.getAttribute(id, attributeName)==null){
				visibleNodes.add(node);
			}
		}
		NetworkViewTools.showNodesInView(visibleNodes, view);
		
		//make edges with visible source and target node visible
		for (CyEdge edge: edgeList){
			if (visibleNodes.contains((CyNode)edge.getSource()) &&
					visibleNodes.contains((CyNode)edge.getTarget()) ){
				view.showGraphObject(view.getEdgeView(edge));
			}
		}		
		view.updateView();
    }
    
	
    /*
     * Apply the flux view to the current network based on the selected flux 
     * distribution. 
     * TODO: Speed improvements -> very slow, especially for large networks.
     */
    public static void applyFluxVizView (String attribute){
        String edgeAttribute = attribute + "_edge";
        String edgeDirAttribute = attribute + "_edge_dir";

        CyNetwork network = Cytoscape.getCurrentNetwork();
        VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
        CalculatorCatalog calc_cat = vmm.getCalculatorCatalog();
        CyNetworkView view = Cytoscape.getCurrentNetworkView();
        
        if (CyFluxVizPlugin.getViStyle() == null){
        	FileUtil.loadViStyle();
        }
        if (!NetworkViewTools.currentViStyleIsFluxVizStyle(vmm.getVisualStyle())){
        	view.setVisualStyle(CyFluxVizPlugin.getViStyle().getName());
            vmm.setVisualStyle(CyFluxVizPlugin.getViStyle());	
        }
        
        // CHANGE THE ATTRIBUTES
        // 1. NODE COLOR
        //Get the appearance calculators from style
        /*
        NodeAppearanceCalculator nodeAppCalc = vi_style.getNodeAppearanceCalculator();
        // Get the Calculator for nodeColor
        Calculator nodeColorCalculator = nodeAppCalc.getCalculator(VisualPropertyType.NODE_FILL_COLOR);
        //get mapping from calculator
        ContinuousMapping continuousMapping = (ContinuousMapping)nodeColorCalculator.getMapping(0);
        // change mapping in the calculator
        continuousMapping.setControllingAttributeName(attribute, network, false);
        */

        // 2. NODE SIZE
        // Get the Calculator for nodeColor
        /*Calculator nodeSizeCalculator = nodeAppCalc.getCalculator(VisualPropertyType.NODE_SIZE);
        //get mapping from calculator
        ContinuousMapping continuousSizeMapping = (ContinuousMapping)nodeSizeCalculator.getMapping(0);
        // change mapping in the calculator
        continuousSizeMapping.setControllingAttributeName(attribute, network, false);
        */

        // 3. EDGE WIDTH
        //Get the appearance calculators from style
        EdgeAppearanceCalculator edgeAppCalc = CyFluxVizPlugin.getViStyle().getEdgeAppearanceCalculator();
        // Get the Calculator for nodeColor
        Calculator edgeWidthCalculator = edgeAppCalc.getCalculator(VisualPropertyType.EDGE_LINE_WIDTH);
        //get mapping from calculator
        ContinuousMapping continuousEdgeWidthMapping = (ContinuousMapping)edgeWidthCalculator.getMapping(0);
        // change mapping in the calculator
        continuousEdgeWidthMapping.setControllingAttributeName(edgeAttribute, network, false);

        // 4. EDGE SOURCE ARROWS
        /*
        //Get the appearance calculators from style
        Calculator edgeSourceArrowCalculator = edgeAppCalc.getCalculator(VisualPropertyType.EDGE_SRCARROW_SHAPE);
        //get mapping from calculator
        DiscreteMapping discreteEdgeSourceArrowMapping = (DiscreteMapping)edgeSourceArrowCalculator.getMapping(0);
        // change mapping in the calculator
        discreteEdgeSourceArrowMapping.setControllingAttributeName(edgeDirAttribute, network, false);
        discreteEdgeSourceArrowMapping.putMapValue(-1, ArrowShape.DELTA);
        discreteEdgeSourceArrowMapping.putMapValue(1, ArrowShape.NONE);
		*/

        // 5. EDGE TARGET ARROWS
        //Get the appearance calculators from style
        Calculator edgeTargetArrowCalculator = edgeAppCalc.getCalculator(VisualPropertyType.EDGE_TGTARROW_SHAPE);
        //get mapping from calculator
        DiscreteMapping discreteEdgeTargetArrowMapping = (DiscreteMapping)edgeTargetArrowCalculator.getMapping(0);
        // change mapping in the calculator
        discreteEdgeTargetArrowMapping.setControllingAttributeName(edgeDirAttribute, network, false);
        discreteEdgeTargetArrowMapping.putMapValue(-1, ArrowShape.NONE);
        discreteEdgeTargetArrowMapping.putMapValue(1, ArrowShape.DELTA);

        // 6. EDGE TOOLTIP
        Calculator edgeTooltipCalculator = edgeAppCalc.getCalculator(VisualPropertyType.EDGE_TOOLTIP);
        //get mapping from calculator
        PassThroughMapping edgeTooltipMapping = (PassThroughMapping)edgeTooltipCalculator.getMapping(0);
        // change mapping in the calculator
        edgeTooltipMapping.setControllingAttributeName(edgeAttribute, network, false);

        //Apply the changes
        vmm.applyAppearances();
        
        
        // Handle the subnetwork feature (different flux subnetworks for the 
        // different flux distributions
        FluxVizPanel panel = CyFluxVizPlugin.getFvPanel(); 
        if (panel.getFluxSubnetCheckbox().isSelected()){
        	NetworkView.viewFluxSubnet(attribute);
        } else {
        	// in the subnetwork generation the view is already updated
        	Cytoscape.getCurrentNetworkView().updateView();
        }
        
        String info = "";

        // flux statistics
        FluxStatisticsMap fluxMap = CyFluxVizPlugin.getFluxStatistics();
        if (fluxMap != null){
        	FluxStatistics fluxStat = CyFluxVizPlugin.getFluxStatistics().getFluxStatistics(attribute);
        	info += fluxStat.toHTML();
        }
        
        // set text and make the tab active
        panel.updateInfoPaneHTMLText(info);
        panel.selectInfoPane();
        
        applyVisualStyleToView(Cytoscape.getCurrentNetworkView());
    }
  

    public static void applyVisualStyleToView(CyNetworkView view){
    	String vsName = CyFluxVizPlugin.getViStyle().getName();
    	VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
    	CalculatorCatalog calc_cat = vmm.getCalculatorCatalog();
    	
        CyFluxVizPlugin.setViStyle(calc_cat.getVisualStyle(vsName));
        view.setVisualStyle(vsName);
        vmm.setVisualStyle(CyFluxVizPlugin.getViStyle());
        vmm.applyAppearances();
        view.updateView();
        view.redrawGraph(true,true);
    }  
}
