package cyfluxviz.io.socket.server;

public class SocketServerThread implements Runnable{
	
	Thread t;
	int port = 4444;
	
	public SocketServerThread(){
		t = new Thread(this,"CyFluxViz Socket Server Thread");
		t.start();
	}
	
	public void run() {
		System.out.println(String.format("*** CyFluxViz SocketServerThread - Listening on localhost:%s***", port));
        SocketServer server = new SocketServer(port);
        // This never finishes (starting in a single thread)
        server.listenSocket();
	}
	
}
