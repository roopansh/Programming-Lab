package task1.sockmatching;

/*
 * Shelf Manager recieves a sock pair and puts it in the right shelf
 * */
class ShelfManager {
    // Count of socks represents the different shelves
    private Integer WhiteSocks;
    private Integer BlackSocks;
    private Integer BlueSocks;
    private Integer GreySocks;

    /*
     * Constructor
     * */
    ShelfManager() {
        WhiteSocks = 0;
        BlackSocks = 0;
        BlueSocks = 0;
        GreySocks = 0;
    }

    /*
     * Recieves a pair of sock of some color
     * put it in the correct shelf
     * */
    void ManageSockPair(int sock) {
        if (sock == Constants.WHITE_SOCK) {
            synchronized (WhiteSocks) {
                WhiteSocks += 2;    // 2 socks
            }
        } else if (sock == Constants.BLACK_SOCK) {
            synchronized (BlackSocks) {
                BlackSocks += 2;    // 2 socks
            }
        } else if (sock == Constants.BLUE_SOCK) {
            synchronized (BlueSocks) {
                BlueSocks += 2;    // 2 socks
            }
        } else if (sock == Constants.GREY_SOCK) {
            synchronized (GreySocks) {
                GreySocks += 2;    // 2 socks
            }
        }
    }

    // Print the collected socks finally when the program ends.
    void PrintShelves() {
        System.out.println(String.format("White: %d\tblack: %d\tblue: %d\tgrey: %d", WhiteSocks, BlackSocks, BlueSocks, GreySocks));
    }
}
