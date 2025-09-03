import java.util.*;
import javax.swing.*;
import java.awt.*;

//GameValue class 
class GameValue {

    static Scanner sc = new Scanner(System.in);

    // Money values
    static int[] moneyArr = { 1, 5, 10, 25, 50, 100, 250, 500,
            1000, 2500, 5000, 7500, 10000, 15000,
            20000, 25000, 50000, 75000, 100000, 250000,
            500000, 1000000, 2500000, 5000000, 7500000, 10000000 };

    // take values
    static int[] takenArray = new int[26];

    // each Level number of box to open
    static HashMap<Integer, Integer> Level = new HashMap<>(
            Map.of(1, 7, 2, 5, 3, 4, 4, 3, 5, 2, 6, 1, 7, 1, 8, 1));

    // Initial choosedBox
    static int chosedBox;

    static boolean offerAcepted = false;
    static int moneyGot = 0;
}

// Box class to represent A Box
class Box {

    int boxNum;
    int boxMoney;
    boolean boxOpen; // true : open , false : closed
    boolean boxClass; // true : golden , false : silver

    // constructor to initialze Box's
    Box(int boxNum, int boxMoney) {

        this.boxNum = boxNum;
        this.boxMoney = boxMoney;
        this.boxOpen = false;
        this.boxClass = isGoldenBoxClass(boxMoney);
    }

    // method for find box class
    static boolean isGoldenBoxClass(int boxMoney) {
        return (boxMoney > 25000);
    }

    // initialze Method
    static Box[] initBox() {
        Box[] box = new Box[26];
        for (int i = 0; i < 26; i++) {
            int randomNum;
            // take random box money
            while (true) {
                randomNum = (int) (Math.random() * 26); // 0 to 25
                if (GameValue.takenArray[randomNum] == 0) {
                    GameValue.takenArray[randomNum] = 1;
                    break;
                }
            }
            box[i] = new Box(i, GameValue.moneyArr[randomNum]);
        }
        return box;
    }

    // open a box
    int openBox() {
        this.boxOpen = true;

        return this.boxMoney;
    }

    // method to select the oprning box
    static void openingBox(Box[] boxs) {
        boolean valid = false;
        while (!valid) {
            System.out.print("Which Box you want to open :");
            int pos = GameValue.sc.nextInt();
            if (pos > 0 && pos <= 26 && pos - 1 != GameValue.chosedBox && !boxs[pos - 1].boxOpen) {
                valid = true;
                System.out.println("In the box " + pos + ":" + boxs[pos - 1].openBox());
            } else {
                System.out.println("Invalid Box Number");

            }
        }

    }

    // Initial picking of my box
    static void pickMyBox() {
        boolean valid = false;
        int pos = 0;
        while (!valid) {
            System.out.print("Which Box you want to Select :");
            pos = GameValue.sc.nextInt();
            if (pos > 0 && pos <= 26) {
                valid = true;

            } else {
                System.out.println("Invalid Box Number");

            }
        }
        GameValue.chosedBox = pos - 1;
    }

}

// Money class to Show Money chart
class Money {

    static void moneyChart(Box[] boxs) {
        for (int i : GameValue.moneyArr) {
            System.out.println("Money       :" + i);
            System.out.println("Money class :" + ((Box.isGoldenBoxClass(i)) ? "Golden" : "Silver"));
            for (int j = 0; j < 26; j++) {
                if (boxs[j].boxMoney == i) {
                    System.out.println("OPEN/NOT :" + ((boxs[j].boxOpen) ? "O" : "N"));
                }
            }

        }
    }
}

// class Banker to makeOffer ,askDeal
class Banker {

    // Are you ready for deal
    static void readyForDeal(int offerMoney) {

        System.out.println("Banker gives you an offer : " + offerMoney);
        System.out.println("If yes '1' or No '0'");
        if (GameValue.sc.nextInt() == 1) {
            GameValue.offerAcepted = true;
            GameValue.moneyGot = offerMoney;
        }

    }

