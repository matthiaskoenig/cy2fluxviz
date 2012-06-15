package fluxviz;

import javax.swing.SwingConstants;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.*;
import cytoscape.visual.VisualStyle;

import fluxviz.attributes.FluxAttributeUtils;
import fluxviz.attributes.ValAttributes;
import fluxviz.gui.PanelDialogs;
import fluxviz.gui.FluxVizPanel;
import fluxviz.statistics.FluxStatisticsMap;
import fluxviz.util.Installation;

/**
 * The CyFluxViz Cytoscape plugin visualizes flux information within Cytoscape networks.
 * Networks are imported as SBML models (sbml stoichiometry is used for scaling fluxes)
 * Flux distributions can be imported as edge information or as reaction flux.
 * Certain node and edge attributes are mandatory and are normally made available from 
 * the imported SBML via CySBML.
 * 
 * TODO : handle all the data in one data structure, which can be accessed via
 * a HashMap. Precalculate all the information. 
 * FluxDistributions are managed in an easy way. On Selection the corresponding
 * data is copied to the cyFlux node and edge attribute.
 * 
 * TODO : add option for removal of flux distributions and filtering.
 * 
 * TODO : no activation at startup, only on selection 
 * 
 * TODO : Reduce the calculation overhead, problematic for large networks (Test with HepatoNet)
 * 
 * @author Matthias Koenig
 * @version 0.82
 * @date 120604
 */

public class CyFluxVizPlugin extends CytoscapePlugin implements  PropertyChangeListener {
	public static final boolean DEVELOP = true;
	
	public static final String NAME = "CyFluxViz";
	public static final String VERSION = "v0.82";
	public static final String INSTALLATON_DIRECTORY = NAME + "-" + VERSION; 
	public static final String DEFAULTVISUALSTYLE = NAME; 
	public static final String NODE_ATTRIBUTE = "cyFlux";
	public static final String EDGE_ATTRIBUTE = "cyFlux";
	
	private static FluxVizPanel fvPanel;
	private static VisualStyle viStyle;
	
	private FluxAttributes fluxAttributes;
	private FluxStatisticsMap fluxStatistics;
	
    public CyFluxVizPlugin() {
    	Cytoscape.getDesktop().getSwingPropertyChangeSupport().
			addPropertyChangeListener(CytoscapeDesktop.NETWORK_VIEW_FOCUSED, this);
    	Cytoscape.getPropertyChangeSupport().
			addPropertyChangeListener(Cytoscape.ATTRIBUTES_CHANGED, this);
    	Cytoscape.getPropertyChangeSupport().
			addPropertyChangeListener(Cytoscape.SESSION_LOADED, this);

    	createFluxVizPanel();
    	initFluxDistributions();
    	Installation.doInstallation();
    }
        
    private void createFluxVizPanel(){
		fvPanel = new FluxVizPanel(this);
		CytoPanel cytoPanel = getCytoPanel();
		cytoPanel.add(NAME, fvPanel);
		cytoPanel.setState(CytoPanelState.DOCK);
		PanelDialogs.setHelp(fvPanel);
		PanelDialogs.setFluxVizInfo(fvPanel);
		PanelDialogs.setExamples(fvPanel);
    }
    
    private static CytoPanel getCytoPanel(){
    	return Cytoscape.getDesktop().getCytoPanel (SwingConstants.WEST);
    }
    
    private void initFluxDistributions(){
		fluxAttributes = new ValAttributes();
		FluxAttributeUtils.initNodeAttributeComboBox();
    }
    
	public static FluxVizPanel getFvPanel() {
		return fvPanel;
	}
	
    public String describe() {
        String description = "FluxViz - Visualisation of flux distributions.";
        return description;
    }
    
	public FluxAttributes getFluxAttributes(){
		return fluxAttributes;
	}
	public void setFluxAttributes(FluxAttributes fluxAttributes){
		this.fluxAttributes = fluxAttributes;
	}
	public FluxStatisticsMap getFluxStatistics() {
		return fluxStatistics;
	}
    public void setFluxStatistics(FluxStatisticsMap fluxStatistics) {
		this.fluxStatistics = fluxStatistics;
	}
    
 
	public static VisualStyle getViStyle() {
		return CyFluxVizPlugin.viStyle;
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
    
    // Handle all via FluxDistributions
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equalsIgnoreCase(Cytoscape.ATTRIBUTES_CHANGED))
		{
			FluxAttributeUtils.updateFluxAttributes();
			FluxAttributeUtils.initNodeAttributeComboBox();
		}
		
		if (e.getPropertyName().equalsIgnoreCase(Cytoscape.SESSION_LOADED))
		{
			FluxAttributeUtils.updateFluxAttributes();
			FluxAttributeUtils.initNodeAttributeComboBox();
			
			//reset the view
			fvPanel.getAttributeSubnetCheckbox().setSelected(false);
			fvPanel.getFluxSubnetCheckbox().setSelected(false);
		}
		
		if (e.getPropertyName().equalsIgnoreCase(CytoscapeDesktop.NETWORK_VIEW_FOCUSED))
		{	
			FluxAttributeUtils.initNodeAttributeComboBox();
		}
	} 
}