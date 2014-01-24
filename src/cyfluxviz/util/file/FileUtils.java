package cyfluxviz.util.file;

import java.io.File;
import javax.swing.JFileChooser;


import cytoscape.CytoscapeInit;
import cytoscape.plugin.PluginManager;

import cyfluxviz.CyFluxVizPlugin;

public class FileUtils {
	
	/* Get the folder for export/import */
	public static File getFolder(){
		final File recentDir = CytoscapeInit.getMRUD();
    	File folder = recentDir;
        JFileChooser fc = new JFileChooser(recentDir);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setDialogTitle("Select folder for export"); 
        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            folder = fc.getSelectedFile();
        }    	
        return folder;
	}
	
    /* Get the directory for the FluxViz data. */
	@Deprecated
    private static File getFluxVizDataDirectory(){
    	File pluginDir = PluginManager.getPluginManager().getPluginManageDirectory();
    	return new File(pluginDir, CyFluxVizPlugin.INSTALLATON_DIRECTORY);    	
    }
	
}