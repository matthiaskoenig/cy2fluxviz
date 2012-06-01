package fluxviz;

import java.io.IOException;
import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.dialogs.ExportAsGraphicsFileChooser;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.*;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.VisualMappingManager;

import fluxviz.attributes.AttributeUtils;
import fluxviz.fasimu.ValAttributes;
import fluxviz.fluxanalysis.FluxStatisticsMap;
import fluxviz.gui.Dialog;
import fluxviz.gui.FluxVizPanel;
import fluxviz.util.ExportAsGraphics;
import fluxviz.util.FileUtil;
import fluxviz.util.FluxVizUtil;
import fluxviz.util.Installation;

/**
 * Cytoscape Plugin class for FluxViz.
 * 
 * The FluxViz Cytoscape plugin visualizes flux information within Cytoscape networks.
 * The networks should be imported as SBML models, the flux distributions should be
 * loaded as additional Flux Information files.
 * 
 * Certain node and edge attributes are necessary which are made available via CySBML
 * based import.
 *  
 * <p>
 * 
 * TODO: The mapping information in the VizMapper has to be updated if the visual<br> 
 * TODO: Additional button for adeptin the mapping of the edge size (absolute and relative).<br>
 * TODO: Automatic selection of all mappings in the vizmapper (not only selected).<br>
 * TODO: Right click menu (delete, show flux values)<br>
 * TODO: update mapping function depending on the min and max values of the fluxes<br>
 * TODO: support of multiple different visual styles for the visualisation (simplification views)<br> 
 * TODO: store the additional information with the Cytoscape file (store 
 * 			plugin information over sessions)
 * TODO: variable Visual Style (use selected) -> multiple styles for selection
 * <p>
 * FluxViz is open source a). 
 * 
 * BUGS: Problems can occur if multiple networks are loaded.
 * The mapping of the identifier to the fluxes can be not complete and lead to
 * errors. Therefor only a single network should be loaded per session and 
 * the calculated flux distributions should match the network.<br>
 * 
 * @author Matthias Koenig
 * @version 0.8
 * @date 120630
 */

public class CyFluxVizPlugin extends CytoscapePlugin implements  PropertyChangeListener {
	
	public static final String NAME = "CyFluxViz";
	public static final String VERSION = "v0.81";
	public static final String INSTALLATON_DIRECTORY = NAME + "-" + VERSION; 
	public static final String DEFAULTVISUALSTYLE = NAME; 
	
	public static final boolean DEVELOP = true;
	
	/** Visual Style for FluxViz */
	public static File props_file = new File(FileUtil.getFluxVizDataDirectory(), 
			"data" + File.separator + DEFAULTVISUALSTYLE +".props");
	
	/** Subset of Cytoscape node attributes which are flux distributions. */
	private static FluxAttributes fluxAttributes;
	/** Additional information from simulations file. */
	private static FluxInformation fluxInformation;
	/** Statistics for the flux distributions.*/ 
	private static FluxStatisticsMap fluxStatistics;

	private static FluxVizPanel fvPanel;
	
	// Visual Style
	private static String vsName;
    private static VisualStyle viStyle;
	private static VisualMappingManager vmm;
	
    public CyFluxVizPlugin() {
    	init();
    	install();
    }
        
    private void init(){
		Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener(CytoscapeDesktop.NETWORK_VIEW_FOCUSED, this);
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(Cytoscape.ATTRIBUTES_CHANGED, this);
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(Cytoscape.SESSION_LOADED, this);
    	try{
    		
    		fluxAttributes = new ValAttributes();
    		
    		fvPanel = new FluxVizPanel(this);
    		CytoPanel cytoPanel = getCytoPanel();
    		cytoPanel.add(NAME, fvPanel);
    		cytoPanel.setState(CytoPanelState.DOCK);
    		
    		Dialog.setHelp(fvPanel);
    		Dialog.setFluxVizInfo(fvPanel);
    		Dialog.setExamples(fvPanel);
    		
    		AttributeUtils.initNodeAttributeComboBox();
    	}
    	catch (Exception e){
    		e.printStackTrace();
    	}
    }
    
