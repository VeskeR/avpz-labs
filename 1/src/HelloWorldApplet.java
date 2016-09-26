/**
 * Created by Andrew on 9/26/16.
 */
import java.applet.*;
import java.awt.*;

public class HelloWorldApplet extends Applet {
    private String msg;

    public void init() {
        msg = "Hello World! It's my first Java Applet application. © Andrey Bulat";
    }

    public void paint(Graphics g) {
        g.drawString(msg, 25, 50);
    }
}
