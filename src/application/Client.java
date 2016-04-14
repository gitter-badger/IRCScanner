package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client {
    int                    messageCount = 0;
    private Socket         socket;
    private String         server;
    private int            port;
    private String         nick;
    private String         channel;
    private BufferedReader in;
    private PrintWriter    out;
    private IrcListener    listener;

    public Client(String server, String nick, String channel, int port, IrcListener listener) {
        this.server   = server;
        this.port     = port;
        this.nick     = nick;
        this.channel  = channel;
        this.listener = listener;
    }

    // connecte le client
    public void connect() throws IOException {
        this.socket = new Socket(this.server, this.port);
        this.in     = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out    = new PrintWriter(socket.getOutputStream(), true);
        this.send("NICK " + this.nick);
        this.send("USER " + this.nick + " 0 * :IRC.bot");
        new ClientReader().start();
    }

    public void send(String message) {
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {}

        if ((message.contains("VERSION")) && (message.contains("PRIVMSG"))) {
            if ((messageCount++ % 5 == 0) && (messageCount > 1)) {

                // évitons le "Message target change too fast."
                System.out.println("------------------------------------");
                System.out.println(String.valueOf(messageCount) + " msg -> sleep 10 sec...");
                System.out.println("------------------------------------");

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {}
            }
        }

        System.out.println("--->" + message);

        // send le message
        out.println(message);
    }

    // interface IrcListener
    public interface IrcListener {
        void listReceived(List<String> pseudos);

        void messageTargetTooFastReceived(String infos);

        void motdReceived();

        void noticeReceived(String pseudo, String version);

        void otherMessageReceived(String message);

        void pingReceived(String count);
    }


    // Reader qui écoute les messages entrants, parse, et envoie les events correspondant au listener
    private class ClientReader extends Thread {
        public ClientReader() {}

        @Override
        public void run() {
            List<String> listUNick = new ArrayList<String>();

            // tant qu'il y a des messages
            while (true) {

                // je lis le message
                String message = null;

                try {
                    message = in.readLine();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (message == null) {
                    break;
                }

                System.out.println("<---" + message);

                // ping
                if (message.contains("PING")) {
                    listener.pingReceived(message.split(":")[1]);

                    // motd
                } else if (message.contains("End of /MOTD command") || message.contains("End of message of the day")) {
                    listener.motdReceived();

                    // channel
                } else if (message.contains(channel)) {
                    String[] parts = message.split(" ");

                    if ((parts.length > 7) && parts[7].contains("list")) {
                        listener.listReceived(listUNick);
                    } else {
                        listUNick.add(parts[7]);
                    }
                }

                // too fast
                else if (message.contains("Message target change too fast. Please wait")) {
                    listener.messageTargetTooFastReceived(message.split(" ")[3]);

                    // notice
                } else if (message.contains("NOTICE") && message.contains("VERSION")) {
                    String pseudal      = message.substring(message.indexOf(":") + 1, message.indexOf("!"));
                    String partsVersion = message.substring(message.indexOf("VERSION") + 8);

                    listener.noticeReceived(pseudal, partsVersion);
                }

                // other
                else {
                    listener.otherMessageReceived(message);
                }
            }
        }
    }
}