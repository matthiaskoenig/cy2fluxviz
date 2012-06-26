package cyfluxviz.util;

import java.io.File;
import javax.swing.JFileChooser;

import cytoscape.plugin.PluginManager;

import cyfluxviz.CyFluxViz;
import cyfluxviz.FluxDis;
import cyfluxviz.FluxDisCollection;
import cyfluxviz.io.ValFluxDistributionImporter;

public class FluxVizFileUtil {

    public static void createFluxDistributionFromValFile(File valFile){
		if (! valFile.getAbsolutePath().endsWith(".val") ){
			return;
		}
		ValFluxDistributionImporter fdImporter = new ValFluxDistributionImporter(valFile);
		FluxDis fluxDistribution = fdImporter.getFluxDistribution();
		FluxDisCollection fdCollection = FluxDisCollection.getInstance();
		fdCollection.addFluxDistribution(fluxDistribution);
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
	
    /* Get the directory for the FluxViz data. */
	@Deprecated
    private static File getFluxVizDataDirectory(){
    	File pluginDir = PluginManager.getPluginManager().getPluginManageDirectory();
    	return new File(pluginDir, CyFluxViz.INSTALLATON_DIRECTORY);    	
    }
	
	@Deprecated
	private static File getVisualStyleFile(){
		String fname = "data" + File.separator + CyFluxViz.DEFAULTVISUALSTYLE + ".props";
		return (new File(FluxVizFileUtil.getFluxVizDataDirectory(), fname));
	}
}