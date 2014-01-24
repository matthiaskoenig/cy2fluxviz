package cyfluxviz.nodesplit.gui;

import javax.swing.JPanel;
import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;

import javax.swing.JTextField;

import cyfluxviz.nodesplit.core.CySplitter;
import cyfluxviz.nodesplit.core.SplitMapping;
import cyfluxviz.nodesplit.core.SplitMappingCollection;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Set;
import javax.swing.JSeparator;
import java.awt.Color;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;



/**
 * GUI dialog for the Node splitting functionality. 
 * @author mkoenig
 */
@SuppressWarnings("serial")
public class CyNodeSplitPanel extends JDialog {
	private static CyNodeSplitPanel uniqueInstance;
	private SplitMappingCollection sMapCollection;
	
	private JButton btnSplitAction;
	private JButton btnUnsplitAction;
	private JTextField textFieldSplitStatus;
	private JTextField textFieldUnsplitCount;
	private JTextField textFieldSplitCount;
	private JTextField textFieldNetworkId;
	private JTextField textFieldSelectedNodes;
	private JSeparator separator_1;
	private JSeparator separator_2;

	/** Create the panel. */	
	private CyNodeSplitPanel(final JFrame parentFrame) {
		
		super(parentFrame, true);
		this.setSize(400, 400);
		this.setResizable(false);
		this.setTitle("CyNodeSplit Dialog");
		this.setLocationRelativeTo(parentFrame);
		
		sMapCollection = SplitMappingCollection.getInstance();
		initComponents();
		System.out.println("CyNodeSplit[INFO]: GUI components initialized.");
	}

	public static synchronized CyNodeSplitPanel getInstance(JFrame parent) {
		if (uniqueInstance == null) {		
			uniqueInstance = new CyNodeSplitPanel(parent);
			uniqueInstance.initComponents();
			uniqueInstance.updateComponents();
		}else{
			uniqueInstance.updateComponents();
		}
		return uniqueInstance;
	}
		
	public SplitMappingCollection getSplitMappingCollection(){
		return sMapCollection;
	}

	/** Updates the components based on the current content
	 * of the SplitMappingCollection. */
	private void updateComponents(){
		System.out.println("CyNodeSplit[INFO]: updateComponents in dialog");
		
		
		// get currentNetwork
		CyNetwork network = Cytoscape.getCurrentNetwork();
		if (network == null || network==Cytoscape.getNullNetwork()){
			System.out.println("CyNodeSplit[INFO]: no network selected");
			textFieldNetworkId.setText("-");
			textFieldSelectedNodes.setText("-");
			textFieldSplitStatus.setText("-");
			textFieldSplitCount.setText("-");
			textFieldUnsplitCount.setText("-");
			btnSplitAction.setEnabled(false);
		} else {
			// get splitMapping
			String id = network.getIdentifier();
			SplitMapping sMap = sMapCollection.getOrCreateSplitMappingForNetworkId(id);
			
			@SuppressWarnings("unchecked")
			Set<CyNode> selected = network.getSelectedNodes();
			textFieldNetworkId.setText(id);
			textFieldSelectedNodes.setText(int2String(selected.size()));
			
			textFieldSplitStatus.setText(boolean2String(sMap.getSplitStatus()));
			textFieldSplitCount.setText(int2String(sMap.getNodeTargetCount()));
			textFieldUnsplitCount.setText(int2String(sMap.getNodeSourceCount()));
			
			if (selected.size() == 0){
				btnSplitAction.setEnabled(false);
			} else {
				btnSplitAction.setEnabled(true);
			}
			
			if (sMap.getSplitStatus() == true){
				btnUnsplitAction.setEnabled(true);
			} else {
				btnUnsplitAction.setEnabled(false);
			}
		}
	}
	
