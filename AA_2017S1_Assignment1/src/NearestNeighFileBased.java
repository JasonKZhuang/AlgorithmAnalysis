/***********************************************************************
 * COSC2123_1710 - Algorithms and Analysis
 * Semester 1 2017 Assignment #1 
 * Partner 1 full Name        : Kaizhi.Zhuang
 * Partner 1 Student Number   : s3535252
 * Partner 2 full Name        : Yang Xu
 * Partner 1 Student Number   : s3577404
 * Course Code      : COSC2123_1710
 * Skeleton code provided by Jeffrey, Youhan
 **********************************************************************/

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import nearestNeigh.Category;
import nearestNeigh.KDTreeNN;
import nearestNeigh.NaiveNN;
import nearestNeigh.NearestNeigh;
import nearestNeigh.Point;

/**
 * This is to be the main class when we run the program in file-based point.
 * It uses the data file to initialise the set of points.
 * It takes a command file as input and output into the output file.
 *
 * @author Jeffrey, Youhan
 */
public class NearestNeighFileBased {
	
    //"naive" "sampleData1000.txt" "test1.in" "test1.exp"
	//"naive" "sampleData10000.txt" "test1.in" "test1.exp"
	//"naive" "sampleData20000.txt" "test1.in" "test1.exp"	
	//"kdtree" "sampleData1000.txt" "test1.in" "test1.exp"
	//"kdtree" "sampleData10000.txt" "test1.in" "test1.exp"
	//"kdtree" "sampleData20000.txt" "test1.in" "test1.exp"
	
	//"naive" "sampleData1000.txt"   "test2.in" "test2.exp"
	//"naive" "sampleData10000.txt"  "test2.in" "test2.exp"
	//"naive" "sampleData20000.txt"  "test2.in" "test2.exp"	
	//"kdtree" "sampleData1000.txt"  "test2.in" "test2.exp"
	//"kdtree" "sampleData10000.txt" "test2.in" "test2.exp"
	//"kdtree" "sampleData20000.txt" "test2.in" "test2.exp"
	
	//"naive" "sampleData1000.txt"   "test800.in" "test800.exp"
	//"naive" "sampleData10000.txt"  "test800.in" "test800.exp"
	//"naive" "sampleData20000.txt"  "test800.in" "test800.exp"	
	//"kdtree" "sampleData1000.txt"  "test800.in" "test800.exp"
	//"kdtree" "sampleData10000.txt" "test800.in" "test800.exp"
	//"kdtree" "sampleData20000.txt" "test800.in" "test800.exp"
	
	
	/**
     * Name of class, used in error messages.
     */
    protected static final String progName = "NearestNeighFileBased";

    /**
     * Print help/usage message.
     */
    public static void usage(String progName) {
        System.err.println(progName + ": <approach> [data fileName] [command fileName] [output fileName]");
        System.err.println("<approach> = <naive | kdtree>");
        System.exit(1);
    } // end of usage

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    	// read command line arguments
        if (args.length != 4) {
            System.err.println("Incorrect number of arguments.");
            usage(progName);
        }

        // initialise search agent
        NearestNeigh agent = null;
        switch (args[0]) {
            case "naive":
                agent = new NaiveNN();
                break;
            case "kdtree":
                agent = new KDTreeNN();
                break;
            default:
                System.err.println("Incorrect argument value.");
                usage(progName);
        }

        // read in data file of initial set of points
        String dataFileName = args[1];
        List<Point> points = new ArrayList<Point>();
        try {
            File dataFile = new File(dataFileName);
            Scanner scanner = new Scanner(dataFile);
            while (scanner.hasNext()) {
                String id = scanner.next();
                Category cat = Point.parseCat(scanner.next());
                Point point = new Point(id, cat, scanner.nextDouble(), scanner.nextDouble());
                points.add(point);
            }
            scanner.close();
            agent.buildIndex(points);
        } catch (FileNotFoundException e) {
            System.err.println("Data file doesn't exist.");
            usage(progName);
        }

        String commandFileName = args[2];
        String outputFileName = args[3];
        File commandFile = new File(commandFileName);
        File outputFile = new File(outputFileName);

        // parse the commands in commandFile
        try {
            Scanner scanner = new Scanner(commandFile);
            PrintWriter writer = new PrintWriter(outputFile);

            // operating commands
            while (scanner.hasNext()) {
                String command = scanner.next();
                String id;
                Category cat;
                // remember lat = latitude (approximately correspond to x-coordinate)
                // remember lon = longitude (approximately correspond to y-coordinate)
                double lat;
                double lon;
                int k;
                Point point;
                switch (command) {
                    // search
                    case "S":
                        cat = Point.parseCat(scanner.next());
                        lat = scanner.nextDouble();
                        lon = scanner.nextDouble();
                        k = scanner.nextInt();
                        point = new Point("searchTerm", cat, lat, lon);
                        List<Point> searchResult = agent.search(point, k);
                        for (Point writePoint : searchResult) {
                            writer.println(writePoint.toString());
                        }
                        break;
                    // add
                    case "A":
                        id = scanner.next();
                        cat = Point.parseCat(scanner.next());
                        lat = scanner.nextDouble();
                        lon = scanner.nextDouble();
                        point = new Point(id, cat, lat, lon);
                        if (!agent.addPoint(point)) {
                            writer.println("Add point failed.");
                        }
                        break;
                    // delete
                    case "D":
                        id = scanner.next();
                        cat = Point.parseCat(scanner.next());
                        lat = scanner.nextDouble();
                        lon = scanner.nextDouble();
                        point = new Point(id, cat, lat, lon);
                        if (!agent.deletePoint(point)) {
                            writer.println("Delete point failed.");
                        }
                        break;
                    // check
                    case "C":
                        id = scanner.next();
                        cat = Point.parseCat(scanner.next());
                        lat = scanner.nextDouble();
                        lon = scanner.nextDouble();
                        point = new Point(id, cat, lat, lon);
                        writer.println(agent.isPointIn(point));
                        break;
                    default:
                        System.err.println("Unknown command.");
                        System.err.println(command + " " + scanner.nextLine());
                }
            }
            scanner.close();
            writer.close();
        } catch (FileNotFoundException e) {
            System.err.println("Command file doesn't exist.");
            usage(progName);
        }
    }
}