    public static CytoPanel getCytoPanel(){
    	return Cytoscape.getDesktop().getCytoPanel (SwingConstants.WEST);
    }
    
    
    /*
     * FluxViz Installation.
     * Only installation if installation data in the .cytoscape folder is not
     * available (normally only performed on the first FluxViz startup).
     * Installation of the documentation examples if necessary.
     * 
     * During the installation process the content of the jar file is extracted.
     * Examples and necessary files like the standard VisualStyle are copied.
     * 
     * TODO: handle earlier installations (after update to newest version
     * 		the installation of the old files should be removed
     */
    public void install(){	
    	Installation installation = new Installation();
    	try {
    		installation.install();
    	}
    	catch (IOException e){
    		try{
    			installation.uninstall();
    		}
    		catch (IOException ex){
    			ex.printStackTrace();
    		}
    	}
    }
            
    /* Short description */
    public String describe() {
        StringBuffer sb = new StringBuffer();
        sb.append("FluxViz - visualisation of flux distributions in networks.");
        return sb.toString();
    }
    
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equalsIgnoreCase(Cytoscape.ATTRIBUTES_CHANGED))
		{
			// Recalculate the flux distributions (can change)
			AttributeUtils.updateFluxAttributes();
			// Recalculate the values in the node mapping
			AttributeUtils.initNodeAttributeComboBox();
			
		}
		
		if (e.getPropertyName().equalsIgnoreCase(Cytoscape.SESSION_LOADED))
		{
			AttributeUtils.updateFluxAttributes();
			// Recalculate the values in the node mapping
			AttributeUtils.initNodeAttributeComboBox();
			//reset the view
			CyFluxVizPlugin.getFvPanel().getAttributeSubnetCheckbox().setSelected(false);
			CyFluxVizPlugin.getFvPanel().getFluxSubnetCheckbox().setSelected(false);
		}
		
		if (e.getPropertyName().equalsIgnoreCase(CytoscapeDesktop.NETWORK_VIEW_FOCUSED))
		{	
			// Recalculate the values in the node mapping
			AttributeUtils.initNodeAttributeComboBox();
		}
	} 
	
	/* Create images */
    public void exportImage(){
    	if (AttributeUtils.getSelectedAttributes(this).length == 0){
			JOptionPane.showMessageDialog(null,
					"No flux distributions selected for export.\n" +
					"Select flux distributions before image export.", "No flux distribution selected", JOptionPane.WARNING_MESSAGE);
    		return;
    	}
   
    	// Select folder for the export
		if (FluxVizUtil.availableNetworkAndView()){
			ExportAsGraphicsFileChooser chooser = new ExportAsGraphicsFileChooser(ExportAsGraphics.FILTERS);
	    	ExportAsGraphics ex = new ExportAsGraphics();
	    	ex.actionPerformed();	
		}
		else {
			JOptionPane.showMessageDialog(null,
					"Image export in empty network or without view not possible.\n" +
					"Load network and select view for the network before image export.",
					"Empty network or view warning", JOptionPane.WARNING_MESSAGE);
		}
    }
    
	public static FluxAttributes getFluxAttributes(){
		return CyFluxVizPlugin.fluxAttributes;
	}
	public static void setFluxAttributes(FluxAttributes fluxAttributes){
		CyFluxVizPlugin.fluxAttributes = fluxAttributes;
	}
	public static FluxInformation getFluxInformation() {
		return fluxInformation;
	}
    public static void setFluxInformation(FluxInformation fluxInformation) {
		CyFluxVizPlugin.fluxInformation = fluxInformation;
	}
	public static FluxStatisticsMap getFluxStatistics() {
		return CyFluxVizPlugin.fluxStatistics;
	}
    public static void setFluxStatistics(FluxStatisticsMap fluxStatistics) {
		CyFluxVizPlugin.fluxStatistics = fluxStatistics;
	}
    
	public static VisualMappingManager getVmm() {
		return vmm;
	}
    public static void setVmm(VisualMappingManager vmm) {
		CyFluxVizPlugin.vmm = vmm;
	}
	public static VisualStyle getViStyle() {
		return viStyle;
	}
    public static void setViStyle(VisualStyle viStyle) {
		CyFluxVizPlugin.viStyle = viStyle;
	}
	public static String getVsName() {
		return vsName;
	}
    public static void setVsName(String vsName) {
		CyFluxVizPlugin.vsName = vsName;
	}

	public static FluxVizPanel getFvPanel() {
		return fvPanel;
	}	
}