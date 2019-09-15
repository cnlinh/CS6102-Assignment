import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Node {

	private static int addressCount = 0;
	public static HashMap<String, Node> nodes = new HashMap<>();
	private String address = "";
	private ArrayList<File> files = new ArrayList<File>();

	private static String newAddress() {
		addressCount++;
		return "127.0.0." + Integer.toString(addressCount);
	}

	public String getAddress() {
		return address;
	}

	public static void displayNodes() {
		if (nodes.size() == 0) {
			System.out.println("NO NODES EXIST");
			return;
		}
		for (Node n : nodes.values()) {
			System.out.println(n);
		}
	}

	public ArrayList<File> getFiles() {
		return files;
	}

	public static File requestFile(Node requester, Node source, String filename) {
		// requester contact source to download file
		// assume accept all download requests
		System.out.println("PEER AT " + requester.address + " CONTACTS NODE AT " + source.address + " TO DOWNLOAD FILE "
				+ filename + ".");
		for (File f : source.files) {
			if (f.getName().contentEquals(filename)) {
				requester.files.add(f);
				System.out.println("FILE SUCCESSFULLY DOWNLOADED");
				return f;
			}
		}
		System.out.println("PEER AT " + source.address + " DO NOT HAVE FILE " + filename);
		return null;
	}

	public void updateFiles() {
		// peer then provides its IP address and a list of files it has to share
		// update files offered on server directory system
		String value = address + "        "; // 8 spaces
		for (File f : files) {
			value += f.getName() + " ";
		}
		new Client(address, 5001, 5, value);
	}

	public String requestFileSource(String filename) {
		// peer, looking for file, sends query to central server
		Client client = new Client(address, 5001, 6, filename);
		String response = client.getResponse();
		System.out.println("ADDRESSES OF PEERS THAT OFFER THE FILE " + filename + " ARE: " + response);
		return response;
	}

	public void join() {
		if (!address.contentEquals("")) {// registered before
			updateFiles();
			System.out.println("NODE AT " + address + " JOINED SERVER.");
			return;
		}
		address = Node.newAddress();
		updateFiles();
		System.out.println("NODE AT " + address + " JOINED SERVER.");
	}

	public void leave() {
		new Client(address, 5001, 7, address);
		System.out.println("NODE AT " + address + " LEFT SERVER.");
	}

	public Node() {
		address = newAddress();
		nodes.put(address, this);
	}

	public String toString() {
		String result = "";
		result += address + ";  ";
		for (File f : files) {
			result += f.getName() + ", ";
		}
		return result;
	}
}
