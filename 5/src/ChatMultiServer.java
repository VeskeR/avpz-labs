import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Andrew on 11/27/2016.
 */
public class ChatMultiServer {
    private volatile boolean listening;
    private int portNumber;
    private ArrayList<ChatMultiServerThread> clients;
    private int clientCounter = 0;
    private ServerSocket socket;
    private ChatMultiServerApplet serverApplet;

    public static void main(String[] args) throws IOException {
        new ChatMultiServer(9000).start();
    }

    public ChatMultiServer(int portNumber) {
       this(portNumber, null);
    }

    public ChatMultiServer(int portNumber, ChatMultiServerApplet serverApplet) {
        this.portNumber = portNumber;
        this.clients = new ArrayList<>();
        this.socket = null;
        this.serverApplet = serverApplet;
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

    public void stop() {
        this.listening = false;
        clientCounter = 0;
        for (int i = 0; i < clients.size(); i++) {
            this.closeClient(clients.get(i).id);
        }
        clients = new ArrayList<>();

        try {
            this.socket.close();
            this.socket = null;
        } catch (IOException e) {
            this.print("Error closing server socket: " + e.getMessage());
        }
    }

    public void handle(int id, String message) {
        if (message.equals("Bye")) {
            this.print("Client " + id + " disconnected");
            this.sendPrivate(message, id);
            this.removeClient(id);
        } else {
            this.print("Client " + id + " says: " + message);
            this.sendAll(message);
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

    public void closeClient(int id) {
        ChatMultiServerThread client = this.findClient(id);
        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {
                print("Error closing client " + client.id + ": " + e.getMessage());
            }
        }
    }

    public void print(String message) {
        System.out.println(message);

        if (this.serverApplet != null) {
            this.serverApplet.print(message);
        }
    }

    public void setPortNumber(int portNumber, boolean force) {
        if (this.listening && force) {
            this.sendAll("Chat server changed listening port to: " + portNumber + ".");
            this.sendAll("Chat server is now closing. Please reconnect.");

            this.stop();

            this.portNumber = portNumber;
        } else if (this.listening) {
            this.print("Can't change port number while listening. Stop server first.");
        } else {
            this.portNumber = portNumber;
        }
    }

    public int getPortNumber() {
        return this.portNumber;
    }

    private void run() {
        this.listening = true;

        while (this.listening) {
            try {
                this.createClient(this.socket.accept());
            } catch (IOException e) {
                print("Server socket accept error: " + e);
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

    private void sendAll(String message) {
        for (int i = 0; i < this.clients.size(); i++) {
            this.clients.get(i).send(message);
        }
    }

    private void sendPrivate(String message, int id) {
        this.findClient(id).send(message);
    }
}
