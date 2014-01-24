package cyfluxviz.io.socket.server;

import java.io.*;
import java.net.*;

public class SocketServer {
	private ServerSocket server = null;
	private Socket client = null;
	private BufferedReader in = null;
	private PrintWriter out = null;
	private String line;
	private int port;

	SocketServer(int port) {
		this.port = port;
	}

	/**  Opens new ServerSocket at port on localhost.
	 */
	public void listenSocket() {
		while (true) {
			try {
				server = new ServerSocket(port);
			} catch (IOException e) {
				System.out.println(String.format("Can not listen on port %s", port));
				System.exit(-1);
			}

			try {
				client = server.accept();
			} catch (IOException e) {
				System.out.println("Accept failed: " + port);
				System.exit(-1);
			}

			try {
				in = new BufferedReader(new InputStreamReader(
						client.getInputStream()));
				out = new PrintWriter(client.getOutputStream(), true);
			} catch (IOException e) {
				System.out.println("Accept failed: " + port);
				System.exit(-1);
			}

			// Client connection is available
			while (true) {
				try {
					// test if client is still available
					System.out.println("Client:" + client);
					if (client == null) {
						System.out.println("Client connection closed.");
						break;
					}

					// Perform tasks with the given client information
					// TODO: handle the send socket information
					line = in.readLine();
					SocketExecution.execute(line);
					

					// Send data back to client
					out.println(line);
					System.out.println("Client info:" + line);
				} catch (IOException e) {
					System.out.println("Read failed");
					System.exit(-1);
				}
			}
		}
	}

	protected void finalize() {
		// Clean up
		try {
			in.close();
			out.close();
			server.close();
		} catch (IOException e) {
			System.out.println("Could not close.");
			System.exit(-1);
		}
	}
}
