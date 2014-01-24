package cyfluxviz.nodesplit.core;

import giny.model.Edge;

import java.util.HashSet;
import java.util.Set;


import cyfluxviz.FluxDis;
import cyfluxviz.FluxDisCollection;
import cyfluxviz.nodesplit.position.CySplitPositioning;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.view.CyNetworkView;


/** Calculate splitting and unsplitting for the 
 *  selected nodes in the network. 
 *
 * @author mkoenig
 */
public class CySplitter {
	
	public static final String SEP = "_split_"; 
	String networkId; 
	SplitMapping sMap;
	
	/** Split selected nodes in network. */
	public CySplitter(String networkId, Set<CyNode> selected){
		this.networkId = networkId;
		sMap = SplitMappingCollection.getInstance().getOrCreateSplitMappingForNetworkId(networkId);
		
		if (selected == null || selected.size() == 0){
			System.out.println("CyNodeSplit[INFO]: unsplit nodes");
			for (Integer nIndex: sMap.getNodeSources()){
				unsplitNode(nIndex);
			}
			// the mapping have to be cleared;
			// TODO updateCyFluxViz();
			sMap.clear();
		} else {
			System.out.println("CyNodeSplit[INFO]: split nodes");
			Object[] tmp = selected.toArray();
			for (int i=0; i<tmp.length; ++i){
				CyNode node = (CyNode) tmp[i];
				splitNode(node.getRootGraphIndex());
			}	
			// reposition the split nodes
			Set<Integer> splitNodes = sMap.getNodeTargets();
			new CySplitPositioning(splitNodes);
			
			// select all the splitted nodes
			CyNetwork network = Cytoscape.getNetwork(networkId);
			for (Integer nIndex: splitNodes){
				network.setSelectedNodeState(network.getNode(nIndex), true);	
			}
			// TODO updateCyFluxViz();
		}
		sMap.updateSplitStatus();
		
		// update visual appearance
		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		view.redrawGraph(false, true);
		view.updateView();
	}
	
	/** Unsplit all nodes in network. */
	public CySplitter(String networkId){
		this(networkId, null);
	}
	
	/** Split the single node. */
	private void splitNode(Integer nIndex){
		CyNetwork network = Cytoscape.getNetwork(networkId);
		int[] edgeInts = network.getAdjacentEdgeIndicesArray(nIndex, true, true, true);
		
		// only split nodes with more than 2 edges
		String nId = network.getNode(nIndex).getIdentifier();
		if (edgeInts.length <=2){
			System.out.println("CyNodeSplit[INFO]: node degree<2  -> not split " + nId);
			return;
		}
		
		Integer count = 1;
		for (int eIndex: edgeInts){
			
			// Copy node & add to network
			String ncId = nId + SEP + count.toString();
			CyNode nc = copyCyNode(ncId, nId);
			Integer ncIndex = (Integer) nc.getRootGraphIndex();
			network.addNode(nc.getRootGraphIndex());
			
			// clone edge
			Edge e = network.getEdge(eIndex);
			Edge ec = copyEdge(network, e, nIndex, ncIndex);
			Integer ecIndex = ec.getRootGraphIndex();
			
			// add copied edge and remove original edge
			network.addEdge(ec.getRootGraphIndex());
			network.removeEdge(eIndex, false);
			
			// Nodes and Edges have to be saved in the sMap
			sMap.putNode(nIndex, ncIndex);
			sMap.putEdge(eIndex, ecIndex);
			
			count++;
			System.out.println("CyNodeSplit[INFO]: split : " + nId + " -> " + ncId);
		}
				
		// remove original node
		network.removeNode(nIndex, false);
	}
	
	
	/** Unsplit the single node. */
	private void unsplitNode(Integer nIndex){
		CyNetwork network = Cytoscape.getNetwork(networkId);
		// remove split edges
		for (Integer eIndex: sMap.getEdgeSources() ){
			Integer ecIndex = sMap.getEdgeTarget(eIndex);
			network.removeEdge(ecIndex, true);
		}
		
		// remove split nodes
		for (Integer ncIndex: sMap.getNodeTargets(nIndex) ){
			network.removeNode(ncIndex, true);
			System.out.println("CyNodeSplit[INFO]: unsplit -> " + ncIndex);
		}
		
		// add original node
		network.addNode(nIndex);
		
		// add original edges
		for (Integer eIndex: sMap.getEdgeSources()){
			network.addEdge(eIndex);
		}
	}
	
