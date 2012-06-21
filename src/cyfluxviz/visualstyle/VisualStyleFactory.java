package cyfluxviz.visualstyle;

import java.awt.Color;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.Semantics;

import cytoscape.visual.ArrowShape;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualPropertyDependency;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.BasicCalculator;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.PassThroughMapping;

import cyfluxviz.CyFluxViz;
import cyfluxviz.mapping.EdgeWidthMapping;
import cysbml.CySBMLConstants;

public class VisualStyleFactory {
	
	public static VisualStyle setFluxVizVisualStyle(){
		VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
		CalculatorCatalog calcCatalog = vmm.getCalculatorCatalog();
		VisualStyle vs = calcCatalog.getVisualStyle(CyFluxViz.DEFAULTVISUALSTYLE);
		if (vs == null){
			// only create once
			vs = createFluxVizVisualStyle(Cytoscape.getCurrentNetwork());
			calcCatalog.addVisualStyle(vs);
		}
		vmm.setVisualStyle(vs);
		vmm.applyAppearances();
		return vs;
	}
	    	    
	private static VisualStyle createFluxVizVisualStyle(CyNetwork network) {
		NodeAppearanceCalculator nodeAppCalc = new NodeAppearanceCalculator();
		nodeAppCalc.setCalculator(createNodeShapeCalculator(network));
		nodeAppCalc.setCalculator(createNodeLabelCalculator(network));
		
		EdgeAppearanceCalculator edgeAppCalc = new EdgeAppearanceCalculator();
		edgeAppCalc.setCalculator(createEdgeWidthCalculator(network));
		edgeAppCalc.setCalculator(createEdgeTooltipCalculator(network));
		edgeAppCalc.setCalculator(createEdgeTargetArrowCalculator(network));
		edgeAppCalc.setCalculator(createEdgeColorCalculator(network));
		edgeAppCalc.setCalculator(createEdgeOpacityCalculator(network));
		
		VisualMappingManager vmManager = Cytoscape.getVisualMappingManager();
		
		GlobalAppearanceCalculator gac = vmManager.getVisualStyle().getGlobalAppearanceCalculator();
		gac.setDefaultBackgroundColor(new Color(new Float(0.95), new Float(0.95), new Float(0.95)));
		
		VisualStyle visualStyle = new VisualStyle(CyFluxViz.DEFAULTVISUALSTYLE, nodeAppCalc, edgeAppCalc, gac);
		VisualPropertyDependency deps = visualStyle.getDependency();
		deps.set(VisualPropertyDependency.Definition.NODE_SIZE_LOCKED, true);
		return visualStyle;
	}
    
    // NODE SHAPE
	private static Calculator createNodeLabelCalculator(CyNetwork network){
		PassThroughMapping mapping = new PassThroughMapping(new String(), ObjectMapping.NODE_MAPPING);
		mapping.setControllingAttributeName(CySBMLConstants.ATT_NAME, network, false);
		return new BasicCalculator("CYSBML_NODE_LABEL", mapping, VisualPropertyType.NODE_LABEL);
	}
	
	private static Calculator createNodeShapeCalculator(CyNetwork network){
		DiscreteMapping disMapping = new DiscreteMapping(NodeShape.RECT,
                ObjectMapping.NODE_MAPPING);
				disMapping.setControllingAttributeName(CySBMLConstants.ATT_TYPE, network, false);
		disMapping.putMapValue(CySBMLConstants.NODETYPE_SPECIES, NodeShape.ELLIPSE);
		disMapping.putMapValue(CySBMLConstants.NODETYPE_REACTION, NodeShape.DIAMOND);
		disMapping.putMapValue(CySBMLConstants.NODETYPE_QUAL_SPECIES, NodeShape.ELLIPSE);
		disMapping.putMapValue(CySBMLConstants.NODETYPE_QUAL_TRANSITION, NodeShape.DIAMOND);
		return new BasicCalculator("CYSBML_NODE_SHAPE", disMapping, VisualPropertyType.NODE_SHAPE);
	}
	
