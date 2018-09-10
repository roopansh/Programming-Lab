package task1.sockmatching;

class Robot extends Thread {
    private SockMatching sockMatching;
    private MatchingMachine matchingMachine;

    Robot(SockMatching sockMatching, MatchingMachine matchingMachine, int name) {
        super();
        setName(String.valueOf(name));
        this.sockMatching = sockMatching;
        this.matchingMachine = matchingMachine;
    }

    @Override
    public void run() {
        while (true) {
            int sockReceived = sockMatching.PickSock();
            if (sockReceived == -1) {
                System.out.println("Thread " + getName() + " Stopped!");
                stop();
            }
            System.out.println("Sock of color " + sockReceived + " recieved by Thread - " + getName());
            matchingMachine.MatchSock(sockReceived);
        }
    }
}
