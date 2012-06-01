package fluxviz.examples;

import java.io.File;
import java.util.logging.Level;

import javax.swing.JOptionPane;
import javax.xml.bind.JAXBException;

import cytoscape.Cytoscape;
import cytoscape.data.readers.CytoscapeSessionReader;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import fluxviz.CyFluxVizPlugin;
import fluxviz.util.FileUtil;

public class ExampleLoader {
	
	/**
	 * Creates ExampleLoaderTask for existing examples.
	 * If user abortion, no ExampleLoaderTask is generated.
	 * @param example
	 */
	public ExampleLoader(int example){
		CyFluxVizPlugin.getLogger().info("ExampleLoader: " + example);
		
		//Get the session file for the example
    	String sep = java.io.File.separator;
    	File sessionFile = null;
    	String sessionName = null;
    	switch (example){	
    		case 1:
    			sessionName = "01_standard" + sep + "standard.cys"; break;
    		case 2:
        		sessionName = "02_erythrocyte" + sep + "ery.cys"; break;
    		case 3:
        		sessionName = "03_standard_layout" + sep + "standard_layout.cys"; break;
    		case 4:
        		sessionName = "04_diploma" + sep + "hepatocyte.cys"; break;
        	default:
        		return;
    	}
    	sessionFile = new File(FileUtil.getFluxVizDataDirectory(), "examples" + sep + sessionName);
		
    	try {
			// Show warning
			String warning = "Current session (all networks/attributes) will be lost.\nDo you want to continue?";
			int result = JOptionPane.showConfirmDialog(Cytoscape.getDesktop(), warning, "Caution!",
		                                           JOptionPane.YES_NO_OPTION,
		                                           JOptionPane.WARNING_MESSAGE, null);

			if (result == JOptionPane.YES_OPTION) {
		    	ExampleLoaderTask task = new ExampleLoaderTask(sessionFile);
				
				// Configure JTask Dialog Pop-Up Box
				JTaskConfig jTaskConfig = new JTaskConfig();
				jTaskConfig.setOwner(Cytoscape.getDesktop());
				jTaskConfig.displayCloseButton(false);
				jTaskConfig.displayCancelButton(false);
				jTaskConfig.displayStatus(false);
				jTaskConfig.setAutoDispose(true);

				// Execute Task in New Thread; pops open JTask Dialog Box.
				TaskManager.executeTask(task, jTaskConfig);

			} else {
				return;
			}	
	    }
	    catch (Exception e){
	    	CyFluxVizPlugin.getLogger().log(Level.WARNING, e.getMessage(), e);
	    }
	}
	
	
    public void loadExample(File sessionFile) throws JAXBException, Exception{
		//Create new session
		Cytoscape.setSessionState(Cytoscape.SESSION_OPENED);
		Cytoscape.createNewSession();
		Cytoscape.getDesktop().setTitle("Cytoscape Desktop (New Session)");
		Cytoscape.getDesktop().getNetworkPanel().repaint();
		Cytoscape.getDesktop().repaint();
		Cytoscape.setSessionState(Cytoscape.SESSION_NEW);
		Cytoscape.getPropertyChangeSupport().firePropertyChange(Cytoscape.CYTOSCAPE_INITIALIZED, null, null);
	
		//Load the new session
		String filename = sessionFile.getCanonicalPath();
		System.out.println("Loading session: " +  filename);
		CytoscapeSessionReader csr = new CytoscapeSessionReader(filename);
		csr.read();
    } 
	
    /**
     * Handels the installation in a task.
     * Uses the Cytoscape Task.
     */
    public class ExampleLoaderTask implements Task {
		private cytoscape.task.TaskMonitor taskMonitor;
		private File sessionFile;
		
		public ExampleLoaderTask(File sessionFile) {
			CyFluxVizPlugin.getLogger().fine("Create new ExampleLoaderTask");
			this.sessionFile = sessionFile;			
		}

		public void setTaskMonitor(TaskMonitor monitor)
				throws IllegalThreadStateException {
			taskMonitor = monitor;
		}

		public void halt() {}

		public String getTitle() {
			return "Load example network";
		}

		public void run() {
			taskMonitor.setStatus("Loading example ...");
			taskMonitor.setPercentCompleted(-1);
			try {
				loadExample(sessionFile);
			}
			catch (Exception e){
				CyFluxVizPlugin.getLogger().log(Level.SEVERE, e.getMessage(), e);
			}
			taskMonitor.setPercentCompleted(100);
		}
    }
	
	
}
