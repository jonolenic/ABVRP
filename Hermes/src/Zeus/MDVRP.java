package Zeus;

import java.io.*; //input-output java package

import java.util.*; //for string tokenizer


/**
 * <p>Title: Zeus - Unified Object Oriented Model for Routeing and Schdeduling Problems</p>
 * <p>Description:  The MDVRP, Multi-Depot Vehicle Routing Problem, consists of more than one
 *    depot used in solving a routing problem. A set of customers are to be serviced from
 *    multiple depots using trucks that can be used from any depot. The objective of the problem
 *    is to service the customers by first minimizing the total number of trucks required from
 *    each depot and second minimizing the total distance traveled by the trucks. The constraint
 *    is not to exceed the total capacity of the truck. </p>
 * <p>Reference: Put the name(s) of multi-depot papers </p>
 * <p>Copyright:(c) 2001-2003<p>
 * <p>Company:<p>
 * @author Sam R. Thangiah
 * @version 1.0
 */
public class MDVRP {
    int m = 0; //maximum capacity of vehicle
    int n = 0; //maximum capacity of vehicle
    int t = 0; //maximum capacity of vehicle
    int D = 0; //maximum capacity of vehicle
    int Q = 0; //maximum capacity of vehicle

    /** Customers read in from the data file are loaded into the ShipmentLinkedList
 *  instance named mainShipments. The mainShipments instance holds all of the
 *  shipments that are avaialble for routing. This list should not be used
 *  for manipulating the problem being solved. The list can be updated if a
 *  customer is added, deleted or edited.
 */
    ShipmentLinkedList mainShipments = new ShipmentLinkedList();

    /** The DepotLinkedList with instance mainDepots forms the root node that keeps
 *  track of the solution that has been obtained for the problem. From the mainDepots
 *  one can obtain the list of trucks and for each truck the list of customers for
 *  the problem.
 */
    DepotLinkedList mainDepots = new DepotLinkedList();

    // variables used in exchanges
    boolean Tabu_Or_Not_To_Tabu;
    int optType;
    int totalInterExchanges;
    int totalIntraExchanges;
    int totalExchanges;
    int maxLoop;

