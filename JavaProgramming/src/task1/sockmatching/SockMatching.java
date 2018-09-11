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
    private int NumberOfRobots;
    private final List<Integer> Socks;
    private List<Robot> Robots;
    private MatchingMachine matchingMachine;
    private ShelfManager shelfManager;
    private List<Semaphore> SemLocks;
    private Random rand = new Random();

    public static void main(String[] args) throws IOException, InterruptedException {
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

    private void startMachine() throws InterruptedException {
        // Activate all the robot arms
        for (Robot robot : Robots) {
            robot.start();
        }

        // wait for all robotarms to stop
        for (Robot robot : Robots) {
            robot.join();
        }
        shelfManager.PrintShelves();
    }

    private void createSockLocks() {
        SemLocks = new ArrayList<>();
        for (int i = 0; i < Socks.size(); i++) {
            Semaphore SemLock = new Semaphore(1);
            SemLocks.add(SemLock);
        }
    }

    private void createRobotArms() {
        Robots = new ArrayList<>();
        for (int i = 0; i < NumberOfRobots; i++) {
            Robot robot = new Robot(this, this.matchingMachine, i);
            Robots.add(robot);
        }
    }

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

    int PickSock() {
        int sock;
        int n = -1;
        boolean flag = false;
        synchronized (Socks) {
            if (Socks.size() > 0) {
                n = rand.nextInt(Socks.size());
            } else {
                flag = true;
            }
        }
        if (flag) {
            return -1;
        }
        boolean success = SemLocks.get(n).tryAcquire();
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
}
