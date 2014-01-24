package cyfluxviz;

/** 
 * Enum to help handle the Flux directions in the network.
 * @author mkoenig
 *
 */
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