    /**
 * Constructor for the MDVRP problem. Reads in the data from the file.
 * Creates the required number of depots specified in the date file.
 * Allocates the customers to the trucks.
 * Performs the local optimization
 * @param fileName Name of the file to be used for the problem
 */
    public MDVRP(String fileName) {
        boolean isDiagnostic = false;
        Shipment tempShip;
        Depot thisDepot;
        int type;
        int depotNo;
        int countAssignLoop;
        boolean status;
        String outputFileName;

        //read in the MDVRP data from the provided filename
        readMDVRPTokenDataFromFile(fileName); //read the data as token

        /* The customers and depots are read into the mainShipments
   shipment linked list. The createDepots method in the
   mainDepots instance creates a creates a linked list of
   depots using the depot information in the mainShipments linked
   list. The linked list is accessible through the mainDepots instance.
   Note: If there is only one depot for the problem, then the linked
   list will be created with only one node.
 */
        mainDepots.createDepots(mainShipments);

        /* mainShipments.displayForwardKeyList();
   mainDepots.displayList();
   mainDepots.displayAllList(); */
        /* Get the shipment that is closest to a depot with respect to a
   criteria
   The method for assinging customers to the depots is as follows. As there are
   a fixed number of depots, start with the first depot and find closest customer to the
   depot and insert that customer into a truck allocated to the depot if it does not exceed
   the constraints.  If it does, then create another truck from the same depot and add the customer
   to it.
   The next step is to go to the next depot and allocate the a customer closest to that depot to
   a truck. This process will loop through all the depots and sequentially allocate customers to the
   depot until all customers have been assigned to a depot.
 */
        /* Type 1: shipment closest with respect to the Euclidean distance
   Type II: shipment closest with to lowest polar coordinate angle
 */
        type = 2;

        //loop while all shipments have not been assigned to the depot
        countAssignLoop = 0;

        while (!mainShipments.ifAllShipmentsAssigned() &&
                (countAssignLoop < n)) {
            for (int depot = 1; (depot <= t) && (countAssignLoop < n);
                    depot++) {
                tempShip = getClosestShipmentMDVRP(depot, type);
                status = mainDepots.insertShipmentMDVRP(tempShip, depot); //add the shipment to the depot

                if (status == true) {
                    mainShipments.setAssigned(tempShip); //log tempShip as being assigned
                } else if (isDiagnostic) {
                    System.out.println("MDVRP: Shipment could not be inserted");
                }

                if (isDiagnostic) {
                    System.out.println("The depot  and shipment is      : " +
                        depot + " " + tempShip.shipNo);
                }

                /* it is assume that everytime the insert shipment is called, a shipment is
   assigned.  This is to prevent the infinite looping
 */
                countAssignLoop += 1;
            }

            //end for
        }

        //end while
        //At this point all shipments have been assigned
        mainDepots.displayForwardKeyListMDVRP();
        mainDepots.calculateTotalNonEmptyTrucksMDVRP();
        status = mainDepots.checkIfAllNodesRoutedMDVRP(n);
        System.out.println("Final check for MDVRP on nodes: " + status);
        System.out.println("Total number of trucks:    " +
            mainDepots.getTotalNonEmptyTrucksMDVRP());
        System.out.println("Total demand for trucks:   " +
            mainDepots.getTotalDemandMDVRP());
        System.out.println("Total distance for trucks: " +
            mainDepots.getTotalDistanceMDVRP());

        // do original exchanges
        // while (old_original_exchanges() > 0) { }
        mainDepots.displayForwardKeyListMDVRP();
        status = mainDepots.checkIfAllNodesRoutedMDVRP(n);
        mainDepots.calculateTotalNonEmptyTrucksMDVRP();

        System.out.println("Final check for MDVRP on nodes: " + status);
        System.out.println("Total number of trucks:    " +
            mainDepots.getTotalNonEmptyTrucksMDVRP());
        System.out.println("Total demand for trucks:   " +
            mainDepots.getTotalDemandMDVRP());
        System.out.println("Total distance for trucks: " +
            mainDepots.getTotalDistanceMDVRP());

        //Use the fileprefix of the filename to write out the output
        String filePrefix = fileName.substring(fileName.lastIndexOf("/") + 1);
        filePrefix = filePrefix.substring(0, filePrefix.lastIndexOf("."))
                               .toLowerCase();

        //Write the solution out to the solution file
        outputFileName = filePrefix + "_detailSolution.txt";
        writeMDVRPDetailSol(outputFileName);

        //Write out the short solution to the solution file
        outputFileName = filePrefix + "_shortSolution.txt";
        writeMDVRPShortSol(outputFileName);

        /* run a couple of checks on the doubly-linked list
   mainDepots.displayAllList();
   mainShipments.deleteFirst();
   mainShipments.displayBackwardKeyList();
   mainShipments.deleteLast();
   mainShipments.displayForwardKeyList();
 */
    }

    /**
 * Do the local exchanges for the depots. No parameters are passed
 * as the mainDepots instance is used to access all the required information
 */
    public void doExchanges() {
        int numExchangePerIteration = 0;
        int numInterExchanges = 0;
        int numIntraExchanges = 0;

        // Select whether to use or not to use Tabu Search
        Tabu_Or_Not_To_Tabu = ZeusConstants.DO_NOT_USE_TABU;

        //Tabu_Or_Not_To_Tabu = ZeusConstants.USE_TABU;
        // Specify the optimization technique to use in the search
        //    optType = ZeusConstants.FIRST_FIRST;
        //    optType = ZeusConstants.FIRST_BEST;
        optType = ZeusConstants.BEST_BEST;

        if (Tabu_Or_Not_To_Tabu) {
            ProblemInfo.tabuSearch = new Tabu.TabuSearch(mainDepots);
        }

        ProblemInfo.isUsingTabu = Tabu_Or_Not_To_Tabu;
        ProblemInfo.optType = optType;

        do {
            numExchangePerIteration = 0;

            do {
                numInterExchanges = 0;
                numInterExchanges = doInterDepotExchanges();
                totalInterExchanges += numInterExchanges;
                numExchangePerIteration += numInterExchanges;
            } while (numInterExchanges > 0);

            do {
                numIntraExchanges = 0;
                numIntraExchanges = doIntraDepotExchanges();
                totalIntraExchanges += numIntraExchanges;
                numExchangePerIteration += numIntraExchanges;
            } while (numIntraExchanges > 0);

            totalExchanges += numExchangePerIteration;
        } while (numExchangePerIteration > 0);

        if (Tabu_Or_Not_To_Tabu) {
            //System.out.println(ProblemInfo.tabuSearch.tabuMainDepots);
            ProblemInfo.tabuSearch.updateTime();
            mainDepots = ProblemInfo.tabuSearch.getBestSolution();
            ProblemInfo.depotLLLevelCostF.calculateTotalsStats(mainDepots);

            // further optimize the solution obtained by tabu search, by running the
            // exchanges without Tabu Search so that no operations are considered Tabu.
            ProblemInfo.isUsingTabu = ZeusConstants.DO_NOT_USE_TABU;
            maxLoop = 1;

            do {
                numInterExchanges = 0;
                numIntraExchanges = 0;

                //allow any exchanges that can improve but are tabu to execute before stopping
                numIntraExchanges = doIntraDepotExchanges();
                numInterExchanges = doInterDepotExchanges();
                totalIntraExchanges += numIntraExchanges;
                totalInterExchanges += numInterExchanges;
                totalExchanges += (numInterExchanges + numIntraExchanges);
            } while (((numIntraExchanges + numInterExchanges) > 0) &&
                    (maxLoop-- > 0));

            // print Tabu Search statistics
            System.out.println(ProblemInfo.tabuSearch.getStatistics());
        }

        // print statistics for the exchanges
        System.out.println(getExchangeStatistics());
    }

