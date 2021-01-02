package Utilities;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

/*
* Thread for read/write between TCP server and client
* It provides interface to BasicTCPServer and BasicTCPClient
*/

public class tcpIOHandlerThread extends Thread {
    Socket sock;
    BufferedWriter writer;
    BufferedReader reader;
    public final int index;
    boolean running = false;

    public tcpIOHandlerThread(Socket sock, int index) {
        this.sock = sock;
        this.index = index;
        try {
            this.reader = new BufferedReader(new InputStreamReader(this.sock.getInputStream(), StandardCharsets.UTF_8));
            this.writer = new BufferedWriter(new OutputStreamWriter(this.sock.getOutputStream(), StandardCharsets.UTF_8));
            this.running = true;
        }
        catch (IOException e) {
            System.out.println("Server read/write IO exception");
        }
    }

    public void sendMessage(String inputStr) throws IOException {
        try {
            writer.write(inputStr + '\n');
            writer.flush();
        }
        catch(SocketException e) {
            System.out.println("Client/Server disconnected");
            this.end();
        }
    }

    public String receiveMessage() throws IOException {
        return reader.readLine();
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isReadable() throws IOException, SocketException {
        try {
            return sock.getInputStream().available() > 0;
        }
        catch(SocketException e) {
            System.out.println("server disconnected");
            this.end();
            return false;
        }
    }

    public void end() {
        try {
            this.sock.close();
            this.writer.close();
            this.reader.close();
            this.running = false;
        } catch (IOException e) {
            System.out.println("sock close IO failure");
        }
    }
}