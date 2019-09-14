import java.io.File;
import java.util.HashMap;

// centralized P2P network
public class Illustration {

	public static void main(String[] args) {
		// initializes nodes which have certain files
		Node n1 = new Node();
		n1.getFiles().add(new File("file1"));
		n1.getFiles().add(new File("file2"));
		n1.getFiles().add(new File("file3"));
		n1.getFiles().add(new File("file4"));
		n1.getFiles().add(new File("file5"));
		System.out.println("n1: " + n1.toString());

		Node n2 = new Node();
		n2.getFiles().add(new File("file5"));
		n2.getFiles().add(new File("file6"));
		n2.getFiles().add(new File("file7"));
		System.out.println("n1: " + n2.toString());

		Node n3 = new Node();
		n3.getFiles().add(new File("file7"));
		n3.getFiles().add(new File("file8"));
		System.out.println("n1: " + n3.toString());
		

		// nodes register with central server, provide IP addresses and lists of files
		n1.join();
		n2.join();
		n3.join();

		
		String filename = "file5";
		
		// n1, looking for <filename>, sends query to central server
		// central server responds with IP addresses of nodes that have the file
		String addresses = n1.requestFileSource(filename);
		// n1 contacts one of the nodes to download file
		String selectedNodeAddress = addresses.split(" ")[0];
		Node.requestFile(n1, Node.nodes.get(selectedNodeAddress), filename);

		
		// directory is constantly updated as nodes join or leave
		n1.leave();
		n2.requestFileSource("file1");
		n1.join();
		n2.requestFileSource("file1");
	}

}
