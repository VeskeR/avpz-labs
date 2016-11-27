import java.net.*;
import java.io.*;

/**
 * Created by Andrew on 11/27/2016.
 */
public class SocketClientThread extends Thread {
    private Socket socket;
    private ClientApplet clientApplet;
    private BufferedReader streamIn;

    public SocketClientThread(ClientApplet clientApplet, Socket socket) {
        this.clientApplet = clientApplet;
        this.socket = socket;

        this.open();
        this.start();
    }

    private void open() {
        try {
            this.streamIn = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        } catch (IOException e) {
            System.out.println("Error getting input stream: " + e.getMessage());
            this.clientApplet.stop();
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
                this.clientApplet.handle(this.streamIn.readLine());
            } catch (IOException e) {
                System.out.println("Listening error: " + e.getMessage());
                this.clientApplet.stop();
            }
        }
    }
}
