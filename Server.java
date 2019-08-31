import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class Server {
    
    private HashMap<String, Integer> hashMap = new HashMap<>();
    
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
                        DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
                    ) {
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
                            hashMap.put(value, hashMap.getOrDefault(value, 0)+1);
                            break;
                        case 3:
                            // Read value from hashmap
                            result = "" + hashMap.getOrDefault(dIn.readUTF(), 0);
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
        }
        catch(IOException i) { System.out.println(i); }
        finally { System.out.println("Server ended"); }
    }
    
    public static void main(String[] args) {
        Server server = new Server(5001);
    }
    
}
