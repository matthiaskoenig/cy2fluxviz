package fluxviz.view;

import java.awt.BorderLayout;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
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
import fluxviz.fluxanalysis.FluxStatistics;
import fluxviz.fluxanalysis.FluxStatisticsMap;
import fluxviz.fluxanalysis.HistPanel;
import fluxviz.gui.FluxVizPanel;
import fluxviz.util.FileUtil;


/**
 * Handles different NetworkViews based on selected criteria.
 * These are the FluxSubnetworkView and the also the AttributeSubnetworkView.
 * TODO: Better handle these views in general NetworkView classes.
 * (generate an interface which handels the view).
 * init()
 * calculateVisibility() - get the hide or show tag for all nodes and edges
 * 						   in the network
 * apply
 */
public class NetworkView {
	
    /**
     * Handles the action which is performed when the flux subnetwork selection box
     * is selected or deselected.
     * Here the mapping of the flux distributions is performed. Set the mapping
     * and apply the subnetwork view depending on the fluxes.
     */
    public static void changeSubnetView(){
    	Logger logger = CyFluxVizPlugin.getLogger();
    	FluxVizPanel panel = CyFluxVizPlugin.getFvPanel();
    	JCheckBox fluxBox = panel.getFluxSubnetCheckbox(); 
    	JCheckBox attributeBox = panel.getAttributeSubnetCheckbox();
    	
    	JTable table = panel.getFluxTable();
    	DefaultTableModel model = panel.getTableModel();
    	
    	if (fluxBox.isSelected() == true ){ //&& table.getSelectedRow() != -1
    		String fluxAttribute = (String)model.getValueAt(table.getSelectedRow(), 0);
    		if (attributeBox.isSelected() == false){
    			logger.info("viewFluxSubnet()");
    			viewFluxSubnet(fluxAttribute);
    		}
    		else {
    			viewFluxAttributeSubnet(fluxAttribute);
    			logger.info("viewFluxAttributeSubnet()");
    		}
    	}
    	if (fluxBox.isSelected() == false ) { //&& table.getSelectedRow() != -1
    		
    		if (attributeBox.isSelected() == false){
    			logger.info("viewFullNet()");
    			viewFullNet();	
    		}
    		else{
    			logger.info("viewAttributeSubnet()");
    			viewAttributeSubnet();
    		}		
    	}
    }
    
	/**
	 * Generate flux attribute subnetwork view for given flux attribute
	 * combination and selected flux distribution.
	 */
    @SuppressWarnings("unchecked")
	public static void viewFluxAttributeSubnet(String fluxAttribute){
    	CyFluxVizPlugin.getLogger().info("viewFluxAttributeSubnet");
    	String edgeAttribute = fluxAttribute + "_edge";
    	
    	CyNetworkView view = Cytoscape.getCurrentNetworkView();
    	List<CyNode> nodeList = Cytoscape.getCyNodesList();
    	List<CyEdge> edgeList = Cytoscape.getCyEdgesList();
    	
		// set all nodes and edges invisible
        hideFullNet();
				
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
		//if the attribute condition is fullfilled
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
		//hide all nodes which are not in the flux subnetwork
		
		
		view.updateView();		
    }
    
    
    /**
     * Generate flux subnetwork view for given flux attribute.
     * Hide all nodes and edges which contain no flux.
     * TODO: minimize loop (hide / show can be performed in one loop)
     */
    @SuppressWarnings("unchecked")
	public static void viewFluxSubnet(String attribute){
    	CyFluxVizPlugin.getLogger().info("viewSubnetwork");
    	
    	String edgeAttribute = attribute + "_edge";
    	
    	CyNetworkView view = Cytoscape.getCurrentNetworkView();
    	List<CyNode> nodeList = Cytoscape.getCyNodesList();
    	List<CyEdge> edgeList = Cytoscape.getCyEdgesList();
    	
		//1. set all nodes and edges invisible
        for (CyNode node: nodeList){
        	view.hideGraphObject(view.getNodeView(node));
        }
				
		//2.1. calculate flux subnetwork
        Set<CyEdge> fluxEdges = new HashSet<CyEdge>(); 
        CyAttributes edgeAttrs = Cytoscape.getEdgeAttributes();
		for (CyEdge edge: edgeList){
			if (edgeAttrs.getDoubleAttribute(edge.getIdentifier(), edgeAttribute) != 0.0){
				fluxEdges.add(edge);
			}
		}
		//2.2 make flux subnetwork visible
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
    
    
    /**
     * View the attribute based subnetwork.
     */
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
    	// only based on strings
    	CyFluxVizPlugin.getLogger().info("viewNodeAttributeSubnet");
    	System.out.println("viewNodeAttributeSubnet");
    	
    	CyAttributes nodeAttrs = Cytoscape.getNodeAttributes();
    	byte attrType = nodeAttrs.getType(attributeName);
    	System.out.println("Attribute type:" + attrType);
    	if (attrType != CyAttributes.TYPE_STRING){
    		CyFluxVizPlugin.getLogger().info("Subnetworks only based on String attributes possible");
    		return;
    	}
    	
    	CyNetworkView view = Cytoscape.getCurrentNetworkView();
    	List<CyNode> nodeList = Cytoscape.getCyNodesList();
    	List<CyEdge> edgeList = Cytoscape.getCyEdgesList();
    	
		//1. set all nodes invisible
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
        
		//2.2 make nodes visible
		for (CyNode node: visibleNodes){
			view.showGraphObject(view.getNodeView(node));
		}
		//2.3 make edges with visible source and target node visible
		for (CyEdge edge: edgeList){
			// probably not necessary, because all edges are still visible
			//if source and target node are visible, make the edge visible
			if (visibleNodes.contains((CyNode)edge.getSource()) &&
					visibleNodes.contains((CyNode)edge.getTarget()) ){
				view.showGraphObject(view.getEdgeView(edge));
			}
		}		
		view.updateView();
    }
    
    
    
