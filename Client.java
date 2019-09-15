import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import java.util.Scanner;

public class Client {
    
    private static Scanner scanner;
    private String response;
    
    public Client(String address, int port, int type, String value) {
        try {
            try (
                    // Initialise client and its IO streams
                    Socket client = new Socket(address, port);
                    DataInputStream dIn = new DataInputStream(client.getInputStream());
                    DataOutputStream dOut = new DataOutputStream(client.getOutputStream());
                ) {
                // Send output to server
                dOut.writeInt(type);
                if (type == 2 || type == 3 || type == 5 || type == 6 || type == 7 || type == 8)
                    dOut.writeUTF(value);
                dOut.flush();
                // Wait for output from server
                while (dIn.available() == 0) {
                    try { TimeUnit.MILLISECONDS.sleep(200); }
                    catch (InterruptedException i) { System.out.println(i); }
                }
                // Receive output from server
                type = dIn.readInt();
                switch (type) {
                    case 1:
                        System.out.println("THE CURRENT DATE AND TIME IS "
                                            + dIn.readUTF());
                        break;
                    case 3:
                        System.out.println("THE NUMBER OF INSTANCES OF THE WORD "
                                            + value + " IS " + dIn.readUTF());
                    case 2:
                        break;
                    case 5:
                    	break;
                    case 6:
                    	response = dIn.readUTF();
                    	break;
                    case 7:
                    	break;
                    case 8:
                    	response = dIn.readUTF();
                    	break;
                    default:
                        System.out.println("Error: dIn.readInt() not recognised");
                }
            }
        }
        catch(UnknownHostException u) { System.out.println(u); }
        catch(IOException i) { System.out.println(i); }
    }
    
    public static void main(String[] args) {
        System.out.print("PLEASE INPUT THE ADDRESS FOR THIS CLIENT: ");
        scanner = new Scanner(System.in);
        String address = scanner.nextLine();
        System.out.println();
        while (true) {
            Client client;
            String input = "", result = "";
            while (result.isEmpty()) {
                printMainPage();
                input = scanner.nextLine();
                result = printSubMenu(input);
            }
            if (result.equals("DATE"))
                client = new Client(address, 5001, 1, "");
            else
                client = new Client(address, 5001, Integer.parseInt(input), result);
            System.out.println();
        }
    }
    
    private static void printMainPage() {
        System.out.println("============================================================================");
        System.out.println("WELCOME! THIS PROGRAM ALLOWS YOU TO:");
        System.out.println(" (1) GET THE CURRENT DATE AND TIME");
        System.out.println(" (2) STORE A STRING INTO OUR DICTIONARY");
        System.out.println(" (3) COUNT THE NUMBER OF TIMES A STRING HAS BEEN STORED");
        System.out.println(" (4) QUIT THE PROGRAM");
        System.out.println("============================================================================\n");
        System.out.print("PLEASE SELECT YOUR OPTION (1-4): ");
    }
    
    private static String printSubMenu(String input) {
        String result = "";
        if (input.equals("1"))
            result = "DATE";
        else if (input.equals("2")) {
            System.out.println("PLEASE ENTER THE STRING YOU WOULD LIKE TO STORE:");
            result = scanner.nextLine();
        }
        else if (input.equals("3")) {
            System.out.println("PLEASE ENTER THE STRING YOU WOULD LIKE TO QUERY:");
            result = scanner.nextLine();
        }
        else if (input.equals("4"))
            System.exit(0);
        else
            System.out.println("THIS INPUT IS NOT RECOGNISED. PLEASE TRY AGAIN.");
        return result;
    }
    
    public String getResponse() {
    	return response;
    }
    
}
