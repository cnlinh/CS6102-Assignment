import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

// centralized P2P network
public class Illustration {
	private static Scanner scan;

	private static void demo() {
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

	public static void main(String[] args) {
		System.out.println("WELCOME TO CENTRALIZED P2P NETWORK!");
		scan = new Scanner(System.in);
		System.out.println();

		while (true) {
			String input = "", result = "";
			while (result.isEmpty()) {
				printMainPage();
				input = scan.nextLine();
				result = printSubMenu(input);

				System.out.println();
			}
		}
	}

	private static void printMainPage() {
		System.out.println("WHAT WOULD YOU LIKE TO DO?");
		System.out.println("============================================================================");
		System.out.println(" (1) SHOW ALL NODE(S)");
		System.out.println(" (2) SHOW ALL PEER(S)");
		System.out.println(" (3) CREATE NEW NODE");
		System.out.println(" (4) ADD FILE TO NODE");
		System.out.println(" (5) MAKE NODE JOIN SERVER");
		System.out.println(" (6) MAKE NODE LEAVE SERVER");
		System.out.println(" (7) MAKE NODE QUERY FILE FROM SERVER");
		System.out.println(" (8) MAKE NODE REQUEST FILE FROM OTHER NODE");
		System.out.println(" (9) QUIT THE PROGRAM");
		System.out.println("============================================================================\n");
		System.out.print("PLEASE SELECT YOUR OPTION (1-9): ");
	}

	private static String printSubMenu(String input) {
		// TODO
		String result = "";
		if (input.equals("1")) { // SHOW ALL NODE(S)
			Node.displayNodes();
		} else if (input.equals("2")) { // SHOW ALL PEER(S)
			Client client = new Client("127.0.0.1", 5001, 8, "");
			String response = client.getResponse();
			if (response.contentEquals("{}")) {
				System.out.println("NO PEERS IN THE NETWORK");
			} else {
				System.out.println(client.getResponse());
			}
		} else if (input.equals("3")) { // CREATE NEW NODE
			Node n = new Node();
			System.out.println("CREATED NEW NODE AT ADDRESS " + n.getAddress());
		} else if (input.equals("4")) { // ADD FILE TO NODE
			System.out.print("ENTER NODE ADDRESS: ");
			String address = scan.nextLine();
			System.out.print("ENTER FILE NAME (USE ONLY ALPHANUMERICAL AND UNDERSCORE): ");
			String filename = scan.nextLine();
			Node.nodes.get(address).getFiles().add(new File(filename));
		} else if (input.equals("5")) { // MAKE NODE JOIN SERVER
			System.out.print("ENTER NODE ADDRESS: ");
			String address = scan.nextLine();
			Node.nodes.get(address).join();
		} else if (input.equals("6")) { // MAKE NODE LEAVE SERVER
			System.out.print("ENTER NODE ADDRESS: ");
			String address = scan.nextLine();
			Node.nodes.get(address).leave();
		} else if (input.equals("7")) { // MAKE NODE QUERY FILE FROM SERVER
			System.out.print("ENTER NODE ADDRESS: ");
			String address = scan.nextLine();
			System.out.print("ENTER FILE NAME (USE ONLY ALPHANUMERICAL AND UNDERSCORE): ");
			String filename = scan.nextLine();
			Node.nodes.get(address).requestFileSource(filename);
		} else if (input.equals("8")) { // MAKE NODE REQUEST FILE FROM OTHER NODE
			System.out.print("ENTER REQUESTER NODE ADDRESS: ");
			String r_address = scan.nextLine();
			System.out.print("ENTER SOURCE NODE ADDRESS: ");
			String s_address = scan.nextLine();
			System.out.print("ENTER FILE NAME (USE ONLY ALPHANUMERICAL AND UNDERSCORE): ");
			String filename = scan.nextLine();
			Node.requestFile(Node.nodes.get(r_address), Node.nodes.get(s_address), filename);
		} else if (input.equals("9")) { // QUIT THE PROGRAM
			System.out.println("HOPE YOU ENJOY USING THE PROGRAM. BYE!");
			System.exit(0);
		} else
			System.out.println("THIS INPUT IS NOT RECOGNISED. PLEASE TRY AGAIN.");
		return result;
	}

}