    /**
     * Show all nodes and edges in the network in the current view.
     */
	@SuppressWarnings("unchecked")
	public static void viewFullNet(){
		CyFluxVizPlugin.getLogger().info("viewFullnetwork");
        CyNetworkView view = Cytoscape.getCurrentNetworkView();
        List<CyNode> nodeList = Cytoscape.getCyNodesList();
        List<CyEdge> edgeList = Cytoscape.getCyEdgesList();
        for (CyNode node: nodeList){
            view.showGraphObject(view.getNodeView(node));
        }
        for (CyEdge edge: edgeList){
        	view.showGraphObject(view.getEdgeView(edge));
        }
        view.updateView();
    }
	
    /**
     * Hide all nodes and edges in the network in the current view.
     */
	@SuppressWarnings("unchecked")
	public static void hideFullNet(){
		CyFluxVizPlugin.getLogger().info("hideFullnetwork");
        CyNetworkView view = Cytoscape.getCurrentNetworkView();
        List<CyNode> nodeList = Cytoscape.getCyNodesList();
        List<CyEdge> edgeList = Cytoscape.getCyEdgesList();
        for (CyNode node: nodeList){
            view.hideGraphObject(view.getNodeView(node));
        }
        for (CyEdge edge: edgeList){
        	view.hideGraphObject(view.getEdgeView(edge));
        }
        view.updateView();
    }
    
    
    /**
     * Apply the flux view to the current network based on the selected flux 
     * distribution. 
     * TODO: Speed improvements -> very slow, especially for large networks.
     * @param attribute
     */
    public static void applyFluxVizView (String attribute){
    	CyFluxVizPlugin.getLogger().info("Selected: " + attribute);
    	
    	// Apply the FluxViz changes
        String edgeAttribute = attribute + "_edge";
        String edgeDirAttribute = attribute + "_edge_dir";

        // Get network, Visual Mapper, CalculatorCatalog and the VisualStyle
        CyNetwork network = Cytoscape.getCurrentNetwork();
        VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
        CalculatorCatalog calc_cat = vmm.getCalculatorCatalog();
        
        // Make sure that the FluxViz VisualStyle is available
        // Problem is with visual style and name of visual style
        //TODO: handle the loading and setting of the style in a consistent way. 
        //loaded and applied Styles have to be consistent !!!!
        if (calc_cat.getVisualStyle(CyFluxVizPlugin.getVsName()) == null){
        	FileUtil.loadViStyle();
        }
        VisualStyle vi_style = CyFluxVizPlugin.getViStyle();
        if (vi_style == null){
        	FileUtil.loadViStyle();
        }
        
        // Set the visual style (necessary every time ?)
        
        Cytoscape.getCurrentNetworkView().setVisualStyle(CyFluxVizPlugin.getVsName());
        vmm.setVisualStyle(vi_style);

        

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

        
        //TODO: BUG !!! java.lang.NullPointerException if network is loaded 
        //and vi_style is imported 
        // TODO:
        EdgeAppearanceCalculator edgeAppCalc = vi_style.getEdgeAppearanceCalculator();
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
        }
        else{
        	// in the subnetwork generation the view is already updated
        	Cytoscape.getCurrentNetworkView().updateView();
        }
        
