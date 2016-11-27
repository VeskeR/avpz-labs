import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by Andrew on 11/27/2016.
 */
public class ChatClientThread extends Thread {
    private Socket socket;
    private ChatClientApplet chatClientApplet;
    private BufferedReader streamIn;

    public ChatClientThread(ChatClientApplet chatClientApplet, Socket socket) {
        this.chatClientApplet = chatClientApplet;
        this.socket = socket;

        this.open();
        this.start();
    }

    private void open() {
        try {
            this.streamIn = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        } catch (IOException e) {
            System.out.println("Error getting input stream: " + e.getMessage());
            this.chatClientApplet.stop();
        }
    }

    public void close() {
        try {
            if (this.streamIn != null) {
                this.streamIn.close();
            }
        } catch (IOException e) {
            System.out.println("Error closing input stream: " + e.getMessage());
        }
    }

    public void run() {
        while (true) {
            try {
                this.chatClientApplet.handle(this.streamIn.readLine());
            } catch (IOException e) {
                System.out.println("Listening error: " + e.getMessage());
                this.chatClientApplet.stop();
            }
        }
    }
}
