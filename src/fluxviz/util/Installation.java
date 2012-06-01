package fluxviz.util;

import java.io.File;
import java.io.IOException;

import cytoscape.Cytoscape;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;


public class Installation {
	
    /* Within the .cytoscape directory create a version-specific FluxViz data directory. */
    public static File createFluxVizDataDirectory() throws IOException{
    	File dir = new File(FileUtil.getFluxVizDataDirectory().getCanonicalPath());
		dir.mkdir();
		return dir;    	
    }
   
    public static boolean isInstalled() throws IOException{
    	File dir = new File(FileUtil.getFluxVizDataDirectory().getCanonicalPath());
    	System.out.println("CyFluxViz[INFO] -> Installation Path:\t" + dir);
    	return dir.exists();
    }
       
    /* Creates the documentation and example files in a subfolder of .cytoscape. */
    public void install() throws IOException, java.util.zip.ZipException{
    	if (isInstalled()){
    		return;
    	}
    	    	
        //TODO: this is not good -> why jar!/ returned ????
    	String path = fluxviz.CyFluxVizPlugin.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        path = path.substring(5, path.length()-2);
    	File moduleFile = new File(path);
    	
    	InstallationTask task = new InstallationTask(moduleFile);
		JTaskConfig jTaskConfig = new JTaskConfig();
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		jTaskConfig.displayCloseButton(false);
		jTaskConfig.displayCancelButton(false);
		jTaskConfig.displayStatus(false);
		jTaskConfig.setAutoDispose(true);
		TaskManager.executeTask(task, jTaskConfig);
    }
    
    /* Removes the FluxViz directory from $HOME/.cytoscape.
     * Folder and all files in the FluxViz folder are removed recursivly. */
    public void uninstall() throws IOException{
    	File dir = new File(FileUtil.getFluxVizDataDirectory().getCanonicalPath());
    	DeleteDir.deleteDirectory(dir);
    }
    

	
}
