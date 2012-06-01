package fluxviz.gui.dialogs;

import fluxviz.CyFluxVizPlugin;
import fluxviz.gui.FluxVizPanel;

public class Dialog {

    /**
     * Set the help information in the Help tab.
     */
    public static void setHelp(FluxVizPanel fvPanel){
    	String help = "<a href='http://www.charite.de/sysbio/people/koenig/software/fluxviz/help/'>Help Tutorial</a>" + " | " +
    			"<a href='http://www.charite.de/sysbio/people/koenig/software/fluxviz/doc'>JavaDoc</a>" + " | " +
    			"<a href='http://www.charite.de/sysbio/people/koenig/'>Contact</a><br><br>" +
    			"<b>[Short Introduction]</b><br>" +
    			"<b>1. Select Network for visualisation</b>" +
    			"<br>Load ('File -> Import Network' or 'File -> Open') or create Network for visualization of flux distributions.<br>" +
    			" FluxViz works on the current NetworkView. " +
    			"The current FluxViz version only works with one network at a time (multiple networks are currently not " +
    			"supported).<br><br>" +
    			
    			"<b>2. Load flux distributions</b><br>" +
    			"Load flux distribution via *.val files or as attributes ('Import' menu below).<br><br>" +
    			
    			"<b>3. Load sim file [optional]</b><br>" +
    			"FASIMU simulation information can be integrated with the flux data. The simulation file can be loaded via" +
    			"the 'Import' menu below.<br><br>" +
    			
    			"<b>4. Adapt mapping functions and subnet view [optional]</b><br>" +
    			"The NetworkView can be reduced to flux containing subnets (Subnet -> Flux subnet) or attribute subnets " +
    			"(Subnet -> Attribute subnet)<br><br>" +
    			
    			"<b>5. Export Images [optional]</b><br>Select val file for Flux visualisation and export images in selected" +
    			"format (PDF, SVG, EPS, JPG, PNG, BMP).<br><br>" +
    			
    			"<br><b>[Subnetworks]</b><br>" +
    			"The visualisation can be limited to flux containing graph elements " +
    			"by selecting 'Subnet -> Flux subnet'.";
    	fvPanel.updateText(fvPanel.getHelpPane(), help);
    }
    
    /**
     * Set the startInfo in the information Tab.
     * General plugin information
     */
    public static void setFluxVizInfo(FluxVizPanel fvPanel){
    	String info = "<a href='http://www.charite.de/sysbio/people/koenig/software/fluxviz/help/'>Help Tutorial</a>" + " | " +
    			"<a href='http://www.charite.de/sysbio/people/koenig/software/fluxviz/doc'>JavaDoc</a>" + " | " +
    			"<a href='http://www.charite.de/sysbio/people/koenig/'>Contact</a><br><br>" +
    			"<b>FluxViz-" + CyFluxVizPlugin.VERSION +"</b><br>" +
    			"Plugin for visualisation of flux distributions.<br><br>" +
    			"Computational Systems Biochemistry Berlin<br>" +
    			"<a href='http://www.charite.de/sysbio/people/koenig/'>Matthias König</a>";
    	fvPanel.updateText(fvPanel.getInfoPane(), info);
    }
	
    /**
     * Set the FluxViz examples in the example tab.
     * Information about the available examples (short description and references)
     */
    public static void setExamples(FluxVizPanel fvPanel){
    	javax.swing.JComboBox box = fvPanel.getExamplesComboBox();
    	box.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "None", "1 [Hepatocyte]", "2 [Erythrocyte]", "3 [Hepatocyte layout]", "4 [Hepatocyte core]" }));
    	String info = "<b>1 [Hepatocyte]</b><br>" +
    			"Basic Hepatocyte network consisting of glycolysis, gluconeogenesis, citrate cycle and " +
    			"pentose phosphate pathway. Standard Cytoscape layout algorithm applied. ATP production as target flux" +
    			"under varying oxygen availability.<br><br>" +
    			
    			"<b>2 [Erythrocyte]</b><br>" +
    			"Human erythrocyte network with example FASIMU FBA simulations.<br><br>" +
    			
    			"<b>3 [Hepatocyte layout]</b><br>" +
    			"Metabolic network and simulations identical to 1 [Hepatocyte]. Manual layout applied.<br><br> "+

    			"<b>4 [Hepatocyte core]</b><br>" +
    			"Hepatocyte core network reconstruction with FASIMU FBA simulations";
    	fvPanel.updateText(fvPanel.getExamplesPane(), info);
    }
    
	
}
