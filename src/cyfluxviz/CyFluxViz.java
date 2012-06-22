package cyfluxviz;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.ImageIcon;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;

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
 * TODO : support of multiple visual styles & settings panel for different styles
 * TODO : export, loading of set of flux distributions
 * TODO : Context menu for flux distributions -> filter for networkId or other criteria,
 * 		  Removal of Flux distributions
 * TODO : Update the attribute information only if the attribute subnets are selected
 * TODO : Sorting of FluxDistributions, Context Menu, Filter by Network
 * TODO : Calculate Secondary Flux Distributions (mean, diff, ...)
 * TODO : Flux Correlation Matrix for Flux Distributions
 * 
 * @author Matthias Koenig
 * @date 200604
 */

public class CyFluxViz extends CytoscapePlugin {
	public static final String NAME = "CyFluxViz";
	public static final String VERSION = "v0.86";
	public static final String INSTALLATON_DIRECTORY = NAME + "-" + VERSION; 
	public static final String DEFAULTVISUALSTYLE = NAME; 
	
	public static final String NODE_FLUX_ATTRIBUTE = "nodeFlux";
	public static final String EDGE_FLUX_ATTRIBUTE = "edgeFlux";
	public static final String EDGE_DIRECTION_ATTRIBUTE = "edgeFluxDirection";
		
    public CyFluxViz() {    	
    	ImageIcon fluxVizIcon = new ImageIcon(getClass().getResource("/cyfluxviz/gui/images/CyFluxViz_logo.png"));
    	CyFluxVizStartAction startAction = new CyFluxVizStartAction(fluxVizIcon, this);
    	Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) startAction);
    }

    public String describe() {
        String description = "CyFluxViz - Visualise fluxes in Cytoscape networks.";
        return description;
    }
    
	@SuppressWarnings("serial")
	public class CyFluxVizStartAction extends CytoscapeAction {
	    public CyFluxVizStartAction() {super("CyFluxViz Startup");}
	    
		public CyFluxVizStartAction(ImageIcon icon, CyFluxViz plugin) {
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
	    	// Creates the unique instance of the singleton CyFluxVizPanel
	    	CyFluxVizPanel fvPanel = CyFluxVizPanel.getInstance();
	    	PanelText.setInfo(fvPanel);
	    	VisualStyleFactory.setFluxVizVisualStyle();
	    }
	}
}