package cyfluxviz.gui;

import java.awt.Font;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import cyfluxviz.CyFluxViz;
import cyfluxviz.FluxDis;
import cyfluxviz.FluxDisCollection;
import cyfluxviz.FluxDisCyAttributes;
import cyfluxviz.io.ValFluxDistributionImporter;
import cyfluxviz.mapping.ApplyEdgeWidthMapping;
import cyfluxviz.netview.NetworkView;
import cyfluxviz.netview.NetworkViewTools;
import cyfluxviz.util.AttributeUtils;
import cyfluxviz.util.CytoscapeWrapper;
import cyfluxviz.util.ExportAsGraphics;
import cytoscape.Cytoscape;
import cytoscape.util.OpenBrowser;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.ui.editors.continuous.C2CMappingEditor;
import java.awt.Color;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class CyFluxVizPanel extends javax.swing.JPanel implements
		PropertyChangeListener, ListSelectionListener {

	private static CyFluxVizPanel uniqueInstance;

	private DefaultTableModel tableModel;
	private final String[] columnNames = { "Name", "Network", "Id" };
	private FluxDisCollection fdCollection = FluxDisCollection.getInstance();

	/* USER INTERFACE COMPONENTS */
	private JPanel exportPanel;
	private JScrollPane exportScrollPane;
	private JScrollPane fluxMapScrollPane;
	private JTable fluxTable;
	private JCheckBox globalMaxBox;
	private JEditorPane helpPane;
	private JScrollPane helpScrollPane;
	private JLabel imageIconLabel;
	private JPanel importPanel;
	private JScrollPane importScrollPane;
	private JEditorPane infoPane;
	private JScrollPane infoScrollPane;
	private JTabbedPane informationPane;
	private JButton btnImportVal;
	
	private JCheckBox chckbxFullNetwork;
	private JCheckBox chckbxFluxSubnet;
	
	
	private JCheckBox jCheckBoxNullVisible;
	private JCheckBox jCheckBoxAttributeSubnet;
	private JLabel jLabel2;
	private JLabel jLabel3;
	private JLabel jLabel6;
	private JLabel jLabel7;
	private JLabel jLabel8;
	private JPanel jPanel1;
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
	private JComboBox nodeAttributeComboBox;
	private JList nodeAttributeList;
	private JTabbedPane settingPane;
	private JPanel subnetPanel;
	private JScrollPane subnetScrollPane;
	private JCheckBox checkBox;
	private JCheckBox checkBox_1;
	private JButton btnExportImages;
	private JLabel label;
	private JButton btnLoadCyfluxviz;

	private CyFluxVizPanel() {
		Cytoscape
				.getDesktop()
				.getSwingPropertyChangeSupport()
				.addPropertyChangeListener(
						CytoscapeDesktop.NETWORK_VIEW_FOCUSED, this);
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(
				Cytoscape.ATTRIBUTES_CHANGED, this);
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(
				Cytoscape.SESSION_LOADED, this);
		initComponents();
	}

	public static synchronized CyFluxVizPanel getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new CyFluxVizPanel();
			uniqueInstance.initTable();
			uniqueInstance.addCyFluxVizPanelToCytoscape();
		}
		uniqueInstance.selectCyFluxVizPanelAndSetDialogs();
		return uniqueInstance;
	}

	public void addCyFluxVizPanelToCytoscape() {
		CytoPanel cytoPanel = getCytoPanel();
		cytoPanel.add(CyFluxViz.NAME, this);
		cytoPanel.setState(CytoPanelState.DOCK);
	}

	public void selectCyFluxVizPanelAndSetDialogs() {
		CytoPanel cytoPanel = getCytoPanel();
		cytoPanel.setSelectedIndex(cytoPanel.indexOfComponent(CyFluxViz.NAME));
		PanelText.setInfo(this);
		PanelText.setHelp(this);
	}

	private CytoPanel getCytoPanel() {
		return Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST);
	}

	public void selectInfoPane() {
		getInformationPane().setSelectedComponent(infoScrollPane);
	}

	// // FLUX DISTRIBUTION TABLE ////
	public void initTable() {
		tableModel = new DefaultTableModel(columnNames, 0);
		fluxTable.setModel(tableModel);
		TableColumn column = fluxTable.getColumnModel().getColumn(0);
		column.setPreferredWidth(60);
		column = fluxTable.getColumnModel().getColumn(1);
		column.setPreferredWidth(60);
		column = fluxTable.getColumnModel().getColumn(2);
		column.setPreferredWidth(60);

		updateFluxDistributionTable();
		updateMappingView();
	}
	
	public void updateFluxDistributionTable() {
		clearFluxDistributionTable();
		
		Set<String> ids = fdCollection.getIdSet();
		if (ids.size()>0){
			for (String fdId : fdCollection.getIdSet()) {
				FluxDis fd = fdCollection.getFluxDistribution(fdId);

				Object[] row = new Object[columnNames.length];
				row[0] = fd.getName();
				row[1] = fd.getNetworkId();
				row[2] = fdId;
				tableModel.addRow(row);
			}
			selectFirstFluxDistribution();
		} else {
			CyNetworkView view = Cytoscape.getCurrentNetworkView();
			if (view != null){
				view.updateView();
			}
			uniqueInstance.selectCyFluxVizPanelAndSetDialogs();
		}
		FluxDisCyAttributes.selectTableAttributes();
	}

	public void clearFluxDistributionTable() {
		if (tableModel.getRowCount() != 0) {
			tableModel.setRowCount(0);
		}
	}

	private void selectFirstFluxDistribution() {
		if (fluxTable.getRowCount() > 0 && fluxTable.getColumnCount() > 0) {
			fluxTable.setRowSelectionInterval(0, 0);
		}
	}

	private void removeSelectedFluxDistributions() {
		Set<FluxDis> selectedFds = getAllSelectedFluxDistributions();
		if (selectedFds.size() > 0) {
			//Dialog
			Object[] options = {"Yes", "No"};
			int n = JOptionPane.showOptionDialog(Cytoscape.getDesktop(),
					"Do you want to delete the selected Flux Distributions?",
					"Delete Flux Distributions",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					options[0]);
			if (n==0){
				FluxDisCollection fdCollection = FluxDisCollection.getInstance();
				for (FluxDis fd : selectedFds) {
					fdCollection.removeFluxDistribution(fd);
					String info = String
						.format("CyFluxViz[INFO] -> Flux Distribution removed: %s | %s | %s",
								fd.getName(), fd.getNetworkId(), fd.getId());
					System.out.println(info);
				}
				updateFluxDistributionTable();
			}
		}
	}

	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting() == false) {
			FluxDis fd = getSelectedFluxDistribution();
			if (fd != null) {
				// Activate & Update information
				fdCollection.setFluxDistributionActive(fd);
				NetworkViewTools.updateFluxDistributionInformation();
			} else {
				// Deactivate
				fdCollection.deactivateFluxDistribution();
			}
			updateNetworkViewForSelectedSettings();
		}
	}

	// / NETWORK VIEW CHANGES ////
	private void updateNetworkViewForSelectedSettings() {
		FluxDis fd = getSelectedFluxDistribution();
		NetworkView.updateNetworkViewsForFluxDistribution(fd);
	}

	public FluxDis getSelectedFluxDistribution() {
		FluxDis fd = null;
		int selected = fluxTable.getSelectedRow();
		if (selected != -1) {
			String fdId = (String) tableModel.getValueAt(selected, 2);
			fd = fdCollection.getFluxDistribution(fdId);
		}
		return fd;
	}

	public Set<FluxDis> getAllSelectedFluxDistributions() {
		Set<FluxDis> selectedFD = new HashSet<FluxDis>();
		int[] selected = fluxTable.getSelectedRows();
		if (selected != null && selected.length > 0) {
			for (int k = 0; k < selected.length; ++k) {
				int index = selected[k];
				String fdId = (String) tableModel.getValueAt(index, 2);
				FluxDis fd = fdCollection.getFluxDistribution(fdId);
				if (fd != null) {
					selectedFD.add(fd);
				}
			}
		}
		return selectedFD;
	}

	// / ATTRIBUTE SUBNETS ////
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
			updateNetworkViewForSelectedSettings();
		}
	}

	// /// HYPERLINK LISTENERS //////
	private void helpPaneHyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {
		openEventURLInBrowser(evt);
	}

	private void infoPaneHyperlinkUpdate(HyperlinkEvent evt) {
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

	// /// FLUX DISTRIBUTION MAPPING //////
	private void updateMappingView() {
		double maxFlux = getMaxFluxForMapping();
		double minEdgeWidth = Double.parseDouble(minEdgeWidthField.getText());
		double maxEdgeWidth = Double.parseDouble(maxEdgeWidthField.getText());

		ApplyEdgeWidthMapping tmp = new ApplyEdgeWidthMapping(maxFlux,
				minEdgeWidth, maxEdgeWidth);
		tmp.changeMapping();

		// Update mapping view
		// ? What is this doing exactly
		imageIconLabel.setIcon(C2CMappingEditor.getIcon(161, 94,
				VisualPropertyType.EDGE_LINE_WIDTH));
	}

	private double getMaxFluxForMapping() {
		double maxFlux = 0.0;
		if (globalMaxBox.isSelected()) {
			maxFlux = fdCollection.getGlobalAbsMax() * 1.01;
		} else if (fdCollection.hasActiveFluxDistribution()) {
			maxFlux = fdCollection.getActiveFluxDistribution()
					.getFluxStatistics().getAbsMax() * 1.01;
		} else {
			maxFlux = 1.0;
		}
		return maxFlux;
	}

	private void localMaxBoxActionPerformed(java.awt.event.ActionEvent evt) {
		globalMaxBox.setSelected(!localMaxBox.isSelected());
		updateMappingView();
	}

	private void globalMaxBoxActionPerformed(java.awt.event.ActionEvent evt) {
		localMaxBox.setSelected(!globalMaxBox.isSelected());
		updateMappingView();
	}

	private void minEdgeWidthFieldActionPerformed(java.awt.event.ActionEvent evt) {
		updateMappingView();
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

		updateMappingView();
	}

	private void maxEdgeWidthSliderMouseReleased(java.awt.event.MouseEvent evt) {
		Integer maxEdgeWidth = maxEdgeWidthSlider.getValue();
		if (maxEdgeWidth <= 80)
			maxEdgeWidthSlider.setMaximum(100);
		else
			maxEdgeWidthSlider.setMaximum(maxEdgeWidth + 50);
		maxEdgeWidthField.setText(maxEdgeWidth.toString());

		updateMappingView();
	}

	private void imageIconLabelMouseClicked(java.awt.event.MouseEvent evt) {
		Object editorObject = C2CMappingEditor.showDialog(450, 450,
				"Advanced flux <-> edgeWidth mapping ",
				VisualPropertyType.EDGE_LINE_WIDTH);
		C2CMappingEditor editor = (C2CMappingEditor) editorObject;
		editor.addWindowListener(new ImageIconListener());
	}

	class ImageIconListener implements WindowListener {
		public void windowClosing(WindowEvent e) {
			imageIconLabel.setIcon(C2CMappingEditor.getIcon(161, 94,
					VisualPropertyType.EDGE_LINE_WIDTH));
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

	// //// SET TEXT IN PANES ////////////

	// TODO: Somehow depending on local files which should not be
	private void updatePaneHTMLText(javax.swing.JEditorPane pane,
			String htmlText) {
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

	// //// Cytoscape Changes ////////////

	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equalsIgnoreCase(Cytoscape.ATTRIBUTES_CHANGED)) {
			AttributeUtils.initNodeAttributeComboBox();
		}

		if (e.getPropertyName().equalsIgnoreCase(Cytoscape.SESSION_LOADED)) {
			AttributeUtils.initNodeAttributeComboBox();
			jCheckBoxAttributeSubnet.setSelected(false);
			chckbxFluxSubnet.setSelected(false);
		}

		if (e.getPropertyName().equalsIgnoreCase(
				CytoscapeDesktop.NETWORK_VIEW_FOCUSED)) {
			AttributeUtils.initNodeAttributeComboBox();
		}
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

	public javax.swing.JComboBox getNodeAttributeComboBox() {
		return nodeAttributeComboBox;
	}

	public javax.swing.JList getNodeAttributeList() {
		return nodeAttributeList;
	}

	public javax.swing.JScrollPane getHelpScrollPane() {
		return helpScrollPane;
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

		helpScrollPane = new javax.swing.JScrollPane();
		helpPane = new javax.swing.JEditorPane();

		importScrollPane = new javax.swing.JScrollPane();
		importPanel = new javax.swing.JPanel();
		btnImportVal = new javax.swing.JButton();
		btnImportVal.setBounds(12, 33, 163, 25);

		subnetScrollPane = new javax.swing.JScrollPane();
		subnetPanel = new javax.swing.JPanel();

		mappingScrollPane = new javax.swing.JScrollPane();
		mappingPanel = new javax.swing.JPanel();

		settingPane = new javax.swing.JTabbedPane();

		exportScrollPane = new javax.swing.JScrollPane();
		exportPanel = new javax.swing.JPanel();

		jSplitPane1 = new javax.swing.JSplitPane();

		jScrollPane2 = new javax.swing.JScrollPane();

		fluxTable = new javax.swing.JTable();

		nodeAttributeComboBox = new javax.swing.JComboBox();

		jScrollPane3 = new javax.swing.JScrollPane();
		nodeAttributeList = new javax.swing.JList();

		chckbxFluxSubnet = new javax.swing.JCheckBox();
		jCheckBoxAttributeSubnet = new javax.swing.JCheckBox();
		jCheckBoxNullVisible = new javax.swing.JCheckBox();

		jSeparator1 = new javax.swing.JSeparator();

		jLabel3 = new javax.swing.JLabel();
		jLabel3.setBounds(12, 12, 163, 15);

		jLabel2 = new javax.swing.JLabel();
		jLabel2.setBounds(12, 12, 162, 15);

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
		helpScrollPane.setViewportView(helpPane);

		informationPane.addTab("Help", helpScrollPane);

		jSplitPane1.setLeftComponent(informationPane);

		jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder(
				null, "Flux distributions",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION,
				new java.awt.Font("DejaVu Sans", 0, 12),
				java.awt.Color.darkGray)); // NOI18N
		jScrollPane2.setForeground(new java.awt.Color(254, 254, 254));
		jScrollPane2.setToolTipText("Overview over  loaded flux distributions");
		jScrollPane2.setPreferredSize(new java.awt.Dimension(180, 500));

		fluxTable.setModel(new javax.swing.table.DefaultTableModel(
				new Object[][] { { null, null, null } }, columnNames));
		jScrollPane2.setViewportView(fluxTable);

		jSplitPane1.setRightComponent(jScrollPane2);
		jScrollPane2.getAccessibleContext().setAccessibleDescription(
				"Overview over loaded flux distributions");

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
		chckbxFluxSubnet.setText("Flux Subnet");
		chckbxFluxSubnet
				.setToolTipText("Only display the subnetwork consisting of reactions with fluxes.");
		chckbxFluxSubnet
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						chckbxFullNetwork.setSelected(!chckbxFluxSubnet.isSelected());
						updateNetworkViewForSelectedSettings();
					}
				});
		chckbxFullNetwork = new JCheckBox();
		chckbxFullNetwork.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				chckbxFluxSubnet.setSelected(!chckbxFullNetwork.isSelected());
			}
		});
		chckbxFullNetwork.setSelected(true);
		chckbxFullNetwork
				.setToolTipText("Only display the subnetwork consisting of reactions with fluxes.");
		chckbxFullNetwork.setText("Full Network");
		chckbxFullNetwork.setBackground(Color.WHITE);


		jCheckBoxAttributeSubnet.setBackground(java.awt.Color.white);
		jCheckBoxAttributeSubnet.setText("Attribute subnet");
		jCheckBoxAttributeSubnet
				.setToolTipText("Only display the subnetwork consisting of reactions with fluxes.");
		jCheckBoxAttributeSubnet
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						updateNetworkViewForSelectedSettings();
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
						updateNetworkViewForSelectedSettings();
					}
				});

	
		javax.swing.GroupLayout subnetPanelLayout = new javax.swing.GroupLayout(
				subnetPanel);
		subnetPanelLayout
				.setHorizontalGroup(subnetPanelLayout
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								subnetPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												subnetPanelLayout
														.createParallelGroup(
																Alignment.LEADING,
																false)
														.addGroup(
																subnetPanelLayout
																		.createSequentialGroup()
																		.addGroup(
																				subnetPanelLayout
																						.createParallelGroup(
																								Alignment.LEADING)
																						.addGroup(
																								subnetPanelLayout
																										.createParallelGroup(
																												Alignment.TRAILING,
																												false)
																										.addComponent(
																												nodeAttributeComboBox,
																												Alignment.LEADING,
																												0,
																												GroupLayout.DEFAULT_SIZE,
																												Short.MAX_VALUE)
																										.addComponent(
																												jScrollPane3,
																												Alignment.LEADING,
																												GroupLayout.DEFAULT_SIZE,
																												196,
																												Short.MAX_VALUE))
																						.addGroup(
																								subnetPanelLayout
																										.createSequentialGroup()
																										.addComponent(
																												jCheckBoxAttributeSubnet,
																												GroupLayout.PREFERRED_SIZE,
																												139,
																												GroupLayout.PREFERRED_SIZE)
																										.addPreferredGap(
																												ComponentPlacement.RELATED)
																										.addComponent(
																												jCheckBoxNullVisible))
																						.addComponent(
																								jSeparator1,
																								GroupLayout.PREFERRED_SIZE,
																								258,
																								GroupLayout.PREFERRED_SIZE))
																		.addGap(22))
														.addGroup(
																Alignment.TRAILING,
																subnetPanelLayout
																		.createSequentialGroup()
																		.addComponent(
																				chckbxFullNetwork,
																				GroupLayout.DEFAULT_SIZE,
																				GroupLayout.DEFAULT_SIZE,
																				Short.MAX_VALUE)
																		.addPreferredGap(
																				ComponentPlacement.RELATED)
																		.addComponent(
																				chckbxFluxSubnet)
																		.addGap(52)))));
		subnetPanelLayout
				.setVerticalGroup(subnetPanelLayout
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								subnetPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												subnetPanelLayout
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																chckbxFullNetwork)
														.addComponent(
																chckbxFluxSubnet))
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addComponent(jSeparator1,
												GroupLayout.PREFERRED_SIZE, 10,
												GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addGroup(
												subnetPanelLayout
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																jCheckBoxAttributeSubnet)
														.addComponent(
																jCheckBoxNullVisible))
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addComponent(nodeAttributeComboBox,
												GroupLayout.PREFERRED_SIZE,
												GroupLayout.DEFAULT_SIZE,
												GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addComponent(jScrollPane3,
												GroupLayout.DEFAULT_SIZE, 134,
												Short.MAX_VALUE)
										.addContainerGap()));
		subnetPanel.setLayout(subnetPanelLayout);
		subnetScrollPane.setViewportView(subnetPanel);
		settingPane.addTab("Subnet", subnetScrollPane);

		jLabel3.setText("Load Flux Distributions");
		importScrollPane.setBorder(null);
		importPanel.setBackground(java.awt.Color.white);
		btnImportVal.setText("Load val");
		btnImportVal.setToolTipText("Load Flux Distributions in val format.");
		btnImportVal.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				ValFluxDistributionImporter.loadValFiles();
			}
		});
		fluxMapScrollPane = new javax.swing.JScrollPane();
		jPanel1 = new javax.swing.JPanel();
		jLabel6 = new javax.swing.JLabel();
		minEdgeWidthField = new javax.swing.JTextField();
		jLabel7 = new javax.swing.JLabel();
		jLabel8 = new javax.swing.JLabel();
		maxEdgeWidthSlider = new javax.swing.JSlider();
		localMaxBox = new javax.swing.JCheckBox();
		globalMaxBox = new javax.swing.JCheckBox();
		maxEdgeWidthField = new javax.swing.JTextField();
		imageIconLabel = new javax.swing.JLabel("Edge Line Mapper");

		jPanel1.setBackground(java.awt.Color.white);

		jLabel6.setBackground(java.awt.Color.white);
		jLabel6.setFont(new java.awt.Font("DejaVu Sans", 1, 13));
		jLabel6.setText("Linear Mapping");

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
		jLabel7.setText("minEdgeWidth");

		jLabel8.setBackground(java.awt.Color.white);
		jLabel8.setFont(new java.awt.Font("DejaVu Sans", 0, 11));
		jLabel8.setText("maxEdgeWidth");

		maxEdgeWidthSlider.setBackground(java.awt.Color.white);
		maxEdgeWidthSlider.setFont(new java.awt.Font("DejaVu Sans", 0, 11));
		maxEdgeWidthSlider.setMajorTickSpacing(25);
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
		localMaxBox.setText("localMax");
		localMaxBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				localMaxBoxActionPerformed(evt);
			}
		});

		globalMaxBox.setBackground(java.awt.Color.white);
		globalMaxBox.setFont(new java.awt.Font("DejaVu Sans", 0, 11));
		globalMaxBox.setText("globalMax");
		globalMaxBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				globalMaxBoxActionPerformed(evt);
			}
		});

		maxEdgeWidthField.setFont(new java.awt.Font("DejaVu Sans", 0, 11));
		maxEdgeWidthField.setText("32.0");
		maxEdgeWidthField
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						maxEdgeWidthFieldActionPerformed(evt);
					}
				});

		imageIconLabel.setIcon(C2CMappingEditor.getIcon(123, 86,
				VisualPropertyType.EDGE_LINE_WIDTH));
		imageIconLabel.setToolTipText("Advanced Mapping");
		imageIconLabel.setPreferredSize(new java.awt.Dimension(100, 80));
		imageIconLabel.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				imageIconLabelMouseClicked(evt);
			}
		});

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(
				jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout
				.setHorizontalGroup(jPanel1Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel1Layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addGap(12,
																				12,
																				12)
																		.addComponent(
																				imageIconLabel,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				161,
																				javax.swing.GroupLayout.PREFERRED_SIZE))
														.addComponent(jLabel7)
														.addComponent(jLabel8)
														.addGroup(
																jPanel1Layout
																		.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.TRAILING,
																				false)
																		.addComponent(
																				maxEdgeWidthSlider,
																				javax.swing.GroupLayout.Alignment.LEADING,
																				0,
																				0,
																				Short.MAX_VALUE)
																		.addComponent(
																				jLabel6,
																				javax.swing.GroupLayout.Alignment.LEADING)
																		.addGroup(
																				javax.swing.GroupLayout.Alignment.LEADING,
																				jPanel1Layout
																						.createSequentialGroup()
																						.addComponent(
																								localMaxBox)
																						.addGap(33,
																								33,
																								33)
																						.addGroup(
																								jPanel1Layout
																										.createParallelGroup(
																												javax.swing.GroupLayout.Alignment.LEADING)
																										.addComponent(
																												globalMaxBox)
																										.addGroup(
																												jPanel1Layout
																														.createParallelGroup(
																																javax.swing.GroupLayout.Alignment.TRAILING)
																														.addComponent(
																																maxEdgeWidthField,
																																javax.swing.GroupLayout.PREFERRED_SIZE,
																																45,
																																javax.swing.GroupLayout.PREFERRED_SIZE)
																														.addComponent(
																																minEdgeWidthField,
																																javax.swing.GroupLayout.PREFERRED_SIZE,
																																46,
																																javax.swing.GroupLayout.PREFERRED_SIZE))))))
										.addContainerGap()));
		jPanel1Layout
				.setVerticalGroup(jPanel1Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel1Layout
										.createSequentialGroup()
										.addComponent(jLabel6)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																localMaxBox)
														.addComponent(
																globalMaxBox))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(jLabel7)
														.addComponent(
																minEdgeWidthField,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(jLabel8)
														.addComponent(
																maxEdgeWidthField,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGap(2, 2, 2)
										.addComponent(
												maxEdgeWidthSlider,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												imageIconLabel,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												94,
												javax.swing.GroupLayout.PREFERRED_SIZE)));

		fluxMapScrollPane.setViewportView(jPanel1);

		settingPane.addTab("FluxMap", fluxMapScrollPane);

		label = new JLabel();
		label.setBounds(12, 70, 163, 15);
		label.setText("Load Flux Distributions");

		btnLoadCyfluxviz = new JButton();
		btnLoadCyfluxviz.setBounds(12, 91, 163, 25);
		btnLoadCyfluxviz.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnLoadCyfluxviz
				.setToolTipText("Load Flux Distributions in CyFluxVizFormat.");
		btnLoadCyfluxviz.setText("Load CyFluxViz");

		importScrollPane.setViewportView(importPanel);
		importPanel.setLayout(null);
		importPanel.add(btnLoadCyfluxviz);
		importPanel.add(label);
		importPanel.add(btnImportVal);
		importPanel.add(jLabel3);

		settingPane.addTab("Import", importScrollPane);
		importScrollPane.getAccessibleContext().setAccessibleName("Import");
		importScrollPane.getAccessibleContext().setAccessibleDescription(
				"Import Flux Distributions");

		exportScrollPane.setBorder(null);

		exportPanel.setBackground(java.awt.Color.white);
		exportPanel.setRequestFocusEnabled(false);

		jLabel2.setText("Save Flux Distributions");

		JButton btnExportFd = new JButton();
		btnExportFd.setBounds(12, 53, 134, 25);
		btnExportFd
				.setToolTipText("Export Images of flux distributions as SVG");
		btnExportFd.setText("Export FD");

		JLabel lblSaveImages = new JLabel();
		lblSaveImages.setBounds(12, 102, 162, 15);
		lblSaveImages.setText("Save Images");

		JSeparator separator = new JSeparator();
		separator.setBounds(12, 90, 188, 10);

		JCheckBox chckbxSaveAllFD = new JCheckBox("All");
		chckbxSaveAllFD.setBackground(Color.WHITE);
		chckbxSaveAllFD.setForeground(Color.BLACK);
		chckbxSaveAllFD.setBounds(12, 29, 42, 23);
		chckbxSaveAllFD.setSelected(true);

		JCheckBox chckbxSaveSelectedFD = new JCheckBox("Selected");
		chckbxSaveSelectedFD.setBackground(Color.WHITE);
		chckbxSaveSelectedFD.setBounds(58, 29, 116, 23);

		checkBox = new JCheckBox("Selected");
		checkBox.setBackground(Color.WHITE);
		checkBox.setBounds(58, 119, 116, 23);

		checkBox_1 = new JCheckBox("All");
		checkBox_1.setBackground(Color.WHITE);
		checkBox_1.setBounds(12, 119, 42, 23);
		checkBox_1.setSelected(true);

		btnExportImages = new JButton();
		btnExportImages.setBounds(12, 150, 134, 25);
		btnExportImages
				.setToolTipText("Export Images of flux distributions as SVG");
		btnExportImages.setText("Export Images");

		exportScrollPane.setViewportView(exportPanel);
		exportPanel.setLayout(null);
		exportPanel.add(checkBox_1);
		exportPanel.add(checkBox);
		exportPanel.add(separator);
		exportPanel.add(btnExportFd);
		exportPanel.add(chckbxSaveAllFD);
		exportPanel.add(chckbxSaveSelectedFD);
		exportPanel.add(jLabel2);
		exportPanel.add(lblSaveImages);
		exportPanel.add(btnExportImages);

		settingPane.addTab("Export", exportScrollPane);
		exportScrollPane.getAccessibleContext().setAccessibleName("Export");

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

		JScrollPane fluxdisScrollPane = new JScrollPane();
		settingPane.addTab("FluxDis", null, fluxdisScrollPane, "Manage Flux Distributions");
		settingPane.setEnabledAt(4, true);

		JPanel fluxDisPanel = new JPanel();
		fluxDisPanel.setBackground(Color.WHITE);
		fluxdisScrollPane.setViewportView(fluxDisPanel);

		JLabel lblRemoveSelectedFlux = new JLabel(
				"Delete Selected Flux Distributions");
		lblRemoveSelectedFlux.setBounds(12, 12, 241, 15);

		JButton btnRemoveSelectedFD = new JButton("Delete");
		btnRemoveSelectedFD.setBounds(12, 33, 81, 25);
		btnRemoveSelectedFD.setToolTipText("Deletes the selected Flux Distributions.");
		btnRemoveSelectedFD.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				removeSelectedFluxDistributions();
			}
		});
		fluxDisPanel.setLayout(null);
		fluxDisPanel.add(lblRemoveSelectedFlux);
		fluxDisPanel.add(btnRemoveSelectedFD);

		settingPane.getAccessibleContext().setAccessibleName("Settings");

		fluxTable.getSelectionModel().addListSelectionListener(this);
	}
}