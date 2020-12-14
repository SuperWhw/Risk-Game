package Client;
import java.net.*;

public class BasicUDPClient {
    private DatagramSocket ds;
    private Player player;
    private int port;
    private InetAddress serverIP;
    private InetAddress clientIP;
    private int timeOut = 1000;
    public BasicUDPClient(Player player) {
        try {
            this.player = player;
            this.port = 4000;
            this.clientIP = InetAddress.getByName("localhost");
            this.ds = new DatagramSocket(this.port);
            byte[] readBuffer = new byte[1024];
            byte[] writeBuffer = new byte[1024];
            this.ds.setSoTimeout(timeOut);
            System.out.println("ds build successfully");
        }
        catch(java.net.SocketException e) {
            System.out.println("Connection error");
        }
        catch(java.net.UnknownHostException e) {
            System.out.println("Couldn't find client localhost");
        }
    }
    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
        try {
            this.ds.setSoTimeout(this.timeOut);
        }
        catch(java.net.SocketException e) {
            System.out.println("set failure");
        }
    }
    public void printClient() {
        System.out.println("Player name is " + this.player.getName());
        System.out.println("TimeOut is " + this.timeOut);
        System.out.println("Client host is " + this.clientIP);
    }
    public void sendMessage(String message) {
        ds.connect(serverIP, 6666);
        DatagramPacket packet = new DatagramPacket(message.getBytes(), message.getBytes().length);
        try {
            ds.send(packet);
        }
        catch (java.io.IOException e){
            System.out.println("Send Message IO Failure");
        }
    }
    public String receiveMessage() {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        String resp = new String(packet.getData(), packet.getOffset(), packet.getLength());
        return resp;
    }
    public void closeConnect() {
        ds.disconnect();
    }
    public static void main(String[] args) {
        Player player = new Player("TCL");
        BasicUDPClient client = new BasicUDPClient(player);
        client.printClient();
    }
}