    /**
 * Get the statistics from the local optimization exchanges. Uses the mainDepots
 * instance to access the required information
 * @return String Contains the the exchange statistics
 */
    public String getExchangeStatistics() {
        String s =
            "\r\n------------------------ EXCHANGE STATISTICS ------------------------\r\n" +
            "Performed a total of " + totalExchanges +
            " exchanges before quitting." + "\r\n\t\tInterDepot Exchanges = " +
            totalInterExchanges + "\r\n\t\tIntraDepot Exchanges = " +
            totalIntraExchanges;

        if (Tabu_Or_Not_To_Tabu && (maxLoop <= 0)) {
            s += "\r\nSolution maybe further improved by increasing the value of maxLoop.";
        }

        s += "\r\n---------------------------------------------------------------------\r\n";

        return s;
    }

    /**
 * Exchange customers between depots.
 * @return int Number of inter-changes
 */
    public int doInterDepotExchanges() {
        int numInterExchanges = 0;

        mainDepots.total01 = 0;
        mainDepots.exchangeMultDepotOpt2(ZeusConstants.EXCHANGE_01);
        numInterExchanges += mainDepots.total01;

        mainDepots.total01 = 0;
        mainDepots.exchangeMultDepotOpt2(ZeusConstants.EXCHANGE_10);
        numInterExchanges += mainDepots.total01;

        mainDepots.total02 = 0;
        mainDepots.exchangeMultDepotOpt2(ZeusConstants.EXCHANGE_02);
        numInterExchanges += mainDepots.total02;

        mainDepots.total02 = 0;
        mainDepots.exchangeMultDepotOpt2(ZeusConstants.EXCHANGE_20);
        numInterExchanges += mainDepots.total02;

        mainDepots.total11 = 0;
        mainDepots.exchangeMultDepotOpt2(ZeusConstants.EXCHANGE_11);
        numInterExchanges += mainDepots.total11;

        mainDepots.total12 = 0;
        mainDepots.exchangeMultDepotOpt2(ZeusConstants.EXCHANGE_12);
        numInterExchanges += mainDepots.total12;

        mainDepots.total12 = 0;
        mainDepots.exchangeMultDepotOpt2(ZeusConstants.EXCHANGE_21);
        numInterExchanges += mainDepots.total12;

        mainDepots.total22 = 0;
        mainDepots.exchangeMultDepotOpt2(ZeusConstants.EXCHANGE_22);
        numInterExchanges += mainDepots.total22;

        return numInterExchanges;
    }

