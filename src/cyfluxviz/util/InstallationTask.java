package cyfluxviz.util;

import java.io.File;

import cyfluxviz.CyFluxViz;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;


public class InstallationTask implements Task {
	private cytoscape.task.TaskMonitor taskMonitor;
	private File jarFile;
	
	public InstallationTask(File jarFile) {
		this.jarFile = jarFile;
	}

	public void setTaskMonitor(TaskMonitor monitor)
			throws IllegalThreadStateException {
		taskMonitor = monitor;
	}

	public void halt() {}

	public String getTitle() {
		return "Install FluxViz Visual Styles";
	}

	public void run() {
		taskMonitor.setStatus("Installation...");
		taskMonitor.setPercentCompleted(-1);
		
		try {
			// during development the code is not loaded from the *.jar)
			if (CyFluxViz.DEVELOP){
				String jarLocation = 
						"/home/mkoenig/Desktop/CySBML/dev/cytoscape-2.8.2/plugins/CyFluxViz_"
								+ CyFluxViz.VERSION + ".jar";
				System.out.println("Jar Location: " + jarLocation);
				ExtractJar exJar = new ExtractJar(new File(jarLocation), FileUtil.getFluxVizDataDirectory());
				exJar.extract();
			}
			else if (jarFile != null && jarFile.getAbsolutePath().endsWith(".jar")){
	    		ExtractJar exJar = new ExtractJar(jarFile, FileUtil.getFluxVizDataDirectory());
	        	exJar.extract();
	    	}
		}
		catch (Exception e) {
			System.out.println("CyFluxViz[INFO] -> Installation error: " + e.getMessage());
			e.printStackTrace();
		}
		taskMonitor.setPercentCompleted(100);
	}
}