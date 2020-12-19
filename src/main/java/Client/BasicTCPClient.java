package Client;

import Utilities.tcpIOHandlerThread;
import java.lang.*;
import java.io.*;
import java.net.*;


public class BasicTCPClient {
    private final int PORT;
    private final String ipv4;
    private final String hostname;
    private tcpIOHandlerThread thread;

    public BasicTCPClient(int PORT, String ipv4, String hostname) {
        this.PORT = PORT;
        this.ipv4 = ipv4;
        this.hostname = hostname;
    }
    public void BuildConnection() {
        try {
            var sock = new Socket(hostname, PORT);
            thread  = new tcpIOHandlerThread(sock);
        }
        catch (IOException e) {
            System.out.println("Client Socket connect failed");
        }
    }

    public void SendMessage(String output) {
        try {
            thread.sendMessage(output);
        }
        catch (IOException e) {
            System.out.println("Send Message failed, maybe server is not working");
        }
    }

    public String ReceiveMessage() {
        try {
            return thread.receiveMessage();
        }
        catch (IOException e) {
            System.out.println("Receive Message failed");
        }
        return "";
    }

    public void end() {
        thread.end();
    }

}