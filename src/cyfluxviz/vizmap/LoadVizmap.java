package cyfluxviz.vizmap;

import java.io.File;
import java.util.Set;

import cyfluxviz.CyFluxViz;
import cytoscape.Cytoscape;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CyFileFilter;
import cytoscape.util.FileUtil;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.VisualStyle;

/* Loads the visual style from given property file. */
public class LoadVizmap {
	
	public LoadVizmap(File propertyFile){
		if (propertyFile == null){
			propertyFile = getVizmapPropertyFile();
		}
		if (propertyFile != null) {
			loadPropertyFile(propertyFile);
		}
	}
	
	private File getVizmapPropertyFile(){
		final CyFileFilter propsFilter = new CyFileFilter();
		propsFilter.addExtension("props");
		propsFilter.setDescription("Property files");
		return FileUtil.getFile("Import Vizmap Property File", FileUtil.LOAD,
	                                   new CyFileFilter[] { propsFilter });
	}
	
	private static void loadPropertyFile(File propertyFile){
		LoadVizmapTask task = new LoadVizmapTask(propertyFile);
		final JTaskConfig jTaskConfig = new JTaskConfig();
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		jTaskConfig.displayCloseButton(true);
		jTaskConfig.displayStatus(false);
		jTaskConfig.setAutoDispose(true);
		TaskManager.executeTask(task, jTaskConfig);
	}
	
	public static void setVisualStyle(String vsName){
		CalculatorCatalog calcCatalog = Cytoscape.getVisualMappingManager().getCalculatorCatalog();
		Set<String> styles = calcCatalog.getVisualStyleNames();
		System.out.println("Available VisualStyles");
		for (String name: styles){
			System.out.println(name);
		}
		System.out.println("-> Name: " + vsName);
		
		VisualStyle vs = calcCatalog.getVisualStyle(vsName);
		
		CyFluxViz.setViStyle(vs);
	}
}