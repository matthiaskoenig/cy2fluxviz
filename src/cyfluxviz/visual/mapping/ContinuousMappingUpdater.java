package cyfluxviz.visual.mapping;

import java.util.Vector;

import cytoscape.Cytoscape;

import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.mappings.BoundaryRangeValues;
import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.continuous.ContinuousMappingPoint;
import cytoscape.visual.ui.editors.continuous.EditorValueRangeTracer;

public class ContinuousMappingUpdater {

	
	/** Updates the mapping points within the ContinuousMapping associated
	 * with the VisualPropertyType.
	 * Points of the new mapping are specified via xvec and yvec.
	 * The mapping is updated for the current VisualStyle.
	 * 
	 * TODO: allow more complex mappings
	 * 
	 * @param vpt	
	 * @param xvec
	 * @param yvec
	 */
	public ContinuousMappingUpdater(){}
		
	/**
	 * Updates the mapping points in the associated mapping.
	 * @param vpt
	 * @param xvec
	 * @param yvec
	 */
	public static void updateMappingPoints(VisualPropertyType vpt, double[] xvec, double[] yvec){
		Calculator calc = getCalculatorForVisualPropertyType(vpt);
		
		// Get old mapping if existing
		Vector<ObjectMapping> maps = calc.getMappings();
		if (maps.size() == 0){
			System.out.println("CyFluxViz[INFO]: mapping does not exist in calculator -> " + vpt);
			return;
		}
		
		// TODO: In mapping are points which have to be removed/reused?
		// Currently all old points are removed and only the given points added
		ContinuousMapping map = (ContinuousMapping) calc.getMapping(0);
		createlinearMapping(map, xvec, yvec);
		
		// Boundaries for mapping view (lower and upper have to be adapted)
		Double min = xvec[0];
		Double max = xvec[xvec.length-1];
		EditorValueRangeTracer.getTracer().setMin(vpt, min);
		EditorValueRangeTracer.getTracer().setMax(vpt, max);
	}

	/** Removes all points from the mapping .*/
	private static void clearPoints(ContinuousMapping map){
		map.getAllPoints().clear();
	}

	/** Adds the mapping point (x,y) to the mapping. */
	private static void addPoint(ContinuousMapping map, Double x, Double y){
		BoundaryRangeValues brv = new BoundaryRangeValues(y, y, y);
		map.addPoint(x, brv);
	}
	
	/**
	 * Generate a linear Mapping between flux and edgeWidth.
	 * The values between the setpoints are linear interpolated.
	 * based on one point.
	 */
	private static void createlinearMapping(ContinuousMapping map, double[] xvec, double[] yvec){
		clearPoints(map);
		for (int i=0; i<xvec.length; ++i){
			addPoint(map, xvec[i], yvec[i]);
		}
	}
	
	/** Gets the calculator associated with the VisualPropertyType. 
	 * If no VisualStyle is set or no Calculator exists associated
	 * with the VisualPropertyType null is returned.
	 */
	private static Calculator getCalculatorForVisualPropertyType(VisualPropertyType vpt){
		Calculator calc = null;
		VisualStyle vs = Cytoscape.getVisualMappingManager().getVisualStyle();
		if (vs != null){
			EdgeAppearanceCalculator eac = vs.getEdgeAppearanceCalculator();
			calc = eac.getCalculator(vpt);
		}
		return calc;
	}
	
	/** Print the mapping points in the mapping. */
	public static void printMapping(ContinuousMapping map){
		String text = toStringMapping(map);
		System.out.println(text);
	}
	
	private static String toStringMapping(ContinuousMapping map){	 
		String output = "### ContinousMapping ###\n";
		for (ContinuousMappingPoint p : map.getAllPoints()){
			output += p + "\n";
			output += p.getValue() + ":\t" + p.getRange();
		}
		return output;
	}
}