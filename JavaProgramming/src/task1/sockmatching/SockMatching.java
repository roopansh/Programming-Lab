package task1.sockmatching;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

/*
* Sock matching machine
* There is a heap of new labelled socks. Each sock is having one of four colours: white, black,
blue and grey. There are several robotic arms which can pick up a single sock at a time and
pass it to a matching machine. The matching machine is able to find two socks of the same
colour and pass the pair of socks to a shelf manager robot. The shelf manager robot then puts
the pair of socks to the appropriate shelf.
**/
public class SockMatching {
    private int NumberOfRobots; // Number of robot arms
    private final List<Integer> Socks;  // Socks buffer
    private List<Robot> Robots; // List of Robot Threads
    private MatchingMachine matchingMachine;    // Sock matcher
    private ShelfManager shelfManager;  // Shelf manager
    private List<Semaphore> SemLocks;   // Semaphore locks for the socks
    private Random rand = new Random(); // random generator

    /*
     * Start the robots and wait for each robot to terminate.
     * Print the final socks shelves count at the end.
     * */
    private void startMachine() throws InterruptedException {
        // Activate all the robot arms
        for (Robot robot : Robots) {
            robot.start();
        }

        // wait for all robotarms to stop
        for (Robot robot : Robots) {
            robot.join();
        }

        // Print the collected socks count
        shelfManager.PrintShelves();
    }

    /*
     * Create semaphores for each sock.
     * */
    private void createSockLocks() {
        SemLocks = new ArrayList<>();
        for (int i = 0; i < Socks.size(); i++) {
            Semaphore SemLock = new Semaphore(1);
            SemLocks.add(SemLock);
        }
    }

    /*
     * Create the required number of robot arms
     * */
    private void createRobotArms() {
        Robots = new ArrayList<>();
        for (int i = 0; i < NumberOfRobots; i++) {
            Robot robot = new Robot(this, this.matchingMachine, i);
            Robots.add(robot);
        }
    }

    /*
     * Constructor
     * */
    private SockMatching(int numberOfRobots, List<Integer> socks) {
        NumberOfRobots = numberOfRobots;
        Socks = socks;

        // Create Shelf manager
        shelfManager = new ShelfManager();

        // Create matching machine
        matchingMachine = new MatchingMachine(shelfManager);

        // Create robotic arms
        createRobotArms();

        // Create locks
        createSockLocks();
    }

    /*
     * Pick a sock
     * If no sock is left, return NULL_SOCK
     * */
    int PickSock() {
        int sock;
        int n = -1;
        boolean flag = false;

        // Generate a random number and lock that sock
        synchronized (Socks) {
            if (Socks.size() > 0) {
                n = rand.nextInt(Socks.size());
            } else {
                flag = true;
            }
        }

        // If no sock is left to return
        if (flag) {
            return Constants.NULL_SOCK;
        }
        boolean success = SemLocks.get(n).tryAcquire();

        // Lock the sock and return the locked object
        // Release the lock so that it can be acquired by some other thread
        if (success && n < Socks.size()) {
            synchronized (Socks) {
                sock = Socks.get(n);
                Socks.remove(n);
            }
            SemLocks.get(n).release();
            return sock;
        } else {
            return PickSock();
        }
    }

    /*
     * The main function to run the Sock matching machine
     * */
    public static void main(String[] args) throws IOException, InterruptedException {
        // File to take input from
        File file = new File(Constants.INPUT_FILE);
        Scanner scanner = new Scanner(file);

        // Take the number of robots as input
        int number_of_robots = scanner.nextInt();

        // Take the list of socks as input
        List<Integer> socks = new ArrayList<>();
        while (scanner.hasNextInt()) {
            socks.add(scanner.nextInt());
        }

        // Create a sock matching machine
        SockMatching sockMatching = new SockMatching(number_of_robots, socks);

        // Start the Sockmatching
        sockMatching.startMachine();
    }


}
