package cyfluxviz.visual.style;

import java.awt.Color;
import java.util.Collection;
import java.util.HashSet;

import cyfluxviz.CyFluxVizPlugin;
import cytoscape.Cytoscape;

import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.NodeAppearanceCalculator;

import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualPropertyDependency;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.Calculator;


/** Factory for generating the visual styles for CyFluxViz.
 * Here all the mappings between the flux/concentration attributes and 
 * node/edge attributes are defined and stored in a visual style.
 * 
 * Here dependencies between CyFluxViz and CySBML occur. 
 * It should be tested if the classes from CySBML are accessible.
 * TODO: Some alternative solutions to allow large networks without the overhead of CySBML.
 * @author mkoenig
 *
 */
public class VisualStyleFactory {

	public static VisualStyle createVisualStyle(String name){
		VisualStyle vs = null;
		if (name.equals(CyFluxVizStyles.DEFAULTVISUALSTYLE)){
			vs = createDefaultVisualStyle();
		} else if (name.equals(CyFluxVizStyles.C13STYLE)){
			vs = createC13VisualStyle();
		} else if (name.equals(CyFluxVizStyles.KINETICSTYLE))
			vs = createKineticVisualStyle();
		else {
			CyFluxVizPlugin.LOGGER.warning("VisualStyle not supported by VisualStyleFactory -> " + name);
		}
		return vs;
	}
	
	/** Creates the standard/default CyFluxViz VisualStyle. */
	private static VisualStyle createDefaultVisualStyle() {
		
		Collection<Calculator> ncalcs = new HashSet<Calculator>();
		ncalcs.add(CalculatorFactory.createNodeShapeCalculator());
		ncalcs.add(CalculatorFactory.createNodeLabelCalculator());
		ncalcs.add(CalculatorFactory.createNodeFillColorCalculator());
		ncalcs.add(CalculatorFactory.createNodeBorderColorCalculator());
		ncalcs.add(CalculatorFactory.createNodeLineWidthCalculator());
		
		Collection<Calculator> ecalcs = new HashSet<Calculator>();
		ecalcs.add(CalculatorFactory.createEdgeWidthCalculator());
		ecalcs.add(CalculatorFactory.createEdgeTooltipCalculator());
		ecalcs.add(CalculatorFactory.createEdgeTargetArrowCalculator());
		ecalcs.add(CalculatorFactory.createEdgeColorCalculator());
		ecalcs.add(CalculatorFactory.createEdgeOpacityCalculator());

		VisualStyle vs = createVisualStyle(CyFluxVizStyles.DEFAULTVISUALSTYLE, ncalcs, ecalcs);
		return vs;
	}
	

	private static VisualStyle createC13VisualStyle() {
		
		Collection<Calculator> ncalcs = new HashSet<Calculator>();
		ncalcs.add(CalculatorFactory.createNodeShapeCalculatorC13());
		ncalcs.add(CalculatorFactory.createNodeLabelCalculator());
		ncalcs.add(CalculatorFactory.createNodeSizeCalculator());
		ncalcs.add(CalculatorFactory.createNodeLabelOpacityCalculator());
		
		Collection<Calculator> ecalcs = new HashSet<Calculator>();
		ecalcs.add(CalculatorFactory.createEdgeWidthCalculator());
		ecalcs.add(CalculatorFactory.createEdgeTooltipCalculator());
		ecalcs.add(CalculatorFactory.createEdgeTargetArrowCalculator());
		ecalcs.add(CalculatorFactory.createEdgeColorCalculator());
		ecalcs.add(CalculatorFactory.createEdgeOpacityCalculator());
		
		VisualStyle vs = createVisualStyle(CyFluxVizStyles.C13STYLE, ncalcs, ecalcs);
		return vs;
	}
	

	private static VisualStyle createKineticVisualStyle() {

		Collection<Calculator> ncalcs = new HashSet<Calculator>();
		ncalcs.add(CalculatorFactory.createNodeShapeCalculator());
		ncalcs.add(CalculatorFactory.createNodeLabelCalculator());
		ncalcs.add(CalculatorFactory.createNodeSizeCalculatorKinetic());
		ncalcs.add(CalculatorFactory.createNodeLabelOpacityCalculator());
		
		Collection<Calculator> ecalcs = new HashSet<Calculator>();
		ecalcs.add(CalculatorFactory.createEdgeWidthCalculator());
		ecalcs.add(CalculatorFactory.createEdgeTooltipCalculator());
		ecalcs.add(CalculatorFactory.createEdgeTargetArrowCalculator());
		ecalcs.add(CalculatorFactory.createEdgeColorCalculator());
		ecalcs.add(CalculatorFactory.createEdgeOpacityCalculator());
		
		VisualStyle vs = createVisualStyle(CyFluxVizStyles.KINETICSTYLE, ncalcs, ecalcs);
		return vs;
	}
	
	/** Creates VisualStyle from the node and edge appearance calculators. */
	private static VisualStyle createVisualStyle(String name, Collection<Calculator> nodeAppearanceCalculators,
			Collection<Calculator> edgeAppearanceCalculators) {
		CyFluxVizPlugin.LOGGER.info("createVisualStyle( " + name + " )");
		VisualStyle vs = new VisualStyle(name);
		VisualPropertyDependency deps = vs.getDependency();
		deps.set(VisualPropertyDependency.Definition.NODE_SIZE_LOCKED, true);
		
		// add node appearance
		NodeAppearanceCalculator nac = new NodeAppearanceCalculator(deps);
		for (Calculator nc: nodeAppearanceCalculators){
			nac.setCalculator(nc);
		}
		vs.setNodeAppearanceCalculator(nac);
		
		// add edge appearance
		EdgeAppearanceCalculator eac = new EdgeAppearanceCalculator(deps);
		for (Calculator ec: edgeAppearanceCalculators){
			eac.setCalculator(ec);
		}
		vs.setEdgeAppearanceCalculator(eac);
		
		// add global appearance
		VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
		GlobalAppearanceCalculator gac = vmm.getVisualStyle().getGlobalAppearanceCalculator();
		gac.setDefaultBackgroundColor(new Color(new Float(1.0), new Float(1.0), new Float(1.0)));
		vs.setGlobalAppearanceCalculator(gac);
		
		// Add to the catalog
		CalculatorCatalog cc = vmm.getCalculatorCatalog();
		cc.addVisualStyle(vs);
		return vs;
	}
	
	
	/** Adds the basic EdgeCalculators to the style for using as CyFluxViz style. */
	public static VisualStyle convertVisualStyleToFluxStyle(VisualStyle vs) {
		CyFluxVizPlugin.LOGGER.info("convertVisualStyleToFluxStyle");
		
		// add edge appearance
		EdgeAppearanceCalculator eac = new EdgeAppearanceCalculator(vs.getDependency());
		eac.setCalculator(CalculatorFactory.createEdgeWidthCalculator());
		eac.setCalculator(CalculatorFactory.createEdgeTooltipCalculator());
		eac.setCalculator(CalculatorFactory.createEdgeTargetArrowCalculator());
		eac.setCalculator(CalculatorFactory.createEdgeOpacityCalculator());
		vs.setEdgeAppearanceCalculator(eac);
		
		// Add to the catalog
		CalculatorCatalog cc = Cytoscape.getVisualMappingManager().getCalculatorCatalog();
		cc.addVisualStyle(vs);
		return vs;
	}
}
