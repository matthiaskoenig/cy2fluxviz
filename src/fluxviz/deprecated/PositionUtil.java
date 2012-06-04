package fluxviz.deprecated;

import cytoscape.CyNode;
import cytoscape.Cytoscape;

@Deprecated
public class PositionUtil {
	/**
	 * Get the position of the given Node.
	 */
	public static Position getPositionOfNode(CyNode node){
		Position pos;
		
		giny.view.NodeView nodeView = Cytoscape.getCurrentNetworkView().getNodeView(node);
		// Handle if no nodeView Exists
		if (nodeView == null){
			pos = new Position(node.getIdentifier());
		}
		else{
			pos = new Position(node.getIdentifier(), 
    							nodeView.getXPosition(),
    							nodeView.getYPosition());
		}
		return pos;
	}
	
	/**
	 * Moves position for x in x direction and y in y direction.
	 * @param position
	 */
	public static void movePosition(Position position, double x, double y){
		position.setX(position.getX() + x);
		position.setX(position.getY() + y);
	}
	
	public static void moveNodeToPosition(CyNode node, Position position){
		giny.view.NodeView nodeView = Cytoscape.getCurrentNetworkView().getNodeView(node);
    	// set the position of the node
    	nodeView.setXPosition(position.getX());
    	nodeView.setYPosition(position.getY());
	}
		
	public enum ShiftDirection {
	    UP, DOWN, LEFT, RIGHT 
	}
	
	public static void shiftNode(CyNode node, ShiftDirection direction, double length){ 
		Position pos = getPositionOfNode(node);
		switch (direction){
			case UP:
				pos.moveUp(length);
				break;
			case DOWN:
				pos.moveDown(length);
				break;				
			case LEFT:
				pos.moveLeft(length);
				break;
			case RIGHT:
				pos.moveRight(length);
				break;				
		}
	}
	
	public static void shiftNode(CyNode node, ShiftDirection direction){
		double defaultLength = 10;
		shiftNode(node, direction, defaultLength);
	}

}