import java.io.*;
import java.net.*;

public class ChatClient {
    private static final String SERVER_ADDRESS = "localhost";  // IP-adressen hvor serveren kører
    private static final int SERVER_PORT = 12345;  // Portnummer serveren lytter på

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);  // Opret forbindelse til serveren
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in, "UTF-8"))) {

            // Tråd til at modtage beskeder fra serveren
            Thread receiverThread = new Thread(() -> {
                try {
                    String incomingMessage;
                    while ((incomingMessage = in.readLine()) != null) {  // Læs beskeder fra serveren
                        System.out.println("Server: " + incomingMessage);  // Udskriv beskeden
                    }
                } catch (IOException e) {
                    e.printStackTrace();  // Håndter eventuelle I/O-fejl
                }
            });

            receiverThread.start();  // Start modtagelsestråden

            String userMessage;
            while ((userMessage = userInput.readLine()) != null) {
                out.println(userMessage);  // Send brugerens besked til serveren
            }
        } catch (IOException e) {
            e.printStackTrace();  // Håndter eventuelle I/O-fejl
        }
    }
}
