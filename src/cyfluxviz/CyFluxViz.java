package cyfluxviz;

import javax.swing.SwingConstants;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.*;
import cytoscape.visual.VisualStyle;

import cyfluxviz.gui.CyFluxVizPanel;
import cyfluxviz.gui.PanelText;
import cyfluxviz.netview.NetworkView;
import cyfluxviz.util.AttributeUtils;
import cyfluxviz.util.FileUtil;
import cyfluxviz.util.Installation;
import cyfluxviz.vizmap.LoadVizmap;

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

public class CyFluxViz extends CytoscapePlugin implements  PropertyChangeListener {
	public static final boolean DEVELOP = true;
	
	public static final String NAME = "CyFluxViz";
	public static final String VERSION = "v0.82";
	public static final String INSTALLATON_DIRECTORY = NAME + "-" + VERSION; 
	public static final String DEFAULTVISUALSTYLE = NAME; 
	
	public static final String NODE_FLUX_ATTRIBUTE = "nodeFlux";
	public static final String NODE_VISIBILITY_ATTRIBUTE = "nodeVisibility";
	public static final String EDGE_FLUX_ATTRIBUTE = "edgeFlux";
	public static final String EDGE_VISIBILITY_ATTRIBUTE = "edgeVisibility";
	public static final String EDGE_DIRECTION_ATTRIBUTE = "edgeFluxDirection";
	
	private static CyFluxVizPanel fvPanel;
	private static VisualStyle viStyle;
		
    public CyFluxViz() {
    	Cytoscape.getDesktop().getSwingPropertyChangeSupport().
			addPropertyChangeListener(CytoscapeDesktop.NETWORK_VIEW_FOCUSED, this);
    	Cytoscape.getPropertyChangeSupport().
			addPropertyChangeListener(Cytoscape.ATTRIBUTES_CHANGED, this);
    	Cytoscape.getPropertyChangeSupport().
			addPropertyChangeListener(Cytoscape.SESSION_LOADED, this);

    	// TODO: Manage the installation in a better way (no installation necessary ?)
    	// Copy the files to correct paths
    	Installation.doInstallation();
    	
    	// TODO: Manage all this stuff in the panel and only activate after click
    	// on the respective plugin icon in the menubar
    	createFluxVizPanel();
    }
        
    private void createFluxVizPanel(){

		fvPanel = new CyFluxVizPanel();
		CytoPanel cytoPanel = getCytoPanel();
		cytoPanel.add(NAME, fvPanel);
		cytoPanel.setState(CytoPanelState.DOCK);
		PanelText.setHelp(fvPanel);
		PanelText.setFluxVizInfo(fvPanel);
		
		// Set the Visual Style at the beginning
		String filenameVS = FileUtil.getFluxVizDataDirectory() + "/data/" + DEFAULTVISUALSTYLE + ".props";
		LoadVizmap loadVM = new LoadVizmap(filenameVS);
		loadVM.loadPropertyFile();
		LoadVizmap.setCyFluxVizVisualStyle();
		// Create mapping to the defined attributes in the visual style
		NetworkView.createCyFluxVizMapping();
    }
    
    private static CytoPanel getCytoPanel(){
    	return Cytoscape.getDesktop().getCytoPanel (SwingConstants.WEST);
    }
        
	public static CyFluxVizPanel getFvPanel() {
		return fvPanel;
	}
	
    public String describe() {
        String description = "CyFluxViz - Visualise fluxes in Cytoscape networks.";
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
			AttributeUtils.initNodeAttributeComboBox();
		}
		
		if (e.getPropertyName().equalsIgnoreCase(Cytoscape.SESSION_LOADED))
		{
			System.out.println("SESSION LOADED -> HANDLE IN MAPPING");
			//FluxAttributeUtils.updateFluxAttributes();
			AttributeUtils.initNodeAttributeComboBox();
			
			//reset the view
			fvPanel.getAttributeSubnetCheckbox().setSelected(false);
			fvPanel.getFluxSubnetCheckbox().setSelected(false);
		}
		
		if (e.getPropertyName().equalsIgnoreCase(CytoscapeDesktop.NETWORK_VIEW_FOCUSED))
		{
			System.out.println("NETWORK_VIEW_FOCUSED -> HANDLE IN MAPPING");
			AttributeUtils.initNodeAttributeComboBox();
		}
	} 
}