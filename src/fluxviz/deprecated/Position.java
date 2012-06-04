package fluxviz.deprecated;

/** Store Positional information for a given node */
@Deprecated
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
	
	// Moving operations
	public void moveRight(Double shift){
		setX(getX() + shift); 
	}
	public void moveLeft(Double shift){
		setX(getX() - shift); 
	}
	public void moveUp(Double shift){
		setX(getY() + shift); 
	}
	public void moveDown(Double shift){
		setX(getX() - shift); 
	}
	
}
