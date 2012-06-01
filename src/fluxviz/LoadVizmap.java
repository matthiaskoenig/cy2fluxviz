package fluxviz;

import java.io.File;

import cytoscape.Cytoscape;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CyFileFilter;
import cytoscape.util.FileUtil;
import cytoscape.visual.CalculatorCatalog;


/*
 * Loads the visual style from given property file.
 * Updates the visual mapper with the loaded information.
 * 
 * FluxViz visual style is necessary because the mapping between the 
 * flux values and the edge width is defined in the visual mapping.
 * 
 * If no FluxViz VisualStyle is available, the visual style has to be loaded.
 * Somewhere the original file has to be specified for loading.
 */
public class LoadVizmap {
	
	/*
	 * Opens Vizmap selection menu to select property file.
	 */
	public LoadVizmap(File props_file){
		CyFluxVizPlugin.getLogger().info("LoadVizmap");
		File file = props_file;
		
		// if no props file, file dialog is opened
		if (props_file == null){
			
			//New property filter
			final CyFileFilter propsFilter = new CyFileFilter();
			propsFilter.addExtension("props");
			propsFilter.setDescription("Property files");

			// Get the file name
			file = FileUtil.getFile("Import Vizmap Property File", FileUtil.LOAD,
		                                   new CyFileFilter[] { propsFilter });
		}
		// if the name is not null, then load
		if (file != null) {
			// Create LoadNetwork Task
			LoadVizmapTask task = new LoadVizmapTask(file);

			// Configure JTask Dialog Pop-Up Box
			final JTaskConfig jTaskConfig = new JTaskConfig();
			jTaskConfig.setOwner(Cytoscape.getDesktop());
			jTaskConfig.displayCloseButton(true);
			jTaskConfig.displayStatus(false);
			jTaskConfig.setAutoDispose(true);

			// Execute Task in New Thread; pops open JTask Dialog Box.
			TaskManager.executeTask(task, jTaskConfig);
			
			// Set the visual Style
			// TODO: Problem with the VI STYLE -> has to be selected and set
			String vs_name = CyFluxVizPlugin.DEFAULTVISUALSTYLE;
			CalculatorCatalog calc_cat = CyFluxVizPlugin.getVmm().getCalculatorCatalog();
			CyFluxVizPlugin.setVsName(vs_name);
			CyFluxVizPlugin.setViStyle(calc_cat.getVisualStyle(vs_name));

            

		}
	}
}

class LoadVizmapTask implements Task {
	private File file;
	private TaskMonitor taskMonitor;

	/**
	 * Constructor.
	 *
	 */
	public LoadVizmapTask(File file) {
		this.file = file;
	}

	/**
	 * Executes Task.
	 */
	public void run() {
		taskMonitor.setStatus("Reading Vizmap File...");
		taskMonitor.setPercentCompleted(-1);
		// this even will load the file
		Cytoscape.firePropertyChange(Cytoscape.VIZMAP_LOADED, null, file.getAbsolutePath());
		taskMonitor.setStatus("Vizmapper updated by the file: " + file.getName());
		taskMonitor.setPercentCompleted(100);
	}

	/**
	 * Sets the Task Monitor.
	 *
	 * @param taskMonitor
	 *            TaskMonitor Object.
	 */
	public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
		this.taskMonitor = taskMonitor;
	}

	/**
	 * Gets the Task Title.
	 *
	 * @return Task Title.
	 */
	public String getTitle() {
		return new String("Importing Vizmap");
	}

	public void halt() {
		// TODO Auto-generated method stub
		
	}
}
	
	

