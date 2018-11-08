package task1.sockmatching;

/*
 * Robot is used to pick up a sock from the buffer and
 * pass it to the matching machine.
 * */
class Robot extends Thread {
    private SockMatching sockMatching;  // To acces the buffer of socks and pick one sock
    private MatchingMachine matchingMachine;    // To pass the picked sock the sock matcher

    /*
     * Constructor
     * */
    Robot(SockMatching sockMatching, MatchingMachine matchingMachine, int name) {
        super();
        setName(String.valueOf(name));
        this.sockMatching = sockMatching;
        this.matchingMachine = matchingMachine;
    }

    @Override
    public void run() {
        while (true) {
            // Pick a sock from the buffer
            int sockReceived = sockMatching.PickSock();
            if (sockReceived == Constants.NULL_SOCK) {   // If no sock left in the
                // Stop the thread
                System.out.println("Thread " + getName() + " Stopped!");
                stop();
            }
            System.out.println("Sock of color " + sockReceived + " recieved by Thread - " + getName());
            // Pass the picked sock the sock matcher
            matchingMachine.MatchSock(sockReceived);
        }
    }
}
