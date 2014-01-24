package cyfluxviz.visual.style;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import cyfluxviz.CyFluxVizPlugin;
import cyfluxviz.util.AttributeUtils;
import cysbml.CySBMLConstants;
import cytoscape.data.Semantics;
import cytoscape.visual.ArrowShape;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.calculators.BasicCalculator;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.mappings.BoundaryRangeValues;
import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.PassThroughMapping;

/** Helper Class to generate the various calculators used 
 * in the CyFluxViz VisualStyles.
 * 
 * NodeCalculators and EdgeCalculators are generated here.
 * @author mkoenig
 *
 */
public class CalculatorFactory {
	
	
	/////////////// NODES //////////////////////////////////////////////////////////////////
	
	/** Calculator with mapping for NODE_SHAPE. */
	public static Calculator createNodeShapeCalculator(){
		DiscreteMapping map = new DiscreteMapping(NodeShape.class, CySBMLConstants.ATT_TYPE);
		
		map.putMapValue(CySBMLConstants.NODETYPE_SPECIES, NodeShape.ELLIPSE);
		map.putMapValue(CySBMLConstants.NODETYPE_REACTION, NodeShape.DIAMOND);
		map.putMapValue(CySBMLConstants.NODETYPE_QUAL_SPECIES, NodeShape.ELLIPSE);
		map.putMapValue(CySBMLConstants.NODETYPE_QUAL_TRANSITION, NodeShape.DIAMOND);

		BasicCalculator calc = new BasicCalculator("CYSBML_NODE_SHAPE", 
											map, VisualPropertyType.NODE_SHAPE);
		return calc;
	}
	
	
	/** Calculator with mapping for NODE_SIZE. */
	public static Calculator createNodeSizeCalculatorKinetic(){
		ContinuousMapping map = new ContinuousMapping(Double.class, CyFluxVizPlugin.NODE_CONCENTRATION_ATTRIBUTE);
		
		// add some basic points
		map.addPoint(0.0, new BoundaryRangeValues(1.0, 1.0, 1.0));
		map.addPoint(10.0, new BoundaryRangeValues(150.0, 150.0, 150.0));
		
		BasicCalculator calc = new BasicCalculator("CyFluxViz_NODE_SIZE", map, VisualPropertyType.NODE_SIZE);
		return calc;	
	}
	
