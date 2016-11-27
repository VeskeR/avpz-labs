import java.applet.*;
import java.awt.*;
import java.io.*;
import java.net.*;

/**
 * Created by Andrew on 11/27/2016.
 */
public class ClientApplet extends Applet {
    private Socket socket;
    private PrintWriter streamOut;
    private SocketClientThread socketClient;

    private TextArea displayTA = new TextArea();
    private TextField inputTF = new TextField();
    private Button sendBtn = new Button("Send");
    private Button connectBtn = new Button("Connect");
    private Button quitBtn = new Button("Quit");

    private String serverName = "localhost";
    private int serverPort = 9000;

    public void init() {
        // Create panel with 'quit' & 'connect' buttons
        Panel keys = new Panel();
        keys.setLayout(new GridLayout(1, 2));
        keys.add(this.quitBtn);
        keys.add(this.connectBtn);

        // Create panel with controls at the bottom of window
        Panel south = new Panel();
        south.setLayout(new BorderLayout());
        south.add("West", keys);
        south.add("Center", this.inputTF);
        south.add("East", this.sendBtn);

        // Create title for applet
        Label title = new Label("Echo Applet via sockets", Label.CENTER);
        title.setFont(new Font("Helvetica", Font.BOLD, 14));

        // Set layout for applet
        setLayout(new BorderLayout());
        this.add("North", title);
        this.add("Center", this.displayTA);
        this.add("South", south);

        this.quitBtn.setEnabled(false);
        this.sendBtn.setEnabled(false);
    }

    public boolean action(Event e, Object o) {
        if (e.target == this.quitBtn) {
            this.inputTF.setText("Bye");
            this.send();

            this.quitBtn.setEnabled(false);
            this.sendBtn.setEnabled(false);
            this.connectBtn.setEnabled(false);
        } else if (e.target == this.connectBtn) {
            this.connect(serverName, serverPort);
        } else if (e.target == this.sendBtn) {
            this.send();
            this.inputTF.requestFocus();
        }
        return true;
    }

    public void handle(String msg) {
        if (msg.equals("Bye")) {
            this.print("Good bye. Have a nice day.");
            this.close();
        } else {
            this.print(msg);
        }
    }

    private void connect(String serverName, int serverPort) {
        this.print("Trying to connect to socket server ...");
        try {
            this.socket = new Socket(serverName, serverPort);

            this.open();

            this.print("Successfully connected to server at: " + serverName + ":" + serverPort);

            this.sendBtn.setEnabled(true);
            this.connectBtn.setEnabled(false);
            this.quitBtn.setEnabled(true);
        } catch (UnknownHostException e) {
            this.print("Host unknown: " + e.getMessage());
        } catch (IOException e) {
            this.print("Unexpected exception: " + e.getMessage());
        }
    }

    private void open() {
        try {
            this.streamOut = new PrintWriter(this.socket.getOutputStream(), true);
            this.socketClient = new SocketClientThread(this, this.socket);
        } catch (IOException e) {
            this.print("Error while opening output streams: " + e.getMessage());
        }
    }

    private void send() {
        try {
            this.streamOut.println(this.inputTF.getText());

            this.inputTF.setText("");
        } catch (Exception e) {
            this.print("Error while sending message: " + e.getMessage());
            this.print("Closing connection.");
            this.close();
        }
    }

    private void close() {
        try {
            if (this.streamOut != null) {
                this.streamOut.close();
            }
            if (this.socket != null) {
                this.socket.close();
            }
        } catch (IOException e) {
            this.print("Error while closing: " + e.getMessage());
        }
        this.socketClient.close();
        this.socketClient.stop();
    }

    private void print(String msg) {
        displayTA.append(msg + "\n");
    }
}
