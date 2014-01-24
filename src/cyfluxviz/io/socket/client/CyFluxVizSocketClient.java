package cyfluxviz.io.socket.client;

import java.io.*;
import java.net.*;

public class CyFluxVizSocketClient {

	Socket socket = null;
	PrintWriter out = null;
	BufferedReader in = null;
	int port; 

	public CyFluxVizSocketClient(int port) {
		this.port = port;
	}

	public void sentTextToServer(String text){
		out.println(text);
		try {
			String line = in.readLine();
			System.out.println("Text received :" + line);
		} catch (IOException e) {
			System.out.println("Read failed");
			System.exit(1);
		}
	}

	public void listenSocket() {
		// Create socket connection
		try {
			socket = new Socket("localhost", port);
			
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			
		} catch (UnknownHostException e) {
			System.out.println("Unknown host: kq6py.eng");
			System.exit(1);
		} catch (IOException e) {
			System.out.println("No I/O");
			System.exit(1);
		}
	}

	
	public static void main(String[] args) {
		CyFluxVizSocketClient client = new CyFluxVizSocketClient(4444);
		client.listenSocket();
		
		
	}
}
