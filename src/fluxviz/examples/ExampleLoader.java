package fluxviz.examples;

import java.io.File;

import javax.swing.JOptionPane;
import javax.xml.bind.JAXBException;

import cytoscape.Cytoscape;
import cytoscape.data.readers.CytoscapeSessionReader;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import fluxviz.util.FileUtil;

public class ExampleLoader {
	public ExampleLoader(int example){
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
			String warning = "Current session (all networks/attributes) will be lost.\nDo you want to continue?";
			int result = JOptionPane.showConfirmDialog(Cytoscape.getDesktop(), warning, "Caution!",
		                                           JOptionPane.YES_NO_OPTION,
		                                           JOptionPane.WARNING_MESSAGE, null);

			if (result == JOptionPane.YES_OPTION) {
		    	ExampleLoaderTask task = new ExampleLoaderTask(sessionFile);
				JTaskConfig jTaskConfig = new JTaskConfig();
				jTaskConfig.setOwner(Cytoscape.getDesktop());
				jTaskConfig.displayCloseButton(false);
				jTaskConfig.displayCancelButton(false);
				jTaskConfig.displayStatus(false);
				jTaskConfig.setAutoDispose(true);
				TaskManager.executeTask(task, jTaskConfig);
			} else {
				return;
			}	
	    }
	    catch (Exception e){
	    	e.printStackTrace();
	    }
	}
	
    public static void loadExample(File sessionFile) throws JAXBException, Exception{
		//Create session
		Cytoscape.setSessionState(Cytoscape.SESSION_OPENED);
		Cytoscape.createNewSession();
		Cytoscape.getDesktop().setTitle("Cytoscape Desktop (New Session)");
		Cytoscape.getDesktop().getNetworkPanel().repaint();
		Cytoscape.getDesktop().repaint();
		Cytoscape.setSessionState(Cytoscape.SESSION_NEW);
		Cytoscape.getPropertyChangeSupport().firePropertyChange(Cytoscape.CYTOSCAPE_INITIALIZED, null, null);
	
		//Load session
		String filename = sessionFile.getCanonicalPath();
		System.out.println("CyFluxViz[INFO] -> Loading session: " +  filename);
		CytoscapeSessionReader csr = new CytoscapeSessionReader(filename);
		csr.read();
    } 
}