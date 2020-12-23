package Utilities;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/*

Thread for read/write between TCP server and client

It provides interface to BasicTCPServer and BasicTCPClient

*/

public class tcpIOHandlerThread extends Thread {
    Socket sock;
    BufferedWriter writer;
    BufferedReader reader;

    public tcpIOHandlerThread(Socket sock) {
        this.sock = sock;
        try {
            this.reader = new BufferedReader(new InputStreamReader(this.sock.getInputStream(), StandardCharsets.UTF_8));
            this.writer = new BufferedWriter(new OutputStreamWriter(this.sock.getOutputStream(), StandardCharsets.UTF_8));
        }
        catch (IOException e) {
            System.out.println("Server read/write IO exception");
        }
    }

    public void sendMessage(String inputStr) throws IOException {
        writer.write(inputStr);
        writer.flush();
    }

    public String receiveMessage() throws IOException {
        System.err.println("readline1");
        return reader.readLine();
    }

    public void end() {
        try {
            this.sock.close();
            this.writer.close();
            this.reader.close();
        } catch (IOException e) {
            System.out.println("sock close IO failure");
        }
    }
}