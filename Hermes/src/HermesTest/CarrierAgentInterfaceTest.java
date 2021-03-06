package HermesTest;

import Hermes.*;

import Hermes.HermesGlobals.*;

import junit.framework.*;

import junit.runner.BaseTestRunner;

import java.io.*;

import java.net.*;

import javax.swing.*;
import java.sql.*;

/**
 * <p>Title: CarrierAgentInterfaceTest.java </p>
 * <p>Description: Tests the carrier agent interface class.</p>
 * @author Ola Laleye, Mike McNamara, Anthony Pitluga
 * @version 2.3
 */
public class CarrierAgentInterfaceTest extends TestCase {
    private Message addressMsg;
    private Message registerMsg;
    private Socket socket;
    private BufferedReader in;
    private ThreadedClient tc;
    private JTextField jtfCarCode = new JTextField("2020");
    private JTextField jtfCarName = new JTextField("UNITED-UPS");
    String carIP;
    CarrierAgentInterface carInterface;
    final private int MIN_SOCKET_NUM = 49152;

    /**
 * Constructor that passes class name to parent class
 * @param name test class name
 */
    public CarrierAgentInterfaceTest(String name) {
        super(name);
    }

    /**
 * Suite that runs all the test in order wanted.
 * @param test class name
 * @return
 */
    public static Test suite() {
        TestSuite suite = new TestSuite("CarrierAgentInterfaceTest");
        suite.addTest(new CarrierAgentInterfaceTest(
                "testRegisterCarrierWithMasterServer"));

        return suite;
    }

    /**
 * runs before every test is perfomed.
 */
    protected void setUp() {
        System.out.println("Entering setUp");

        try {
            addressMsg = new Message();
            registerMsg = new Message();
            socket = null;
            in = null;

            try {
                carIP = InetAddress.getLocalHost().toString().substring(InetAddress.getLocalHost()
                                                                                   .toString()
                                                                                   .indexOf("/") +
                        1);
            } catch (UnknownHostException ex) {
                System.err.print("Error setting Carrier IP: " + ex);
            }

            carInterface = new CarrierAgentInterface(jtfCarCode.getText(), carIP);
        } catch (Exception ex) {
        }

        System.out.println("Exiting setUp");
    }

    /**
 * executes automatically after each test. Close socket connections
 */
    protected void tearDown() {
        System.out.println("Entering tearDown");

        System.out.println("Exiting tearDown");
    }

    /**
 * tests to see if the message being passed to the master server is being
 * constructed properly also test to see if a portnumber is returned
 */
    public void testRegisterCarrierWithMasterServer() {
        int portNum;
        new Master(); //start master to receive register message
        portNum = carInterface.registerCarrierWithMasterServer("car", "car",
                "192.168.1.1");
        assertTrue(portNum > MIN_SOCKET_NUM);
        assertFalse(portNum <= MIN_SOCKET_NUM);
    }

    public void testUnregisterWithMasterServer() {
        //query database to see if carrier still inside after the unregister
    }
}
