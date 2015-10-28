package cyfluxviz;

/* Copyright (c) 2015 Matthias Koenig */

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.ImageIcon;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;

import cyfluxviz.gui.CyFluxVizPanel;
import cysbml.logging.LogCyPlugin;

/**
 * CyFluxViz visualizes flux information within Cytoscape networks.
 * 
 * Networks are imported as SBML models (sbml stoichiometry is used for scaling fluxes)
 * Flux distributions can be imported as edge information or as reaction flux.
 * Certain node and edge attributes are mandatory and are normally made available from 
 * the imported SBML via CySBML.
 *
 * Critical bugs for next release
 * ----------------------------------------------------------------------------------- 
 * TODO : Recalculate directions for the fluxDistributions
 * TODO : extensive testing

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
*/

public class CyFluxVizPlugin extends CytoscapePlugin {
	public static final String NAME = "cy2fluxviz";
	public static final String VERSION = "v1.0.0";
	public static LogCyPlugin LOGGER = new LogCyPlugin(NAME);
	public static final String INSTALLATON_DIRECTORY = NAME + "-" + VERSION; 
	
	public static final String NODE_FLUX_ATTRIBUTE = "nodeFlux";
	public static final String EDGE_FLUX_ATTRIBUTE = "edgeFlux";
	public static final String EDGE_DIRECTION_ATTRIBUTE = "edgeFluxDirection";

	public static final String NODE_CONCENTRATION_ATTRIBUTE = "nodeConcentration";
	
		
    public CyFluxVizPlugin() {    	
    	LOGGER.info(getVersionedName());
    	try {
	    	ImageIcon fluxVizIcon = new ImageIcon(getClass().getResource("/cyfluxviz/gui/images/CyFluxViz_logo.png"));
	    	CyFluxVizStartAction startAction = new CyFluxVizStartAction(fluxVizIcon, this);
	    	Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) startAction);
		}
		catch (Exception e){
			e.printStackTrace();
		}
    }

    public String describe() {
        return "CyFluxViz - Visualize fluxes in Cytoscape networks.";
    }
    
	public static String getVersionedName(){
		return NAME + "-" + VERSION;
	}
    
	@SuppressWarnings("serial")
	public class CyFluxVizStartAction extends CytoscapeAction {
	    public CyFluxVizStartAction() {super("CyFluxViz Startup");}
	    
		public CyFluxVizStartAction(ImageIcon icon, CyFluxVizPlugin plugin) {
			super("", icon);
			this.putValue(Action.SHORT_DESCRIPTION, "cy2fluxviz start");
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