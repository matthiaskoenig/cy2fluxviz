package cyfluxviz.vizmap;

import java.io.File;

import cyfluxviz.CyFluxViz;
import cytoscape.Cytoscape;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CyFileFilter;
import cytoscape.util.FileUtil;
import cytoscape.visual.CalculatorCatalog;

/* Loads the visual style from given property file. */
public class LoadVizmap {
	
	public LoadVizmap(File propertyFile){
		if (propertyFile == null){
			propertyFile = getVizmapPropertyFile();
		}
		if (propertyFile != null) {
			loadPropertyFile(propertyFile);
			setVisualStyle(CyFluxViz.DEFAULTVISUALSTYLE);
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
	
	private static void setVisualStyle(String vsName){
		CalculatorCatalog calc_cat = Cytoscape.getVisualMappingManager().getCalculatorCatalog();
		CyFluxViz.setViStyle(calc_cat.getVisualStyle(vsName));
	}
}