package cyfluxviz.netview;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.help.Map;
import javax.swing.JCheckBox;

import cyfluxviz.CyFluxViz;
import cyfluxviz.FluxDis;
import cyfluxviz.FluxDisCollection;
import cyfluxviz.FluxDisStatistics;
import cyfluxviz.gui.CyFluxVizPanel;
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
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.PassThroughMapping;

public class NetworkView {
	
    public static void changeSubnetView(){
    	CyFluxVizPanel panel = CyFluxViz.getFvPanel();
    	JCheckBox fluxBox = panel.getFluxSubnetCheckbox(); 
    	JCheckBox attributeBox = panel.getAttributeSubnetCheckbox();
    	
    	if        (fluxBox.isSelected() && attributeBox.isSelected()){ 
    		viewFluxAttributeSubnet();
    	} else if (fluxBox.isSelected() && !attributeBox.isSelected()){
    		viewFluxSubnet();
    	} else if (!fluxBox.isSelected() && attributeBox.isSelected()){
    		viewAttributeSubnet();
    	} else if (!fluxBox.isSelected() && !attributeBox.isSelected()){
    		NetworkViewTools.showAllNodesAndEdgesInCurrentView();	
    	}
    }
    
    @SuppressWarnings("unchecked")
	public static void viewFluxAttributeSubnet(){
    	String edgeAttribute = CyFluxViz.EDGE_FLUX_ATTRIBUTE;
    	
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
    	CyFluxVizPanel panel = CyFluxViz.getFvPanel();
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
    
    
	public static void viewFluxSubnet(){    	
    	String edgeAttribute = CyFluxViz.EDGE_FLUX_ATTRIBUTE;
    	
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
    	CyFluxVizPanel panel = CyFluxViz.getFvPanel();
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
    
    public static void viewNodeAttributeSubnet(String attributeName, Set selected, Boolean nullVisible){
    	
    	CyAttributes nodeAttrs = Cytoscape.getNodeAttributes();
    	byte attrType = nodeAttrs.getType(attributeName);
    	if (attrType != CyAttributes.TYPE_STRING){
    		System.out.println("CyFluxViz[INFO] -> Subnetworks only based on String attributes possible");
    		return;
    	}
    	
        //Calculate the set of visible nodes based on the attribute
        // and the set of selected values which should be displayed
        Set<CyNode> visibleNodes = new HashSet<CyNode>(); 
        List<CyNode> nodeList = Cytoscape.getCyNodesList();
		for (CyNode node: nodeList){
			String id = node.getIdentifier();
			if (selected.contains(nodeAttrs.getAttribute(id, attributeName))){
				visibleNodes.add(node);
			}
			else if (nullVisible==true && nodeAttrs.getAttribute(id, attributeName)==null){
				visibleNodes.add(node);
			}
		}
		
		CyNetworkView view = Cytoscape.getCurrentNetworkView();
    	
		NetworkViewTools.hideAllNodesAndEdgesInView(view);
		NetworkViewTools.showNodesInView(visibleNodes, view);
		NetworkViewTools.showEdgesBetweenNodesInView(visibleNodes, view);
		
		view.updateView();
    }
    
    public static void applyFluxVizView (String networkId){
    	CyNetwork network = Cytoscape.getNetwork(networkId);
    	if (network != null){
    		List<CyNetworkView> views = getCyNetworkViewsForNetworkId(networkId);
    		for (CyNetworkView view: views){
    			NetworkViewTools.setFluxVizVisualStyleForView(view);
    			applyVisualStyleToView(view);
    		}
    	}
    }
    	
    public static List<CyNetworkView> getCyNetworkViewsForNetworkId(String networkId){
    	List<CyNetworkView> views = new LinkedList<CyNetworkView> ();
    	HashMap<String, CyNetworkView> netViewMap = (HashMap<String, CyNetworkView>) 
    														Cytoscape.getNetworkViewMap();
    	for (CyNetworkView view: netViewMap.values()){
    		String viewNetId = view.getNetwork().getIdentifier();
    		if (viewNetId.equals(networkId)){
    			views.add(view);
    		} else if (view.getTitle().startsWith(networkId + "--child")){
    			views.add(view);
    		}
    	}
    	return views;
    }
        
    public static void applyVisualStyleToCurrentView(){
    	applyVisualStyleToView(Cytoscape.getCurrentNetworkView());
    }
    public static void applyVisualStyleToView(CyNetworkView view){
        view.updateView();
        view.redrawGraph(true,true);
    }
    
    
    //TODO BUG: This has do be done only once at the beginning !!!!!
    // -> alternative to loading the CyFluxViz Visual Style
    // create the style once at the beginning
    //much better via the new mapping structure to FluxDistributions

    public static void createCyFluxVizMapping(){
        CyNetwork network = Cytoscape.getCurrentNetwork();
        VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
        // NODE COLOR
        /* NodeAppearanceCalculator nodeAppCalc = vi_style.getNodeAppearanceCalculator();
        Calculator nodeColorCalculator = nodeAppCalc.getCalculator(VisualPropertyType.NODE_FILL_COLOR);
        ContinuousMapping continuousMapping = (ContinuousMapping)nodeColorCalculator.getMapping(0);
        continuousMapping.setControllingAttributeName(attribute, network, false);
        */
        
        //NODE SIZE 
        /*
      	Calculator nodeSizeCalculator = nodeAppCalc.getCalculator(VisualPropertyType.NODE_SIZE);
        ContinuousMapping continuousSizeMapping = (ContinuousMapping)nodeSizeCalculator.getMapping(0);
        continuousSizeMapping.setControllingAttributeName(attribute, network, false);
        */

        // EDGE WIDTH
        EdgeAppearanceCalculator edgeAppCalc = CyFluxViz.getViStyle().getEdgeAppearanceCalculator();
        Calculator edgeWidthCalculator = edgeAppCalc.getCalculator(VisualPropertyType.EDGE_LINE_WIDTH);
        ContinuousMapping continuousEdgeWidthMapping = (ContinuousMapping)edgeWidthCalculator.getMapping(0);
        continuousEdgeWidthMapping.setControllingAttributeName(CyFluxViz.EDGE_FLUX_ATTRIBUTE, network, false);
        
        // EDGE TARGET ARROWS
        Calculator edgeTargetArrowCalculator = edgeAppCalc.getCalculator(VisualPropertyType.EDGE_TGTARROW_SHAPE);
        DiscreteMapping discreteEdgeTargetArrowMapping = (DiscreteMapping)edgeTargetArrowCalculator.getMapping(0);
        discreteEdgeTargetArrowMapping.setControllingAttributeName(CyFluxViz.EDGE_DIRECTION_ATTRIBUTE, network, false);
        discreteEdgeTargetArrowMapping.putMapValue(-1, ArrowShape.NONE);
        discreteEdgeTargetArrowMapping.putMapValue(1, ArrowShape.DELTA);

        // EDGE TOOLTIP
        Calculator edgeTooltipCalculator = edgeAppCalc.getCalculator(VisualPropertyType.EDGE_TOOLTIP);
        PassThroughMapping edgeTooltipMapping = (PassThroughMapping)edgeTooltipCalculator.getMapping(0);
        edgeTooltipMapping.setControllingAttributeName(CyFluxViz.EDGE_FLUX_ATTRIBUTE, network, false);

        //Apply the changes
        vmm.applyAppearances();
    }
}
