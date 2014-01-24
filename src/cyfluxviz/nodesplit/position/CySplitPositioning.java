package cyfluxviz.nodesplit.position;

import giny.model.Edge;
import giny.model.Node;
import giny.view.NodeView;

import java.util.Set;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;

/**
 * Position the nodes after the split and unsplit operation.
 * @author mkoenig
 */
public class CySplitPositioning {
	private final static Double EPS = 0.001;
	private final static Double UNPOS = 10.0;	// if unpositioned
	
	CyNetwork network;
	CyNetworkView view;
	
	public CySplitPositioning(Set<Integer> nInds){
		network = Cytoscape.getCurrentNetwork();
		view = Cytoscape.getCurrentNetworkView();
		for (Integer nIndex: nInds){
			Node node = network.getNode(nIndex);
			Position pos = getPositionForSplitNode(node);
			positionNode(node, pos);
		}
	}
	
	/** Calculate the split node position based of the local neighbor positions
	 * of the connecting node. */
	private Position getPositionForSplitNode(Node n0){
		
		// test if already positioned
		Position pos0 = getPositionForNode(n0);
		System.out.println("CySplitNode[INFO] -> "+ n0.getIdentifier() + " <" + pos0.getX() + "|" + pos0.getY() + ">");
		if (isPositioned(pos0)){
			return pos0;
		}
		
		// Get neighbor position;
		Position[] npos = getNeighborPositions(n0);
		
		// Take the first one
		Position pos1 = npos[0];
		Node n1 = Cytoscape.getCyNode(pos1.getId());
		System.out.println(String.format("CySplitNode[INFO] -> %s <%s|%s>", 
				pos1.getId(), pos1.getX().toString(), pos1.getY().toString()));
		
		
		// Get all positions of the neighbors of n1
		Double xm = 0.0;
		Double ym = 0.0;
		npos = getNeighborPositions(n1);
		for (int i=0; i<npos.length; ++i){
			Position ptmp = npos[i];
			if (ptmp.getId() == n0.getIdentifier()){
				continue;
			}
			xm += 1.0* (ptmp.getX() - pos1.getX())/npos.length;
			ym += 1.0* (ptmp.getY() - pos1.getY())/npos.length;
	
		}
		// position relativ to n1 weighted with positions of other neighbors
		xm = pos1.getX() - xm;
		ym = pos1.getY() - ym;
		System.out.println("CySplitNode[INFO] -> n <" + xm + "|" + ym + ">");
		return new Position(n0.getIdentifier(), xm, ym);
	}
	
	private void positionNode(Node node, Position pos){
		NodeView nodeView = view.getNodeView(node);
		nodeView.setXPosition(pos.getX());
		nodeView.setYPosition(pos.getY());
	}
	
	private Position getPositionForNode(Node n){
		NodeView nView = view.getNodeView(n);
		return new Position(n.getIdentifier(), nView.getXPosition(), nView.getYPosition());
	}
	
	/** Test if node already has position. */
	private boolean isPositioned(Position pos){
		return (Math.abs(pos.getX()-UNPOS)>EPS || Math.abs(pos.getY()-UNPOS)>EPS);
	}
	
	private Position[] getNeighborPositions(Node n0){
		// Get neighbor position;
		Integer nIndex = n0.getRootGraphIndex();
		int[] eInds = network.getAdjacentEdgeIndicesArray(nIndex, true, true, true);
		Position[] positions = new Position[eInds.length];
		for (int i=0; i<positions.length; ++i){
			Integer eIndex = eInds[i];           // this should always work because split node is connected
			Edge e = network.getEdge(eIndex);
			Node n1 = e.getSource();
			if (n1.getRootGraphIndex() == nIndex){
				n1 = e.getTarget();
			}
			positions[i] = getPositionForNode(n1);
		}
		return positions;
	}
	
	
}
