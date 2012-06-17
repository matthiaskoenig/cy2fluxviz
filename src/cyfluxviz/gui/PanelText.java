package cyfluxviz.gui;

import javax.swing.JOptionPane;

import cyfluxviz.CyFluxViz;
import cytoscape.Cytoscape;

public class PanelText {
	private PanelText(){};
	
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
	
	public static void setFluxVizInfo(CyFluxVizPanel fvPanel){
    	String info = 
    			getHTMLHeader() +
    			"<b>" + CyFluxViz.NAME + "-" + CyFluxViz.VERSION +"</b><br>" +
    			"Visualisation of flux distributions in networks.<br>" +
    			"Developed by <span color=\"gray\">Matthias König</span> at Charite Berlin.";
    	fvPanel.updateInfoPaneHTMLText(info);
    }
		
    public static void setHelp(CyFluxVizPanel fvPanel){
    	String help = 
    			getHTMLHeader() +
    			"<b>1. Select Network for visualisation</b>" +
    			"<br>Load ('File -> Import Network' or 'File -> Open') or create Network for visualization of flux distributions.<br>" +
    			" CyFluxViz works on the current NetworkView.<br />" +
    			
    			"<b>2. Load flux distributions</b><br/>" +
    			"Import flux distribution via *.val files ('Import' menu).<br/>" +
    			
    			"<b>3. Adapt mapping functions and subnet view [optional]</b><br>" +
    			"The NetworkView can be reduced to flux containing subnets (Subnet -> Flux subnet) or attribute subnets " +
    			"(Subnet -> Attribute subnet)<br />" +
    			
    			"<b>4. Export Images [optional]</b><br>Select val file for Flux visualisation and export images in selected" +
    			"format (PDF, SVG, EPS, JPG, PNG, BMP).";
    	fvPanel.updateHelpPaneHTMLText(help);
    }
}