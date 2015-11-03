package cyfluxviz.gui;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle.ComponentPlacement;

import java.awt.Font;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.util.FileUtil;
import cytoscape.util.OpenBrowser;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;

import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.ui.editors.continuous.C2CMappingEditor;

import cyfluxviz.CyFluxVizPlugin;
import cyfluxviz.FluxDis;
import cyfluxviz.FluxDisCollection;
import cyfluxviz.FluxDisCyAttributes;
import cyfluxviz.FluxDisStatistics;
import cyfluxviz.io.file.VALFluxDisReader;
import cyfluxviz.io.file.XMLFluxDisReader;
import cyfluxviz.io.file.XMLFluxDisWriter;
import cyfluxviz.io.image.GraphicsExporter;
import cyfluxviz.util.AttributeUtils;
import cyfluxviz.util.CytoscapeWrapper;
import cyfluxviz.visual.mapping.ContinuousMappingUpdater;
import cyfluxviz.visual.mapping.MappingUtils;
import cyfluxviz.visual.style.CyFluxVizStyles;
import cyfluxviz.visual.view.NetworkViewTools;
import cyfluxviz.visual.view.NetworkViewUpdater;
import javax.swing.DefaultComboBoxModel;
import javax.swing.AbstractAction;
import javax.swing.Action;