	private String int2String(int i){
		return ((Integer) i).toString();
	}
	private String boolean2String(boolean b){
		return ((Boolean) b).toString();
	}
	
	
	private void initComponents(){
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		textFieldSplitStatus = new JTextField();
		textFieldSplitStatus.setEditable(false);
		textFieldSplitStatus.setText("false");
		textFieldSplitStatus.setBounds(102, 106, 42, 20);
		panel.add(textFieldSplitStatus);
		textFieldSplitStatus.setColumns(10);
		
		JLabel lblSplitStatus = new JLabel("Split Status");
		lblSplitStatus.setBounds(10, 109, 103, 14);
		panel.add(lblSplitStatus);
		
		textFieldUnsplitCount = new JTextField();
		textFieldUnsplitCount.setEditable(false);
		textFieldUnsplitCount.setText("0");
		textFieldUnsplitCount.setColumns(10);
		textFieldUnsplitCount.setBounds(10, 137, 32, 20);
		panel.add(textFieldUnsplitCount);
		
		JLabel lblSplitNodes = new JLabel("Unsplit => Split Nodes");
		lblSplitNodes.setBounds(52, 140, 134, 14);
		panel.add(lblSplitNodes);
		
		textFieldSplitCount = new JTextField();
		textFieldSplitCount.setEditable(false);
		textFieldSplitCount.setText("0");
		textFieldSplitCount.setColumns(10);
		textFieldSplitCount.setBounds(206, 137, 32, 20);
		panel.add(textFieldSplitCount);
		
		JLabel lblNetworkId = new JLabel("Network Id");
		lblNetworkId.setBounds(10, 28, 103, 14);
		panel.add(lblNetworkId);
		
		textFieldNetworkId = new JTextField();
		textFieldNetworkId.setEditable(false);
		textFieldNetworkId.setColumns(10);
		textFieldNetworkId.setBounds(132, 25, 252, 20);
		panel.add(textFieldNetworkId);
		
		JLabel lblSelectedNodes = new JLabel("Selected Nodes");
		lblSelectedNodes.setBounds(10, 56, 103, 14);
		panel.add(lblSelectedNodes);
		
		textFieldSelectedNodes = new JTextField();
		textFieldSelectedNodes.setEditable(false);
		textFieldSelectedNodes.setColumns(10);
		textFieldSelectedNodes.setBounds(132, 53, 252, 20);
		panel.add(textFieldSelectedNodes);
		
		btnSplitAction = new JButton("Split Selected Nodes");
		btnSplitAction.setIcon(new ImageIcon(CyNodeSplitPanel.class.getResource("/cynodesplit/gui/images/split.png")));
		btnSplitAction.setEnabled(false);
		btnSplitAction.setBounds(10, 313, 175, 34);
		panel.add(btnSplitAction);
		
		btnUnsplitAction = new JButton("Unsplit All Nodes");
		btnUnsplitAction.setIcon(new ImageIcon(CyNodeSplitPanel.class.getResource("/cynodesplit/gui/images/unsplit.png")));
		// Unsplit nodes
		btnUnsplitAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String networkId = Cytoscape.getCurrentNetwork().getIdentifier();
				new CySplitter(networkId);
				CyNodeSplitPanel.getInstance(null).setVisible(false);
			}
		});
		btnUnsplitAction.setEnabled(false);
		btnUnsplitAction.setBounds(209, 313, 175, 34);
		panel.add(btnUnsplitAction);
		
		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		separator.setForeground(Color.BLACK);
		separator.setBounds(195, 313, 7, 34);
		panel.add(separator);
		
		separator_1 = new JSeparator();
		separator_1.setBounds(10, 96, 374, 2);
		panel.add(separator_1);
		
		separator_2 = new JSeparator();
		separator_2.setBounds(10, 168, 374, 2);
		panel.add(separator_2);
		
		// Split nodes
		btnSplitAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				CyNetwork network = Cytoscape.getCurrentNetwork();
				@SuppressWarnings("unchecked")
				Set<CyNode> selected = network.getSelectedNodes();
				new CySplitter(network.getIdentifier(), selected);
				
				CyNodeSplitPanel.getInstance(null).setVisible(false);
			}
		});
	}
	
	
	public static void main(String[] args){
		System.out.println("Test the NodeSplit Dialog");
		CyNodeSplitPanel nsPanel = CyNodeSplitPanel.getInstance(null);
		nsPanel.setVisible(true);
		
	}
}
