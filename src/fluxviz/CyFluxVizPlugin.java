package fluxviz;

import java.io.IOException;
import java.io.File;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.JOptionPane;
import javax.swing.SwingConstants;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;

import cytoscape.plugin.CytoscapePlugin;

import cytoscape.dialogs.ExportAsGraphicsFileChooser;

import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.*;

import cytoscape.visual.VisualStyle;
import cytoscape.visual.VisualMappingManager;

import fluxviz.attributes.AttributeUtils;
import fluxviz.examples.LoadExample;
import fluxviz.fasimu.ValAttributes;
import fluxviz.fluxanalysis.FluxStatisticsMap;
import fluxviz.gui.FluxVizPanel;
import fluxviz.gui.dialogs.Dialog;
import fluxviz.images.ExportAsGraphics;
import fluxviz.install.FluxVizInstall;

import fluxviz.util.FileUtil;
import fluxviz.util.FluxVizUtil;

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
 * @version 0.14
 */

public class CyFluxVizPlugin extends CytoscapePlugin implements  PropertyChangeListener {
	/** Debug mode */
    public static final boolean DEBUG = true; 
    /** Develop mode */
    public static final boolean DEVELOP = false; 
    /** FluxViz logger */
	private static Logger logger = Logger.getLogger(CyFluxVizPlugin.class.getName());
	
    
    /** FluxViz version */
	public static final String VERSION = "v0.14";
	/** Installation directory for help and examples */
	public static final String INSTALLATON_DIRECTORY = "FluxViz-" + VERSION;
	/** Default Visual Style for the flux visualisation */ 
	public static final String DEFAULTVISUALSTYLE = "FluxViz"; 
	/** Visual Style for FluxViz */
	public static File props_file = new File(FileUtil.getFluxVizDataDirectory(), "data" + File.separator + 
			DEFAULTVISUALSTYLE +"_5.props");
	
	
	/** Subset of Cytoscape node attributes which are flux distributions. */
	private static FluxAttributes fluxAttributes;
	/** Additional information from simulations file. */
	private static FluxInformation fluxInformation;
	/** Statistics for the flux distributions.*/ 
	private static FluxStatisticsMap fluxStatistics;

	
	/** Cytoscape Panel which holds the FluxViz panel. */
	private static CytoPanel cytoPanel;
	/** FluxViz panel on the left side. */
	private static FluxVizPanel fvPanel;
	
	/** Name of VisualStyle used for FluxViz visualization*/
	private static String vsName;
	/** Current VisualStyle */
    private static VisualStyle viStyle;
    /** VisualMappingManager */
	private static VisualMappingManager vmm;
	
    /** 
     * Creates the FluxViz GUI and integrates it into Cytoscape.
     * Plugin initialisation and installation is performed here. 
     * PropertyChangeListeners are registered, logger initialisied and  
     * plugin menu and FluxViz panel are generated.
     */
    public CyFluxVizPlugin() {
    	init();
    	install();
    	startup();
    	
	    // Create a new action to respond to menu activation and set in menu
    	/*
	    FluxVizAction action = new FluxVizAction();
	    action.setPreferredMenu("Plugins");
	    Cytoscape.getDesktop().getCyMenus().addAction(action);
	    */    
    }
    
    
    /** handle the secludeNode feature */
    /*
	@SuppressWarnings("serial")
	public class FluxVizAction extends cytoscape.util.CytoscapeAction {
		
		// The constructor sets the text that should appear on the menu item.
	    public FluxVizAction() {super("Seclude & Unite");}
	    
	    // This method is called when the user selects the menu item.//
	    @SuppressWarnings("unchecked")
		@Override
		public void actionPerformed(ActionEvent ae) {	
	    	SecludeNodes secNodes = (SecludeNodes) SecludeNodes.getInstance();
	    	// Unite the nodes if secluded
	    	if (secNodes.getState() == true){
	    		//secNodes.unitNodes();
	    		System.out.println("Seclude selected nodes");
	    		secNodes.secludeNodes(Cytoscape.getCurrentNetwork().getSelectedNodes());
	    	}
	    	// Seclude nodes if united
	    	else{
	    		
	    		// Seclude by degree
	    		//int degree = 3;
	    		//secNodes.secludeNodes(SecludeNodes.getNodesByDegree(degree));
	    		
	    		// Seclude selected
	    		System.out.println("Seclude selected nodes");
	    		secNodes.secludeNodes(Cytoscape.getCurrentNetwork().getSelectedNodes());
	    	}
	    }
	}
	*/
	
