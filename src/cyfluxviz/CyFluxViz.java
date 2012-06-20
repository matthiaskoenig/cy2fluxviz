package cyfluxviz;

import javax.swing.SwingConstants;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.cytopanels.*;

import cyfluxviz.gui.CyFluxVizPanel;
import cyfluxviz.gui.PanelText;
import cyfluxviz.visualstyle.VisualStyleFactory;

/**
 * CyFluxViz visualizes flux information within Cytoscape networks.
 * 
 * Networks are imported as SBML models (sbml stoichiometry is used for scaling fluxes)
 * Flux distributions can be imported as edge information or as reaction flux.
 * Certain node and edge attributes are mandatory and are normally made available from 
 * the imported SBML via CySBML.
 * 
 * TODO : no activation at startup, only on selection 
 * TODO : Reduce the calculation overhead, problematic for large networks (Test with HepatoNet)
 * 		  (Especially the subnetwork generation)
 * TODO : support of multiple visual styles & settings panel for different styles
 * TODO : export, loading of set of flux distributions
 * TODO : creation of VisualSyle instead of import
 * TODO : Context menu for flux distributions -> filter for networkId or other criteria,
 * 		  Removal of Flux distributions
 * 
 * @author Matthias Koenig
 * @version 0.82
 * @date 120604
 */

public class CyFluxViz extends CytoscapePlugin {
	public static final boolean DEVELOP = true;
	
	public static final String NAME = "CyFluxViz";
	public static final String VERSION = "v0.82";
	public static final String INSTALLATON_DIRECTORY = NAME + "-" + VERSION; 
	public static final String DEFAULTVISUALSTYLE = NAME; 
	
	public static final String NODE_FLUX_ATTRIBUTE = "nodeFlux";
	public static final String EDGE_FLUX_ATTRIBUTE = "edgeFlux";
	public static final String EDGE_DIRECTION_ATTRIBUTE = "edgeFluxDirection";
		
    public CyFluxViz() {    	
    	
    	// TODO: Manage all this stuff in the panel and only activate after click
    	// on the respective plugin icon in the menubar
    	// Just Create the icon with the action -> not activated at startup.
    	createFluxVizPanel();
    }
    
    private void createFluxVizPanel(){
    	VisualStyleFactory.setFluxVizVisualStyle();
		CyFluxVizPanel fvPanel = CyFluxVizPanel.getInstance();
		addCyFluxVizPanelToCytoscape(fvPanel);
    }
    
    private static void addCyFluxVizPanelToCytoscape(CyFluxVizPanel fvPanel){
    	CytoPanel cytoPanel = getCytoPanel();
    	cytoPanel.add(NAME, fvPanel);
		cytoPanel.setState(CytoPanelState.DOCK);
		PanelText.setHelp(fvPanel);
		PanelText.setFluxVizInfo(fvPanel);
    }
    
    private static CytoPanel getCytoPanel(){
    	return Cytoscape.getDesktop().getCytoPanel (SwingConstants.WEST);
    }
        	
    public String describe() {
        String description = "CyFluxViz - Visualise fluxes in Cytoscape networks.";
        return description;
    }
    
    /*
    // VisualStyle methods //
	public static VisualStyle getViStyle() {
		return CyFluxViz.viStyle;
	}
	public static void setViStyle(VisualStyle newViStyle) {
		viStyle = newViStyle;
	}
	public static boolean isSetViStyle(){
    	return (viStyle != null);
    }
    public static String getViStyleName(){
		String vsName = null;
		if (isSetViStyle()){
			vsName = viStyle.getName();
		}
		return vsName;
	}
	*/
}