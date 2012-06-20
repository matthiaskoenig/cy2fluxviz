package cyfluxviz.mapping;

import java.util.List;

import cytoscape.visual.mappings.BoundaryRangeValues;
import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.mappings.continuous.ContinuousMappingPoint;


/* Define points for the EdgeWidthMapping and change the Mapper. */
public class EdgeWidthMapping {
	
	private static final double DEFAULTMINWIDTH = 1.0;
	private ContinuousMapping mapping;
	private List<ContinuousMappingPoint> points;		
	
	/* Initialisation generates a linear mapping with the maximal 
	 * flux value. */
	public EdgeWidthMapping(ContinuousMapping mapping, double flux, double edgeWidth){
		this.mapping = mapping;
		this.points = mapping.getAllPoints();
		double[] fluxes = {0.0, flux};
		double[] edgeWidths = {DEFAULTMINWIDTH, edgeWidth};
		linearMapping(fluxes, edgeWidths);	
	}
	
	/* Generate a linear mapping between given setpoints. */
	public EdgeWidthMapping(ContinuousMapping mapping, double[] fluxes, double[] edgeWidths){
		this.mapping = mapping;
		this.points = mapping.getAllPoints();
		linearMapping(fluxes, edgeWidths);	
	}
	
	public ContinuousMapping getMapping(){
		return mapping;
	}
	
	private void clearPointsFromMapping(){
		points.clear();
	}

	private void addPoint(Double flux, Double edgeWidth){
		BoundaryRangeValues brv = new BoundaryRangeValues(edgeWidth, edgeWidth, edgeWidth);
		mapping.addPoint(flux, brv);
	}
	
	/**
	 * Generate a linear Mapping between flux and edgeWidth.
	 * The values between the setpoints are linear interpolated.
	 * based on one point.
	 */
	private void linearMapping(double[] fluxes, double[] edgeWidths){
		assert (fluxes.length == edgeWidths.length) : "Length of fluxes and edgeWidths has to be equal."; 
		
		clearPointsFromMapping();
		for (int i=0; i<fluxes.length; ++i){
			addPoint(fluxes[i], edgeWidths[i]);
		}
	}
	
	public String toString(){	 
		List<ContinuousMappingPoint> points = mapping.getAllPoints();
		String output = "### ContinousMapping ###\n";
		for (ContinuousMappingPoint p : points){
			output += p + "\n";
			output += p.getValue() + ":\t" + p.getRange();
		}
		return output;
	}
}
