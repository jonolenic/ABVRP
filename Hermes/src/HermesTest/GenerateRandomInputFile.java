package HermesTest;

import java.io.*;


/**
 * <p>Title: GenerateRandomInputFile.java </p>
 * <p>Description: Generates a random problem file of shipments, under the set
 * constraints defined by the constant values, to be solved by the system.  </p>
 * @author Ola Laleye, Mike McNamara, Anthony Pitluga
 * @version 2.3
 */
public class GenerateRandomInputFile {
    //information for the shipment file
    final int NUM_SHIPS = 100;
    final int MIN_DEMAND = 10;
    final int MAX_DEMAND = 70;
    final int MIN_SERV_TIME = 1;
    final int MAX_SERV_TIME = 10;
    final int LATEST_TIME = 240;
    final float MAX_X = 70;
    final float MAX_Y = 80;

    //information for depots
    final int MAX_DIST = 230;
    final int MAX_CAP = 200;
    String junk = "0";

    /**
 * Constructor - starts the writing of the problem file
 */
    public GenerateRandomInputFile() {
        writeShipmentFile("R_" + NUM_SHIPS + ".txt");
    }

    /**
 * Generates a problem file of randomly generated shipments.
 * @param fileName  name of problem file
 */
    public void writeShipmentFile(String fileName) {
        PrintWriter pw = null;

        try {
            //create the file and open a writer to it
            File file = new File(fileName);
            file.createNewFile();
            pw = new PrintWriter(new FileOutputStream(fileName));

            //print header and file information
            pw.println("# X Dist  Y Dist  Num Nodes");
            pw.println("" + MAX_X + "\t" + MAX_Y + "\t" + NUM_SHIPS);

            //print shipment information
            pw.println("# X\tY\tDEMAND\tNODE\tDIST\tTHETA\tEAR\tLAT\tSERVICE");

            for (int i = 0; i < NUM_SHIPS; i++) {
                //calculate random information for the shipment
                float x = (float) (Math.random() * MAX_X);

                //make x randomly + or -
                x = x * (float) Math.pow(-1, Math.round(Math.random()));
                x = roundto(x, 2);

                float y = (float) (Math.random() * MAX_Y);

                //make y randomly + or -
                y = y * (float) Math.pow(-1, Math.round(Math.random()));
                y = roundto(y, 2);

                int demand = MIN_DEMAND + (int) (Math.random() * MAX_DEMAND);
                int ear = (int) (Math.random() * LATEST_TIME);
                int serv = MIN_SERV_TIME +
                    (int) (Math.random() * MAX_SERV_TIME);
                int lat = ear + serv;

                //print line for shipment
                pw.println(x + "\t" + y + "\t" + demand + "\t" + (i + 1) +
                    "\t" + junk + "\t" + junk + "\t" + ear + "\t" + lat + "\t" +
                    serv);
            }

            //print the constraints
            pw.println("# MAXIMUM DISTANCE");
            pw.println(MAX_DIST);
            pw.println("# VEHICLE CAPACITY");
            pw.println(MAX_CAP);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            pw.close();
        }
    }

    /**
 * Rounds the given number to the given decimal places
 * @param num  the given number
 * @param places  the given decimal places
 * @return float  the rounded number
 */
    private float roundto(float num, int places) {
        num = num * (float) Math.pow(10, places);
        num = Math.round(num);
        num = num / (float) Math.pow(10, places);

        return num;
    }

    /**
 * Driver method for the random input file generator.
 * @param args command line arguments - none used
 */
    public static void main(String[] args) {
        new GenerateRandomInputFile();
    }
}