@SuppressWarnings("serial")
public class CyFluxVizPanel extends javax.swing.JPanel implements
		PropertyChangeListener, ListSelectionListener {

	private static CyFluxVizPanel uniqueInstance;

	// Unique instances of FluxDisCollection
	private FluxDisCollection fdCollection = FluxDisCollection.getInstance();
	private JTable fluxTable;
	private FluxDisTableModel tableModel;
	private TableRowSorter<FluxDisTableModel> fluxTableSorter;
	private JScrollPane fluxMapScrollPane;
	
	private JCheckBox globalMaxBox;
	private JEditorPane helpPane;
	private JScrollPane tutorialScrollPane;
	private JLabel imageIconLabel;
	private JPanel importExportPanel;
	private JScrollPane importExportScrollPane;
	private JEditorPane infoPane;
	private JScrollPane infoScrollPane;
	private JTabbedPane informationPane;
	private JButton btnImportVal;
	
	private JCheckBox chckbxFullNetwork;
	private JCheckBox chckbxFluxSubnet;
	
	private JCheckBox jCheckBoxNullVisible;
	private JCheckBox jCheckBoxAttributeSubnet;
	private JLabel jLabel3;
	private JLabel jLabel6;
	private JLabel jLabel7;
	private JLabel jLabel8;
	private JPanel fluxMapPanel;
	private JScrollPane jScrollPane2;
	private JScrollPane jScrollPane3;
	private JSeparator jSeparator1;
	private JSplitPane jSplitPane1;
	private JCheckBox localMaxBox;
	private JPanel mappingPanel;
	private JScrollPane mappingScrollPane;
	private JTextField maxEdgeWidthField;
	private JSlider maxEdgeWidthSlider;
	private JTextField minEdgeWidthField;
	private JComboBox<?>nodeAttributeComboBox;
	private JList<Object> nodeAttributeList;
	private JTabbedPane settingPane;
	private JPanel subnetPanel;
	private JScrollPane subnetScrollPane;
	private JLabel lblLoadXmlFlux;
	private JButton btnLoadCyfluxviz;
	private JLabel lblCreateSubnetworks;
	private JLabel lblManageFluxdistributions;
	private JScrollPane aboutScrollPane;
	private JEditorPane aboutPane;
	private JComboBox<String> comboBoxExportImages;
	private JComboBox<String> comboBoxExportXML;
	private final Action action = new SwingAction();
	
	
	//////// PANEL RELATED THINGS ////////////////////////////
	
	private CyFluxVizPanel() {
		// Add listeners
		Cytoscape
				.getDesktop()
				.getSwingPropertyChangeSupport()
				.addPropertyChangeListener(CytoscapeDesktop.NETWORK_VIEW_FOCUSED, this);
		
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(Cytoscape.ATTRIBUTES_CHANGED, this);
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(Cytoscape.SESSION_LOADED, this);
		
		initComponents();
		System.out.println("CyFluxViz[INFO]: GUI components initialized.");
	}

	public static synchronized CyFluxVizPanel getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new CyFluxVizPanel();
			uniqueInstance.initFluxDistributionTable();
			uniqueInstance.addCyFluxVizPanelToCytoscape();
			uniqueInstance.selectCyFluxVizPanelAndSetDialogs();
			AttributeUtils.initNodeAttributeComboBox();
		}
		return uniqueInstance;
	}

	public void addCyFluxVizPanelToCytoscape() {
		CytoPanel cytoPanel = getCytoPanel();
		cytoPanel.add(CyFluxVizPlugin.NAME, this);
		cytoPanel.setState(CytoPanelState.DOCK);
	}

	public void selectCyFluxVizPanelAndSetDialogs() {
		CytoPanel cytoPanel = getCytoPanel();
		cytoPanel.setSelectedIndex(cytoPanel.indexOfComponent(CyFluxVizPlugin.NAME));
		PanelText.setInfo(this);
		PanelText.setHelp(this);
		PanelText.setAbout(this);
	}

	private CytoPanel getCytoPanel() {
		return Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST);
	}

	public void selectInfoPane() {
		getInformationPane().setSelectedComponent(infoScrollPane);
	}

	
	//////// PROPERTY CHANGE LISTENERS ////////////////////////////

	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equalsIgnoreCase(Cytoscape.ATTRIBUTES_CHANGED)) {
			System.out.println("<-- Cytoscape.ATTRIBUTES_CHANGED -->");
			AttributeUtils.initNodeAttributeComboBox();
		}

		if (e.getPropertyName().equalsIgnoreCase(Cytoscape.SESSION_LOADED)) {
			System.out.println("<-- Cytoscape.SESSION_LOADED -->");
			AttributeUtils.initNodeAttributeComboBox();
			jCheckBoxAttributeSubnet.setSelected(false);
			chckbxFluxSubnet.setSelected(false);
		}

		if (e.getPropertyName().equalsIgnoreCase(
				CytoscapeDesktop.NETWORK_VIEW_FOCUSED)) {
			AttributeUtils.initNodeAttributeComboBox();
		}
		
	}
	
	
	//////// FLUX DISTRIBUTION TABLE STUFF ////////////////////////////
	
	/** Initialize the FluxDistribution table. */
	public void initFluxDistributionTable() {
		// set table model
		tableModel = new FluxDisTableModel();
		fluxTable.setModel(tableModel);
		
		// row sorting
		fluxTableSorter = new TableRowSorter<FluxDisTableModel>(tableModel);
		fluxTable.setRowSorter(fluxTableSorter);
		
		// set layout
		TableColumn column = fluxTable.getColumnModel().getColumn(0);
		column.setPreferredWidth(7);
		column = fluxTable.getColumnModel().getColumn(1);
		column.setPreferredWidth(60);
		column = fluxTable.getColumnModel().getColumn(2);
		column.setPreferredWidth(60);

		// update the table
		updateFluxDistributionTable();
	}
	
	/** Updates the FluxDistributionTable with the current values in the
	 * FluxDisCollection. */
	public void updateFluxDistributionTable() {
		// select the attributes
		
		//FIXME: here is the bug
		FluxDisCyAttributes.selectCyFluxVizTableAttributes();
		
		
		// update the table 
		tableModel.updateTableModel();
		fluxTableSorter.sort();
		selectFirstFluxDistribution();		
	}

	/** Selects the first FluxDistribution in the table. 
	 * Via the selection the active FluxDistribution is changed and 
	 * all visual changes are applied.*/
	private void selectFirstFluxDistribution() {
		if (fluxTable.getRowCount() > 0 && fluxTable.getColumnCount() > 0) {
			fluxTable.setRowSelectionInterval(0, 0);
		}
	}

	/** Remove all selected FluxDistributions from CyFluxViz. */
	private void removeSelectedFluxDistributions() {
		// Dialog
		Object[] options = { "Yes", "No" };
		int n = JOptionPane.showOptionDialog(Cytoscape.getDesktop(),
				"Do you want to delete the selected Flux Distributions?",
				"Delete Flux Distributions", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		if (n == 0) {
			Set<FluxDis> selectedFds = getAllSelectedFluxDistributions();
			FluxDisCollection.getInstance()
					.removeFluxDistributions(selectedFds);
			updateFluxDistributionTable();
		}
	}
	
	//////// CHANGE OF SELECTED FLUX DISTRIBUTION  ////////////////////////////
	
	/** This function is performed if a new FluxDistribution is selected. 
	 * Here the core magic is performed. */
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting() == false) {
			
			// Store the selected nodes in the current network (probably the redraw)
			// TODO: look how handled in the normal changer of VisualStyles
			// 		-> problem that lost is probably related to the redrawing of the graph
			
			// current network used to track the selected nodes and edges
			CyNetwork network = Cytoscape.getCurrentNetwork();
			Set<?> selNodes = null;
			Set<?> selEdges = null;
			if (network != null){
				// generate via copy constructor
				selNodes = new HashSet(network.getSelectedNodes());
				selEdges = new HashSet(network.getSelectedEdges());
			}
			
			// Activate/Deactivate the Fluxdistribution
			// -> CyAttributes are updated automatically via Observer pattern
			FluxDis fd = getSelectedFluxDistribution();
			if (fd != null) {
				fdCollection.activateFluxDistribution(fd);
			} else {
				fdCollection.deactivateFluxDistribution();
			}
			
			// Information in Panel is updated
			updateFluxDistributionInformation();
			
			// Style is updated if necessary
			CyFluxVizStyles.updateStyle();
			
			// Views are updated and drawn
			updateNetworkViewsForSelectedFluxDis();
			
			// Mappings have to be updated for the selected FluxDistribution
			updateMappings();
						
			
			// Re-apply the selection
			if (network != null){
				network.setSelectedNodeState(selNodes, true);
				network.setSelectedEdgeState(selEdges, true);
			}
		}
	}

	/** Update all the views for the selected flux distribution 
	 * in the FluxDistribution table. */
	private void updateNetworkViewsForSelectedFluxDis() {
		// fd == null (no selected FluxDistribution
		FluxDis fd = getSelectedFluxDistribution();
		NetworkViewUpdater nvu = new NetworkViewUpdater(fd);
		nvu.updateNetworkViewsForFluxDistribution();
	}

	/** Returns the last selected FluxDistribution in the table.
	 * Returns null if no FluxDistribution is selected. */
	public FluxDis getSelectedFluxDistribution() {
		int selected = fluxTable.getSelectedRow();
		if (selected != -1) {
			// due to sorting
			int rowNum = fluxTable.convertRowIndexToModel(selected);
			Integer fdId = tableModel.getFluxDisId(rowNum);
			return fdCollection.getFluxDistribution(fdId);
		}
		return null;
	}

	/** Returns all selected FluxDistributions in table. */
	public Set<FluxDis> getAllSelectedFluxDistributions() {
		Set<FluxDis> selectedFD = new HashSet<FluxDis>();
		int[] selected = fluxTable.getSelectedRows();
		if (selected != null && selected.length > 0) {
			for (int k = 0; k < selected.length; ++k) {
				// due to sorting
				int rowNum = fluxTable.convertRowIndexToModel(selected[k]);
				Integer fdId = tableModel.getFluxDisId(rowNum);
				selectedFD.add(fdCollection.getFluxDistribution(fdId));
			}
		}
		return selectedFD;
	}

	
	//////// ATTRIBUTE SUBNETWORKS  ////////////////////////////
	
	private void nodeAttributeComboBoxActionPerformed(
			java.awt.event.ActionEvent evt) {
		int index = nodeAttributeComboBox.getSelectedIndex();
		if (index != -1) {
			String attribute = (String) nodeAttributeComboBox.getSelectedItem();
			AttributeUtils.initNodeAttributeList(attribute);
		}
	}

	private void nodeAttributeListValueChanged(
			javax.swing.event.ListSelectionEvent evt) {
		if (evt.getValueIsAdjusting() == false) {
			updateNetworkViewsForSelectedFluxDis();
		}
	}

	
	//////// MAPPING RELATED THINGS ////////////////////////////
	
	/** Update the edge width mapping, meaning different mapping points are necessary.
	 * The function changes the mapping points.
	 * The visibility of nodes and edges is not changed. Only the actual mapping
	 * in the Calculator is adapted to the settings. */
	public void updateMappings() {
		VisualPropertyType vpt = VisualPropertyType.EDGE_LINE_WIDTH;
		if (!MappingUtils.existsMappingForVisualPropertyType(vpt)){
			return;
		}
		
		// Get values necessary to update the mappings
		double minFlux = 0.0;
		double maxFlux = getMaxFluxForMapping();
		double minEdgeWidth = Double.parseDouble(minEdgeWidthField.getText());
		double maxEdgeWidth = Double.parseDouble(maxEdgeWidthField.getText());

		double[] xvec = {minFlux, maxFlux};
		double[] yvec = {minEdgeWidth, maxEdgeWidth};
		
		// The points in the mapping are updated
		ContinuousMappingUpdater.updateMappingPoints(vpt, xvec, yvec);
		
		// Recalculate all the calculators global, node, edges
		Cytoscape.getVisualMappingManager().applyAppearances();

		// Apply the changes in the VizMap to all the views
		CyNetwork network = Cytoscape.getCurrentNetwork();
		if (network != null){
			for (CyNetworkView view : NetworkViewTools.getViewsForNetwork(network.getTitle())){
				NetworkViewUpdater.applyVizMapChangesToView(view);
			}
		}
		
		// Updates the mapping overview icon
		updateEdgeWidthIcon();		
	}

	/** Get the maximal flux from the settings and available
	 * flux distributons. */
	private double getMaxFluxForMapping() {
		double maxFlux = 1.0;
		if (globalMaxBox.isSelected()) {
			maxFlux = fdCollection.getGlobalAbsMax() * 1.01;
		} else if (fdCollection.hasActiveFluxDistribution()) {
			maxFlux = fdCollection.getActiveFluxDistribution()
					.getFluxStatistics().getAbsMax() * 1.01;
		}
		return maxFlux;
	}

	// Multiple options which change the mapping view.
	// Here the dependencies between the FluxDistribution values and the 
	// actual EdgeWidth, NodeSize, ... are updated.
	private void localMaxBoxActionPerformed(java.awt.event.ActionEvent evt) {
		globalMaxBox.setSelected(!localMaxBox.isSelected());
		updateMappings();
	}

	private void globalMaxBoxActionPerformed(java.awt.event.ActionEvent evt) {
		localMaxBox.setSelected(!globalMaxBox.isSelected());
		updateMappings();
	}
	
	private void minEdgeWidthFieldActionPerformed(java.awt.event.ActionEvent evt) {
		updateMappings();
	}

	private void maxEdgeWidthFieldActionPerformed(java.awt.event.ActionEvent evt) {
		// Update the slider setting
		int maxEdgeWidth = 50;
		try {
			maxEdgeWidth = (int) Double
					.parseDouble(maxEdgeWidthField.getText());
		} catch (Exception e) {
			maxEdgeWidthField.setText("50");
		}
		maxEdgeWidthSlider.setValue(maxEdgeWidth);
		if (maxEdgeWidth <= 80)
			maxEdgeWidthSlider.setMaximum(100);
		else
			maxEdgeWidthSlider.setMaximum(maxEdgeWidth + 50);

		updateMappings();
	}

	private void maxEdgeWidthSliderMouseReleased(java.awt.event.MouseEvent evt) {
		Integer maxEdgeWidth = maxEdgeWidthSlider.getValue();
		if (maxEdgeWidth <= 80)
			maxEdgeWidthSlider.setMaximum(100);
		else
			maxEdgeWidthSlider.setMaximum(maxEdgeWidth + 50);
		maxEdgeWidthField.setText(maxEdgeWidth.toString());

		updateMappings();
	}

	
	// Things associated to the EdgeWidth Icon view.
	private void imageIconLabelMouseClicked(java.awt.event.MouseEvent evt) {
		VisualPropertyType vpt = VisualPropertyType.EDGE_LINE_WIDTH;
		if (MappingUtils.existsMappingForVisualPropertyType(vpt)) {
			Object editorObject = C2CMappingEditor.showDialog(600, 450,
					"Advanced flux <-> edgeWidth mapping ", vpt);
			C2CMappingEditor editor = (C2CMappingEditor) editorObject;
			editor.addWindowListener(new ImageIconListener());
		}
	}
	
	public void updateEdgeWidthIcon(){
		imageIconLabel.setIcon(C2CMappingEditor.getIcon(161, 94, VisualPropertyType.EDGE_LINE_WIDTH));
	}
	
	class ImageIconListener implements WindowListener {
		public void windowClosing(WindowEvent e) {
			// update the icon in the mapping tab
			updateEdgeWidthIcon();
			// TODO: apply the mapping to the current views
			
		}

		public void windowOpened(WindowEvent arg0) {
		}

		public void windowClosed(WindowEvent arg0) {
		}

		public void windowIconified(WindowEvent arg0) {
		}

		public void windowDeiconified(WindowEvent arg0) {
		}

		public void windowActivated(WindowEvent arg0) {
		}

		public void windowDeactivated(WindowEvent arg0) {
		}
	}

	
	//////// HYPERLINK LISTENERS ////////////////////////////
	
	private void infoPaneHyperlinkUpdate(HyperlinkEvent evt) {
		openEventURLInBrowser(evt);
	}
	
	private void helpPaneHyperlinkUpdate(HyperlinkEvent evt) {
		openEventURLInBrowser(evt);
	}

	private void aboutPaneHyperlinkUpdate(HyperlinkEvent evt) {
		openEventURLInBrowser(evt);
	}

	private void openEventURLInBrowser(HyperlinkEvent evt) {
		URL url = evt.getURL();
		if (url != null) {
			if (evt.getEventType() == HyperlinkEvent.EventType.ENTERED) {
				CytoscapeWrapper.setStatusBarMsg(url.toString());
			} else if (evt.getEventType() == HyperlinkEvent.EventType.EXITED) {
				CytoscapeWrapper.clearStatusBar();
			} else if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				OpenBrowser.openURL(url.toString());
			}
		}
	}
	
	
	//// SET TEXT IN PANES ////////////

	// TODO: Depending on local files ?????? why, what is this ???
	private void updatePaneHTMLText(javax.swing.JEditorPane pane, String htmlText) {
		String info = String
				.format("<html>"
						+ "<head>"
						+ "	<base href='file:///home/mkoenig/workspace/fluxviz/'></base>"
						+ "	<style type='text/css'>body{ font-family: sans-serif; font-size: 11pt; }</style>"
						+ "</head>" + "<body>%s</body></html>", htmlText);
		pane.setText(info);
	}

	private void updatePaneHTMLText(javax.swing.JEditorPane pane, URL url) {
		try {
			pane.setPage(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    /** Updates the information for the flux distribution in the information panel. */
    public void updateFluxDistributionInformation(){
    	String info = "";
    	if (fdCollection.hasActiveFluxDistribution()){
    		FluxDis fd = fdCollection.getActiveFluxDistribution();
            FluxDisStatistics fds = fd.getFluxStatistics();
            info += fd.toHTML(); 
            info += fds.toHTML();
    	}
        updateInfoPaneHTMLText(info);
        selectInfoPane();
    }
	
	public void updateInfoPaneHTMLText(String htmlText) {
		updatePaneHTMLText(infoPane, htmlText);
	}

	public void updateInfoPaneHTMLText(URL url) {
		updatePaneHTMLText(infoPane, url);
	}

	public void updateHelpPaneHTMLText(String htmlText) {
		updatePaneHTMLText(helpPane, htmlText);
	}

	public void updateHelpPaneHTMLText(URL url) {
		updatePaneHTMLText(helpPane, url);
	}
	
	public void updateAboutPaneHTMLText(String htmlText) {
		updatePaneHTMLText(aboutPane, htmlText);
	}

	public void updateAboutPaneHTMLText(URL url) {
		updatePaneHTMLText(aboutPane, url);
	}

	// //// GETTER AND SETTER ////////////
	public javax.swing.JTable getFluxTable() {
		return fluxTable;
	}

	public javax.swing.table.DefaultTableModel getTableModel() {
		return tableModel;
	}

	public javax.swing.JCheckBox getFluxSubnetCheckbox() {
		return chckbxFluxSubnet;
	}

	public javax.swing.JCheckBox getAttributeSubnetCheckbox() {
		return jCheckBoxAttributeSubnet;
	}

	public javax.swing.JCheckBox getNullVisibleCheckbox() {
		return jCheckBoxNullVisible;
	}

	public javax.swing.JTabbedPane getInformationPane() {
		return informationPane;
	}

	public javax.swing.JComboBox<?> getNodeAttributeComboBox() {
		return nodeAttributeComboBox;
	}

	public javax.swing.JList<Object> getNodeAttributeList() {
		return nodeAttributeList;
	}

	public javax.swing.JScrollPane getHelpScrollPane() {
		return tutorialScrollPane;
	}

	public javax.swing.JEditorPane getHelpPane() {
		return helpPane;
	}

	public javax.swing.JScrollPane getExamplesScrollPane() {
		return infoScrollPane;
	}

	// //// INIT GUI ////////////
	private void initComponents() {
		infoPane = new javax.swing.JEditorPane();

		informationPane = new javax.swing.JTabbedPane();
		infoScrollPane = new javax.swing.JScrollPane();

		tutorialScrollPane = new javax.swing.JScrollPane();
		helpPane = new javax.swing.JEditorPane();

		mappingScrollPane = new javax.swing.JScrollPane();
		mappingPanel = new javax.swing.JPanel();

		settingPane = new javax.swing.JTabbedPane();

		jSplitPane1 = new javax.swing.JSplitPane();

		jScrollPane2 = new javax.swing.JScrollPane();

		fluxTable = new javax.swing.JTable();

		setMaximumSize(new java.awt.Dimension(32000, 32000));
		setMinimumSize(new java.awt.Dimension(180, 300));
		setPreferredSize(new java.awt.Dimension(300, 700));

		// Mapping Panel
		mappingPanel.setBackground(java.awt.Color.white);
		javax.swing.GroupLayout mappingPanelLayout = new javax.swing.GroupLayout(mappingPanel);
		mappingPanel.setLayout(mappingPanelLayout);
		mappingScrollPane.setViewportView(mappingPanel);

		jSplitPane1.setDividerLocation(300);
		jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
		jSplitPane1.setMinimumSize(new java.awt.Dimension(40, 40));
		jSplitPane1.setPreferredSize(new java.awt.Dimension(180, 600));

		infoPane.setContentType("text/html");
		infoPane.setEditable(false);
		infoPane.setFont(new Font("Dialog", Font.PLAIN, 11));
		infoPane.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
			public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {
				infoPaneHyperlinkUpdate(evt);
			}
		});
		infoScrollPane.setViewportView(infoPane);

		ImageIcon fluxVizIcon = new ImageIcon(getClass().getResource(
				"/cyfluxviz/gui/images/CyFluxViz_logo_small.png"));
		informationPane.addTab("Info ", fluxVizIcon, infoScrollPane);

		helpPane.setContentType("text/html");
		helpPane.setEditable(false);
		helpPane.setFont(new Font("Dialog", Font.PLAIN, 11));
		helpPane.setToolTipText("Information about the flux distributions.");
		helpPane.setMinimumSize(new java.awt.Dimension(130, 50));
		helpPane.setPreferredSize(new java.awt.Dimension(180, 100));
		helpPane.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
			public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {
				helpPaneHyperlinkUpdate(evt);
			}
		});
		tutorialScrollPane.setViewportView(helpPane);

		informationPane.addTab("Tutorial", tutorialScrollPane);

		jSplitPane1.setLeftComponent(informationPane);
		
		aboutScrollPane = new JScrollPane();
		informationPane.addTab("About", null, aboutScrollPane, null);
		
		aboutPane = new JEditorPane();
		aboutPane.setEditable(false);
		aboutPane.setFont(new Font("Dialog", Font.PLAIN, 11));
		aboutPane.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
			public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {
				aboutPaneHyperlinkUpdate(evt);
			}
		});
		
		aboutScrollPane.setViewportView(aboutPane);

		jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder(
				null, "Flux distributions",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION,
				new java.awt.Font("DejaVu Sans", 0, 12),
				java.awt.Color.darkGray)); // NOI18N
		jScrollPane2.setForeground(new java.awt.Color(254, 254, 254));
		jScrollPane2.setToolTipText("Overview over  loaded flux distributions");
		jScrollPane2.setPreferredSize(new java.awt.Dimension(180, 500));

		fluxTable.setModel(new FluxDisTableModel());
		jScrollPane2.setViewportView(fluxTable);

		jSplitPane1.setRightComponent(jScrollPane2);
		jScrollPane2.getAccessibleContext().setAccessibleDescription(
				"Overview over loaded flux distributions");
		
				importExportScrollPane = new javax.swing.JScrollPane();
				importExportPanel = new javax.swing.JPanel();
				importExportPanel.setToolTipText("");
				btnImportVal = new javax.swing.JButton();
				btnImportVal.setBounds(148, 53, 65, 25);
				
						jLabel3 = new javax.swing.JLabel();
						jLabel3.setToolTipText("");
						jLabel3.setFont(new Font("Tahoma", Font.BOLD, 11));
						jLabel3.setBounds(10, 58, 89, 15);
						
								jLabel3.setText("*.val Format");
								importExportPanel.setBackground(java.awt.Color.white);
								btnImportVal.setText("Import");
								btnImportVal.setToolTipText("Import FluxDistributions in CyFluxViz VAL format");
								btnImportVal.addActionListener(new java.awt.event.ActionListener() {
									public void actionPerformed(java.awt.event.ActionEvent evt) {
										VALFluxDisReader reader = new VALFluxDisReader();
										Collection<FluxDis> fds = reader.read();
										
										String msg = fdCollection.addFluxDistributions(fds);
										if (msg != null && msg.length() > 0){
											PanelText.showMessage(msg, "Loading FluxDistributions from *.val");
										}
									}
								});
								
										lblLoadXmlFlux = new JLabel();
										lblLoadXmlFlux.setToolTipText("");
										lblLoadXmlFlux.setFont(new Font("Tahoma", Font.BOLD, 11));
										lblLoadXmlFlux.setBounds(10, 27, 89, 15);
										lblLoadXmlFlux.setText("*.xml Format");
										
												btnLoadCyfluxviz = new JButton();
												btnLoadCyfluxviz.setBounds(148, 22, 65, 25);
												btnLoadCyfluxviz.addActionListener(new ActionListener() {
													public void actionPerformed(ActionEvent arg0) {
														XMLFluxDisReader reader = new XMLFluxDisReader();	
														Collection<FluxDis> fds = reader.read();
														String msg = fdCollection.addFluxDistributions(fds);
														if (msg != null && msg.length() > 0){
															PanelText.showMessage(msg, "Network not found while loading Flux Distributions");
														}
													}
												});
												btnLoadCyfluxviz
														.setToolTipText("Import FluxDistributions in CyFluxViz XML format");
												btnLoadCyfluxviz.setText("Import");
												
														importExportScrollPane.setViewportView(importExportPanel);
														importExportPanel.setLayout(null);
														importExportPanel.add(btnLoadCyfluxviz);
														importExportPanel.add(lblLoadXmlFlux);
														importExportPanel.add(btnImportVal);
														importExportPanel.add(jLabel3);
														
														JLabel lblImportFluxdistributions = new JLabel();
														lblImportFluxdistributions.setToolTipText("");
														lblImportFluxdistributions.setText("Import FluxDistributions");
														lblImportFluxdistributions.setFont(new Font("DejaVu Sans", Font.BOLD, 13));
														lblImportFluxdistributions.setBackground(Color.WHITE);
														lblImportFluxdistributions.setBounds(10, 0, 192, 17);
														importExportPanel.add(lblImportFluxdistributions);
														
														JButton btnExportImages = new JButton();
														btnExportImages.addActionListener(new ActionListener() {
															public void actionPerformed(ActionEvent arg0) {
																// export the images
																int selected = comboBoxExportImages.getSelectedIndex();
																Collection<FluxDis> fds = null;
																if (selected == 0){
																	System.out.println("CyFluxViz[INFO]: Export Images for all FluxDistributions");
																	fds = fdCollection.getFluxDistributions();
																} else if (selected == 1){
																	fds = getAllSelectedFluxDistributions();
																	System.out.println("CyFluxViz[INFO]: Export Images for selected FluxDistributions");
																}
																GraphicsExporter exp = new GraphicsExporter(fds);
																exp.exportImages();
															}
														});
														btnExportImages.setToolTipText("Export NetworkViews to images");
														btnExportImages.setText("Export");
														btnExportImages.setBounds(148, 180, 65, 25);
														importExportPanel.add(btnExportImages);
														
														JLabel lblExportImages = new JLabel();
														lblExportImages.setToolTipText("");
														lblExportImages.setText("Export Network Images");
														lblExportImages.setFont(new Font("DejaVu Sans", Font.BOLD, 13));
														lblExportImages.setBackground(Color.WHITE);
														lblExportImages.setBounds(10, 157, 215, 17);
														importExportPanel.add(lblExportImages);
														
														JSeparator separator = new JSeparator();
														separator.setBounds(10, 153, 216, 10);
														importExportPanel.add(separator);
														
														JButton btnExportXML = new JButton();
														btnExportXML.addActionListener(new ActionListener() {
															public void actionPerformed(ActionEvent arg0) {
																// get the fluxDistributions
																int selected = comboBoxExportXML.getSelectedIndex();
																System.out.println("Selected option -> " + selected);
																Collection<FluxDis> fds = null;
																if (selected == 0){
																	System.out.println("CyFluxViz[INFO]: Export all FluxDistributions");
																	fds = fdCollection.getFluxDistributions();
																} else if (selected == 1){
																	fds = getAllSelectedFluxDistributions();
																	System.out.println("CyFluxViz[INFO]: Export selected FluxDistributions");
																}
																// Export FLuxDistributions
																File xmlFile = FileUtil.getFile("Export FluxDistributions", FileUtil.LOAD);
																XMLFluxDisWriter writer = new XMLFluxDisWriter(xmlFile);
																writer.write(fds);
															}
														});
														btnExportXML.setAction(action);
														btnExportXML.setToolTipText("Export FluxDistributions to CyFluxViz XML format");
														btnExportXML.setText("Export");
														btnExportXML.setBounds(148, 117, 65, 25);
														importExportPanel.add(btnExportXML);
														
														JLabel lblExportFluxDistributions = new JLabel();
														lblExportFluxDistributions.setToolTipText("");
														lblExportFluxDistributions.setText("Export FluxDistributions");
														lblExportFluxDistributions.setFont(new Font("DejaVu Sans", Font.BOLD, 13));
														lblExportFluxDistributions.setBackground(Color.WHITE);
														lblExportFluxDistributions.setBounds(10, 94, 192, 17);
														importExportPanel.add(lblExportFluxDistributions);
														
														comboBoxExportXML = new JComboBox();
														comboBoxExportXML.setToolTipText("");
														comboBoxExportXML.setMaximumRowCount(2);
														comboBoxExportXML.setModel(new DefaultComboBoxModel(new String[] {"All", "Selected Only"}));
														comboBoxExportXML.setSelectedIndex(0);
														comboBoxExportXML.setBounds(10, 119, 128, 20);
														importExportPanel.add(comboBoxExportXML);
														
														comboBoxExportImages = new JComboBox();
														comboBoxExportImages.setToolTipText("");
														comboBoxExportImages.setMaximumRowCount(2);
														comboBoxExportImages.setModel(new DefaultComboBoxModel(new String[] {"All", "Selected Only"}));
														comboBoxExportImages.setSelectedIndex(0);
														comboBoxExportImages.setBounds(10, 182, 128, 20);
														importExportPanel.add(comboBoxExportImages);
														
																settingPane.addTab("Import/Export", importExportScrollPane);
																importExportScrollPane.getAccessibleContext().setAccessibleName("Import");
																importExportScrollPane.getAccessibleContext().setAccessibleDescription(
																		"Import Flux Distributions");
		fluxMapScrollPane = new javax.swing.JScrollPane();
		fluxMapPanel = new javax.swing.JPanel();
		fluxMapPanel.setBorder(null);
		jLabel6 = new javax.swing.JLabel();
		jLabel6.setToolTipText("");
		minEdgeWidthField = new javax.swing.JTextField();
		minEdgeWidthField.setToolTipText("Minimal edge width in mapping");
		jLabel7 = new javax.swing.JLabel();
		jLabel7.setToolTipText("");
		jLabel8 = new javax.swing.JLabel();
		jLabel8.setToolTipText("");
		maxEdgeWidthSlider = new javax.swing.JSlider();
		maxEdgeWidthSlider.setToolTipText("Set maximum Edge Width");
		maxEdgeWidthSlider.setMaximum(50);
		maxEdgeWidthSlider.setValue(15);
		localMaxBox = new javax.swing.JCheckBox();
		localMaxBox.setToolTipText("");
		globalMaxBox = new javax.swing.JCheckBox();
		globalMaxBox.setToolTipText("");
		maxEdgeWidthField = new javax.swing.JTextField();
		maxEdgeWidthField.setToolTipText("Maximal edge width in mapping");
		imageIconLabel = new javax.swing.JLabel("");
		
		fluxMapPanel.setBackground(java.awt.Color.white);

		jLabel6.setBackground(java.awt.Color.white);
		jLabel6.setFont(new java.awt.Font("DejaVu Sans", 1, 13));
		jLabel6.setText("Settings for Flux Mapping");

		minEdgeWidthField.setFont(new java.awt.Font("DejaVu Sans", 0, 11));
		minEdgeWidthField.setText("1.0");
		minEdgeWidthField
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						minEdgeWidthFieldActionPerformed(evt);
					}
				});

		jLabel7.setBackground(java.awt.Color.white);
		jLabel7.setFont(new java.awt.Font("DejaVu Sans", 0, 11));
		jLabel7.setText("Min Edge Width");

		jLabel8.setBackground(java.awt.Color.white);
		jLabel8.setFont(new java.awt.Font("DejaVu Sans", 0, 11));
		jLabel8.setText("Max Edge Width");

		maxEdgeWidthSlider.setBackground(java.awt.Color.white);
		maxEdgeWidthSlider.setFont(new java.awt.Font("DejaVu Sans", 0, 11));
		maxEdgeWidthSlider.setMajorTickSpacing(10);
		maxEdgeWidthSlider.setPaintLabels(true);
		maxEdgeWidthSlider.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				maxEdgeWidthSliderMouseReleased(evt);
			}
		});
		maxEdgeWidthSlider
				.addChangeListener(new javax.swing.event.ChangeListener() {
					public void stateChanged(javax.swing.event.ChangeEvent evt) {
						maxEdgeWidthSliderStateChanged(evt);
					}

					private void maxEdgeWidthSliderStateChanged(ChangeEvent evt) {
					}
				});

		localMaxBox.setBackground(java.awt.Color.white);
		localMaxBox.setFont(new java.awt.Font("DejaVu Sans", 0, 11));
		localMaxBox.setSelected(true);
		localMaxBox.setText("local Max");
		localMaxBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				localMaxBoxActionPerformed(evt);
			}
		});

		globalMaxBox.setBackground(java.awt.Color.white);
		globalMaxBox.setFont(new java.awt.Font("DejaVu Sans", 0, 11));
		globalMaxBox.setText("global Max");
		globalMaxBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				globalMaxBoxActionPerformed(evt);
			}
		});

		maxEdgeWidthField.setFont(new java.awt.Font("DejaVu Sans", 0, 11));
		maxEdgeWidthField.setText("15.0");
		maxEdgeWidthField
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						maxEdgeWidthFieldActionPerformed(evt);
					}
				});

		
		imageIconLabel.setToolTipText("");
		imageIconLabel.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				imageIconLabelMouseClicked(evt);
			}
		});
		

		javax.swing.GroupLayout gl_fluxMapPanel = new javax.swing.GroupLayout(
				fluxMapPanel);
		gl_fluxMapPanel.setHorizontalGroup(
			gl_fluxMapPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_fluxMapPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_fluxMapPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_fluxMapPanel.createSequentialGroup()
							.addGroup(gl_fluxMapPanel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_fluxMapPanel.createSequentialGroup()
									.addGap(12)
									.addComponent(imageIconLabel, GroupLayout.PREFERRED_SIZE, 161, GroupLayout.PREFERRED_SIZE))
								.addComponent(jLabel7)
								.addComponent(jLabel8)
								.addComponent(jLabel6)
								.addGroup(gl_fluxMapPanel.createSequentialGroup()
									.addComponent(localMaxBox)
									.addGroup(gl_fluxMapPanel.createParallelGroup(Alignment.LEADING)
										.addGroup(gl_fluxMapPanel.createSequentialGroup()
											.addGap(33)
											.addGroup(gl_fluxMapPanel.createParallelGroup(Alignment.TRAILING)
												.addComponent(maxEdgeWidthField, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
												.addComponent(minEdgeWidthField, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)))
										.addGroup(gl_fluxMapPanel.createSequentialGroup()
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addComponent(globalMaxBox)))))
							.addGap(91))
						.addGroup(gl_fluxMapPanel.createSequentialGroup()
							.addComponent(maxEdgeWidthSlider, 0, 0, Short.MAX_VALUE)
							.addGap(93))))
		);
		gl_fluxMapPanel.setVerticalGroup(
			gl_fluxMapPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_fluxMapPanel.createSequentialGroup()
					.addComponent(jLabel6)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_fluxMapPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(localMaxBox)
						.addComponent(globalMaxBox))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_fluxMapPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(jLabel7)
						.addComponent(minEdgeWidthField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_fluxMapPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(jLabel8)
						.addComponent(maxEdgeWidthField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(2)
					.addComponent(maxEdgeWidthSlider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(imageIconLabel, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE))
		);
		fluxMapPanel.setLayout(gl_fluxMapPanel);

		fluxMapScrollPane.setViewportView(fluxMapPanel);

		settingPane.addTab("FluxMapping", fluxMapScrollPane);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(jSplitPane1,
						javax.swing.GroupLayout.DEFAULT_SIZE,
						javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(settingPane,
						javax.swing.GroupLayout.DEFAULT_SIZE, 180,
						Short.MAX_VALUE));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup()
								.addComponent(jSplitPane1,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										418, Short.MAX_VALUE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(settingPane,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										276,
										javax.swing.GroupLayout.PREFERRED_SIZE)));
		
				subnetScrollPane = new javax.swing.JScrollPane();
				subnetPanel = new javax.swing.JPanel();
				subnetPanel.setForeground(Color.BLACK);
				subnetPanel.setBorder(null);
				subnetPanel.setToolTipText("Create subnetworks based on flux and network attributes.");
				
						nodeAttributeComboBox = new javax.swing.JComboBox();
						nodeAttributeComboBox.setToolTipText("Select attribute for subnetwork");
						
								jScrollPane3 = new javax.swing.JScrollPane();
								nodeAttributeList = new javax.swing.JList();
								
										chckbxFluxSubnet = new javax.swing.JCheckBox();
										jCheckBoxAttributeSubnet = new javax.swing.JCheckBox();
										jCheckBoxNullVisible = new javax.swing.JCheckBox();
										jCheckBoxNullVisible.setToolTipText("Display also nodes which have 'null' as value for the selected attributes.");
										
												jSeparator1 = new javax.swing.JSeparator();
												
														subnetPanel.setBackground(java.awt.Color.white);
														subnetPanel.setPreferredSize(new java.awt.Dimension(200, 161));
														
																jScrollPane3
																		.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
																
																		nodeAttributeList.setToolTipText("Select displayed attribute values");
																		nodeAttributeList
																				.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
																					public void valueChanged(
																							javax.swing.event.ListSelectionEvent evt) {
																						nodeAttributeListValueChanged(evt);
																					}
																				});
																		jScrollPane3.setViewportView(nodeAttributeList);
																		
																				chckbxFluxSubnet.setBackground(java.awt.Color.white);
																				chckbxFluxSubnet.setText("Flux Subnetwork");
																				chckbxFluxSubnet
																						.setToolTipText("Reduce view to flux containing sub-network");
																				chckbxFluxSubnet
																						.addActionListener(new java.awt.event.ActionListener() {
																							public void actionPerformed(java.awt.event.ActionEvent evt) {
																								chckbxFullNetwork.setSelected(!chckbxFluxSubnet.isSelected());
																								updateNetworkViewsForSelectedFluxDis();
																							}
																						});
																				chckbxFullNetwork = new JCheckBox();
																				chckbxFullNetwork.addActionListener(new ActionListener() {
																					public void actionPerformed(ActionEvent arg0) {
																						chckbxFluxSubnet.setSelected(!chckbxFullNetwork.isSelected());
																						updateNetworkViewsForSelectedFluxDis();
																					}
																				});
																				chckbxFullNetwork.setSelected(true);
																				chckbxFullNetwork
																						.setToolTipText("Display full network");
																				chckbxFullNetwork.setText("Full Network");
																				chckbxFullNetwork.setBackground(Color.WHITE);
																				
																				
																						jCheckBoxAttributeSubnet.setBackground(java.awt.Color.white);
																						jCheckBoxAttributeSubnet.setText("Attribute Subnetwork");
																						jCheckBoxAttributeSubnet
																								.setToolTipText("Create a subnetwork based on attributes");
																						jCheckBoxAttributeSubnet
																								.addActionListener(new java.awt.event.ActionListener() {
																									public void actionPerformed(java.awt.event.ActionEvent evt) {
																										updateNetworkViewsForSelectedFluxDis();
																									}
																								});
																						
																								nodeAttributeComboBox
																										.addActionListener(new java.awt.event.ActionListener() {
																											public void actionPerformed(java.awt.event.ActionEvent evt) {
																												nodeAttributeComboBoxActionPerformed(evt);
																											}
																										});
																								
																										jCheckBoxNullVisible.setBackground(java.awt.Color.white);
																										jCheckBoxNullVisible.setFont(new java.awt.Font("DejaVu Sans", 0, 10));
																										jCheckBoxNullVisible.setSelected(true);
																										jCheckBoxNullVisible.setText("NullVisible");
																										jCheckBoxNullVisible
																												.addActionListener(new java.awt.event.ActionListener() {
																													public void actionPerformed(java.awt.event.ActionEvent evt) {
																														updateNetworkViewsForSelectedFluxDis();
																													}
																												});
																										
																										lblCreateSubnetworks = new JLabel();
																										lblCreateSubnetworks.setToolTipText("Create subnetworks based on flux and network attributes.");
																										lblCreateSubnetworks.setText("Create Subnetworks");
																										lblCreateSubnetworks.setFont(new Font("DejaVu Sans", Font.BOLD, 13));
																										lblCreateSubnetworks.setBackground(Color.WHITE);
																										
																											
																												javax.swing.GroupLayout subnetPanelLayout = new javax.swing.GroupLayout(
																														subnetPanel);
																												subnetPanelLayout.setHorizontalGroup(
																													subnetPanelLayout.createParallelGroup(Alignment.LEADING)
																														.addGroup(subnetPanelLayout.createSequentialGroup()
																															.addContainerGap()
																															.addGroup(subnetPanelLayout.createParallelGroup(Alignment.TRAILING, false)
																																.addComponent(jSeparator1, Alignment.LEADING)
																																.addGroup(Alignment.LEADING, subnetPanelLayout.createSequentialGroup()
																																	.addComponent(chckbxFullNetwork, GroupLayout.PREFERRED_SIZE, 93, GroupLayout.PREFERRED_SIZE)
																																	.addPreferredGap(ComponentPlacement.RELATED)
																																	.addComponent(chckbxFluxSubnet))
																																.addGroup(Alignment.LEADING, subnetPanelLayout.createParallelGroup(Alignment.TRAILING, false)
																																	.addComponent(nodeAttributeComboBox, Alignment.LEADING, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
																																	.addComponent(jScrollPane3, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE))
																																.addGroup(Alignment.LEADING, subnetPanelLayout.createSequentialGroup()
																																	.addComponent(jCheckBoxAttributeSubnet)
																																	.addPreferredGap(ComponentPlacement.UNRELATED)
																																	.addComponent(jCheckBoxNullVisible))
																																.addComponent(lblCreateSubnetworks, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 192, GroupLayout.PREFERRED_SIZE))
																															.addContainerGap(79, Short.MAX_VALUE))
																												);
																												subnetPanelLayout.setVerticalGroup(
																													subnetPanelLayout.createParallelGroup(Alignment.LEADING)
																														.addGroup(subnetPanelLayout.createSequentialGroup()
																															.addComponent(lblCreateSubnetworks, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
																															.addPreferredGap(ComponentPlacement.RELATED)
																															.addGroup(subnetPanelLayout.createParallelGroup(Alignment.BASELINE)
																																.addComponent(chckbxFullNetwork)
																																.addComponent(chckbxFluxSubnet))
																															.addPreferredGap(ComponentPlacement.RELATED)
																															.addComponent(jSeparator1, GroupLayout.PREFERRED_SIZE, 5, GroupLayout.PREFERRED_SIZE)
																															.addPreferredGap(ComponentPlacement.RELATED)
																															.addGroup(subnetPanelLayout.createParallelGroup(Alignment.BASELINE)
																																.addComponent(jCheckBoxAttributeSubnet)
																																.addComponent(jCheckBoxNullVisible))
																															.addPreferredGap(ComponentPlacement.RELATED)
																															.addComponent(nodeAttributeComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
																															.addPreferredGap(ComponentPlacement.RELATED)
																															.addComponent(jScrollPane3, GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
																															.addContainerGap())
																												);
																												subnetPanel.setLayout(subnetPanelLayout);
																												subnetScrollPane.setViewportView(subnetPanel);
																												settingPane.addTab("SubNetwork", subnetScrollPane);

		JScrollPane fluxdisScrollPane = new JScrollPane();
		settingPane.addTab("FluxDistributions", null, fluxdisScrollPane, "Manage Flux Distributions");
		settingPane.setEnabledAt(3, true);

		JPanel fluxDisPanel = new JPanel();
		fluxDisPanel.setBorder(null);
		fluxDisPanel.setToolTipText("");
		fluxDisPanel.setBackground(Color.WHITE);
		fluxdisScrollPane.setViewportView(fluxDisPanel);

		JLabel lblRemoveSelectedFlux = new JLabel(
				"Delete FluxDistributions");
		lblRemoveSelectedFlux.setToolTipText("");
		lblRemoveSelectedFlux.setBounds(16, 28, 121, 15);

		JButton btnRemoveSelectedFD = new JButton("Delete");
		btnRemoveSelectedFD.setBounds(138, 23, 81, 25);
		btnRemoveSelectedFD.setToolTipText("Delete selected FluxDistributions from CyFluxViz");
		btnRemoveSelectedFD.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				removeSelectedFluxDistributions();
			}
		});
		fluxDisPanel.setLayout(null);
		fluxDisPanel.add(lblRemoveSelectedFlux);
		fluxDisPanel.add(btnRemoveSelectedFD);
		
		lblManageFluxdistributions = new JLabel();
		lblManageFluxdistributions.setToolTipText("");
		lblManageFluxdistributions.setText("Manage FluxDistributions");
		lblManageFluxdistributions.setFont(new Font("DejaVu Sans", Font.BOLD, 13));
		lblManageFluxdistributions.setBackground(Color.WHITE);
		lblManageFluxdistributions.setBounds(12, 0, 192, 17);
		fluxDisPanel.add(lblManageFluxdistributions);

		settingPane.getAccessibleContext().setAccessibleName("Settings");
		settingPane.setSelectedIndex(0);
		fluxTable.getSelectionModel().addListSelectionListener(this);
	}
	
	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, "SwingAction");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
}