package fluxviz.mapping;

import java.util.List;

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

public class ApplyEdgeWidthMapping {

	/* Enum for the different Mapping types*/
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
	
	/* Change the mapping based on the current minimal & maximal values. */
	public void changeMapping(){

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
		
		//Apply the changes & update the view
		vmm.applyAppearances();
		Cytoscape.getCurrentNetworkView().updateView();
	}
	
	
	/* Prints the points in the current mapping of the current visual style. */
	public void printMapping(){
		VisualStyle vi_style = CyFluxVizPlugin.getViStyle();
		EdgeAppearanceCalculator edgeAppCalc = vi_style.getEdgeAppearanceCalculator();
		Calculator edgeWidthCalculator = edgeAppCalc.getCalculator(VisualPropertyType.EDGE_LINE_WIDTH);
		
		//get mapping from calculator
		ContinuousMapping continuousEdgeWidthMapping = (ContinuousMapping)edgeWidthCalculator.getMapping(0);
		printMapping(continuousEdgeWidthMapping); 
	}
	
	/* Prints points in given mapping. */
	public void printMapping(ContinuousMapping mapping){
		//get the mapping points 
		List<ContinuousMappingPoint> points = mapping.getAllPoints();
		System.out.println("\n### ContinousMapping ###");
		for (ContinuousMappingPoint p : points){
			System.out.println(p);
			System.out.println(p.getValue() + ":\t" + p.getRange());
		}
	}
}