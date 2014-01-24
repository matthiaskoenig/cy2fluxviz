
package cyfluxviz.io.socket.server;

/**
 * Creates a SocketServer in a Thread so that CyFluxViz can listen to external programs 
 * generating flux distributions.
 * 
 * @author Matthias Koenig
 * @date 10 October 2012
 */
public class SocketListener {

	public SocketListener() {

		// information about the current thread
		Thread t = Thread.currentThread();
	    System.out.println("Current thread: " + t);
		
	    // Create a new thread which starts the server
		new SocketServerThread();
		
    }
	
	public static void main(String[] args){
		new SocketListener();
		
	}
	
}