	/** Calculator with mapping for NODE_SHAPE. */
	public static Calculator createNodeShapeCalculatorC13(){
		DiscreteMapping map = new DiscreteMapping(NodeShape.class, CySBMLConstants.ATT_TYPE);
		
		map.putMapValue(CySBMLConstants.NODETYPE_SPECIES, NodeShape.ROUND_RECT);
		map.putMapValue(CySBMLConstants.NODETYPE_REACTION, NodeShape.DIAMOND);
		map.putMapValue(CySBMLConstants.NODETYPE_QUAL_SPECIES, NodeShape.ROUND_RECT);
		map.putMapValue(CySBMLConstants.NODETYPE_QUAL_TRANSITION, NodeShape.DIAMOND);

		BasicCalculator calc = new BasicCalculator("CYSBML_NODE_SHAPE", 
											map, VisualPropertyType.NODE_SHAPE);
		return calc;
	}
	
	
	/** Calculator with mapping for NODE_LABEL_OPACITY. */
	public static Calculator createNodeLabelOpacityCalculator(){
		DiscreteMapping map = new DiscreteMapping(Double.class, CySBMLConstants.ATT_TYPE);
		
		map.putMapValue(CySBMLConstants.NODETYPE_SPECIES, new Double(255.0));
		map.putMapValue(CySBMLConstants.NODETYPE_REACTION, new Double(1.0));
		map.putMapValue(CySBMLConstants.NODETYPE_QUAL_SPECIES, new Double(255.0));
		map.putMapValue(CySBMLConstants.NODETYPE_QUAL_TRANSITION, new Double(1.0));

		BasicCalculator calc = new BasicCalculator("CYSBML_NODE_LABEL_OPACITY", 
											map, VisualPropertyType.NODE_LABEL_OPACITY);
		return calc;
	}
	
	
	/** Calculator with mapping for NODE_SIZE. */
	public static Calculator createNodeSizeCalculator(){
		DiscreteMapping map = new DiscreteMapping(Double.class, CySBMLConstants.ATT_TYPE);
		
		map.putMapValue(CySBMLConstants.NODETYPE_SPECIES, new Double(35.0));
		map.putMapValue(CySBMLConstants.NODETYPE_REACTION, new Double(1.0));
		map.putMapValue(CySBMLConstants.NODETYPE_QUAL_SPECIES, new Double(35.0));
		map.putMapValue(CySBMLConstants.NODETYPE_QUAL_TRANSITION, new Double(1.0));

		return new BasicCalculator("CYSBML_NODE_SIZE", 
											map, VisualPropertyType.NODE_SIZE);	
	}
	
	
    /** Calculator with mapping for NODE_LABEL. */
	public static Calculator createNodeLabelCalculator(){
		PassThroughMapping map = new PassThroughMapping(String.class , CySBMLConstants.ATT_NAME);
		BasicCalculator calc = new BasicCalculator("CYSBML_NODE_LABEL", map, VisualPropertyType.NODE_LABEL);
		return calc;
	}
	
	
	 /** Calculator with mapping for NODE_FILL_COLOR. */
	public static Calculator createNodeFillColorCalculator(){
	
		DiscreteMapping map = new DiscreteMapping(Color.class, CySBMLConstants.ATT_REVERSIBLE);
		// Color crev = new Color(0,153,0);  //green
		Color crev = new Color(204,204,204);  	 //gray
		map.putMapValue(true, crev);
		
		Color cirrev = new Color(255,102,102); //red
		map.putMapValue(false, cirrev);
		
		BasicCalculator calc = new BasicCalculator("CYSBML_NODE_FILL_COLOR", map, VisualPropertyType.NODE_FILL_COLOR);
		return calc;
	}
	
	
	 /** Calculator with mapping for NODE_BORDER_COLOR. */
	public static Calculator createNodeBorderColorCalculator(){
		Set<Object> compartments = AttributeUtils.getValueSet(CySBMLConstants.ATT_COMPARTMENT);
		List<String> colorClasses = new LinkedList<String>();
		for (Object obj: compartments){
			colorClasses.add( (String) obj);
		}
		
		List<Color> colorSet = new LinkedList<Color>();
		colorSet.add(Color.BLACK);
		colorSet.add(Color.RED);
		colorSet.add(Color.BLUE);
		colorSet.add(Color.ORANGE);
		colorSet.add(Color.GREEN);
		colorSet.add(Color.CYAN);
		colorSet.add(Color.MAGENTA);
		colorSet.add(Color.PINK);
				
		DiscreteMapping map = new DiscreteMapping(Color.class, CySBMLConstants.ATT_COMPARTMENT);
		map.putMapValue("-", Color.LIGHT_GRAY);
		map.putMapValue("", Color.WHITE);
		int k=0;
		for (String colorClass: colorClasses){
			if (colorClass.equals("-") || colorClass.equals("")){
				continue;
			}
			
			if (k<colorSet.size()){
				map.putMapValue(colorClass, colorSet.get(k));
			} else {
				map.putMapValue(colorClass, Color.LIGHT_GRAY);
			}
			k++;
		}
		BasicCalculator calc = new BasicCalculator("CYSBML_NODE_BORDER_COLOR", map, VisualPropertyType.NODE_BORDER_COLOR);
		return calc;	
	}
	
