/**
 * Created by Andrew on 10/25/16.
 */
import java.io.*;
import java.net.*;
public class SocketServer {
    public static final int PORT = 2500;
    private static final int TIME_SEND_SLEEP = 100;
    private static final int COUNT_TO_SEND = 10;
    private int listenerCount = 0;
    private ServerSocket servSocket;

    public static void main(String[] args) {
        SocketServer server = new SocketServer();
        server.go();
    }
    public SocketServer() {
        try {
            servSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            System.err.println("Unable to open Server Socket : " + e.toString());
        }
    }
    public void go() {

        // Класс-поток для работы с подключившимся клиентом
        class Listener implements Runnable {
            private int id;
            Socket socket;
            public Listener(Socket aSocket){
                this.id = listenerCount++;
                socket = aSocket;
            }
            public void run(){
                try {
                    System.out.println("Listener " + this.id + " started");

                    System.out.println("Listener read: " + readString(socket.getInputStream()));

                    int count = 0;
                    OutputStream out = socket.getOutputStream();
                    OutputStreamWriter writer = new OutputStreamWriter(out);
                    PrintWriter pWriter = new PrintWriter(writer);
                    while (count < COUNT_TO_SEND) {
                        count++;
                        pWriter.print(((count > 1) ? "," : "") + "Say" + count);
                        sleeps(TIME_SEND_SLEEP);
                    }
                    pWriter.close();
                    System.out.println("Listener " + this.id + " finished");
                } catch (IOException e){
                    System.err.println("Exception : " + e.toString());
                }
            }
        }

        // Основной поток, циклически выполняющий метод accept()
        System.out.println("Server started");
        while(true){
            try{
                Socket socket = servSocket.accept();
                Listener listener = new Listener(socket);
                Thread thread = new Thread(listener);
                thread.start();
            }catch(IOException e){
                System.err.println("IOException : " + e.toString());
            }
        }
    }

    public void sleeps(long time) {
        try{
            Thread.sleep(time);
        }catch(InterruptedException e){
        }
    }

    private String readString(InputStream in) throws IOException {
        // буффер данных в 64 килобайта
        byte buf[] = new byte[64*1024];
        // читаем 64кб от клиента, результат - кол-во реально принятых данных
        int r = in.read(buf);

        // создаём строку, содержащую полученную от клиента информацию
        String data = new String(buf, 0, r);

        return data;
    }
}
