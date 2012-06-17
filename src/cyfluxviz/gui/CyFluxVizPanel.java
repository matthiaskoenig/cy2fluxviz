/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FluxVizPanel.java
 *
 * Created on Jan 22, 2010, 3:15:26 PM
 */

package cyfluxviz.gui;

import java.net.URL;

import javax.swing.event.ChangeEvent;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import cyfluxviz.FluxDis;
import cyfluxviz.FluxDisCollection;
import cyfluxviz.io.FluxDistributionImporter;
import cyfluxviz.netview.NetworkView;
import cyfluxviz.netview.NetworkViewTools;
import cyfluxviz.util.AttributeUtils;
import cyfluxviz.util.CytoscapeWrapper;
import cyfluxviz.util.ExportAsGraphics;
import cyfluxviz.vizmap.ApplyEdgeWidthMapping;
import cytoscape.util.OpenBrowser;

import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.ui.editors.continuous.C2CMappingEditor;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;


@SuppressWarnings("serial")
public class CyFluxVizPanel extends javax.swing.JPanel implements ListSelectionListener {
	
    private DefaultTableModel tableModel;
	private final String[] columnNames = {"Name", "Network", "Id"};
    
    public CyFluxVizPanel() {
        initComponents();
		initTable();
    }

    private void initComponents() {
        testScrollPane = new javax.swing.JScrollPane();
        testPanel = new javax.swing.JPanel();
        hideButton = new javax.swing.JButton();
        showButton = new javax.swing.JButton();
        histogrammScrollPane = new javax.swing.JScrollPane();
        histogrammPane = new javax.swing.JPanel();
        mappingScrollPane = new javax.swing.JScrollPane();
        mappingPanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        jCheckBox4 = new javax.swing.JCheckBox();
        jCheckBox5 = new javax.swing.JCheckBox();
        jCheckBox6 = new javax.swing.JCheckBox();
        jCheckBox7 = new javax.swing.JCheckBox();
        jCheckBox8 = new javax.swing.JCheckBox();
        jCheckBox9 = new javax.swing.JCheckBox();
        jSplitPane1 = new javax.swing.JSplitPane();
        informationPane = new javax.swing.JTabbedPane();
        infoScrollPane = new javax.swing.JScrollPane();
        infoPane = new javax.swing.JEditorPane();
        helpScrollPane = new javax.swing.JScrollPane();
        helpPane = new javax.swing.JEditorPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        fluxTable = new javax.swing.JTable();
        settingPane = new javax.swing.JTabbedPane();
        subnetScrollPane = new javax.swing.JScrollPane();
        subnetPanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        nodeAttributeList = new javax.swing.JList();
        jCheckBoxFluxSubnet = new javax.swing.JCheckBox();
        jCheckBoxAttributeSubnet = new javax.swing.JCheckBox();
        nodeAttributeComboBox = new javax.swing.JComboBox();
        jSeparator1 = new javax.swing.JSeparator();
        jCheckBoxNullVisible = new javax.swing.JCheckBox();
        importScrollPane = new javax.swing.JScrollPane();
        importPanel = new javax.swing.JPanel();
        jButtonImportSim = new javax.swing.JButton();
        jButtonImportVal = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        exportScrollPane = new javax.swing.JScrollPane();
        exportPanel = new javax.swing.JPanel();
        jButtonExportImage = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
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
        imageIconLabel = new javax.swing.JLabel();

        hideButton.setText("Hide");
        hideButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hideButtonActionPerformed(evt);
            }
        });

        showButton.setText("Show");
        showButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout testPanelLayout = new javax.swing.GroupLayout(testPanel);
        testPanel.setLayout(testPanelLayout);
        testPanelLayout.setHorizontalGroup(
            testPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(testPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(testPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(showButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(hideButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE))
                .addContainerGap(122, Short.MAX_VALUE))
        );
        testPanelLayout.setVerticalGroup(
            testPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(testPanelLayout.createSequentialGroup()
                .addComponent(hideButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showButton)
                .addContainerGap(120, Short.MAX_VALUE))
        );

        testScrollPane.setViewportView(testPanel);

        javax.swing.GroupLayout histogrammPaneLayout = new javax.swing.GroupLayout(histogrammPane);
        histogrammPane.setLayout(histogrammPaneLayout);
        histogrammPaneLayout.setHorizontalGroup(
            histogrammPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 168, Short.MAX_VALUE)
        );
        histogrammPaneLayout.setVerticalGroup(
            histogrammPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 80, Short.MAX_VALUE)
        );

        histogrammScrollPane.setViewportView(histogrammPane);

        mappingPanel.setBackground(java.awt.Color.white);

        javax.swing.GroupLayout mappingPanelLayout = new javax.swing.GroupLayout(mappingPanel);
        mappingPanel.setLayout(mappingPanelLayout);
        mappingPanelLayout.setHorizontalGroup(
            mappingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mappingPanelLayout.createSequentialGroup()
                .addGroup(mappingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mappingPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(mappingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addGroup(mappingPanelLayout.createSequentialGroup()
                                .addComponent(jCheckBox1)
                                .addGap(18, 18, 18)
                                .addComponent(jCheckBox2))))
                    .addGroup(mappingPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jCheckBox6))
                    .addGroup(mappingPanelLayout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(mappingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox3)
                            .addComponent(jCheckBox5)
                            .addComponent(jCheckBox4)
                            .addComponent(jCheckBox9)
                            .addComponent(jCheckBox7)
                            .addComponent(jCheckBox8))))
                .addContainerGap(141, Short.MAX_VALUE))
        );
        mappingPanelLayout.setVerticalGroup(
            mappingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mappingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mappingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox1)
                    .addComponent(jCheckBox2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox6)
                .addGap(18, 18, 18)
                .addComponent(jCheckBox3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox8)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mappingScrollPane.setViewportView(mappingPanel);

        setMaximumSize(new java.awt.Dimension(32000, 32000));
        setMinimumSize(new java.awt.Dimension(100, 300));
        setPreferredSize(new java.awt.Dimension(180, 700));

        jSplitPane1.setDividerLocation(300);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setMinimumSize(new java.awt.Dimension(40, 40));
        jSplitPane1.setPreferredSize(new java.awt.Dimension(180, 600));

        infoPane.setContentType("text/html");
        infoPane.setEditable(false);
        infoPane.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
            public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {
                infoPaneHyperlinkUpdate(evt);
            }
        });
        infoScrollPane.setViewportView(infoPane);

        informationPane.addTab("Info", infoScrollPane);

        helpPane.setContentType("text/html");
        helpPane.setEditable(false);
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
        informationPane.getAccessibleContext().setAccessibleName("histogramm");

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Flux distributions", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("DejaVu Sans", 0, 12), java.awt.Color.darkGray)); // NOI18N
        jScrollPane2.setForeground(new java.awt.Color(254, 254, 254));
        jScrollPane2.setToolTipText("Overview over  loaded flux distributions");
        jScrollPane2.setPreferredSize(new java.awt.Dimension(180, 500));

        fluxTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(fluxTable);

        jSplitPane1.setRightComponent(jScrollPane2);
        jScrollPane2.getAccessibleContext().setAccessibleDescription("Overview over loaded flux distributions");

        subnetPanel.setBackground(java.awt.Color.white);
        subnetPanel.setPreferredSize(new java.awt.Dimension(200, 161));

        jScrollPane3.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        nodeAttributeList.setToolTipText("Select displayed attribute values");
        nodeAttributeList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                nodeAttributeListValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(nodeAttributeList);

        jCheckBoxFluxSubnet.setBackground(java.awt.Color.white);
        jCheckBoxFluxSubnet.setText("Flux subnet");
        jCheckBoxFluxSubnet.setToolTipText("Only display the subnetwork consisting of reactions with fluxes.");
        jCheckBoxFluxSubnet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxFluxSubnetActionPerformed(evt);
            }
        });

        jCheckBoxAttributeSubnet.setBackground(java.awt.Color.white);
        jCheckBoxAttributeSubnet.setText("Attribute subnet");
        jCheckBoxAttributeSubnet.setToolTipText("Only display the subnetwork consisting of reactions with fluxes.");
        jCheckBoxAttributeSubnet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxAttributeSubnetActionPerformed(evt);
            }
        });

        nodeAttributeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nodeAttributeComboBoxActionPerformed(evt);
            }
        });

        jCheckBoxNullVisible.setBackground(java.awt.Color.white);
        jCheckBoxNullVisible.setFont(new java.awt.Font("DejaVu Sans", 0, 10));
        jCheckBoxNullVisible.setSelected(true);
        jCheckBoxNullVisible.setText("NullVisible");
        jCheckBoxNullVisible.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxNullVisibleActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout subnetPanelLayout = new javax.swing.GroupLayout(subnetPanel);
        subnetPanel.setLayout(subnetPanelLayout);
        subnetPanelLayout.setHorizontalGroup(
            subnetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(subnetPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(subnetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(subnetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(nodeAttributeComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE))
                    .addComponent(jCheckBoxFluxSubnet)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(subnetPanelLayout.createSequentialGroup()
                        .addComponent(jCheckBoxAttributeSubnet, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBoxNullVisible))))
        );
        subnetPanelLayout.setVerticalGroup(
            subnetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(subnetPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBoxFluxSubnet)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(subnetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBoxAttributeSubnet)
                    .addComponent(jCheckBoxNullVisible))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nodeAttributeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE)
                .addContainerGap())
        );

        subnetScrollPane.setViewportView(subnetPanel);

        settingPane.addTab("Subnet", subnetScrollPane);

        importScrollPane.setBorder(null);

        importPanel.setBackground(java.awt.Color.white);

        jButtonImportVal.setText("Load val");
        jButtonImportVal.setToolTipText("Load additional val files for the current network.");
        jButtonImportVal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonImportValActionPerformed(evt);
            }
        });

        jLabel3.setText("Load Flux Distributions");

        jLabel4.setText("Load Simulation Information");

        javax.swing.GroupLayout importPanelLayout = new javax.swing.GroupLayout(importPanel);
        importPanel.setLayout(importPanelLayout);
        importPanelLayout.setHorizontalGroup(
            importPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(importPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(importPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(importPanelLayout.createSequentialGroup()
                        .addComponent(jButtonImportSim, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                        .addGap(171, 171, 171))
                    .addGroup(importPanelLayout.createSequentialGroup()
                        .addComponent(jButtonImportVal, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                        .addGap(171, 171, 171))
                    .addGroup(importPanelLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addContainerGap(119, Short.MAX_VALUE))
                    .addGroup(importPanelLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addContainerGap(83, Short.MAX_VALUE))))
        );
        importPanelLayout.setVerticalGroup(
            importPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(importPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonImportVal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addGap(4, 4, 4)
                .addComponent(jButtonImportSim)
                .addContainerGap())
        );

        importScrollPane.setViewportView(importPanel);

        settingPane.addTab("Import", importScrollPane);
        importScrollPane.getAccessibleContext().setAccessibleName("Import");
        importScrollPane.getAccessibleContext().setAccessibleDescription("Import Flux Distributions");

        exportScrollPane.setBorder(null);

        exportPanel.setBackground(java.awt.Color.white);
        exportPanel.setRequestFocusEnabled(false);

        jButtonExportImage.setText("Export Images");
        jButtonExportImage.setToolTipText("Export Images of flux distributions as SVG");
        jButtonExportImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportImageActionPerformed(evt);
            }
        });

        jLabel2.setText("Export NetworkViews");

        javax.swing.GroupLayout exportPanelLayout = new javax.swing.GroupLayout(exportPanel);
        exportPanel.setLayout(exportPanelLayout);
        exportPanelLayout.setHorizontalGroup(
            exportPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(exportPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(exportPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jButtonExportImage))
                .addContainerGap(63, Short.MAX_VALUE))
        );
        exportPanelLayout.setVerticalGroup(
            exportPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(exportPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonExportImage)
                .addContainerGap(155, Short.MAX_VALUE))
        );

        exportScrollPane.setViewportView(exportPanel);

        settingPane.addTab("Export", exportScrollPane);
        exportScrollPane.getAccessibleContext().setAccessibleName("Export");

        jPanel1.setBackground(java.awt.Color.white);

        jLabel6.setBackground(java.awt.Color.white);
        jLabel6.setFont(new java.awt.Font("DejaVu Sans", 1, 13));
        jLabel6.setText("Linear Mapping");

        minEdgeWidthField.setFont(new java.awt.Font("DejaVu Sans", 0, 11));
        minEdgeWidthField.setText("1.0");
        minEdgeWidthField.addActionListener(new java.awt.event.ActionListener() {
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
        maxEdgeWidthSlider.addChangeListener(new javax.swing.event.ChangeListener() {
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
        maxEdgeWidthField.setText("50.0");
        maxEdgeWidthField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maxEdgeWidthFieldActionPerformed(evt);
            }
        });

        imageIconLabel.setIcon(C2CMappingEditor.getIcon(123, 86, VisualPropertyType.EDGE_LINE_WIDTH));
        imageIconLabel.setToolTipText("Advanced Mapping");
        imageIconLabel.setPreferredSize(new java.awt.Dimension(100, 80));
        imageIconLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                imageIconLabelMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(imageIconLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(maxEdgeWidthSlider, javax.swing.GroupLayout.Alignment.LEADING, 0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addComponent(localMaxBox)
                            .addGap(33, 33, 33)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(globalMaxBox)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(maxEdgeWidthField, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(minEdgeWidthField, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(localMaxBox)
                    .addComponent(globalMaxBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(minEdgeWidthField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(maxEdgeWidthField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addComponent(maxEdgeWidthSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(imageIconLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        fluxMapScrollPane.setViewportView(jPanel1);

        settingPane.addTab("FluxMap", fluxMapScrollPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(settingPane, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(settingPane, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        settingPane.getAccessibleContext().setAccessibleName("Settings");
        
        fluxTable.getSelectionModel().addListSelectionListener(this);
    }

    /////////////////////////////////////////////////////////////////////////////////
    
    private void jButtonExportImageActionPerformed(java.awt.event.ActionEvent evt) {                                                   
        ExportAsGraphics.exportImage();
    }                                                  

    private void jButtonImportValActionPerformed(java.awt.event.ActionEvent evt) {                                                 
        FluxDistributionImporter.loadValFiles();
    }                                                                                            

    private void jCheckBoxFluxSubnetActionPerformed(java.awt.event.ActionEvent evt) {                                                    
        if (getFluxTable().getModel().getRowCount() > 0){
            NetworkView.changeSubNetworkView();
        }
    }         

    private void helpPaneHyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {                                         
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

    private void infoPaneHyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {                                         
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

    private void jCheckBoxAttributeSubnetActionPerformed(java.awt.event.ActionEvent evt) {                                                         
        NetworkView.changeSubNetworkView();
    }                                                        

    private void nodeAttributeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {                                                      
        // if new value is selected the content of the ValueSet list has to be
        // changed accordingly
        int index = nodeAttributeComboBox.getSelectedIndex();
        if (index!= -1){
            String attribute = (String) nodeAttributeComboBox.getSelectedItem();
            AttributeUtils.initNodeAttributeList(attribute);
        }
    }                                                     

    private void hideButtonActionPerformed(java.awt.event.ActionEvent evt) {                                           
        NetworkViewTools.hideAllNodesAndEdgesInCurrentView();
    }                                          

    private void showButtonActionPerformed(java.awt.event.ActionEvent evt) {                                           
        NetworkViewTools.showAllNodesAndEdgesInCurrentView();
    }                                          

    private void nodeAttributeListValueChanged(javax.swing.event.ListSelectionEvent evt) {                                               
        //Listening to selection events
        if (evt.getValueIsAdjusting() == false) {
            // Only change view if the attribute subnetwork feature is activated.
            if (jCheckBoxAttributeSubnet.isSelected() == false){
                return;
            }
            else{
                NetworkView.changeSubNetworkView();
            }
        }
    }                                              

    private void jCheckBoxNullVisibleActionPerformed(java.awt.event.ActionEvent evt) {                                                     
        NetworkView.changeSubNetworkView();
    }                                                    

    public void selectInfoPane(){
    	getInformationPane().setSelectedComponent(infoScrollPane);
    }
    
    private void updateMappingView(){
    	double maxFlux = getMaxFluxForMapping();
        double minEdgeWidth = Double.parseDouble(minEdgeWidthField.getText());
        double maxEdgeWidth = Double.parseDouble(maxEdgeWidthField.getText());

        ApplyEdgeWidthMapping tmp = new ApplyEdgeWidthMapping(maxFlux, minEdgeWidth, maxEdgeWidth);
        tmp.changeMapping();

        // Update mapping view
        // ? What is this doing exactly
        imageIconLabel.setIcon(C2CMappingEditor.getIcon(161, 94, VisualPropertyType.EDGE_LINE_WIDTH));
    }
    
    private double getMaxFluxForMapping(){
    	FluxDisCollection fdCollection = FluxDisCollection.getInstance();   
    	double maxFlux = 0.0;
        if (globalMaxBox.isSelected()){
            maxFlux = fdCollection.getGlobalAbsMax() * 1.01;
        }
        else if (fdCollection.hasActiveFluxDistribution()){
            maxFlux = fdCollection.getActiveFluxDistribution().getFluxStatistics().getAbsMax() * 1.01;
        } else {
        	System.out.println("No global or local ABS MAX setting applied !");
        	maxFlux = 1.0;
        }
        return maxFlux;
    }

    private void localMaxBoxActionPerformed(java.awt.event.ActionEvent evt) {                                            
        // change state of localMaxBox
        if (localMaxBox.isSelected() == true) {
        	globalMaxBox.setSelected(false);
        }
        else {
        	globalMaxBox.setSelected(true);
        }
        updateMappingView();
    }                                           

    private void globalMaxBoxActionPerformed(java.awt.event.ActionEvent evt) {                                             
    	// change state of globalMaxBox
        if (globalMaxBox.isSelected() == true){
        	localMaxBox.setSelected(false);
        }
        else {
        	localMaxBox.setSelected(true);
        }
        updateMappingView();
    }                                            

    private void minEdgeWidthFieldActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        updateMappingView();
    }                                                 

    private void maxEdgeWidthFieldActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        //Update the slider setting
        int maxEdgeWidth;
        try {
            maxEdgeWidth = (int) Double.parseDouble(maxEdgeWidthField.getText());
        }
        catch(Exception e){
            maxEdgeWidth = 50;
        }
        maxEdgeWidthSlider.setValue(maxEdgeWidth);
        if (maxEdgeWidth <= 80) maxEdgeWidthSlider.setMaximum(100);
        else maxEdgeWidthSlider.setMaximum(maxEdgeWidth + 50);
        
        updateMappingView();
    }                                                                                            

    private void maxEdgeWidthSliderMouseReleased(java.awt.event.MouseEvent evt) {                                                 
        Integer maxEdgeWidth = maxEdgeWidthSlider.getValue();
        if (maxEdgeWidth <= 80) maxEdgeWidthSlider.setMaximum(100);
        else maxEdgeWidthSlider.setMaximum(maxEdgeWidth + 50);

        maxEdgeWidthField.setText(maxEdgeWidth.toString());

        updateMappingView();
    }                                                

    private void imageIconLabelMouseClicked(java.awt.event.MouseEvent evt) {
        Object editorObject = C2CMappingEditor.showDialog(450, 450, "Advanced flux <-> edgeWidth mapping ", VisualPropertyType.EDGE_LINE_WIDTH);
        C2CMappingEditor editor = (C2CMappingEditor) editorObject;
        editor.addWindowListener(new ImageIconListener());
    }                                           

    class ImageIconListener implements WindowListener{
        public void windowClosing(WindowEvent e) {
            imageIconLabel.setIcon(C2CMappingEditor.getIcon(161, 94, VisualPropertyType.EDGE_LINE_WIDTH));
        }
        public void windowOpened(WindowEvent arg0) {}
        public void windowClosed(WindowEvent arg0) {}
        public void windowIconified(WindowEvent arg0) {}
        public void windowDeiconified(WindowEvent arg0) {}
        public void windowActivated(WindowEvent arg0) {}
        public void windowDeactivated(WindowEvent arg0) {}
    }

    
    
    // Table Changes //
    public void initTable(){
        tableModel = new DefaultTableModel(columnNames, 0);
        fluxTable.setModel(tableModel);

        TableColumn column = fluxTable.getColumnModel().getColumn(0);
		column.setPreferredWidth(220);
		column = fluxTable.getColumnModel().getColumn(1);
		column.setPreferredWidth(40);
		column = fluxTable.getColumnModel().getColumn(2);
		column.setPreferredWidth(40);
		
		updateTable();
	}

    public void clearTable(){
    	if (tableModel.getRowCount() != 0){
    		tableModel.setRowCount(0);
    	}
    }

    /* Updates list of flux distributions */
    public void updateTable(){
        clearTable();
        
        FluxDisCollection fdCollection = FluxDisCollection.getInstance();
        for (String fdId : fdCollection.getIdSet()){
        	FluxDis fd = fdCollection.getFluxDistribution(fdId);
        	
        	Object[] row = new Object[columnNames.length];
            row[0] = fd.getName();
            row[1] = fd.getNetworkId();
            row[2] = fdId;
            tableModel.addRow(row);
        }
        selectFirstFluxDistribution();
    }
    
    private void selectFirstFluxDistribution(){
    	if (fluxTable.getRowCount()>0 && fluxTable.getColumnCount()>0){
            fluxTable.setRowSelectionInterval(0, 0);
        }
    }

    //TODO: Somehow depending on local files which should not be
	private void updatePaneHTMLText(javax.swing.JEditorPane pane,  String htmlText){
        String info = String.format(
        		"<html>" +
        		"<head>" +
        		"	<base href='file:///home/mkoenig/workspace/fluxviz/'></base>" +
				"	<style type='text/css'>body{ font-family: sans-serif; font-size: 11pt; }</style>" +
				"</head>" +
				"<body>%s</body></html>", htmlText);
		pane.setText(info);
	}
	
	public void updateInfoPaneHTMLText(String htmlText){
		updatePaneHTMLText(infoPane, htmlText);
	}
	public void updateHelpPaneHTMLText(String htmlText){
		updatePaneHTMLText(helpPane, htmlText);
	}
	
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {
        	FluxDisCollection fdCollection = FluxDisCollection.getInstance();
        	
        	int selected = fluxTable.getSelectedRow();
            if (selected != -1){	
            	// Activate FluxDistribution
                String fdId = (String)tableModel.getValueAt(selected, 2);
                fdCollection.setFluxDistributionActive(fdId);
                // Update information
                NetworkViewTools.updateFluxDistributionInformation();
                
            } else {
            	// Deactivate FluxDistribution
            	fdCollection.deactivateFluxDistribution();
            }
        }
    }
    
    public javax.swing.JTable getFluxTable(){
        return this.fluxTable;
    }
    public javax.swing.table.DefaultTableModel getTableModel(){
        return this.tableModel;
    }
    public javax.swing.JCheckBox getFluxSubnetCheckbox(){
        return this.jCheckBoxFluxSubnet;
    }
    public javax.swing.JCheckBox getAttributeSubnetCheckbox(){
        return this.jCheckBoxAttributeSubnet;
    }
    public javax.swing.JCheckBox getNullVisibleCheckbox(){
        return this.jCheckBoxNullVisible;
    }
    public javax.swing.JTabbedPane getInformationPane(){
        return informationPane;
    }

    public javax.swing.JPanel getHistogramPane() {
		return histogrammPane;
	}
    public void setHistogramPanel(javax.swing.JPanel histogramPane) {
		this.histogrammPane = histogramPane;
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

    // Variables declaration - do not modify
    private javax.swing.JPanel exportPanel;
    private javax.swing.JScrollPane exportScrollPane;
    private javax.swing.JScrollPane fluxMapScrollPane;
    private javax.swing.JTable fluxTable;
    private javax.swing.JCheckBox globalMaxBox;
    private javax.swing.JEditorPane helpPane;
    private javax.swing.JScrollPane helpScrollPane;
    private javax.swing.JButton hideButton;
    private javax.swing.JPanel histogrammPane;
    private javax.swing.JScrollPane histogrammScrollPane;
    private javax.swing.JLabel imageIconLabel;
    private javax.swing.JPanel importPanel;
    private javax.swing.JScrollPane importScrollPane;
    private javax.swing.JEditorPane infoPane;
    private javax.swing.JScrollPane infoScrollPane;
    private javax.swing.JTabbedPane informationPane;
    private javax.swing.JButton jButtonExportImage;
    private javax.swing.JButton jButtonImportSim;
    private javax.swing.JButton jButtonImportVal;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JCheckBox jCheckBox5;
    private javax.swing.JCheckBox jCheckBox6;
    private javax.swing.JCheckBox jCheckBox7;
    private javax.swing.JCheckBox jCheckBox8;
    private javax.swing.JCheckBox jCheckBox9;
    private javax.swing.JCheckBox jCheckBoxAttributeSubnet;
    private javax.swing.JCheckBox jCheckBoxFluxSubnet;
    private javax.swing.JCheckBox jCheckBoxNullVisible;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JCheckBox localMaxBox;
    private javax.swing.JPanel mappingPanel;
    private javax.swing.JScrollPane mappingScrollPane;
    private javax.swing.JTextField maxEdgeWidthField;
    private javax.swing.JSlider maxEdgeWidthSlider;
    private javax.swing.JTextField minEdgeWidthField;
    private javax.swing.JComboBox nodeAttributeComboBox;
    private javax.swing.JList nodeAttributeList;
    private javax.swing.JTabbedPane settingPane;
    private javax.swing.JButton showButton;
    private javax.swing.JPanel subnetPanel;
    private javax.swing.JScrollPane subnetScrollPane;
    private javax.swing.JPanel testPanel;
    private javax.swing.JScrollPane testScrollPane;
    // End of variables declaration
}
