package Hermes;

import java.io.*;

import java.net.*;


/**
 * <p>Title: MasterMessageListener.java </p>
 * <p>Description: Thread that will listen for incoming messages to the master server.</p>
 * @author Ola Laleye, Mike McNamara, Anthony Pitluga
 * @version 2.3
 */
public class MasterMessageListener extends Thread {
    private MasterAgentInterface mai;
    MasterMessageHandler mhandler;
    private boolean keepRunning = true;
    private RegisterShipperCarrierDatabaseInterface regShipCarDB;

    /**
    * socket that will listen for connections and spawn new sockets once new
    * connections are made
    */
    private ServerSocket server;

    /**
    * Constructor, will make a server socket and run the thread to listen on it.
    * @param portNo port to listen to.
    * @param rscDB the registerShipperCarrier database interface
    * @param mai  instance of MasterAgentInterface for backwards communication
    */
    public MasterMessageListener(int portNo,
        RegisterShipperCarrierDatabaseInterface rscDB, MasterAgentInterface mai) {
        try {
            this.mai = mai;
            regShipCarDB = rscDB;
            server = new ServerSocket(portNo);
            start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
    * Constructor, using given server socket, will run the thread to listen on it.
    * @param ss  given server socket
    * @param rscDB  the registerShipperCarrier database interface
    * @param mai  instance of MasterAgentInterface for backwards communication
    */
    public MasterMessageListener(ServerSocket ss,
        RegisterShipperCarrierDatabaseInterface rscDB, MasterAgentInterface mai) {
        try {
            this.mai = mai;
            regShipCarDB = rscDB;
            server = ss;
            start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
    * Will stop the loop for the thread.
    */
    public void stopThread() {
        keepRunning = false;
    }

    /**
    * Sets the instance of MasterAgentInterface that created this listener
    * @param inter  the MasterAgentInterface that created this listener
    */
    public void setAgent(MasterAgentInterface inter) {
        mai = inter;
    }

    /**
    * The message listener thread's run method. Will listen for connections and
    * spawn new threads once the connections are made.
    */
    public void run() {
        try {
            while (keepRunning) {
                Socket incoming;

                //once a new connection is made assign the socket to incoming
                try {
                    incoming = server.accept();
                    new MasterMessageHandler(incoming, regShipCarDB, mai);
                } catch (IOException ex) {
                    System.err.println("Accept failed: " + ex);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
    * Exit application
    */
    public void myExit() {
        System.exit(3); //exit listener
    }
}
