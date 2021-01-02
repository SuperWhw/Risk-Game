package Client;

import Server.BasicTCPServer;
import Utilities.tcpIOHandlerThread;
import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;


public class BasicTCPClient {
    private final int PORT;
    private final String hostname;
    private tcpIOHandlerThread thread;
    private long lastSendTime;
    private boolean isReceivedReady = false;
    private String readBuffer;

    public BasicTCPClient(int PORT, String hostname) {
        this.PORT = PORT;
        this.hostname = hostname;
    }

    public void buildConnection() {
        try {
            var sock = new Socket(hostname, PORT);
            this.thread = new tcpIOHandlerThread(sock, 0);
            this.lastSendTime = System.currentTimeMillis();
            new Thread(new HeartBeaterSender()).start();
            new Thread(new HeartBeaterReceiver()).start();
        } catch (IOException e) {
            System.out.println("Client Socket connect failed");
        }
    }

    public void sendMessage(String output) {
        try {
            thread.sendMessage(output);
        } catch (IOException e) {
            System.out.println("Send Message failed, maybe server is not working");
            e.printStackTrace();
            this.end();
        }
    }

    public String receiveMessage() {
        //System.out.println("waiting for answer");
        while (true) {
            if (getReady()) {
                break;
            }
            if (!thread.isRunning()) {
                return null;
            }
        }
        setReady(false);
        return readBuffer;
    }

    /*
     * send heart beat to server every 2s
     * The thread is to make sure server is available all the time
     */
    class HeartBeaterSender implements Runnable {
        long checkDelay = 10;
        long keepAliveDelay = 2000;

        public void run() {
            while (thread.isRunning()) {
                //System.out.println(System.currentTimeMillis() - lastSendTime);
                if (System.currentTimeMillis() - lastSendTime > keepAliveDelay) {
                    BasicTCPClient.this.sendMessage("@Active");
                    //System.out.println("sendHeartBeat: @Active 2s");
                    lastSendTime = System.currentTimeMillis();
                } else {
                    try {
                        Thread.sleep(checkDelay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        BasicTCPClient.this.end();
                    }
                }
            }
        }
    }

    /*
     * receive committed heart beat message from server
     * If not received within 3s, it will automatically disconnected
     */
    class HeartBeaterReceiver implements Runnable {
        private final long checkDelay = 10;
        private long lastReceiveTime;
        private final long receiveTimeDelay = 60000;

        public HeartBeaterReceiver() {
            super();
            lastReceiveTime = System.currentTimeMillis();
        }

        public void run() {
            while (thread.isRunning()) {
                //System.out.println(System.currentTimeMillis() - lastReceiveTime);
                if (System.currentTimeMillis() - lastReceiveTime > receiveTimeDelay) {
                    System.out.println("You've lost your connection to server");
                    thread.end();
                } else {
                    try {
                        if (thread.isReadable()) {
                            String rec = thread.receiveMessage();
                            lastReceiveTime = System.currentTimeMillis();
                            //System.out.println(rec + "received");
                            if (!rec.equals("@Received")) {
                                //System.out.println("set ready true");
                                readBuffer = rec;
                                setReady(true);
                            }
                        } else {
                            Thread.sleep(checkDelay);
                        }
                    } catch (IOException e) {
                        System.out.println("receive IO failure!");
                        e.printStackTrace();
                        BasicTCPClient.this.end();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        BasicTCPClient.this.end();
                    }
                }
            }
        }
    }

    public void end() {
        thread.end();
    }

    private synchronized void setReady(boolean value) {
        isReceivedReady = value;
    }

    private synchronized boolean getReady() {
        return isReceivedReady;
    }

    public boolean isRunning() {
        return thread.isRunning();
    }
    public static void main(String[] args) throws IOException {
        var a = new BasicTCPClient(6666,"localhost");
        a.buildConnection();
        System.out.println("BuildConnect");
        var str = a.receiveMessage();
        System.out.println(str);
        var scanner = new Scanner(System.in);
        a.sendMessage(scanner.nextLine());
        var str2 = a.receiveMessage();
        System.out.println(str2);
        a.sendMessage(scanner.nextLine());
        a.end();
    }
}