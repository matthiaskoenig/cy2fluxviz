package cyfluxviz.util;

import java.io.File;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.plugin.PluginManager;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.CalculatorCatalog;

import cyfluxviz.CyFluxViz;
import cyfluxviz.FluxDistribution;
import cyfluxviz.FluxDistributionCollection;
import cyfluxviz.attributes.FluxDistributionImporter;
import cyfluxviz.gui.PanelDialogs;
import cyfluxviz.vizmap.LoadVizmap;

public class FileUtil {

    /* Get the directory for the FluxViz data. */
    public static File getFluxVizDataDirectory(){
    	File pluginDir = PluginManager.getPluginManager().getPluginManageDirectory();
    	return new File(pluginDir, CyFluxViz.INSTALLATON_DIRECTORY);    	
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
	
	public static File getVisualStyleFile(){
		String fname = "data" + File.separator + CyFluxViz.DEFAULTVISUALSTYLE + ".props";
		return (new File(FileUtil.getFluxVizDataDirectory(), fname));
	}
	
    /* Opens file selection menu for val files. */
    public static File[] selectValFiles(){    	
        File[] valFiles = null;
        JFileChooser fc = new JFileChooser();
        
        fc.setFileHidingEnabled(false); 
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(true);
        fc.setDialogTitle("Select *.val files for visualisation.");
        
        if (fc.showOpenDialog(Cytoscape.getDesktop()) == JFileChooser.APPROVE_OPTION) {
            valFiles = fc.getSelectedFiles();
        }
        return valFiles;
    }
    
   // TODO: Refactor all the tests
    public static void loadValFiles(){
    	JCheckBox checkbox = CyFluxViz.getFvPanel().getFluxSubnetCheckbox(); 
    	if (checkbox.isSelected() == true){
    		checkbox.doClick();
    	}
        CyNetwork network = Cytoscape.getCurrentNetwork();
        CyNetworkView networkView = Cytoscape.getCurrentNetworkView();
        
    	if (FluxVizUtil.hasCompleteSBMLTypeAttribute() == false){
    		String title = "SBML type not complete.";
    		String msg = "No complete 'sbml type' attribute.\nEvery node has to be classified as " +
        			"either 'reaction' or 'species'.\nIf the network was not imported as SBML create attribute 'sbml type' manually\nand " +
        			"classify all nodes as either 'reaction' or 'species'.";
        	PanelDialogs.showMessage(msg, title);
        	return;
        }
    	
    	if (FluxVizUtil.hasCompleteStoichiometryAttribute() == false){
    		String title = "Stoichiometry not complete.";
    		String msg = "Every edge should have stoichiometric information associated.\n" +
        			"Missing stoichiometric coefficients are handled as '1.0' in the visualisation.";
    		PanelDialogs.showMessage(msg, title);
        }
    	
        // Test if network for val files available
        network.selectAllNodes();
        if (networkView.getSelectedNodes().size() == 0) {
        	String title = "No network warning.";
        	String msg = "No nodes in network. Network must be loaded and selected before loading of val files.";
        	PanelDialogs.showMessage(msg, title);
        	return;
        }
        network.unselectAllNodes();
    	
	    // Select the val files and create the attributes
    	File[] valFiles = FileUtil.selectValFiles();
    	if (valFiles != null){
    		for (int k=0; k<valFiles.length; ++k){
    			FileUtil.createFluxDistributionFromValFile(valFiles[k]);
    		}
    	}
    }
    
    public static void createFluxDistributionFromValFile(File valFile){
		if (! valFile.getAbsolutePath().endsWith(".val") ){
			return;
		}
		FluxDistributionImporter fdImporter = new FluxDistributionImporter(valFile);
		FluxDistribution fluxDistribution = fdImporter.getFluxDistribution();
		FluxDistributionCollection fdCollection = FluxDistributionCollection.getInstance();
		fdCollection.addFluxDistribution(fluxDistribution);
    }
        
    public static void loadVisualStyle(){
        CalculatorCatalog calc_cat = Cytoscape.getVisualMappingManager().getCalculatorCatalog();
        CyFluxViz.setViStyle(calc_cat.getVisualStyle(CyFluxViz.DEFAULTVISUALSTYLE));
        
        // Load Visual Style if not available
        if (CyFluxViz.getViStyle() == null) {        	
        	@SuppressWarnings("unused")
			LoadVizmap loadVM = new LoadVizmap(FileUtil.getVisualStyleFile());
        	CyFluxViz.setViStyle(calc_cat.getVisualStyle(CyFluxViz.getViStyleName()));
        }	
    }
}