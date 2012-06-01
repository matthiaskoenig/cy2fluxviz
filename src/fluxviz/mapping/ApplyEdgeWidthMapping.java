package fluxviz.mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.mappings.continuous.ContinuousMappingPoint;
import cytoscape.visual.ui.editors.continuous.EditorValueRangeTracer;
import fluxviz.CyFluxVizPlugin;
import fluxviz.util.FileUtil;

/**
 * Apply a given EdgeWidthMapping to the system.
 * @author mkoenig
 *
 */
public class ApplyEdgeWidthMapping {

	/** Enum for the different Mapping types*/
	public static enum MappingType {
	    LINEAR, LOG 
	}
	
	private double maxFlux;
	private double minEdgeWidth;
	private double maxEdgeWidth;
	@SuppressWarnings("unused")
	private MappingType mtype;
	
	public ApplyEdgeWidthMapping(double maxFlux, double minEdgeWidth, double maxEdgeWidth, MappingType mtype){
		this.maxFlux = maxFlux;
		this.minEdgeWidth = minEdgeWidth;
		this.maxEdgeWidth = maxEdgeWidth;
		this.mtype = mtype;
	}
	
	/**
	 * Change the mapping based on the current minimal, maximal
	 */
	public void changeMapping(){
		Logger logger = CyFluxVizPlugin.getLogger();
		logger.info("ApplyEdgeWidthMapping.changeMapping()");

		// Get network, Visual Mapper, CalculatorCatalog and the VisualStyle
		@SuppressWarnings("unused")
		CyNetwork network = Cytoscape.getCurrentNetwork();
		VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
		CalculatorCatalog calc_cat = vmm.getCalculatorCatalog();
        if (calc_cat.getVisualStyle(CyFluxVizPlugin.getVsName()) == null){
        		FileUtil.loadViStyle();
        }
		//Set the visual style (necessary every time ?)
		Cytoscape.getCurrentNetworkView().setVisualStyle(CyFluxVizPlugin.getVsName());
		vmm.setVisualStyle(CyFluxVizPlugin.getViStyle());
	
    
		// 3. EDGE WIDTH
		//Get the appearance calculators from style
		VisualStyle vi_style = CyFluxVizPlugin.getViStyle();
		
		EdgeAppearanceCalculator edgeAppCalc = vi_style.getEdgeAppearanceCalculator();
		Calculator edgeWidthCalculator = edgeAppCalc.getCalculator(VisualPropertyType.EDGE_LINE_WIDTH);
		ContinuousMapping mapping = (ContinuousMapping)edgeWidthCalculator.getMapping(0);
		
		// Change the points in the existing mapping
		printMapping();
		// Calculate points based on the mapping type
		// In log mapping additional help points have to be calculated
		double[] fluxes = {0.0, maxFlux};
		double[] edgeWidths = {minEdgeWidth, maxEdgeWidth};
		@SuppressWarnings("unused")
		EdgeWidthMapping newMapping = new EdgeWidthMapping(mapping, fluxes, edgeWidths);
		printMapping();
		
		// Change the upper and lower values
		VisualPropertyType type = VisualPropertyType.EDGE_LINE_WIDTH;
		EditorValueRangeTracer.getTracer().setMin(type, 0.0);
		EditorValueRangeTracer.getTracer().setMax(type, maxFlux);
		//ContinuousMappingEditorPanel.updateMap();
		Cytoscape.getCurrentNetworkView().redrawGraph(false, true);
		
		
		//Apply the changes
		vmm.applyAppearances();
		//Update the view
		Cytoscape.getCurrentNetworkView().updateView();
	}
	
	
	/**
	 * Prints the points in the current mapping of the current visual style.
	 */
	public void printMapping(){
		VisualStyle vi_style = CyFluxVizPlugin.getViStyle();
		EdgeAppearanceCalculator edgeAppCalc = vi_style.getEdgeAppearanceCalculator();
		Calculator edgeWidthCalculator = edgeAppCalc.getCalculator(VisualPropertyType.EDGE_LINE_WIDTH);
		
		//get mapping from calculator
		ContinuousMapping continuousEdgeWidthMapping = (ContinuousMapping)edgeWidthCalculator.getMapping(0);
		printMapping(continuousEdgeWidthMapping); 
		
	}
	/**
	 * Prints the points in given mapping.
	 */
	public void printMapping(ContinuousMapping mapping){
		//get the mapping points 
		List<ContinuousMappingPoint> points = mapping.getAllPoints();
		System.out.println("\n### ContinousMapping ###");
		for (ContinuousMappingPoint p : points){
			System.out.println(p);
			System.out.println(p.getValue() + ":\t" + p.getRange());
		}
	}

	
	/**
	 * Test changes in the EdgeWidth mapping.
	 */
	public void test(){
		Logger logger = CyFluxVizPlugin.getLogger();
		logger.info("ApplyEdgeWidthMapping.test()");

		// Get network, Visual Mapper, CalculatorCatalog and the VisualStyle
		@SuppressWarnings("unused")
		CyNetwork network = Cytoscape.getCurrentNetwork();
		VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
		CalculatorCatalog calc_cat = vmm.getCalculatorCatalog();
        if (calc_cat.getVisualStyle(CyFluxVizPlugin.getVsName()) == null){
        		FileUtil.loadViStyle();
        }
			
		//Set the visual style (necessary every time ?)
		Cytoscape.getCurrentNetworkView().setVisualStyle(CyFluxVizPlugin.getVsName());
		vmm.setVisualStyle(CyFluxVizPlugin.getViStyle());
	
    
		// 3. EDGE WIDTH
		//Get the appearance calculators from style
		VisualStyle vi_style = CyFluxVizPlugin.getViStyle();
		
		EdgeAppearanceCalculator edgeAppCalc = vi_style.getEdgeAppearanceCalculator();
		Calculator edgeWidthCalculator = edgeAppCalc.getCalculator(VisualPropertyType.EDGE_LINE_WIDTH);
		ContinuousMapping mapping = (ContinuousMapping)edgeWidthCalculator.getMapping(0);
		
		// Change the points in the existing mapping
		printMapping();
		double[] fluxes = {0.0, maxFlux};
		double[] edgeWidths = {minEdgeWidth, maxEdgeWidth};
		@SuppressWarnings("unused")
		EdgeWidthMapping newMapping = new EdgeWidthMapping(mapping, fluxes, edgeWidths);
		printMapping();
		
		
		// Generate a new mapping and apply to the calculator
		/*
		if (true){
			logger.info("Generate new mapping");
			// Add the mapping points
			
			//Create a new Continouss mapping and replace the old one
			//change the mapping in the calculator
			int selectedRow = FluxViz.getFvPanel().getFluxTable().getSelectedRow();
			String attribute = (String)FluxViz.getFvPanel().getTableModel().getValueAt(selectedRow, 0); 
			
			//Generate new mapping
			double defaultObj = 1.0;
			ContinuousMapping newMapping = new ContinuousMapping(defaultObj, ObjectMapping.EDGE_MAPPING);
			newMapping.setControllingAttributeName(attribute, network, false);

			BoundaryRangeValues brv_low = new BoundaryRangeValues(1.0, 1.0, 1.0);
			BoundaryRangeValues brv_up = new BoundaryRangeValues(maxEdgeWidth, maxEdgeWidth, maxEdgeWidth);			
			mapping.addPoint(0.0, brv_low);
			mapping.addPoint(maxFlux, brv_up);

			
			//current flux distribution is controlling attribute
			BasicCalculator newCalculator = new BasicCalculator("testCalculator", newMapping, VisualPropertyType.EDGE_LINE_WIDTH);
			
			printMapping();
			//mapping has to be set in the catalog
			edgeWidthCalculator = newCalculator;
			
			//
			printMapping();
			
		}
		*/
		
		//Apply the changes
		vmm.applyAppearances();
		//Update the view
		Cytoscape.getCurrentNetworkView().updateView();
		
	}

}
