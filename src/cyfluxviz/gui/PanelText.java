package cyfluxviz.gui;

import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JOptionPane;

import cysbml.gui.NavigationPanel;
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
		String source = "/cyfluxviz/gui/dialogs/cyfluxviz_about.html";
		try {
			fvPanel.updateInfoPaneHTMLText(getResourceURL(source));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
    }
		
    public static void setHelp(CyFluxVizPanel fvPanel){
		String source = "/cyfluxviz/gui/dialogs/cyfluxviz_help.html";
		try {
			fvPanel.updateHelpPaneHTMLText(getResourceURL(source));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
    }
    
	public static void setAbout(CyFluxVizPanel fvPanel){
		String source = "/cyfluxviz/gui/dialogs/cyfluxviz_about.html";
		try {
			fvPanel.updateAboutPaneHTMLText(getResourceURL(source));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
    }
    
	
    public static URL getResourceURL(String source) throws MalformedURLException{
    	String res = getResourceString(source);
    	URL url = new URL(res);
    	return url;
    }
	
    /** Some magic to get the right filename within the src folder
     * to load some additional files.
     * @param source
     * @return
     */
    public static String getResourceString(String source){
    	String res = NavigationPanel.class.getResource(source).toString();
    	return res;
    }
	
}