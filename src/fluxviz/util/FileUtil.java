package fluxviz.util;

import java.io.File;
import java.io.IOException;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;


import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.plugin.PluginManager;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.CalculatorCatalog;
import fluxviz.FluxInformation;
import fluxviz.CyFluxVizPlugin;
import fluxviz.LoadVizmap;
import fluxviz.attributes.AttributeUtils;
import fluxviz.fasimu.FluxAttributeCreator;

public class FileUtil {

    /* Get the directory for the FluxViz data. */
    public static File getFluxVizDataDirectory(){
    	File pluginDir = PluginManager.getPluginManager().getPluginManageDirectory();
    	return new File(pluginDir, CyFluxVizPlugin.INSTALLATON_DIRECTORY);    	
    }
	
	/* Get the folder for export/import */
	public static File getFolder(){
    	File folder = null;
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setDialogTitle("Select folder for export"); 
        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            folder = fc.getSelectedFile();
        }    	
        return folder;
	}
	
    /*
     * Opens file selection menu for val files.
     * Returns list of selected files. Returns null if no files are selected.
     * Array of files consists of all selected files.
     */
    public static File[] selectValFiles(){    	
        File[] valFiles = null;
        JFileChooser fc = new JFileChooser();
        
        fc.setFileHidingEnabled(false); 
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(true);
        fc.setDialogTitle("Select *.val files for visualisation.");
        
        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            valFiles = fc.getSelectedFiles();
        }
        return valFiles;
    }
    
    /*
     * Loads *.val files and creates the corresponding attributes. 
     * This function is called by clicking on 'Load val' in the FluxViz Control panel.
     * The val files in the tableModel are updated after the val files are loaded.
     * Some attribute tests and network tests are performed.
     * 
     * TODO: export the loading and generation into task.
     * The process of attribute generation can take very long.
     */
    public static void loadValFiles(){
    	JCheckBox checkbox = CyFluxVizPlugin.getFvPanel().getFluxSubnetCheckbox(); 
    	if (checkbox.isSelected() == true){
    		checkbox.doClick();
    	}
    	
        CyNetwork network = Cytoscape.getCurrentNetwork();
        CyNetworkView networkView = Cytoscape.getCurrentNetworkView();
        
        //Test if 'sbml_type' node attribute is available
    	if (FluxVizUtil.hasCompleteSBMLTypeAttribute() == false){
        	JOptionPane.showMessageDialog(null, "No complete 'sbml type' attribute.\nEvery node has to be classified as " +
        			"either 'reaction' or 'species'.\nIf the network was not imported as SBML create attribute 'sbml type' manually\nand " +
        			"classify all nodes as either 'reaction' or 'species'.", 
        			"SBML type not complete.", JOptionPane.WARNING_MESSAGE);
        	return;
        }
    	// Test if the attribute 'stoichiometry' is available for all network nodes
    	if (FluxVizUtil.hasCompleteStoichiometryAttribute() == false){
        	JOptionPane.showMessageDialog(null, "No complete 'stoichiometry' edge attribute.\n" +
        			"Every edge should have stoichiometric information associated.\n" +
        			"Missing stoichiometric coefficients are handled as '1.0' in the visualisation.", 
        			"Stoichiometry not complete.", JOptionPane.WARNING_MESSAGE);
        }
    	
        // Test if network for val files available
        network.selectAllNodes();
        if (networkView.getSelectedNodes().size() == 0) {
        	JOptionPane.showMessageDialog(null, "No nodes in network. Network must be loaded and selected before loading of val files.", 
        			"No network warning", JOptionPane.WARNING_MESSAGE);
        	return;
        }
        network.unselectAllNodes();
    	
	    // Select the val files and create the attributes
    	File[] valFiles = FileUtil.selectValFiles();
    	if (valFiles != null){
    		for (int k=0; k<valFiles.length; ++k){
    			FileUtil.createAttributesFromFile(valFiles[k]);
    		}
    	}
	    AttributeUtils.updateFluxAttributes();
    }
    
    /* Creates the attributes from given val file.
     * From the flux information node attribute file and edge attribute files
     * are generated. */
    public static void createAttributesFromFile(File valFile){
		if (! valFile.getAbsolutePath().endsWith(".val")){
			return;
		}
		// load the data and create the attributes (changes the attributes in place)
		@SuppressWarnings("unused")
		FluxAttributeCreator faCreator = new FluxAttributeCreator(valFile);
    }
    
    
    /**
     * Selects the simulation file.
     * Opens the File selection menu for the simulation file.
     * TODO: general file selectioner and opener for val and simulation files.
     * TODO: standard directory and handling of hidden files.
     */
    public static File selectSimulationFile(){
        File simFile = null;
        int loadVal = JOptionPane.showConfirmDialog(null, "Select simulation file for the fluxes.\n" +
        		"Only information from the last loaded simulation file is used. !",
        		"Select simulation file", 0);
        if (loadVal == 0){
            JFileChooser fc = new JFileChooser();
            
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);  
            fc.setDialogTitle("Select simulation file.");
            fc.setFileHidingEnabled(false); 
            
            if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                simFile = fc.getSelectedFile();
            } else {
            	JOptionPane.showMessageDialog(null, "No simulation file selected.");
            }
        }
        else {
        	JOptionPane.showMessageDialog(null, "No simulation file selected.");
        }
        return simFile;
    }
    
    /**
     * Load FASIMU simulation information.
     * Loads the additional simulation information from FASIMU (constraints,
     * target fluxes, comment.
     * TODO: load the full information and use for visualisation -> influxes,
     * effluxes, forbidden reactions.
     */
    public static void loadSimulationFile(){
    	File simFile = FileUtil.selectSimulationFile();
    	CyFluxVizPlugin.setFluxInformation(new FluxInformation(simFile));
    } 
    
    /** 
     * Loads the Visual Style necessary for the FluxViz Plugin.
     * Changes the FluxViz visual Style
     * @throws IOException 
     */
    public static void loadViStyle(){
        CyFluxVizPlugin.setVmm(Cytoscape.getVisualMappingManager());
        CalculatorCatalog calc_cat = CyFluxVizPlugin.getVmm().getCalculatorCatalog();
        CyFluxVizPlugin.setViStyle(calc_cat.getVisualStyle(CyFluxVizPlugin.DEFAULTVISUALSTYLE));
        
        // Load Visual Style if not available
        if (CyFluxVizPlugin.getViStyle() == null) {        	
        	@SuppressWarnings("unused")
			LoadVizmap loadVM = new LoadVizmap(CyFluxVizPlugin.props_file);
        	CyFluxVizPlugin.setViStyle(calc_cat.getVisualStyle(CyFluxVizPlugin.getVsName()));
        }	
    }
	
}
