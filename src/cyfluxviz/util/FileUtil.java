package cyfluxviz.util;

import java.io.File;
import javax.swing.JFileChooser;

import cytoscape.Cytoscape;
import cytoscape.plugin.PluginManager;

import cyfluxviz.CyFluxViz;
import cyfluxviz.FluxDis;
import cyfluxviz.FluxDisCollection;
import cyfluxviz.io.ValFluxDistributionImporter;

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
    
    /* Opens file selection menu for xml fluxdistributions files. */
    public static File selectXMLFile(){    	
        File xmlFile = null;
        JFileChooser fc = new JFileChooser();
        fc.setFileHidingEnabled(false); 
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(false);
        fc.setDialogTitle("Select *.xml files for flux distribution.");
        if (fc.showOpenDialog(Cytoscape.getDesktop()) == JFileChooser.APPROVE_OPTION) {
            xmlFile = fc.getSelectedFile();
        }
        return xmlFile;
    }
    

    public static void createFluxDistributionFromValFile(File valFile){
		if (! valFile.getAbsolutePath().endsWith(".val") ){
			return;
		}
		ValFluxDistributionImporter fdImporter = new ValFluxDistributionImporter(valFile);
		FluxDis fluxDistribution = fdImporter.getFluxDistribution();
		FluxDisCollection fdCollection = FluxDisCollection.getInstance();
		fdCollection.addFluxDistribution(fluxDistribution);
    }
}