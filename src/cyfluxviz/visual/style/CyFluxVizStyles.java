package cyfluxviz.visual.style;

import java.util.HashSet;

import cyfluxviz.CyFluxVizPlugin;
import cyfluxviz.visual.style.VisualStyleFactory;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;

/** Manage the Cytoscape Visual Styles which can be associated
 * with CyFluxViz.
 * Minimal requirements for the styles exist, namely EdgeWidth and the EdgeArrow attributes.
 
 * A set of standard styles is provided by CyFluxViz.
 */
public class CyFluxVizStyles {
	public static final String DEFAULTVISUALSTYLE = CyFluxVizPlugin.NAME;
	public static final String C13STYLE = CyFluxVizPlugin.NAME + "-C13";
	public static final String KINETICSTYLE = CyFluxVizPlugin.NAME + "-Kinetic";
	
	
	private static HashSet<String> fluxStyles;
	
	static {
		fluxStyles = new HashSet<String>();
		
		fluxStyles.add(DEFAULTVISUALSTYLE);
		VisualStyleFactory.createVisualStyle(DEFAULTVISUALSTYLE);
		
		fluxStyles.add(C13STYLE);
		VisualStyleFactory.createVisualStyle(C13STYLE);
		
		fluxStyles.add(KINETICSTYLE);
		VisualStyleFactory.createVisualStyle(KINETICSTYLE);
	}
	
	
	/** Only styles in the fluxStyles are supported.
	 * Test if in the supported collection.
	 * The supported styles were generated at CyFluxViz initialization. */
	private static boolean isStyleSupported(VisualStyle vs){
		if (vs==null)
			return false;
		return isStyleSupported(vs.getName());
	}
	
	private static boolean isStyleSupported(String name){
		boolean res = fluxStyles.contains(name);
		return res;
	}
	
	private static void setDefaultStyle(){
		VisualStyle vs = getVisualStyle(DEFAULTVISUALSTYLE);
		setVisualStyle(vs);
	}
	
	/** Updates the current visual style. 
	 * If the Style is not existing or not supported by CyFluxVis
	 * the default visual style will be set.
	 */
	public static void updateStyle(){
		VisualStyle vs = getCurrentVisualStyle();
		if (vs == null || !isStyleSupported(vs)){
			setDefaultStyle();
		}
		//} else {
		//	setVisualStyle(vs);
		//}
	}
	
	/** Get the currently set VisualStyle from the Visual mapping manager.
	 * Returns null if the VisualStyle does not exist
	 */
	public static VisualStyle getCurrentVisualStyle(){
		VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
		return vmm.getVisualStyle();
	}
	
	/** Gets visual style by name from the Calculater Catalog. 
	 * Returns null if the VisualStyle does not exist.
	 * @param vsName
	 * @return
	 */
	private static VisualStyle getVisualStyle(String vsName){
		VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
		CalculatorCatalog calcCatalog = vmm.getCalculatorCatalog();
		VisualStyle vs = calcCatalog.getVisualStyle(vsName);
		return vs;
	}
	
	/** Sets visual style in the Visual Mapping Manager. */
	private static void setVisualStyle(VisualStyle vs){
		VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
		vmm.setVisualStyle(vs);
		//vmm.applyAppearances();
	}
	
	public static void setVisualStyleForCurrentView(){
		setVisualStyleForView(Cytoscape.getCurrentNetworkView());
	}
	
    public static void setVisualStyleForView(CyNetworkView view){
        VisualStyle vs = getCurrentVisualStyle();
        view.setVisualStyle(vs.getName());
    }
	
}
