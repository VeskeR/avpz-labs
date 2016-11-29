import java.applet.*;
import java.awt.*;
import java.io.*;
import java.net.*;

/**
 * Created by Andrew on 11/29/2016.
 */
public class ChatMultiServerApplet extends Applet {
    private ChatMultiServer server;

    private TextArea displayTA = new TextArea();
    private TextField portNumberTF = new TextField();
    private TextField inputTF = new TextField();
    private Button startBtn = new Button("Start");
    private Button sendBtn = new Button("Send all");
    private Button stopBtn = new Button("Stop");

    private int serverPort;

    private enum State {
        Idling,
        Running
    }

    public void init() {
        // Create panel with text field for port number input, 'start' & 'stop' buttons
        Panel keys = new Panel();
        keys.setLayout(new GridLayout(1, 3));
        keys.add(this.portNumberTF);
        keys.add(this.startBtn);
        keys.add(this.stopBtn);

        portNumberTF.setText("9000");

        // Create panel with controls at the bottom of window
        Panel south = new Panel();
        south.setLayout(new BorderLayout());
        south.add("West", keys);
        south.add("Center", this.inputTF);
        south.add("East", this.sendBtn);

        // Create title for applet
        Label title = new Label("Web Interface for Chat Server", Label.CENTER);
        title.setFont(new Font("Helvetica", Font.BOLD, 14));

        // Set layout for applet
        setLayout(new BorderLayout());
        this.add("North", title);
        this.add("Center", this.displayTA);
        this.add("South", south);

        this.serverPort = 9000;
        this.server = new ChatMultiServer(serverPort, this);

        this.setState(State.Idling);
    }

    public boolean action(Event e, Object o) {
        if (e.target == this.stopBtn) {
            this.stopServer();
        } else if (e.target == this.startBtn) {
            this.startServer();
        } else if (e.target == this.sendBtn) {
            this.send();
        }
        return true;
    }

    public void print(String message) {
        this.displayTA.append(message + "\n");
    }

    private void startServer() {
        this.print("Trying to open chat server ...");

        try {
            this.serverPort = Integer.parseInt(this.portNumberTF.getText());
            if (this.serverPort < 0 || this.serverPort > 65535) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            this.print("Error starting server: you have to specify correct port number.");
            this.serverPort = -1;
        }

        if (this.serverPort != -1) {
            this.server.setPortNumber(serverPort);
            Thread thread = new Thread(this.server);
            thread.start();
            this.setState(State.Running);
        }
    }

    private void stopServer() {
        this.server.stop();
        this.setState(State.Idling);
    }

    private void send() {
        String message = this.inputTF.getText();
        this.server.sendAll(message);
        this.inputTF.setText("");
    }

    private void setState(State state) {
        switch (state) {
            case Idling:
                this.portNumberTF.setEnabled(true);
                this.startBtn.setEnabled(true);
                this.stopBtn.setEnabled(false);
                this.inputTF.setEnabled(false);
                this.sendBtn.setEnabled(false);
                break;
            case Running:
                this.portNumberTF.setEnabled(false);
                this.startBtn.setEnabled(false);
                this.stopBtn.setEnabled(true);
                this.inputTF.setEnabled(true);
                this.sendBtn.setEnabled(true);
                break;
        }
    }
}
