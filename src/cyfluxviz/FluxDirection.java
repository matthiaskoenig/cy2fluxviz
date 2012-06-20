package cyfluxviz;

public enum FluxDirection {
	FORWARD(1), REVERSE(-1);
	
	private int value;
    private FluxDirection(int value) {
    	this.value = value;
    }
    
    public int toInt(){
    	return value;
    }
}