    // EDGE WIDTH
	private static Calculator createEdgeWidthCalculator(CyNetwork network){
		ContinuousMapping mapping = new ContinuousMapping(new Double(1.0), ObjectMapping.NODE_MAPPING);
		EdgeWidthMapping edgeWidthMapping = new EdgeWidthMapping(mapping, 5.0, 40.0);
		mapping.setControllingAttributeName(CyFluxViz.EDGE_FLUX_ATTRIBUTE, network, false);
		return new BasicCalculator("CyFluxViz_EDGE_LINE_WIDTH", mapping, VisualPropertyType.EDGE_LINE_WIDTH);
	}
    
	// EDGE TOOLTIP
	private static Calculator createEdgeTooltipCalculator(CyNetwork network){
		PassThroughMapping mapping = new PassThroughMapping(new String(), ObjectMapping.EDGE_MAPPING);
        mapping.setControllingAttributeName(CyFluxViz.EDGE_FLUX_ATTRIBUTE, network, false);
		return new BasicCalculator("CyFluxViz_EDGE_TOOLTIP",
										mapping, VisualPropertyType.EDGE_TOOLTIP);
	}
	
	// EDGE TARGET ARROW
	private static Calculator createEdgeTargetArrowCalculator(CyNetwork network){
		DiscreteMapping mapping = new DiscreteMapping(ArrowShape.DELTA, ObjectMapping.NODE_MAPPING);
	    mapping.setControllingAttributeName(CyFluxViz.EDGE_DIRECTION_ATTRIBUTE, network, false);
	    mapping.putMapValue(-1, ArrowShape.NONE);
	    mapping.putMapValue(1, ArrowShape.DELTA);
		return new BasicCalculator("CyFluxViz_EDGE_TGTARROW_SHAPE", mapping, VisualPropertyType.EDGE_TGTARROW_SHAPE);
	}
	    
	// EDGE COLOR
	private static Calculator createEdgeColorCalculator(CyNetwork network){
		DiscreteMapping edgeColorMapping = new DiscreteMapping(Color.BLACK,
                ObjectMapping.EDGE_MAPPING);
		edgeColorMapping.setControllingAttributeName(Semantics.INTERACTION, network, false);
		edgeColorMapping.putMapValue(CySBMLConstants.EDGETYPE_REACTION_PRODUCT, Color.BLACK);
		edgeColorMapping.putMapValue(CySBMLConstants.EDGETYPE_REACTION_REACTANT, Color.BLACK);
		edgeColorMapping.putMapValue(CySBMLConstants.EDGETYPE_REACTION_MODIFIER, Color.BLUE);
		edgeColorMapping.putMapValue(CySBMLConstants.EDGETYPE_TRANSITION_OUTPUT, Color.BLACK);
		edgeColorMapping.putMapValue(CySBMLConstants.EDGETYPE_TRANSITION_INPUT, Color.BLACK);
		return new BasicCalculator("CYSBML_EDGE_COLOR",
                         edgeColorMapping, VisualPropertyType.EDGE_COLOR);
	}
	// EDGE OPACITY
	private static Calculator createEdgeOpacityCalculator(CyNetwork network){
		int opacity = 200;
		DiscreteMapping edgeColorMapping = new DiscreteMapping(opacity,
                ObjectMapping.EDGE_MAPPING);
		edgeColorMapping.setControllingAttributeName(Semantics.INTERACTION, network, false);
		edgeColorMapping.putMapValue(CySBMLConstants.EDGETYPE_REACTION_PRODUCT, opacity);
		edgeColorMapping.putMapValue(CySBMLConstants.EDGETYPE_REACTION_REACTANT, opacity);
		edgeColorMapping.putMapValue(CySBMLConstants.EDGETYPE_REACTION_MODIFIER, opacity);
		edgeColorMapping.putMapValue(CySBMLConstants.EDGETYPE_TRANSITION_OUTPUT, opacity);
		edgeColorMapping.putMapValue(CySBMLConstants.EDGETYPE_TRANSITION_INPUT, opacity);
		return new BasicCalculator("CYSBML_EDGE_OPACITY",
                         edgeColorMapping, VisualPropertyType.EDGE_OPACITY);
	}
}
