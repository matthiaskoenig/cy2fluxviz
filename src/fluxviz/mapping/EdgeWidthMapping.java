package fluxviz.mapping;

import java.util.ArrayList;
import java.util.List;

import cytoscape.visual.mappings.BoundaryRangeValues;
import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.mappings.continuous.ContinuousMappingPoint;


/**
 * Handle the edgeWidthMapping
 * @author mkoenig
 *
 *
 * Define some points for the mapping and change the mapper
 * 
 * Global vs. local settings.
 * For all the par
 * 
 */
public class EdgeWidthMapping {
	private static final double DEFAULTMINWIDTH = 1.0;
	
	
	/** Points for the flux, edgewidth mapping */
	private ContinuousMapping mapping;
	private List<ContinuousMappingPoint> points;		
	
	/** Initialisation generates a linear mapping with the maximal 
	 *	flux value;
	 */
	public EdgeWidthMapping(ContinuousMapping mapping, double flux, double edgeWidth){
		this.mapping = mapping;
		this.points = mapping.getAllPoints();
		double[] fluxes = {0.0, flux};
		double[] edgeWidths = {DEFAULTMINWIDTH, edgeWidth};
		linearMapping(fluxes, edgeWidths);	
	}
	/**
	 * Generates a linear mapping between the given setpoints.
	 * @param mapping
	 * @param fluxes
	 * @param edgeWidths
	 */
	public EdgeWidthMapping(ContinuousMapping mapping, double[] fluxes, double[] edgeWidths){
		this.mapping = mapping;
		this.points = mapping.getAllPoints();
		linearMapping(fluxes, edgeWidths);	
	}
	
	/**
	 * Clear all points from the mapping.
	 */
	public void clearPoints(){
		points.clear();
	}
	
	/**
	 * Adds additional point to the mapping at index in the list.
	 * @param flux
	 * @param edgeWidth
	 * @param index
	 */
	public void addPoint(Double flux, Double edgeWidth, int index){
		BoundaryRangeValues brv = new BoundaryRangeValues(edgeWidth, edgeWidth, edgeWidth);
		mapping.addPoint(flux, brv);
	}
	
	/**
	 * Generate a linear Mapping between flux and edgeWidth.
	 * The values between the setpoints are linear interpolated.
	 * based on one point.
	 */
	public void linearMapping(double[] fluxes, double[] edgeWidths){
		assert (fluxes.length == edgeWidths.length) : "Length of fluxes and edgeWidths has to be equal."; 
		
		clearPoints();
		// Rest points of mapping
		for (int i=0; i<fluxes.length; ++i){
			addPoint(fluxes[i], edgeWidths[i], i);
		}
	}
	
	/** Get the currently assigned mapping.
	 * @return used mapping between flux values and edgeWidth
	 */
	public ContinuousMapping getMapping(){
		return this.mapping;
	}

}
