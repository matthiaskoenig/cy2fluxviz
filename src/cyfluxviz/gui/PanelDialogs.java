package cyfluxviz.gui;

import javax.swing.JOptionPane;

import cyfluxviz.CyFluxViz;
import cytoscape.Cytoscape;

public class PanelDialogs {
	private PanelDialogs(){};
	
	public static void showMessage(String msg, String title){
		JOptionPane.showMessageDialog(Cytoscape.getDesktop(), msg, 
			title, JOptionPane.WARNING_MESSAGE);
	}
	
	public static String getHTMLHeader(){
		String header = 
				"<a href='http://www.charite.de/sysbio/people/koenig/software/fluxviz/help/'>Online Help</a>" + " | " +
    			"<a href='http://www.charite.de/sysbio/people/koenig/'>Contact</a><br><br>";
		return header;
	}
	
	public static void setFluxVizInfo(FluxVizPanel fvPanel){
    	String info = 
    			getHTMLHeader() +
    			"<b>" + CyFluxViz.NAME + "-" + CyFluxViz.VERSION +"</b><br>" +
    			"Visualisation of flux distributions in networks.<br>" +
    			"Developed by <span color=\"gray\">Matthias König</span> at Charite Berlin.";
    	fvPanel.updateInfoPaneHTMLText(info);
    }
		
    public static void setHelp(FluxVizPanel fvPanel){
    	String help = 
    			getHTMLHeader() +
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
    	fvPanel.updateHelpPaneHTMLText(help);
    }
}