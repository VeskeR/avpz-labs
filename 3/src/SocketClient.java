/**
 * Created by Andrew on 10/25/16.
 */
import java.io.*;
import java.net.*;

public class SocketClient implements Runnable{
    public static final int PORT = 2500;
    public static final String HOST = "localhost";
    public static final int CLIENTS_COUNT = 5;
    public static final int READ_BUFFER_SIZE = 10;

    private String name = null;

    public static void main(String[] args) {
        String name = "name";
        for(int i=1; i<=CLIENTS_COUNT; i++){
            SocketClient client = new SocketClient(name+i);
            Thread thread = new Thread(client);
            thread.start();
        }
    }

    public SocketClient(String name) {
        this.name = name;
    }

    public void run() {
        char[] readed = new char[READ_BUFFER_SIZE];
        StringBuffer strBuff = new StringBuffer();
        try{
            Socket socket = new Socket(HOST, PORT);

            OutputStream out = socket.getOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(out);

            InputStream in = socket.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);

            PrintWriter pWriter = new PrintWriter(writer);
            pWriter.print("Hello from client " + this.name);

            while (true) {
                int count = reader.read(readed, 0, READ_BUFFER_SIZE);
                if(count==-1)break;
                strBuff.append(readed, 0, count);
                Thread.yield();
            }

            pWriter.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("client " + this.name + "   read : " + strBuff.toString());
    }
}
