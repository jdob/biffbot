import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class BiffBot {

    private String host;
    private int port;
    private String nick;
    private String name;

    private PrintWriter out;
    private BufferedReader in;

    private List<BiffPlugin> plugins = new ArrayList<BiffPlugin>();

    public BiffBot(String host, int port, String nick, String name) {
        this.host = host;
        this.port = port;
        this.nick = nick;
        this.name = name;
    }

    public void connect() throws Exception {
        Socket socket = new Socket(this.host, this.port);
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        send("NICK " + this.nick + "\r\n");
        send("USER " + this.nick + " 0 * :" + this.name + "\r\n");

        Thread listenThread = new Thread(new Listener(), "BiffListener");
        listenThread.start();
    }

    public void join(String channel) throws Exception {
        send("JOIN " + channel + "\r\n");
    }

    public void send(String message) {
        out.print(message);
        out.flush();
    }

    public void addPlugin(BiffPlugin plugin) {
        plugins.add(plugin);
    }

    public void removePlugin(BiffPlugin plugin) {
        plugins.remove(plugin);
    }

    private String read() throws Exception {
        String data = in.readLine();
        System.out.println(data);
        return data;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getNick() {
        return nick;
    }

    public String getName() {
        return name;
    }

    public PrintWriter getOut() {
        return out;
    }

    private class Listener implements Runnable {

        public void run() {

            for (; ;) {
                String data;
                try {
                    data = read();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    break;
                }

                if (data.startsWith("PING")) {
                    send("PONG\r\n");
                }

                BiffMessage message = new BiffMessage(BiffBot.this, data);

                for (BiffPlugin plugin : plugins) {
                    try {
                        plugin.message(message);
                    }
                    catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        }

    }

    public static void main(String[] args) {
        try {
            BiffBot bot = new BiffBot("localhost", 6667, "biff", "BiffBot");
            bot.connect();
            bot.join("#biff");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
