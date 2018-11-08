package task1.sockmatching;

/*
 * Maintain a count of the socks received of a particular type
 * Whenever a sock can be paired with another sock of same color,
 * pass the sock pair to the shelf manager
 * */
class MatchingMachine {
    private ShelfManager shelfManager;
    private Boolean whiteSock;
    private Boolean blackSock;
    private Boolean blueSock;
    private Boolean greySock;

    MatchingMachine(ShelfManager shelfManager) {
        whiteSock = false;
        blackSock = false;
        blueSock = false;
        greySock = false;
        this.shelfManager = shelfManager;
    }


    void MatchSock(int sock) {
        if (sock == Constants.WHITE_SOCK) { //white
            synchronized (whiteSock) {
                if (whiteSock) {  // already have one sock
                    // pass one pair of white socks to the shelf manager
                    shelfManager.ManageSockPair(Constants.WHITE_SOCK);
                    // decrease the whitesock counter
                    whiteSock = false;
                } else { // don't have any sock of that color
                    whiteSock = true;
                }
            }
        } else if (sock == Constants.BLACK_SOCK) {     // black
            synchronized (blackSock) {
                if (blackSock) {  // already have one sock
                    // pass one pair of black socks to the shelf manager
                    shelfManager.ManageSockPair(Constants.BLACK_SOCK);
                    // decrease the blacksock counter
                    blackSock = false;
                } else { // don't have any sock of that color
                    blackSock = true;
                }
            }
        } else if (sock == Constants.BLUE_SOCK) { // blue
            synchronized (blueSock) {
                if (blueSock) {  // already have one sock
                    // pass one pair of blueSockks to the shelf manager
                    shelfManager.ManageSockPair(Constants.BLUE_SOCK);
                    // decrease the blueSock counter
                    blueSock = false;
                } else { // don't have any sock of that color
                    blueSock = true;
                }
            }
        } else if (sock == Constants.GREY_SOCK) { // grey
            synchronized (greySock) {
                if (greySock) {  // already have one sock
                    // pass one pair of grey socks to the shelf manager
                    shelfManager.ManageSockPair(Constants.GREY_SOCK);
                    // decrease the greysock counter
                    greySock = false;
                } else { // don't have any sock of that color
                    greySock = true;
                }
            }
        }
    }
}