    /**
     * Initialisation of FluxViz components.
     * Logger is initialised and Logger FileHandler generated.
     * PropertyChangeListeners registered.
     * FluxViz Panel initialised.
     */
    public void init(){
    	//Create a file handler for the log
    	try{
	    	FileHandler fh = new FileHandler(FileUtil.getFluxVizDataDirectory() + File.separator + "fluxviz.log");
	    	fh.setFormatter(new SimpleFormatter());
	    	fh.setLevel(Level.FINE);
	    	logger.addHandler(fh);
	    }
	    catch (IOException e){
	    	logger.log(Level.WARNING, e.getMessage(), e);
	    }
	    
	    // Set Level of the logger and the Handlers for the logger
    	if (DEBUG | DEVELOP){
    		logger.setLevel(Level.FINE);
    		Handler[] handlers = logger.getHandlers();
    		for(int i=0; i<handlers.length; ++i){
    			handlers[i].setLevel(Level.WARNING);
    		}
    	}
    	
	    
		// Register this class as a listener to listen Cytoscape events
	    logger.fine("Register the Cytoscape event listener");
		Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener(CytoscapeDesktop.NETWORK_VIEW_FOCUSED, this);
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(Cytoscape.ATTRIBUTES_CHANGED, this);
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(Cytoscape.SESSION_LOADED, this);

		// Create FluxViz Panel
		logger.fine("Create the FluxViz Panel");
    	CytoscapeDesktop desktop = Cytoscape.getDesktop();
    	cytoPanel = desktop.getCytoPanel (SwingConstants.WEST);
    	try{
    		// Create the fluxAttributes 
    		fluxAttributes = new ValAttributes();
    		
    		// Create the Fluxviz panel
    		fvPanel = new FluxVizPanel(this);
    		cytoPanel.add("FluxViz", fvPanel);
    		cytoPanel.setState(CytoPanelState.DOCK);
    		
    		//set the FluxViz panel as active panel
    		//no good style for the deployed Plugin
    		if (DEBUG | DEVELOP){
    			int index = cytoPanel.indexOfComponent(fvPanel);
    			cytoPanel.setSelectedIndex(index);
    		}
    		
    		//Set the help in the helpPane
    		Dialog.setHelp(fvPanel);
    		//Set info in the infoPane
    		Dialog.setFluxVizInfo(fvPanel);
    		//Set examples content
    		Dialog.setExamples(fvPanel);
    		
    		//init the NodeAttributeBox
    		AttributeUtils.initNodeAttributeComboBox();
    	}
    	catch (Exception e){
    		System.out.println("Error" + e.getMessage());
    		e.printStackTrace();
    		logger.log(Level.SEVERE, e.getMessage(), e);
    	}
    }
    
    
    /**
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
    	logger.fine("installation");
    	FluxVizInstall installation = new FluxVizInstall();
    	try {
    		installation.install();
    	}
    	catch (IOException e){
    		logger.log(Level.SEVERE, e.getMessage(), e);
    		try{
    			// undo the installation work (deleting the created folder)
    			installation.uninstall();
    		}
    		catch (IOException ex){
    			logger.log(Level.SEVERE, ex.getMessage(), ex);
    		}
    	}
    }
       
    /**
     * Handles additional startup options.
     * Parses the Cytoscape startup properties. Handled is the loading of startup networks.
     * TODO: include libSBML library ? not necessary
     * TODO: load given sbml and val files in the network.
     * Actions have to be loaded after final startup (use Listener to startup action). 
     */
    public void startup(){	
    	logger.fine("Control additional FluxViz startup properties.");
    	Properties props = CytoscapeInit.getProperties();
    	
    	// Parse given startup network
    	String startup = props.getProperty("FluxVizExample");
    	if (startup != null){
    		logger.info("FluxVizExample=" + startup);
    		LoadExample.loadExample(Integer.parseInt(startup));
    	}
    	
    	
    }
     
    /**
     *  Gives short description of the plugin.
     *  @return the description string
     */
    public String describe() {
        StringBuffer sb = new StringBuffer();
        sb.append("FluxViz - visualisation of flux distributions in networks.");
        return sb.toString();
    }
    
	/**
	 *  Handle the PropertyChangeEvents of Cytoscape.
	 *  Plugin listens to some events, for example attribute changes and session loading.  
	 *  TODO: control the new events available in 2.7
	 */
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equalsIgnoreCase(Cytoscape.ATTRIBUTES_CHANGED))
		{
			logger.fine("Received an event -- Cytoscape.ATTRIBUTES_CHANGED!");
			// Recalculate the flux distributions (can change)
			AttributeUtils.updateFluxAttributes();
			// Recalculate the values in the node mapping
			AttributeUtils.initNodeAttributeComboBox();
			
		}
		if (e.getPropertyName().equalsIgnoreCase(Cytoscape.SESSION_LOADED))
		{
			logger.fine("Received an event -- Cytoscape.SESSION_LOADED!");
			AttributeUtils.updateFluxAttributes();
			//removeValAttributes();
			//loadViStyle();
			// Recalculate the values in the node mapping
			AttributeUtils.initNodeAttributeComboBox();
			//reset the view
			CyFluxVizPlugin.getFvPanel().getAttributeSubnetCheckbox().setSelected(false);
			CyFluxVizPlugin.getFvPanel().getFluxSubnetCheckbox().setSelected(false);
			
		}
		
		
		if (e.getPropertyName().equalsIgnoreCase(CytoscapeDesktop.NETWORK_VIEW_FOCUSED))
		{	
			logger.fine("Received an event -- CytoscapeDesktop.NETWORK_VIEW_FOCUSED!");
			System.out.println("Received an event -- CytoscapeDesktop.NETWORK_VIEW_FOCUSED!");
			// Recalculate the values in the node mapping
			AttributeUtils.initNodeAttributeComboBox();
		}
		if (e.getPropertyName().equalsIgnoreCase(CytoscapeDesktop.NETWORK_VIEW_CREATED)){
			// ! IS NOT SEND BY CYTOSCAPE - USE THE NETWORK_VIEW_FOCUSED property
			logger.fine("Received an event -- CytoscapeDesktop.NETWORK_VIEW_CREATED!");
			System.out.println("Received an event -- CytoscapeDesktop.NETWORK_VIEW_CREATED!");
		}
	} 
	
    /**
     * Export selected flux distributions as image.
     */
    public void exportImage(){
    	if (AttributeUtils.getSelectedAttributes(this).length == 0){
			JOptionPane.showMessageDialog(null,
					"No flux distributions selected for export.\n" +
					"Select flux distributions before image export.", "No flux distribution selected", JOptionPane.WARNING_MESSAGE);
    		return;
    	}
   
    	// Select folder for the export
		if (FluxVizUtil.availableNetworkAndView()){
	    	@SuppressWarnings("unused")
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
    
	public static Logger getLogger() {
		return logger;
	}

	public static FluxVizPanel getFvPanel() {
		return fvPanel;
	}	
}