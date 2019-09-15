import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {

	private HashMap<String, Integer> hashMap = new HashMap<>();
	private HashMap<String, String[]> directorySystem = new HashMap<>();

	public Server(int port) {
		try {
			// Initialise server (permanent)
			ServerSocket server = new ServerSocket(port);
			System.out.println("Server started");
			while (true) {
				System.out.println("Waiting for a client");
				Socket socket = server.accept();
				System.out.println("Client accepted");
				try (
						// Initialise server IO streams
						DataInputStream dIn = new DataInputStream(socket.getInputStream());
						DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());) {
					// Receive input from socket
					String result = "";
					int type = dIn.readInt();
					switch (type) {
					case 1:
						// Get the current time
						DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
						result = dtf.format(LocalDateTime.now());
						break;
					case 2:
						// Store a value in hashmap
						String value = dIn.readUTF();
						hashMap.put(value, hashMap.getOrDefault(value, 0) + 1);
						break;
					case 3:
						// Read value from hashmap
						result = "" + hashMap.getOrDefault(dIn.readUTF(), 0);
						break;
					case 5:
						// update directory system (hashmap) / join
						String peerfiles = dIn.readUTF();
						String peer = peerfiles.split("        ")[0];
						String[] filenames = {}; 
						if (peerfiles.split("        ").length>1) {
							filenames = peerfiles.split("        ")[1].split(" ");
						}
						directorySystem.put(peer, filenames);
						System.out.println("directorySystem: " + directorySystem.toString());
						break;
					case 6:
						// search directory system (hashmap)
						String filename = dIn.readUTF();
						// server search directory
						for (String key : directorySystem.keySet()) {
							for (String s : directorySystem.get(key)) {
								// respond with IP addresses of nodes that have a copy of the file.
								if (s.contentEquals(filename)) {
									result += key + " ";
									break;
								}
							}
						}
						if (result.isEmpty()) {// key is address so should not be empty
							result = "NOT FOUND";
						}
						break;
					case 7:
						// remove node when leave
						String address = dIn.readUTF();
						directorySystem.remove(address);
						System.out.println("directorySystem: " + directorySystem.toString());
						break;
					case 8:
						// return all peers
						System.out.println("directorySystem: " + directorySystem.toString());
						result = directorySystem.toString();
						break;
					default:
						System.out.println("Error: dIn.readInt() not recognised");
					}
					// Output result to socket
					dOut.writeInt(type);
					if (!result.isEmpty())
						dOut.writeUTF(result);
					dOut.flush();
					System.out.println("Client disconnected\n");
				}
			}
		} catch (IOException i) {
			System.out.println(i);
		} finally {
			System.out.println("Server ended");
		}
	}

	public void removeNode(String address) {
		directorySystem.remove(address);
	}

	public static void main(String[] args) {
		Server server = new Server(5001);
	}

}
