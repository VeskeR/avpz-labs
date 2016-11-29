import java.applet.Applet;
import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Andrew on 11/27/2016.
 */
public class ChatClientApplet extends Applet {
    private Socket socket;
    private PrintWriter streamOut;
    private ChatClientThread socketClient;

    private TextArea displayTA = new TextArea();
    private TextField inputTF = new TextField();
    private TextField portNumberTF = new TextField();
    private Button sendBtn = new Button("Send");
    private Button connectBtn = new Button("Connect");
    private Button quitBtn = new Button("Quit");

    private String serverName = "127.0.0.1";
    private int serverPort;

    private enum State {
        Idling,
        Connected
    }

    public void init() {
        // Create panel with 'quit', 'connect' buttons & text field for port number input
        Panel keys = new Panel();
        keys.setLayout(new GridLayout(1, 3));
        keys.add(this.portNumberTF);
        keys.add(this.connectBtn);
        keys.add(this.quitBtn);

        portNumberTF.setText("9000");

        // Create panel with controls at the bottom of window
        Panel south = new Panel();
        south.setLayout(new BorderLayout());
        south.add("West", keys);
        south.add("Center", this.inputTF);
        south.add("East", this.sendBtn);

        // Create title for applet
        Label title = new Label("Simple Chat via sockets", Label.CENTER);
        title.setFont(new Font("Helvetica", Font.BOLD, 14));

        // Set layout for applet
        setLayout(new BorderLayout());
        this.add("North", title);
        this.add("Center", this.displayTA);
        this.add("South", south);

        this.setState(State.Idling);
    }

    public boolean action(Event e, Object o) {
        if (e.target == this.quitBtn) {
            this.inputTF.setText("Bye");
            this.send();
        } else if (e.target == this.connectBtn) {
            this.connect();
        } else if (e.target == this.sendBtn) {
            this.send();
            this.inputTF.requestFocus();
        }
        return true;
    }

    public void handle(String message) {
        if (message.equals("Bye")) {
            this.print("Good bye. Have a nice day.");
            this.closeConnection();
        } else {
            this.print(message);
        }
    }

    private void connect() {
        this.print("Trying to connect to chat server ...");

        try {
            this.serverPort = Integer.parseInt(this.portNumberTF.getText());
            if (this.serverPort < 0 || this.serverPort > 65535) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            this.print("Error while connecting to server: you have to specify correct port number to connect to.");
            this.serverPort = -1;
        }

        if (this.serverPort != -1) {
            try {
                this.socket = new Socket(this.serverName, this.serverPort);

                this.open();

                this.print("Successfully connected to server at: " + this.serverName + ":" + this.serverPort);

                this.setState(State.Connected);
            } catch (UnknownHostException e) {
                this.print("Host unknown: " + e.getMessage());
            } catch (Exception e) {
                this.print("Unexpected exception: " + e.getMessage());
            }
        }
    }

    private void open() {
        try {
            this.streamOut = new PrintWriter(this.socket.getOutputStream(), true);
            this.socketClient = new ChatClientThread(this, this.socket);
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
            this.closeConnection();
        }
    }

    public void closeConnection() {
        this.print("Closing connection.");

        try {
            if (this.streamOut != null) {
                this.streamOut.close();
                this.streamOut = null;
            }
            if (this.socket != null) {
                this.socket.close();
                this.socket = null;
            }

            this.socketClient.close();
            this.socketClient = null;
        } catch (IOException e) {
            this.print("Error while closing: " + e.getMessage());
        }

        this.print("Connection successfully closed.");
        this.setState(State.Idling);
    }

    public void print(String message) {
        displayTA.append(message + "\n");
    }

    private void setState(State state) {
        switch (state) {
            case Idling:
                this.portNumberTF.setEnabled(true);
                this.connectBtn.setEnabled(true);
                this.quitBtn.setEnabled(false);
                this.inputTF.setEnabled(false);
                this.sendBtn.setEnabled(false);
                break;
            case Connected:
                this.portNumberTF.setEnabled(false);
                this.connectBtn.setEnabled(false);
                this.quitBtn.setEnabled(true);
                this.inputTF.setEnabled(true);
                this.sendBtn.setEnabled(true);
                break;
        }
    }
}
