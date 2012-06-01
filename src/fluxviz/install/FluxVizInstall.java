package fluxviz.install;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;

import cytoscape.Cytoscape;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import fluxviz.CyFluxVizPlugin;
import fluxviz.util.DeleteDir;
import fluxviz.util.ExtractJar;
import fluxviz.util.FileUtil;


/**
 * Handles the FluxViz installation.
 * Creates the folder and the files in the Cytoscape plugin installation 
 * directory.
 * @author Matthias König
 *
 */
public class FluxVizInstall {
	
    /**
     * Within the .cytoscape directory create a version-specific FluxViz data directory. 
     * @return FluxViz installation directory
     * @throws IOException 
     */
    public static File createFluxVizDataDirectory() throws IOException{
    	File dir = new File(FileUtil.getFluxVizDataDirectory().getCanonicalPath());
		dir.mkdir();
		return dir;    	
    }
   
    /** 
     * Test if installed.
     * Is installed if the FluxViz installation folder exists.
     * @throws IOException 
     */
    public static boolean isInstalled() throws IOException{
    	File dir = new File(FileUtil.getFluxVizDataDirectory().getCanonicalPath());
    	System.out.println("FluxVizPath:\t" + dir);
    	return dir.exists();
    }
       
    /**
     * Creates the documentation and example files in a subfolder of .cytoscape.
     * @throws URISyntaxException 
     * @throws IOException 
     * @throws URISyntaxException 
     */
    public void install() throws IOException, java.util.zip.ZipException{
    	if (isInstalled()){
    		CyFluxVizPlugin.getLogger().info("FluxViz already installed");
    		return;
    	}
    	
    	System.out.println("Is installed: " + isInstalled());
    	// installation
    	CyFluxVizPlugin.getLogger().info("Start FluxViz installation");
    	
        //TODO: this is not good -> why jar!/ returned ????
    	String path = fluxviz.CyFluxVizPlugin.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        path = path.substring(5, path.length()-2);
    	File moduleFile = new File(path);
    	
    	CyFluxVizPlugin.getLogger().log(Level.INFO, "module: " + moduleFile);
    	InstallationTask task = new InstallationTask(moduleFile);
		
		// Configure JTask Dialog Pop-Up Box
		JTaskConfig jTaskConfig = new JTaskConfig();
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		jTaskConfig.displayCloseButton(false);
		jTaskConfig.displayCancelButton(false);
		jTaskConfig.displayStatus(false);
		jTaskConfig.setAutoDispose(true);

		// Execute Task in New Thread; pops open JTask Dialog Box.
		TaskManager.executeTask(task, jTaskConfig);
    }
    
    /**
     * Removes the FluxViz directory from $HOME/.cytoscape.
     * Folder and all files in the FluxViz folder are removed recursivly.
     * @throws IOException 
     */
    public void uninstall() throws IOException{
    	File dir = new File(FileUtil.getFluxVizDataDirectory().getCanonicalPath());
    	DeleteDir.deleteDirectory(dir);
    }
    
    
    /**
     * Handels the installation in a task.
     * Uses the Cytoscape Task.
     */
    public class InstallationTask implements Task {
		private cytoscape.task.TaskMonitor taskMonitor;
		private File jarFile;
		
		public InstallationTask(File jarFile) {
			CyFluxVizPlugin.getLogger().fine("Create new InstallationTask");
			this.jarFile = jarFile;
		}

		public void setTaskMonitor(TaskMonitor monitor)
				throws IllegalThreadStateException {
			taskMonitor = monitor;
		}

		public void halt() {}

		public String getTitle() {
			return "Install FluxViz examples & documentation";
		}

		public void run() {
			taskMonitor.setStatus("Installation...");
			taskMonitor.setPercentCompleted(-1);
			
			try {
				
				if (CyFluxVizPlugin.DEVELOP){
					// For eclipse testing (during development the code is not loaded from the *.jar)
					CyFluxVizPlugin.getLogger().info("Using local installation jar.");
					ExtractJar exJar = new ExtractJar(new File("/home/mkoenig/Desktop/Cytoscape_v2.6.3/plugins/FluxViz-v0.11.jar"), FileUtil.getFluxVizDataDirectory());
					exJar.extract();
				}
				else if (jarFile != null && !jarFile.getAbsolutePath().endsWith(".jar")){
					CyFluxVizPlugin.getLogger().severe("jarFile no valid *.jar: " + jarFile);
				}
				
				else if (jarFile != null && jarFile.getAbsolutePath().endsWith(".jar")){
		        	// extract the jar content to this location
		    		ExtractJar exJar = new ExtractJar(jarFile, FileUtil.getFluxVizDataDirectory());
		        	exJar.extract();
		    	}
			}
			catch (Exception e) {
				System.out.println("Error installation: " + e.getMessage());
				e.printStackTrace();
				CyFluxVizPlugin.getLogger().log(Level.WARNING, e.getMessage(), e);
			}
			taskMonitor.setPercentCompleted(100);
		}
    }
	
}
