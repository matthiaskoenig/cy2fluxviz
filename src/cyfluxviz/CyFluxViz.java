package cyfluxviz;

import javax.swing.SwingConstants;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.*;
import cytoscape.visual.VisualStyle;

import cyfluxviz.gui.FluxVizPanel;
import cyfluxviz.gui.PanelDialogs;
import cyfluxviz.util.Installation;

/**
 * CyFluxViz visualizes flux information within Cytoscape networks.
 * 
 * Networks are imported as SBML models (sbml stoichiometry is used for scaling fluxes)
 * Flux distributions can be imported as edge information or as reaction flux.
 * Certain node and edge attributes are mandatory and are normally made available from 
 * the imported SBML via CySBML.
 * 
 * TODO : add option for removal of flux distributions and filtering.
 * TODO : no activation at startup, only on selection 
 * TODO : Reduce the calculation overhead, problematic for large networks (Test with HepatoNet)
 * 
 * @author Matthias Koenig
 * @version 0.82
 * @date 120604
 */

public class CyFluxViz extends CytoscapePlugin implements  PropertyChangeListener {
	public static final boolean DEVELOP = true;
	
	public static final String NAME = "CyFluxViz";
	public static final String VERSION = "v0.82";
	public static final String INSTALLATON_DIRECTORY = NAME + "-" + VERSION; 
	public static final String DEFAULTVISUALSTYLE = NAME; 
	public static final String NODE_ATTRIBUTE = "nodeFlux";
	public static final String EDGE_ATTRIBUTE = "edgeFlux";
	public static final String EDGE_DIRECTION_ATTRIBUTE = "edgeFluxDirection";
	
	private static FluxVizPanel fvPanel;
	private static VisualStyle viStyle;
		
    public CyFluxViz() {
    	Cytoscape.getDesktop().getSwingPropertyChangeSupport().
			addPropertyChangeListener(CytoscapeDesktop.NETWORK_VIEW_FOCUSED, this);
    	Cytoscape.getPropertyChangeSupport().
			addPropertyChangeListener(Cytoscape.ATTRIBUTES_CHANGED, this);
    	Cytoscape.getPropertyChangeSupport().
			addPropertyChangeListener(Cytoscape.SESSION_LOADED, this);

    	// TODO: Manage all this stuff in the panel and only activate after click
    	// on the respective plugin icon in the menubar
    	createFluxVizPanel();
    	
    	// TODO: Manage the installation in a better way (no installation necessary ?)
    	Installation.doInstallation();
    }
        
    private void createFluxVizPanel(){

		fvPanel = new FluxVizPanel();
		CytoPanel cytoPanel = getCytoPanel();
		cytoPanel.add(NAME, fvPanel);
		cytoPanel.setState(CytoPanelState.DOCK);
		PanelDialogs.setHelp(fvPanel);
		PanelDialogs.setFluxVizInfo(fvPanel);
		
		// Set the Visual Style at the beginning from the file
		
    }
    
    private static CytoPanel getCytoPanel(){
    	return Cytoscape.getDesktop().getCytoPanel (SwingConstants.WEST);
    }
        
	public static FluxVizPanel getFvPanel() {
		return fvPanel;
	}
	
    public String describe() {
        String description = "FluxViz - Visualisation of flux distributions.";
        return description;
    }
     
	public static VisualStyle getViStyle() {
		return CyFluxViz.viStyle;
	}
	public static String getViStyleName(){
		if (viStyle == null){
			return null;
		}
		return viStyle.getName();
	}
    public static void setViStyle(VisualStyle newViStyle) {
		viStyle = newViStyle;
	}
    
    
    // Handle all via FluxDistributions and via the panel
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equalsIgnoreCase(Cytoscape.ATTRIBUTES_CHANGED))
		{
			System.out.println("ATTRIBUTE CHANGED -> HANDLE IN MAPPING");
			//FluxAttributeUtils.updateFluxAttributes();
			//FluxAttributeUtils.initNodeAttributeComboBox();
		}
		
		if (e.getPropertyName().equalsIgnoreCase(Cytoscape.SESSION_LOADED))
		{
			System.out.println("SESSION LOADED -> HANDLE IN MAPPING");
			//FluxAttributeUtils.updateFluxAttributes();
			//FluxAttributeUtils.initNodeAttributeComboBox();
			
			//reset the view
			fvPanel.getAttributeSubnetCheckbox().setSelected(false);
			fvPanel.getFluxSubnetCheckbox().setSelected(false);
		}
		
		if (e.getPropertyName().equalsIgnoreCase(CytoscapeDesktop.NETWORK_VIEW_FOCUSED))
		{
			System.out.println("NETWORK_VIEW_FOCUSED -> HANDLE IN MAPPING");
			//FluxAttributeUtils.initNodeAttributeComboBox();
		}
	} 
}