        // The mapping has to be adapted (the continuous mappers have to fit to
        // the flux data in the system
        // Minimal values and maximal values have to be used ()
        // TODO: Reuse the calculated fluxStatistics (no recalculation)
        // Can be expensive for large networks (store the data somewhere)
        String info = "";
        // Get the available simulation information
        if (CyFluxVizPlugin.getFluxInformation() != null){
            FluxInfo fluxInfo = CyFluxVizPlugin.getFluxInformation().attributeInformation.get(attribute);
            if (fluxInfo != null){
            	info += fluxInfo.toHTML();
            }
        }

        // Statistical data
        FluxStatisticsMap fluxMap = CyFluxVizPlugin.getFluxStatistics();
        if (fluxMap != null){
        	FluxStatistics fluxStat = CyFluxVizPlugin.getFluxStatistics().get(attribute);
        	info += fluxStat.toHTML();
        	
        	// TODO: Histogramm
    		// Update the histogramm Pane and display
            System.out.println("Update Histogramm");
            
    		//HistPanel histPanel = new HistPanel(fluxStat.getFHistogram());
    		//FluxViz.getFvPanel().getHistogramPane().add(histPanel,"Center");
    		
    		//histPanel.paintComponents(arg0)
    		//histPanel.repaint();
    		
    		
    	    JPanel hpanel = new JPanel (new BorderLayout ());

    	    // JPanel subclass here.
    	    HistPanel fOutputPanel = new HistPanel (fluxStat.getFHistogram());
    	    hpanel.add (fOutputPanel,"Center");	
    	    CyFluxVizPlugin.getFvPanel().setHistogramPanel(hpanel);
    		hpanel.setVisible(true);
    		hpanel.repaint();
    		
    		//UIDrawHist hist = new UIDrawHist();
            //hist.init();
        }
        
        // Display the information
        // set text and make the tab active
        panel.updateText(panel.getInfoPane(), info);
        panel.getInformationPane().setSelectedComponent(panel.getInfoScrollPane());
        

        
        CyNetworkView view = Cytoscape.getCurrentNetworkView();
        applyVisualStyle(view);
    }
  
    /**
     * Applies the visual style after node hiding or reappearance.
     * @param view
     */
    public static void applyVisualStyle(CyNetworkView view){
		VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
    	CalculatorCatalog calc_cat = vmm.getCalculatorCatalog();
        CyFluxVizPlugin.setViStyle(calc_cat.getVisualStyle(CyFluxVizPlugin.getVsName()));
        view.setVisualStyle(CyFluxVizPlugin.getVsName());
        vmm.setVisualStyle(CyFluxVizPlugin.getViStyle());
        vmm.applyAppearances();
        view.updateView();
        view.redrawGraph(true,true);
    }
    
    
    /**
     * Hides all nodes and edges in the view.
     * Used for testing the subnetwork views.
     */
    @SuppressWarnings("unchecked")
	public static void testHide(){
    	//FluxViz.getLogger().info("testHide()");
        CyNetworkView view = Cytoscape.getCurrentNetworkView();
        List<CyNode> nodeList = Cytoscape.getCyNodesList();
        List<CyEdge> edgeList = Cytoscape.getCyEdgesList();

        for (CyNode node: nodeList){
        	view.hideGraphObject(view.getNodeView(node));
        }
        for (CyEdge edge: edgeList){
        	view.hideGraphObject(view.getEdgeView(edge));
        }
        view.updateView();
    } 

    /**
     * Shows all nodes and edges in the view.
     * Used for testing the subnetwork views.
     */
    @SuppressWarnings("unchecked")
	public static void testShow(){
    	//FluxViz.getLogger().info("testShow()");
        CyNetworkView view = Cytoscape.getCurrentNetworkView();
        List<CyNode> nodeList = Cytoscape.getCyNodesList();
        List<CyEdge> edgeList = Cytoscape.getCyEdgesList();

        for (CyNode node: nodeList){
            view.showGraphObject(view.getNodeView(node));
        }
        for (CyEdge edge: edgeList){
        	view.showGraphObject(view.getEdgeView(edge));
        }
        view.updateView();
    } 
    
    
}