    /**
 * Exchange customers between trucks in a single depot.
 * @return int Number of intra-changes
 */
    public int doIntraDepotExchanges() {
        int numIntraExchanges = 0;

        mainDepots.total01 = 0;
        mainDepots.exchangeOneDepotOpt(ZeusConstants.EXCHANGE_01);
        numIntraExchanges += mainDepots.total01;

        mainDepots.total01 = 0;
        mainDepots.exchangeOneDepotOpt(ZeusConstants.EXCHANGE_10);
        numIntraExchanges += mainDepots.total01;

        mainDepots.total02 = 0;
        mainDepots.exchangeOneDepotOpt(ZeusConstants.EXCHANGE_02);
        numIntraExchanges += mainDepots.total02;

        mainDepots.total02 = 0;
        mainDepots.exchangeOneDepotOpt(ZeusConstants.EXCHANGE_20);
        numIntraExchanges += mainDepots.total02;

        mainDepots.total11 = 0;
        mainDepots.exchangeOneDepotOpt(ZeusConstants.EXCHANGE_11);
        numIntraExchanges += mainDepots.total11;

        mainDepots.total12 = 0;
        mainDepots.exchangeOneDepotOpt(ZeusConstants.EXCHANGE_12);
        numIntraExchanges += mainDepots.total12;

        mainDepots.total12 = 0;
        mainDepots.exchangeOneDepotOpt(ZeusConstants.EXCHANGE_21);
        numIntraExchanges += mainDepots.total12;

        mainDepots.total22 = 0;
        mainDepots.exchangeOneDepotOpt(ZeusConstants.EXCHANGE_22);
        numIntraExchanges += mainDepots.total22;

        return numIntraExchanges;
    }

    /**
 * Exchanges that were used before Tabu exchanges were implemented.
 * @return int Number of inter- and intra-changes
 */
    public int old_original_exchanges() {
        // original exchanges in Zeus
        int numExchanges = 0;

        mainDepots.total01 = 0;
        mainDepots.exchangeOneDepot01();
        numExchanges += mainDepots.total01;

        mainDepots.total01 = 0;
        mainDepots.exchangeOneDepot10();
        numExchanges += mainDepots.total01;

        mainDepots.total02 = 0;
        mainDepots.exchangeOneDepot02();
        numExchanges += mainDepots.total02;

        mainDepots.total02 = 0;
        mainDepots.exchangeOneDepot20();
        numExchanges += mainDepots.total02;

        mainDepots.total11 = 0;
        mainDepots.exchangeOneDepot11();
        numExchanges += mainDepots.total11;

        mainDepots.total12 = 0;
        mainDepots.exchangeOneDepot12();
        numExchanges += mainDepots.total12;

        mainDepots.total12 = 0;
        mainDepots.exchangeOneDepot21();
        numExchanges += mainDepots.total12;

        mainDepots.total22 = 0;
        mainDepots.exchangeOneDepot22();
        numExchanges += mainDepots.total22;

        return numExchanges;
    }

    /**
 * Write out the detail solution to the file - short version
 * @param outFileName A String type of the file into which the output is to be
 *        written
 */
    public void writeMDVRPShortSol(String outFileName) {
        double maxDistance = 0;
        PrintWriter solOutFile = null;
        Shipment curr = mainShipments.first;
        int j = 0;

        try {
            solOutFile = new PrintWriter(new FileWriter(outFileName));

            //print out the problem information to the file
            if (ProblemInfo.maxDistance >= 999999999) {
                maxDistance = 0;
            }

            solOutFile.println(ProblemInfo.fileName + " " +
                ProblemInfo.noOfShips + " " + ProblemInfo.noOfDays + " " +
                ProblemInfo.maxCapacity + " " + maxDistance);

            //write out the route information
            mainDepots.writeShortDepotsSol(solOutFile);
        } catch (IOException ioe) {
            System.out.println("IO error " + ioe.getMessage());
        } finally {
            if (solOutFile != null) {
                solOutFile.close();
            } else {
                System.out.println("Detail solution file not open.");
            }
        }

        //end finally
    }

    // end writeMDVRPDetailSol()

    /**
 * Write out the detail solution to the file - longer version.
 * @param outFileName A String type of the file into which the output is to be
 *        written
 */
    public void writeMDVRPDetailSol(String outFileName) {
        PrintWriter solOutFile = null;
        Shipment curr = mainShipments.first;
        int j = 0;

        try {
            solOutFile = new PrintWriter(new FileWriter(outFileName));

            //print out the problem information to the file
            solOutFile.println("MDVRP File       : " + ProblemInfo.fileName);
            solOutFile.println("Type             : " + ProblemInfo.probType);
            solOutFile.println("No. of Vehs      : " + ProblemInfo.noOfVehs);
            solOutFile.println("No. of Shipments : " + ProblemInfo.noOfShips);
            solOutFile.println("No. of Depots    : " + ProblemInfo.noOfDays);
            solOutFile.println("Maximum capcity  : " + ProblemInfo.maxCapacity);
            solOutFile.println("Maximum Distance : " + ProblemInfo.maxDistance);

            if (ProblemInfo.tabuSearch != null) {
                solOutFile.println(ProblemInfo.tabuSearch.getStatistics());
            }

            solOutFile.println(getExchangeStatistics());

            //write out the route information
            mainDepots.writeDetailDepotsSol(solOutFile);
        } catch (IOException ioe) {
            System.out.println("IO error " + ioe.getMessage());
        } finally {
            if (solOutFile != null) {
                solOutFile.close();
            } else {
                System.out.println("Solution file not open.");
            }
        }

        //end finally
    }

