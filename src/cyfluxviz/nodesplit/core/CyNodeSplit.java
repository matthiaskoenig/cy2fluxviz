/*
 Copyright (c) 2013, Matthias Koenig, Computational Systems Biochemistry, 
 Charite Berlin
 matthias.koenig [at] charite.de

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package cyfluxviz.nodesplit.core;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFrame;


import cyfluxviz.nodesplit.gui.CyNodeSplitPanel;
import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;

/**
 * CyNodeSplitPlugin for Cytoscape v2.8.3.
 * 
 * Plugin for splitting selected nodes into multiple nodes. 
 * The splitting is performed on all selected nodes. 
 * Helper routines for the selection of nodes based on graph features (node degree) or 
 * attributes exist. 
 * All node attributes are copied to the split nodes. 
 * 
 * TODO: integration with CyFluxViz (mappings have to be recalculated for the split nodes)
 * 
 * @author mkoenig
 * @date 2013-10-01
 */
public class CyNodeSplit extends CytoscapePlugin {
	public static final String NAME = "CyNodeSplit";
	public static final String VERSION = "v0.01";
	public static final String INSTALLATON_DIRECTORY = NAME + "-" + VERSION; 
		
	/** Creates CyNodeSplit action in Cytoscape icon bar. */
	public CyNodeSplit() {
    	System.out.println("CyNodeSplit[INFO] -> " + NAME + "-" + VERSION);  
    	ImageIcon nodeSplitIcon = new ImageIcon(getClass().getResource("/cynodesplit/gui/images/CyNodeSplit_logo.png"));
    	CyNodeSplitAction startAction = new CyNodeSplitAction(nodeSplitIcon, this);
    	Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) startAction);
    }

    public String describe() {
        String description = "CyNodeSplit - Split selected nodes in current network.";
        return description;
    }
    
    /** Action performed when CyNodeSplit icon is clicked. */
	@SuppressWarnings("serial")
	public class CyNodeSplitAction extends CytoscapeAction {
		public CyNodeSplitAction() {super(NAME);}
	    
		public CyNodeSplitAction(ImageIcon icon, CyNodeSplit plugin) {
			super("", icon);
			this.putValue(Action.SHORT_DESCRIPTION, NAME + " - split nodes");
		}
		public boolean isInToolBar() {
			return true;
		}
		public boolean isInMenuBar() {
			return false;
		}
		
		/** This method is called when the user selects the menu item. */
		public void actionPerformed(ActionEvent ae) {	
	    	// Creates the unique instance of the singleton CyFluxVizPanel
	    	CyNodeSplitPanel nsPanel = CyNodeSplitPanel.getInstance((JFrame) Cytoscape.getDesktop());
	    	nsPanel.setVisible(true);
		}
	}
}
