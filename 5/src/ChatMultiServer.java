import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Andrew on 11/27/2016.
 */
public class ChatMultiServer {
    private int portNumber = 9000;
    private ArrayList<ChatMultiServerThread> clients;
    private int clientCounter = 0;
    private ServerSocket socket;

    public static void main(String[] args) throws IOException {
        new ChatMultiServer().start();
    }

    public ChatMultiServer() {
        this.clients = new ArrayList<>();
        this.socket = null;
    }

    public void start() {
        print("Opening chat server listening on port " + portNumber);

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            print("Chat server successfully opened at port " + portNumber);

            this.socket = serverSocket;
            this.run();
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }

    private void run() {
        boolean listening = true;
        while (listening) {
            try {
                this.createClient(this.socket.accept());
            } catch (IOException e) {
                print("Server socket accept error: " + e);
            }
        }
    }

    public void handle(int id, String message) {
        if (message.equals("Bye")) {
            this.print("Client " + id + " disconnected");
            this.findClient(id).send(message);
            this.removeClient(id);
        } else {
            this.print("Client " + id + " says: " + message);
            for (int i = 0; i < this.clients.size(); i++) {
                this.clients.get(i).send(message);
            }
        }
    }

    public void createClient(Socket socket) {
        int threadId = clientCounter++;
        ChatMultiServerThread thread = new ChatMultiServerThread(this, socket, threadId);
        this.clients.add(thread);
        print("Accepted client: " + socket);

        try {
            thread.open();
            thread.start();
        } catch (IOException e) {
            print("Error opening connection for client " + thread.id + ": " + e.getMessage());
            this.removeClient(thread.id);
        }
    }

    public void removeClient(int id) {
        ChatMultiServerThread client = this.findClient(id);
        if (client != null) {
            this.clients.remove(client);

            try {
                client.close();
            } catch (IOException e) {
                print("Error closing client " + client.id + ": " + e.getMessage());
            }
        }
    }

    private ChatMultiServerThread findClient(int id) {
        for (int i = 0; i < this.clients.size(); i++) {
           if (this.clients.get(i).id == id) {
               return this.clients.get(i);
           }
        }
        return null;
    }

    public void print(String message) {
        System.out.println(message);
    }
}