    // end writeMDVRPDetailSol()

    /**
 * <p> Create a depot with depot number and return the pointer of
 * of the newly created depot node </p>
 * @param depotNo Depot number of the depot to assign a shipment.
 * @param type Type of heuristic to be used to obtain the customer closest to the shipment
 * @return Shipment The closest shipment to the depot based on the type of heuristic
 */
    public Shipment getClosestShipmentMDVRP(int depotNo, int type) {
        Depot tempDepot;
        Shipment tempShip;

        //find the node for depotNo in the linked list
        tempDepot = mainDepots.find(depotNo);

        //call on shipmentLinked list to find the polar and distance values
        tempShip = mainShipments.getClosestShipmentMDVRP(tempDepot.x,
                tempDepot.y, type);

        return tempShip; //stub
    }

    /**
 * Read in the data from the datafile and load it into the mainShipments
 * ShipmentLinkedList using the tokenizer.
 * @param MDVRPFileName String type of the filename consisting of the data
 * @return int Return 1 if all successful, 0 for failure
 */
    public int readMDVRPTokenDataFromFile(String MDVRPFileName) {
        /* read in the MDVRP data from the listed file and load the information
   into the availShipments linked list
   type = 0 (MDVRP)
        = 1 (PTSP)
        = 2 (PVRP)
 */
        char ch;
        String temp = "";
        int index = 0; //type
        int j = 0; //type
        int type = 0; //type

        int p = 3; //Np neighborhood size
        int depotIndex;

        //open the requested file
        FileInputStream fis;
        InputStreamReader isr;
        BufferedReader br;

        try {
            fis = new FileInputStream(MDVRPFileName);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
        } catch (Exception e) {
            System.out.println("File is not present");

            return 0;
        }

        //String to hold the entire line read in
        String readLn;

        //StringTokenizer to obtain data in tokens
        StringTokenizer st;

        /* This reads in the first line that is used to determine the total
   numer of customers and type of problem
   typePvrp is + type);
   numVeh is         + m; //not really used in an MDVRP
   numCust is        + n;
   Days is           + t; //Depots in the case of MDVRP
   Depot duration is + D;
   capacity is       + Q;
 */
        try {
            //read in the first line
            readLn = br.readLine();

            //print out the line that was read
            //System.out.println("This is s:" + s);
            //tokenize the first line
            st = new StringTokenizer(readLn);

            while (st.hasMoreTokens()) { //while there are more tokens

                //int shValue =  Integer.parseInt(st.nextToken());
                switch (index) {
                case 0:
                    type = Integer.parseInt(st.nextToken());

                    break;

                case 1:
                    m = Integer.parseInt(st.nextToken());

                    break;

                case 2:
                    n = Integer.parseInt(st.nextToken());

                    break;

                case 3:
                    t = Integer.parseInt(st.nextToken());

                    break;

                case 4:
                    D = Integer.parseInt(st.nextToken());

                    break;

                case 5:
                    Q = Integer.parseInt(st.nextToken());

                    break;
                } //end switch

                index += 1;
            }

            //end while
        } catch (Exception e) {
            System.out.println("Line could not be read in");
        }

        //Put the problem information into the ProblemInfo class. This is used
        //to identify the file type and the information that was read in from
        //the input file. When printing the output this information can be
        //used to associate with the date file that was read in.
        ProblemInfo.fileName = MDVRPFileName; //name of the file being read in
        ProblemInfo.probType = type; //problem type
        ProblemInfo.noOfVehs = m; //number of vehicles
        ProblemInfo.noOfShips = n; //number of shipments
        ProblemInfo.noOfDays = t; //number of days (horizon) or number of depots for MDVRP

        if (Q == 0) { //if there is no maximum capacity, set it to a very large number
            Q = 999999999;
        }

        if (D == 0) { //if there is no maximum capacity, set it to a very large number
            D = 999999999; //if there is not maximum distance, set it to a very large number
        }

        ProblemInfo.maxCapacity = Q; //maximum capacity of a vehicle
        ProblemInfo.maxDistance = D; //maximum distance of a vehicle

        //place the number of depots and number of shipments in the linked list instance
        mainShipments.noShipments = n;
        mainShipments.noDepots = t;
        mainShipments.maxCapacity = Q;
        mainShipments.maxDuration = D;

        // display the information from the first line

        /* System.out.println("typePvrp is       " + type);
   System.out.println("numVeh is         " + m);
   System.out.println("numCust is        " + n);
   System.out.println("days is           " + t);
   System.out.println("Depot duration is " + D);
   System.out.println("capacity is       " + Q);
 */

        //Check type of problem
        if (type != 0) //then it is not an MDVRP problem
         {
            System.out.println("Problem is not an MDVRP problem");

            return 0;
        }

        // This section will get the depot x and y for the PVRP and the PTSP.
        float x = 0; //x coordinate
        float y = 0; //y coordinate
        int i = 0;
        int d = 0;
        int q = 0;
        int f = 0;
        int a = 0;
        int vIndex = 1;
        int custCnt = 0;
        int runTimes; //total number of lines to read, including depots

        int[] list = new int[ZeusConstants.MaxCombinations];

        //array of 0'1 and 1's for the combinations
        int[][] currentComb = new int[ZeusConstants.MaxHorizon][ZeusConstants.MaxCombinations];

        //if MDVRP problem, readn in n+t lines
        if (type == 0) {
            runTimes = n + t;
        }
        //if  PVRP/PTSP, read in n+1 lines
        else {
            runTimes = n + 1;
        }

        //This section will get the customers/depots and related information
        try {
            readLn = br.readLine();

            //print out the line that was read in
            //System.out.println("This is s:" + s);

            /* The first for loop runtimes dependent upon how many lines are to be read
   The next for loop reads the line into s.  Then the entire string in s
   is processesd until the the entire line is processed and there are no
   more characters are to be processed. There is a case for each index
   except for the combinations.  The combinations are processed
   until the last character in s is processed
 */

            //runTimes includes the total number of customers and depots
            for (int k = 0; k < runTimes; k++) {
                index = 0;
                temp = "";
                vIndex = 0;
                custCnt++;
                st = new StringTokenizer(readLn);

                while (st.hasMoreElements()) {
                    switch (index) {
                    case 0:
                        i = Integer.parseInt(st.nextToken());

                        //System.out.println("custNum is " + custNum);
                        break;

                    case 1: //x = Double.parseDouble(temp);
                        x = Integer.parseInt(st.nextToken());

                        //System.out.println("x is " + vertexX);
                        break;

                    case 2:
                        y = Integer.parseInt(st.nextToken());

                        //y = Double.parseDouble(temp);
                        //System.out.println("y is " + vertexY);
                        break;

                    case 3:
                        d = Integer.parseInt(st.nextToken());

                        //System.out.println("duration is " + duration);
                        break;

                    case 4:
                        q = Integer.parseInt(st.nextToken());

                        //System.out.println("demand is " + demand);
                        break;

                    case 5:
                        f = Integer.parseInt(st.nextToken());

                        //System.out.println("frequency is " + frequency);
                        break;

                    case 6:
                        a = Integer.parseInt(st.nextToken());

                        //System.out.println("number of comb is " + numComb);
                        break;

                    default:
                        list[vIndex] = Integer.parseInt(st.nextToken());

                        //System.out.println("visitComb[" + vIndex +"] is " + visitComb[vIndex]);
                        vIndex++;

                        break;
                    } //end switch

                    index += 1;
                }

                //end while

                /* Each combination gets its own set of 0 and 1 combinations
   a = number of Combinations, list = [] of comb as ints,
   l=index of combination to be decoded,
   t = days in planning horizon or #depots
 */
                for (int l = 0; l < a; l++)
                    currentComb[l] = mainShipments.getCurrentComb(list, l, t); // current visit comb

                mainShipments.insertShipment(i, x, y, d, q, f, a, list,
                    currentComb);

                //read the next line from the file
                try {
                    readLn = br.readLine();
                } //try
                catch (Exception e) {
                    System.out.println("Reading in the next line");
                }

                //catch
                //System.out.println("This is s:" + s);
            }

            //end for - runTimes
        } catch (Exception e) {
            System.out.println("Reading the line");
        }

        //print out the shipment numbers on command line
        mainShipments.printShipNos();

        //call method to send the data to file

        /*    try {
   //availShipments.outputMDVRPShipData(type, t, MDVRPFileName, "outCust.txt");   //problem type, #days or depots
   outputMDVRPShipData(type, t, MDVRPFileName, "outCust.txt");   //problem type, #days or depots
   } catch (Exception e) {
     System.out.println("Shipment information could not be sent to the file");
   }*/
        return 1;
    }

