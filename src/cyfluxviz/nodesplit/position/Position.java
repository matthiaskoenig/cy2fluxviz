package cyfluxviz.nodesplit.position;

/**
	Stores the positional information for a given node
*/
public class Position {
	public static final Double xDEFAULT = 0.0;
	public static final Double yDEFAULT = 0.0;
	
	private String id;
	private Double x;
	private Double y;
	
	public Position(String id){
		setId(id);
		setX(xDEFAULT);
		setY(yDEFAULT);
	}
	
	public Position(String id, Double x, Double y){
		setId(id);
		setX(x);
		setY(y);		
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public Double getX() {
		return x;
	}
	public void setX(Double pos) {
		x = pos;
	}
	public Double getY() {
		return y;
	}
	public void setY(Double pos) {
		y = pos;
	}
}
