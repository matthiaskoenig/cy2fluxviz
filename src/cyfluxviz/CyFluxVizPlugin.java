package cyfluxviz;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.ImageIcon;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;

import cyfluxviz.gui.CyFluxVizPanel;

/**
 * CyFluxViz visualizes flux information within Cytoscape networks.
 * 
 * Networks are imported as SBML models (sbml stoichiometry is used for scaling fluxes)
 * Flux distributions can be imported as edge information or as reaction flux.
 * Certain node and edge attributes are mandatory and are normally made available from 
 * the imported SBML via CySBML.
 * 
 * @author Matthias Koenig
 * @date 2013-07-11
 
 * Critical bugs for next release
 * ----------------------------------------------------------------------------------- 
 * TODO : Recalculate directions for the fluxDistributions
 * TODO : extensive testing
 * TODO : additional examples
 * 			- large & small network support (Aliosha)
 * 			- zonated hepatic glucose model (SBML)
 * 			- simple hepatic glucose model v1 and v2
 * 			- genome scale-models

 * Things to come for Cy2
 * -----------------------------------------------------------------------------------
 * TODO : implement logging in line with other plugins
 * TODO : implement cofactor nodes
 * TODO : implement file generator -> general use for all SBML models;
 * TODO : implement : JSON support 
 * TODO : implement : socket connections
 * TODO : implement : nonlinear mapping with multiple points (interpolator) & arbitrary mappings
 * TODO : implement : concentration support for kinetic visualization
 * TODO : implement : XSD Stylesheet and validation of the FluxDistribution files
 * TODO : bug: mayor problems with multiple networks (application of mappings to to right network view when multiple
 * 			   networks are loaded)
 * TODO : bug : update the view when VisualStyle is changing (changes in values are not applied immediately !).
 * TODO : bug: don't apply CyFluxViz view automatically to all the networks, but for every view the right CyFluxViz Style has to
 * 		 	   be set. 

 
 * Things to come for Cy3
 * --------------------------------------------------------------------------------------
 * TODO : generate OSGI app
 * TODO : handle all dependencies with Maven
 * TODO : write Tests
 * TODO : dependency visualization of classes
 * TODO : jUnit tests for the main classes 
 * TODO : Update the attribute information only if the attribute subnets are selected
 * TODO : Calculate Secondary Flux Distributions (mean, diff, ...)
 * TODO : Context menu for managing flux distributions (remove, rename, filter?)
 * TODO : Flux Correlation Matrix for Flux Distributions
 * TODO : Dynamic changes between the different states.
 * -----------------------------------------------------------------------------------* 
 
 * CySBML bugs
 * --------------------------------------------------------------------------------------
 * TODO : connection tests with the proxies (real problem if changing network settings)
 */

public class CyFluxVizPlugin extends CytoscapePlugin {
	public static final String NAME = "cyfluxviz";
	public static final String VERSION = "v0.95";
	public static final String INSTALLATON_DIRECTORY = NAME + "-" + VERSION; 
	
	public static final String NODE_FLUX_ATTRIBUTE = "nodeFlux";
	public static final String EDGE_FLUX_ATTRIBUTE = "edgeFlux";
	public static final String EDGE_DIRECTION_ATTRIBUTE = "edgeFluxDirection";

	public static final String NODE_CONCENTRATION_ATTRIBUTE = "nodeConcentration";
	
		
    public CyFluxVizPlugin() {    	
    	System.out.println("CyFluxViz[INFO] -> " + NAME + "-" + VERSION);  
    	ImageIcon fluxVizIcon = new ImageIcon(getClass().getResource("/cyfluxviz/gui/images/CyFluxViz_logo.png"));
    	CyFluxVizStartAction startAction = new CyFluxVizStartAction(fluxVizIcon, this);
    	Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) startAction);
    }

    public String describe() {
        String description = "CyFluxViz - Visualize fluxes in Cytoscape networks.";
        return description;
    }
    
	@SuppressWarnings("serial")
	public class CyFluxVizStartAction extends CytoscapeAction {
	    public CyFluxVizStartAction() {super("CyFluxViz Startup");}
	    
		public CyFluxVizStartAction(ImageIcon icon, CyFluxVizPlugin plugin) {
			super("", icon);
			this.putValue(Action.SHORT_DESCRIPTION, "CyFluxViz Startup");
		}
		public boolean isInToolBar() {
			return true;
		}
		public boolean isInMenuBar() {
			return false;
		}
		
		/* This method is called when the user selects the menu item. */
	    public void actionPerformed(ActionEvent ae) {
			// register as an observer for the FluxDisCollection
	    	FluxDisCollection fdc = FluxDisCollection.getInstance();
	    	FluxDisCyAttributes fda = FluxDisCyAttributes.getInstance();
	    	fdc.addObserver(fda);
	    	
	    	// Creates the unique instance of the singleton CyFluxVizPanel
	    	CyFluxVizPanel fvPanel = CyFluxVizPanel.getInstance();
	    	fvPanel.selectCyFluxVizPanelAndSetDialogs();
	    }
	}
}