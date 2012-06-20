package cyfluxviz.netview;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import cyfluxviz.CyFluxViz;
import cyfluxviz.util.FileUtil;
import cyfluxviz.vizmap.LoadVizmap;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.ArrowShape;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.PassThroughMapping;

public class FluxDistributionView {
    public static void createFluxVizVisualStyle(){
		// Set the Visual Style at the beginning
		String filenameVS = FileUtil.getFluxVizDataDirectory() + "/data/" + CyFluxViz.DEFAULTVISUALSTYLE + ".props";
		LoadVizmap loadVM = new LoadVizmap(filenameVS);
		loadVM.loadPropertyFile();
		LoadVizmap.setCyFluxVizVisualStyle();
		// Create mapping to the defined attributes in the visual style
		FluxDistributionView.createCyFluxVizMapping();
    }
	
	
    /* Apply the Style & View to all views related to the network */
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
