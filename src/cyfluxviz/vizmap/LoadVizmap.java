package cyfluxviz.vizmap;

import java.io.File;

import cyfluxviz.CyFluxViz;
import cytoscape.Cytoscape;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CyFileFilter;
import cytoscape.util.FileUtil;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.VisualStyle;

public class LoadVizmap {
	private File propertyFile;
	
	public LoadVizmap(File file){
		propertyFile = getPropertyFile(file);
	}
	public LoadVizmap(String filename){
		propertyFile = getPropertyFile(filename);
	}
	
	private File getPropertyFile(String filename){
		File file = new File(filename);
		return getPropertyFile(file);
	}
	
	private File getPropertyFile(File file){
		if (file == null){
			file = getVizmapPropertyFile();
		}
		return file;
	}
	
	private File getVizmapPropertyFile(){
		final CyFileFilter propsFilter = new CyFileFilter();
		propsFilter.addExtension("props");
		propsFilter.setDescription("Property files");
		return FileUtil.getFile("Import Vizmap Property File", FileUtil.LOAD,
	                                   new CyFileFilter[] { propsFilter });
	}
	
	public void loadPropertyFile(){
		if (propertyFile != null) {
			System.out.println("CyFluxViz[INFO] -> load VisualStyle Property file");
			System.out.println(propertyFile.getName());
			LoadVizmapTask task = new LoadVizmapTask(propertyFile);
			final JTaskConfig jTaskConfig = new JTaskConfig();
			jTaskConfig.setOwner(Cytoscape.getDesktop());
			jTaskConfig.displayCloseButton(true);
			jTaskConfig.displayStatus(false);
			jTaskConfig.setAutoDispose(true);
			TaskManager.executeTask(task, jTaskConfig);
		} else {
			System.out.println("CyFluxViz[INFO] -> no VisualStyle property file found !");
		}		
	}
	
	public static void setCyFluxVizVisualStyle(){
		setVisualStyle(CyFluxViz.DEFAULTVISUALSTYLE);
	}
	
	public static void setVisualStyle(String vsName){
		CalculatorCatalog calcCatalog = Cytoscape.getVisualMappingManager().getCalculatorCatalog();
		VisualStyle vs = calcCatalog.getVisualStyle(vsName);
		CyFluxViz.setViStyle(vs);
	}
}