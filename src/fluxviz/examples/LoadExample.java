package fluxviz.examples;

import javax.swing.JCheckBox;

import fluxviz.CyFluxVizPlugin;
import fluxviz.view.NetworkView;

public class LoadExample {
    /**
     * Loads the given example network in a CytoscapeTask.
     * @param example network
     */
    public static void loadExample(int example){
    	//Back to full network view
    	JCheckBox checkbox = CyFluxVizPlugin.getFvPanel().getFluxSubnetCheckbox();
    	checkbox.setSelected(false);
    	NetworkView.viewFullNet();
    	
    	//load example
    	@SuppressWarnings("unused")
		ExampleLoader loader = new ExampleLoader(example);
    }
	
}
