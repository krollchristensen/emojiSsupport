import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 12345;  // Portnummer serveren lytter på
    private static Set<PrintWriter> clientWriters = new HashSet<>();  // Sæt til at holde alle klienters output streams

    public static void main(String[] args) {
        System.out.println("Chat server started...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {  // Opret en server socket på den angivne port
            while (true) {
                // Accepterer en ny klientforbindelse og starter en ny tråd til at håndtere den
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();  // Udskriv eventuelle I/O-fejl
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;  // Klientens socket-forbindelse
        private PrintWriter out;  // Output stream til at sende data til klienten
        private BufferedReader in;  // Input stream til at modtage data fra klienten

        public ClientHandler(Socket socket) {
            this.socket = socket;  // Initialiser socketten for denne klient
        }

        public void run() {
            try {
                // Opret streams til kommunikation med klienten, bruger UTF-8 for at understøtte emojis
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);

                // Tilføj klientens output stream til sættet af clientWriters
                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

                String message;
                while ((message = in.readLine()) != null) {  // Læs meddelelser fra klienten
                    System.out.println("Received: " + message);  // Udskriv modtaget meddelelse på serverens konsol

                    // Send meddelelsen til alle tilsluttede klienter
                    synchronized (clientWriters) {
                        for (PrintWriter writer : clientWriters) {
                            writer.println(message);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();  // Håndter eventuelle I/O-fejl
            } finally {
                try {
                    socket.close();  // Luk socketforbindelsen
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Fjern klientens output stream fra sættet af clientWriters, når klienten afbryder forbindelsen
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                }
            }
        }
    }
}
