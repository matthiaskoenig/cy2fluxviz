package cyfluxviz.mapping;

import cyfluxviz.CyFluxViz;
import cyfluxviz.netview.NetworkViewTools;
import cyfluxviz.visualstyle.VisualStyleFactory;
import cytoscape.Cytoscape;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.ui.editors.continuous.EditorValueRangeTracer;


public class ApplyEdgeWidthMapping {
	private double maxFlux;
	private double minEdgeWidth;
	private double maxEdgeWidth;
	
	public ApplyEdgeWidthMapping(double maxFlux, double minEdgeWidth, double maxEdgeWidth){
		this.maxFlux = maxFlux;
		this.minEdgeWidth = minEdgeWidth;
		this.maxEdgeWidth = maxEdgeWidth;
	}
	
	public void changeMapping(){
		VisualStyle vs = VisualStyleFactory.setFluxVizVisualStyle(); 
		
		EdgeAppearanceCalculator edgeAppCalc = vs.getEdgeAppearanceCalculator();
		Calculator edgeWidthCalculator = edgeAppCalc.getCalculator(VisualPropertyType.EDGE_LINE_WIDTH);
		ContinuousMapping mapping = (ContinuousMapping)edgeWidthCalculator.getMapping(0);
		
		// Calculate points based on the mapping type
		// In log mapping additional help points have to be calculated
		double[] fluxes = {0.0, maxFlux};
		double[] edgeWidths = {minEdgeWidth, maxEdgeWidth};
		@SuppressWarnings("unused")
		EdgeWidthMapping newMapping = new EdgeWidthMapping(mapping, fluxes, edgeWidths);
		
		// Change the upper and lower values
		VisualPropertyType type = VisualPropertyType.EDGE_LINE_WIDTH;
		EditorValueRangeTracer.getTracer().setMin(type, 0.0);
		EditorValueRangeTracer.getTracer().setMax(type, maxFlux);
		
		Cytoscape.getVisualMappingManager().applyAppearances();
		NetworkViewTools.applyFluxVizView(Cytoscape.getCurrentNetwork().getIdentifier());
		
		// necessary ?
		//Cytoscape.getCurrentNetworkView().redrawGraph(false, true);
		//Cytoscape.getCurrentNetworkView().updateView();
	}
	
	
	/* Prints the points in the current mapping of the current visual style. */
	public void printCurrentMapping(){
		VisualStyle vs = VisualStyleFactory.setFluxVizVisualStyle(); 
		
		EdgeAppearanceCalculator edgeAppCalc = vs.getEdgeAppearanceCalculator();
		Calculator edgeWidthCalculator = edgeAppCalc.getCalculator(VisualPropertyType.EDGE_LINE_WIDTH);
		ContinuousMapping continuousEdgeWidthMapping = (ContinuousMapping)edgeWidthCalculator.getMapping(0);
		System.out.println(continuousEdgeWidthMapping); 
	}
}