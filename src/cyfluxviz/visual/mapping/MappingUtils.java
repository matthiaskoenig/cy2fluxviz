package cyfluxviz.visual.mapping;

import cytoscape.Cytoscape;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.VisualPropertyType;

public class MappingUtils {
	
	/** Test if for the VisualPropertyType a calculator exists.
	 * Does not explicitly test for the mapping, but asumes that every calculator has 
	 * at least one mapping.
	 */
	public static boolean existsMappingForVisualPropertyType(VisualPropertyType vpt){
		CalculatorCatalog cc = Cytoscape.getVisualMappingManager()
				.getCalculatorCatalog();

		// Test if mapping exists (Calculator for the Visual Property)
		return (cc.getCalculators(vpt).size() > 0);
	}
	
}