    // Method for offer money
    static int offerMoney(Box[] boxs, int level) {
        int money = 0, sum = 0, countNopen = 0;

        int maxNMoney = 0, minNMoney = 10000000;
        int openedGold = 0, openedSilver = 0;

        for (Box box : boxs) {
            if (box.boxOpen == false) {
                countNopen++;
                sum += box.boxMoney;
                if (maxNMoney < box.boxMoney) {
                    maxNMoney = box.boxMoney;
                }
                if (minNMoney > box.boxMoney) {
                    minNMoney = box.boxMoney;
                }

            } else {
                if (box.boxClass == true) {
                    openedGold += 1;
                } else {
                    openedSilver += 1;
                }
            }

        }
        double EV = sum / countNopen;

        double f = bankerFactor(maxNMoney, minNMoney, openedGold, openedSilver, level);
        money = (int) (EV * f);
        return money;
    }

    static double clamp(double x, double lo, double hi) {
        return Math.max(lo, Math.min(hi, x));
    }

    static double bankerFactor(double maxNMoney, double minNMoney,
            int openedGold, int openedSilver, int level) {
        double denomMoney = maxNMoney + minNMoney;
        double risk = denomMoney > 0 ? (maxNMoney - minNMoney) / denomMoney : 0.0;

        double totalOpened = openedGold + openedSilver + 1.0; // +1 avoids divide-by-zero
        double delta = (openedSilver - openedGold) / totalOpened;

        double f = 0.55 + 0.30 * delta + 0.25 * (1.0 - risk) + 0.02 * level;
        return clamp(f, 0.40, 1.05);
    }

}

// class Game to Main loop of Gamining
class Game {
    // Method for final message
    static void finalMsg(Box box) {
        System.out.println("You have wined" + GameValue.moneyGot);
        System.out.println("Inside your selected box : " + box.boxMoney);
    }

    // method that clear the screen
    static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    // main game loop
    static void mainGameLoop() {
        Box[] boxs = Box.initBox();

        clearScreen();
        Box.pickMyBox();

        for (int i = 0; i < GameValue.Level.size(); i++) {
            for (int j = 0; j < GameValue.Level.get(i + 1); j++) {
                Box.openingBox(boxs);
            }
            if (!GameValue.offerAcepted) {
                Money.moneyChart(boxs);
                Banker.readyForDeal(Banker.offerMoney(boxs, i + 1));
            }

        }
        finalMsg(boxs[GameValue.chosedBox]);

    }
}

class GameScreen {
    JFrame mainFrame = new JFrame("Deal or Not Deal"); // main window
    JPanel topPanel = new JPanel(); // panel for top section
    JPanel rightPanel = new JPanel(); // panel for top section
    JPanel centerPanel = new JPanel(); // panel for top section
    JPanel leftPanel = new JPanel(); // panel for top section
    JLabel centerTitle = new JLabel("Deal or Not Deal");

    GameScreen() {
        // Frame settings
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1500, 800);
        mainFrame.setLayout(new BorderLayout());

        // Top panel settings
        topPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        centerTitle.setFont(new Font("Serif", Font.BOLD, 24));
        topPanel.add(centerTitle);
        topPanel.setBackground(new Color(255, 100, 50));
        topPanel.setPreferredSize(new Dimension(topPanel.getPreferredSize().width, 50));

        // Right panel settings
        rightPanel.add(new JLabel("Hello"));
        rightPanel.setPreferredSize(new Dimension(topPanel.getPreferredSize().width * 2, 750));
        rightPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        rightPanel.setBackground(new Color(255, 215, 0));
        // Left panel settings
        leftPanel.add(new JLabel("Hello"));
        leftPanel.setPreferredSize(new Dimension(topPanel.getPreferredSize().width * 2, 750));
        leftPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        leftPanel.setBackground(new Color(192, 192, 192));
        // center panel settings
        centerPanel.add(new JLabel("Hello"));
        centerPanel.setPreferredSize(new Dimension(topPanel.getPreferredSize().width * 6, 750));
        centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        // Add panels to frame
        mainFrame.add(topPanel, BorderLayout.NORTH);
        mainFrame.add(centerPanel, BorderLayout.CENTER);
        mainFrame.add(rightPanel, BorderLayout.EAST);
        mainFrame.add(leftPanel, BorderLayout.WEST);
        // Show frame
        mainFrame.setVisible(true);
    }
}

// class Main
class Main {

    // main method
    public static void main(String[] args) {

        new GameScreen();
    }
}