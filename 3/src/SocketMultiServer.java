import java.io.*;
import java.net.*;

/**
 * Created by Andrew on 11/27/2016.
 */
public class SocketMultiServer {
    private static int portNumber = 9000;

    public static void main(String[] args) throws IOException {
        System.out.println("Opening socket server on port " + portNumber);

        boolean listening = true;

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("Socket server successfully opened at port " + portNumber);

            while (listening) {
                new SocketMultiServerThread(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }
}
