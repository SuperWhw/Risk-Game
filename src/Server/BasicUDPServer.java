package Server;

import javax.xml.crypto.Data;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

public class BasicUDPServer {
    private DatagramSocket ds;
    private int port;
    private byte[] buffer;
    private DatagramPacket packet;
    public BasicUDPServer(int port) {
        try {
            this.ds = new DatagramSocket();
            this.buffer = new byte[1024];
            this.port = port;
            this.packet = new DatagramPacket(buffer, buffer.length);
        }
        catch (java.net.SocketException e){
            System.out.println("Socket Build Exeception");
        }
    }
    public String receiveMessage() {
        try {
            ds.receive(this.packet);

        }
        catch(java.io.IOException e) {
            System.out.println("Server receive error");
        }
        finally {
            return "";
        }
    }
    public void sendMessage(String message) {
        try {
            byte[] data = message.getBytes(StandardCharsets.UTF_8);
            this.packet.setData(data);
            this.ds.send(this.packet);
        }
        catch(java.io.IOException e){
            System.out.println("Server Send Data error");
        }
    }
    public void closeConnection() {
        this.ds.close();
    }
}