	/** Copy node with all node attributes */
	private CyNode copyCyNode(String ncId, String nId){
		
		// Test if id is already used for copy node
		CyNode nc = Cytoscape.getCyNode(ncId, false);
		if (nc != null){
			System.out.println("CyNodeSplit[WARNING]: node exists - id already used: " + ncId);
		} else {
			// Create node 
			nc = Cytoscape.getCyNode(ncId, true);
		}
		
		// copy all attributes
		// TODO: this is a shitty hack	
		CyAttributes atts = Cytoscape.getNodeAttributes();
		for (String aname : atts.getAttributeNames()){
			
			// Resolve how to deal with all data types.
			Object obj = atts.getAttribute(nId, aname);
			if (obj != null){
				// TODO: this is a shitty hack
				byte type = atts.getType(aname);
				if (type == CyAttributes.TYPE_BOOLEAN){
					atts.setAttribute(ncId, aname, (Boolean) obj);
				}if (type == CyAttributes.TYPE_INTEGER){
					atts.setAttribute(ncId, aname, (Integer) obj);
				}if (type == CyAttributes.TYPE_FLOATING){
					atts.setAttribute(ncId, aname, (Double) obj);
				}if (type == CyAttributes.TYPE_STRING){
					atts.setAttribute(ncId, aname, (String) obj);
				}	
			}
		}
		return nc;
	}
	
	/** Create altered edge and copy edge attributes. */
	private Edge copyEdge(CyNetwork network, Edge e, Integer nIndex, Integer ncIndex){		
		// Get source and target of new edge
		Integer source;
		Integer target;
		if (e.getSource().getRootGraphIndex() == nIndex){
			source = ncIndex;
			target = e.getTarget().getRootGraphIndex();
		} else {
			source = e.getSource().getRootGraphIndex();
			target = ncIndex;
		}
		
		CyEdge ec = Cytoscape.getCyEdge(network.getNode(source), network.getNode(target), Semantics.INTERACTION, SEP , true);
		String ecId = ec.getIdentifier();
		
		String eId = e.getIdentifier();
		CyAttributes atts = Cytoscape.getEdgeAttributes();
		for (String aname : atts.getAttributeNames()){
			Object obj = atts.getAttribute(eId, aname);
			if (obj != null){
				// TODO: this is a shitty hack
				byte type = atts.getType(aname);
				if (type == CyAttributes.TYPE_BOOLEAN){
					atts.setAttribute(ecId, aname, (Boolean) obj);
				}if (type == CyAttributes.TYPE_INTEGER){
					atts.setAttribute(ecId, aname, (Integer) obj);
				}if (type == CyAttributes.TYPE_FLOATING){
					atts.setAttribute(ecId, aname, (Double) obj);
				}if (type == CyAttributes.TYPE_STRING){
					atts.setAttribute(ecId, aname, (String) obj);
				}
			}
		}
		return ec;
	}	
	
	/** The dictionaries in CyFluxViz have to be updated. 
	private void updateCyFluxViz(){
		try{
			FluxDisCollection fdCollection = FluxDisCollection.getInstance();
			Object[] fdIds = fdCollection.getIdSet().toArray();
			Set<FluxDis> fdUpdateSet = new HashSet<FluxDis>(); // update
			for (int i=0; i<fdIds.length; ++i){
				Integer fdId = (Integer) fdIds[i];
				FluxDis fd = fdCollection.getFluxDistribution(fdId);
				if (fd.getNetworkId().equals(networkId)){
					// update the mapping of the edge and node attribute
					fd  = updateFluxDistributionWithSplitMapping(fd, sMap);
					fdUpdateSet.add(fd);
				}
			}
			fdCollection.addFluxDistributions(fdUpdateSet);
			
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private FluxDis updateFluxDistributionWithSplitMapping(FluxDis fd, SplitMapping sMap){
		// Update here
		FluxDis fdNew = null;
		return fdNew;
	}
	*/
}
