import java.applet.*;
import java.awt.*;
import java.io.*;
import java.net.*;

/**
 * Created by Andrew on 11/29/2016.
 */
public class ChatMultiServerApplet extends Applet {
    private TextArea displayTA = new TextArea();
    private ChatMultiServer server;

    public void init() {
        // Set layout for applet
        setLayout(new BorderLayout());
        this.add("Center", this.displayTA);

        server = new ChatMultiServer(9000, this);
    }

    public void start() {
        server.start();
    }

    public void print(String message) {
        this.displayTA.append(message + "\n");
    }
}