    /** Method to output shipment information to a file
   shipLinkedList list for  MDVRP starts with
   one, depots are n+1 to n+t, PTSP and PVRP start with depot at 0
   @param type Problem type, 0=MDVRP, 1=PTSP, 2=PVRP
   @param noDepots Number of depots or number of days in planning horizon
   @param fileName String name for the output file
   @throws IOException Exception if file cannot be created
 */
    public void outputMDVRPShipData(int type, int noDepots, String fileName)
        throws IOException {
        PrintWriter out = null;
        Shipment curr = mainShipments.first;
        int j = 0;

        try {
            //check to make sure that it is an MDVRP problem
            if (type != 0) {
                System.out.println(
                    "ShipmentLinkedList: Problem file is not an MDVRP problem");

                return;
            }

            out = new PrintWriter(new FileWriter(fileName));

            //print out the problem information to the file
            //The information is obtained from the staic ProbmlemInfo class
            out.println("MDVRP File       : " + ProblemInfo.fileName);
            out.println("Type             : " + ProblemInfo.probType);
            out.println("No. of Vehs      : " + ProblemInfo.noOfVehs);
            out.println("No. of Shipments : " + ProblemInfo.noOfShips);
            out.println("No. of Depots    : " + ProblemInfo.noOfDays);
            out.println("Maximum capcity  : " + ProblemInfo.maxCapacity);
            out.println("Maximum Distance : " + ProblemInfo.maxDistance);

            out.println("");

            //start processing the data in the data structure
            while (curr != null) {
                String s = Integer.toString(curr.getShipNo());
                out.print(s);
                out.print("  ");
                s = Float.toString(curr.getX());
                out.print(s);
                out.print("  ");
                s = Float.toString(curr.getY());
                out.print(s);
                out.print("  ");
                s = Integer.toString(Math.round(curr.getDuration()));
                out.print(s);
                out.print("  ");
                s = Integer.toString(Math.round(curr.getDemand()));
                out.print(s);
                out.print("  ");
                s = Integer.toString(curr.getFrequency());
                out.print(s);
                out.print("  ");

                //check if the shipment has been assigned
                //out.print(curr.isAssigned());
                //out.print("  ");
                //print out the combination of the visits to the depots
                s = Integer.toString(curr.getNoComb());
                out.print(s);
                out.print("  ");

                int numberOfComb = curr.getNoComb();
                int[] tempComb = curr.getVisitComb();

                for (int c = 0; c < numberOfComb; c++) {
                    s = Integer.toString(tempComb[c]);
                    out.print(s);
                    out.print(" ");
                }

                out.println("");

                //this check seperates the shipments from the depots when printing
                //the information
                if (j >= (mainShipments.noShipments - noDepots)) //MDVRP depot
                 {
                    j++;
                    curr = curr.next;

                    continue;
                }

                //these are combinations that have been assigned to the depots
                //The 0 locations are not used
                int[][] curComb = curr.getCurrentComb();

                //for each of the depots print out the combinations
                for (int h = 0; h < numberOfComb; h++) {
                    for (int k = 0; k < curComb[h].length; k++) {
                        s = Integer.toString(curComb[h][k]);
                        out.print(s);
                    }

                    out.println("");
                }

                out.println("");

                j++;

                //go to the next link
                curr = curr.next;
            }

            //end for
            out.close();
        } //end try
        catch (IOException ioe) {
            System.out.println("IO error " + ioe.getMessage());
        } finally {
            if (out != null) {
                System.out.println("closing file");
                out.close();
            } else {
                System.out.println("File not open.");
            }
        }

        //end finally
    }

    // end outputMDVRPShipData
}


//End of MDVRP file
