package Server;

import java.io.*;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import Utilities.*;

// code-writing Tips: Do not forget to initialize ArrayList<> before you use it!!!

public class BasicTCPServer {
    private final int PORT;
    private final int playerNum;
    private ArrayList<tcpIOHandlerThread> threads;
    private String[] readBuffer;
    private boolean running;
    private final long receiveTimeDelay = 3000;


    public BasicTCPServer(int PORT, int playerNum) throws IOException {
        this.PORT = PORT;
        this.playerNum = playerNum;
        this.threads = new ArrayList<>();
        this.running = true;
        this.readBuffer = new String[playerNum];
        Arrays.fill(readBuffer,"");
    }

    public void CreateSockets() throws IOException {
        var ss = new ServerSocket(PORT);
        try {
            System.out.println("server is running...");
            for(int i = 0; i < playerNum; ++i) {
                var sock = ss.accept();
                System.out.println("connected from " + sock.getRemoteSocketAddress());
                var t = new tcpIOHandlerThread(sock, i);
                new Thread(new HeartBeatWatchDog(t)).start();
                threads.add(t);
            }
        } catch(IOException e) {
            System.out.println("TCP Server Creation error");
        }
    }

    public void sendMessage(String input) {
        try {
            for (var thread : this.threads) {
                if(thread.isRunning())
                    //System.out.println("Send message to " + thread.getName());
                    thread.sendMessage(input);
            }
            for (var thread : this.threads) {
                thread.join();
            }
            //System.out.println("Send message done ");
        }
        catch(IOException e) {
            System.out.println("Send Message Failure");
        }
        catch(InterruptedException e) {
            System.out.println("Send Message interrupted");
        }
    }

    public ArrayList<String> receiveMessage() {
        //System.out.println("checkhere");
        while(!isReadyForRead()) {
            try {
                Thread.sleep(100);
            }
            catch(InterruptedException e) {
                System.out.println("thread interrupted");
                e.printStackTrace();
                this.end();
            }
        }
        var ret = new ArrayList<String>();
        for(int i = 0; i < playerNum; ++i) {
            if(!getBuffer(i).equals("")) {
                ret.add(getBuffer(i));
                writeBuffer("", i);
            }
        }
        return ret;
    }

    /*
     * heart beat is written to make sure network status is good
     * if receive time between two packages is longer than 3s, the connection will be closed
     */
    class HeartBeatWatchDog implements Runnable {
        private long lastReceiveTime;
        private tcpIOHandlerThread thread;
        private long checkDelay = 10;

        public HeartBeatWatchDog(tcpIOHandlerThread thread) {
            this.thread = thread;
            lastReceiveTime = System.currentTimeMillis();
        }

        public void run() {
            while (running && thread.isRunning()) {
                //if(thread.getName().equals("Thread-0")) {
                    //System.out.println(System.currentTimeMillis() - lastReceiveTime);
                //}
                if (System.currentTimeMillis() - lastReceiveTime > receiveTimeDelay) {
                    System.out.println("Request time out for thread " + thread.getName());
                    thread.end();
                } else {
                    try {
                        if (thread.isReadable()) {
                            String rec = thread.receiveMessage();
                            this.lastReceiveTime = System.currentTimeMillis();
                            if (rec.equals("@Active")) {
                                //System.out.println("@Active Received from " + thread.getName());
                                thread.sendMessage("@Received");
                            } else {
                                System.out.println(rec + " received");
                                writeBuffer(rec, thread.index);
                            }
                        } else {
                            thread.sleep(checkDelay);
                        }
                    } catch (IOException e) {
                        System.out.println("Server: receive IO failure!");
                        e.printStackTrace();
                        thread.end();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        thread.end();
                    }
                }
            }
        }
    }

    public void end() {
        for (var thread : this.threads) {
            if(thread.isRunning())
                thread.end();
        }
        running = false;
    }

    private synchronized void writeBuffer(String recStr, int i) {
        readBuffer[i] = recStr;
    }

    private synchronized String getBuffer(int i) {
        return readBuffer[i];
    }
    private boolean isReadyForRead() {
        for(int i = 0; i < playerNum; ++i) {
            if(threads.get(i).isRunning() && getBuffer(i).equals("")) {
                return false;
            }
        }
        return true;
    }

    public int getRunningNum() {
        return threads.stream().map(n->n.isRunning() ? 1 : 0).reduce(0,(sum,n)-> sum + n);
    }

    public static void main(String[] args) throws IOException {
        var a = new BasicTCPServer(6666,3);
        a.CreateSockets();
        System.out.println("hello this is your server");
        a.sendMessage("hello this is your server");
        var str = a.receiveMessage();
        System.out.println("here is received String" + str);
        a.sendMessage("hello again");
        var str2 = a.receiveMessage();
        System.out.println("here is received String" + str2);
        a.end();
    }
}
