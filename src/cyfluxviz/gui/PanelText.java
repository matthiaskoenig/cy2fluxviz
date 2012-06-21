package cyfluxviz.gui;

import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JOptionPane;

import cysbml.gui.CySBMLNavigationPanel;
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
	
		
	public static void setInfo(CyFluxVizPanel fvPanel){
		URL url;
		try {
			url = new URL(CySBMLNavigationPanel.class.getResource("/cyfluxviz/gui/dialogs/cyfluxviz_info.html").toString());
			fvPanel.updateInfoPaneHTMLText(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
    }
		
    public static void setHelp(CyFluxVizPanel fvPanel){
		URL url;
		try {
			url = new URL(CySBMLNavigationPanel.class.getResource("/cyfluxviz/gui/dialogs/cyfluxviz_help.html").toString());
			fvPanel.updateHelpPaneHTMLText(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
    }
}