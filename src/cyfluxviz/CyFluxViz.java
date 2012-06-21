package cyfluxviz;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
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
 * TODO : support of multiple visual styles & settings panel for different styles
 * TODO : export, loading of set of flux distributions
 * TODO : Context menu for flux distributions -> filter for networkId or other criteria,
 * 		  Removal of Flux distributions
 * 
 * @author Matthias Koenig
 * @date 200604
 */

public class CyFluxViz extends CytoscapePlugin {
	public static final String NAME = "CyFluxViz";
	public static final String VERSION = "v0.85";
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
	    	createFluxVizPanel();
	    }
	    
	    private void createFluxVizPanel(){
	    	VisualStyleFactory.setFluxVizVisualStyle();
			CyFluxVizPanel fvPanel = CyFluxVizPanel.getInstance();
			addCyFluxVizPanelToCytoscape(fvPanel);
	    }
	    
	    private void addCyFluxVizPanelToCytoscape(CyFluxVizPanel fvPanel){
	    	String name = CyFluxViz.NAME;
	    	CytoPanel cytoPanel = getCytoPanel();
	    	cytoPanel.add(name, fvPanel);
			cytoPanel.setState(CytoPanelState.DOCK);
			cytoPanel.setSelectedIndex(cytoPanel.indexOfComponent(name));
			PanelText.setInfo(fvPanel);
			PanelText.setHelp(fvPanel);
	    }
	    
	    private CytoPanel getCytoPanel(){
	    	return Cytoscape.getDesktop().getCytoPanel (SwingConstants.WEST);
	    }
	}
}