	 /** Calculator with mapping for NODE_LINE_WIDTH. */
	public static Calculator createNodeLineWidthCalculator(){
		DiscreteMapping map = new DiscreteMapping(Double.class, CySBMLConstants.ATT_TYPE);
		map.putMapValue(CySBMLConstants.NODETYPE_SPECIES, 5.0);
		map.putMapValue(CySBMLConstants.NODETYPE_REACTION, 1.0);
		map.putMapValue(CySBMLConstants.NODETYPE_QUAL_SPECIES, 5.0);
		map.putMapValue(CySBMLConstants.NODETYPE_QUAL_TRANSITION, 1.0);
		return new BasicCalculator("CYSBML_NODE_LINE_WIDTH", map, VisualPropertyType.NODE_LINE_WIDTH);
	}
	
	/////////////// EDGES //////////////////////////////////////////////////////////////////
	
	 /** Calculator with mapping for EDGE_LINE_WIDTH. */
	public static Calculator createEdgeWidthCalculator(){
		ContinuousMapping map = new ContinuousMapping(Double.class, CyFluxVizPlugin.EDGE_FLUX_ATTRIBUTE);
		BasicCalculator calc = new BasicCalculator("CyFluxViz_EDGE_LINE_WIDTH", map, VisualPropertyType.EDGE_LINE_WIDTH);
		return calc;
	}
    
	/** Calculator with mapping for EDGE_TOOLTIP. */
	public static Calculator createEdgeTooltipCalculator(){
		PassThroughMapping map = new PassThroughMapping(String.class, CyFluxVizPlugin.EDGE_FLUX_ATTRIBUTE);
		return new BasicCalculator("CyFluxViz_EDGE_TOOLTIP", map, VisualPropertyType.EDGE_TOOLTIP);
	}
	
	/** Calculator with mapping for EDGE_TGTARROW_SHAPE. */
	public static Calculator createEdgeTargetArrowCalculator(){
		
		DiscreteMapping map = new DiscreteMapping(ArrowShape.class, CyFluxVizPlugin.EDGE_DIRECTION_ATTRIBUTE);
	    map.putMapValue(-1, ArrowShape.NONE);
	    map.putMapValue(1, ArrowShape.DELTA);
		return new BasicCalculator("CyFluxViz_EDGE_TGTARROW_SHAPE", map, VisualPropertyType.EDGE_TGTARROW_SHAPE);
	}
	    
	/** Calculator with mapping for EDGE_COLOR. */
	public static Calculator createEdgeColorCalculator(){
		
		DiscreteMapping map = new DiscreteMapping(Color.class, Semantics.INTERACTION);

		map.putMapValue(CySBMLConstants.EDGETYPE_REACTION_PRODUCT, Color.BLACK);
		map.putMapValue(CySBMLConstants.EDGETYPE_REACTION_REACTANT, Color.BLACK);
		map.putMapValue(CySBMLConstants.EDGETYPE_REACTION_MODIFIER, Color.BLUE);
		map.putMapValue(CySBMLConstants.EDGETYPE_TRANSITION_OUTPUT, Color.BLACK);
		map.putMapValue(CySBMLConstants.EDGETYPE_TRANSITION_INPUT, Color.BLACK);
		return new BasicCalculator("CYSBML_EDGE_COLOR", map, VisualPropertyType.EDGE_COLOR);
	}
	
	/** Calculator with mapping for EDGE_OPACITY. */
	public static Calculator createEdgeOpacityCalculator(){
		DiscreteMapping map = new DiscreteMapping(Integer.class, Semantics.INTERACTION);
		Integer opacity = 200;
		map.putMapValue(CySBMLConstants.EDGETYPE_REACTION_PRODUCT, opacity);
		map.putMapValue(CySBMLConstants.EDGETYPE_REACTION_REACTANT, opacity);
		map.putMapValue(CySBMLConstants.EDGETYPE_REACTION_MODIFIER, opacity);
		map.putMapValue(CySBMLConstants.EDGETYPE_TRANSITION_OUTPUT, opacity);
		map.putMapValue(CySBMLConstants.EDGETYPE_TRANSITION_INPUT, opacity);
		return new BasicCalculator("CYSBML_EDGE_OPACITY",
                         map, VisualPropertyType.EDGE_OPACITY);
	}
	
}
