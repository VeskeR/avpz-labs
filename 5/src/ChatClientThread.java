import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by Andrew on 11/27/2016.
 */
public class ChatClientThread extends Thread {
    private volatile boolean exit = false;
    private Socket socket;
    private ChatClientApplet chatClientApplet;
    private BufferedReader streamIn;

    public ChatClientThread(ChatClientApplet chatClientApplet, Socket socket) {
        super("ChatClientThread");

        this.chatClientApplet = chatClientApplet;
        this.socket = socket;

        this.open();
        this.start();
    }

    private void open() {
        try {
            this.streamIn = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        } catch (IOException e) {
            chatClientApplet.print("Error getting input stream: " + e.getMessage());
            this.chatClientApplet.stop();
        }
    }

    public void run() {
        while (!exit) {
            try {
                this.chatClientApplet.handle(this.streamIn.readLine());
            } catch (IOException e) {
                chatClientApplet.print("Listening error: " + e.getMessage());
                this.chatClientApplet.closeConnection();
            }
        }
    }

    public void close() {
        this.exit = true;

        try {
            if (this.streamIn != null) {
                this.streamIn.close();
                this.streamIn = null;
            }
        } catch (IOException e) {
            chatClientApplet.print("Error closing input stream: " + e.getMessage());
        }
    }
}
