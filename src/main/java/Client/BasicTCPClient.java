package Client;

import Utilities.tcpIOHandlerThread;
import java.lang.*;
import java.io.*;
import java.net.*;


public class BasicTCPClient {
    private final int PORT;
    private final String hostname;
    private tcpIOHandlerThread thread;

    public BasicTCPClient(int PORT, String hostname) {
        this.PORT = PORT;
        this.hostname = hostname;
    }
    public void buildConnection() {
        try {
            var sock = new Socket(hostname, PORT);
            this.thread  = new tcpIOHandlerThread(sock);
        }
        catch (IOException e) {
            System.out.println("Client Socket connect failed");
        }
    }

    public void sendMessage(String output) {
        try {
            thread.sendMessage(output + '\n');
        }
        catch (IOException e) {
            System.out.println("Send Message failed, maybe server is not working");
        }
    }

    public String receiveMessage() {
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