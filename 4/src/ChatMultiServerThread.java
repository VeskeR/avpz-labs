import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Andrew on 11/27/2016.
 */
public class ChatMultiServerThread extends Thread {
    private ChatMultiServer server;
    private Socket socket;
    public int id;
    private PrintWriter out;
    private BufferedReader in;

    public ChatMultiServerThread(ChatMultiServer server, Socket socket, int id) {
        super("ChatMultiServerThread");

        this.server = server;
        this.socket = socket;
        this.id = id;

        System.out.println("New connection has been established with ID: " + this.id);
    }

    public void open() throws IOException {
        this.out = new PrintWriter(this.socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
    }

    public void run() {
        System.out.println("Client thread " + this.id + " is running.");

        while(true) {
            try {
                this.server.handle(this.id, this.in.readLine());
            } catch (IOException e) {
                System.out.println("Client thread " + this.id + " error while reading: " + e.getMessage());
                this.server.removeClient(this.id);
                this.stop();
            }
        }
    }

    public void send(String message) {
        this.out.println(message);
    }

    public void close() throws IOException {
        if (this.out != null) {
            this.out.close();
        }
        if (this.in != null) {
            this.in.close();
        }
        if (this.socket != null) {
            this.socket.close();
        }
    }
}
