import java.net.*;
import java.io.*;

/**
 * Created by Andrew on 11/27/2016.
 */
public class SocketMultiServerThread extends Thread {
    private static int counter = 0;
    private Socket socket;
    private int responds = 0;
    private int instanceId;

    public SocketMultiServerThread(Socket socket) {
        super("SocketMultiServerThread");
        this.socket = socket;
        this.instanceId = SocketMultiServerThread.counter++;

        System.out.println("New connection has been established with ID: " + this.instanceId);
    }

    public void run() {
        try (
                PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        ) {
            String inputLine;
            String outputLine;

            while ((inputLine = in.readLine()) != null) {
                System.out.println("Connection ID: " + this.instanceId + ". Got input: " + inputLine);
                if (inputLine.equals("Bye")) {
                    break;
                }

                outputLine = "Server responded " + ++responds + " times. Connection ID: " + this.instanceId + ". Echo: " + inputLine;
                out.println(outputLine);
            }
            this.socket.close();
        } catch (IOException e) {
            System.err.println("Connection ID: " + this.instanceId + ". Error during communication with client: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
