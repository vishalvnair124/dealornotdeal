import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
    static int chosedBox = -1;

    static boolean offerAcepted = false;

    static int moneyGot = 0;
    static int offerMoney = 0;

    static boolean hasMsg = true;
    static boolean hasAction = false;

    static int mainI = 0;
    static int mainJ = 0;

    static String msg = "pick your box";
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
    static void openBox(int pos, Box[] boxs) {
        boxs[pos].boxOpen = true;

    }

    // Initial picking of my box
    static void pickMyBox(int pos) {

        if (pos > 0 && pos <= 26) {
            GameValue.chosedBox = pos - 1;

        } else {
            System.out.println("Invalid Box Number");

        }

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

        GameValue.hasAction = true;
        GameValue.offerMoney = offerMoney;
        GameValue.msg = "New Offer " + offerMoney;

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
        GameScreen screen = new GameScreen();
        screen.GameScreenUi(boxs, screen);

        clearScreen();

    }
}

class actButtonsActions implements ActionListener {
    boolean input;
    Box[] boxs;
    GameScreen screen;

    actButtonsActions(boolean input, Box[] boxs, GameScreen screen) {
        this.input = input;
        this.boxs = boxs;
        this.screen = screen;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (input == true) {
            GameValue.offerAcepted = true;
            GameValue.moneyGot = GameValue.offerMoney;
            GameValue.msg = "You win" + GameValue.moneyGot;
        } else {
            GameValue.offerAcepted = false;
            GameValue.msg = "pick your box";
        }
        GameValue.hasAction = false;

        screen.mainFrame.dispose();
        screen = new GameScreen();
        screen.GameScreenUi(boxs, screen);
    }

}

class BoxButtonAction implements ActionListener {
    int j;
    Box[] boxs;
    GameScreen screen;

    BoxButtonAction(int j, Box[] boxs, GameScreen screen) {
        this.j = j;
        this.boxs = boxs;
        this.screen = screen;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (boxs[j].boxOpen != true) {
            if (GameValue.chosedBox == -1) {
                GameValue.chosedBox = j;
            } else {
                Box.openBox(j, boxs);
                GameValue.mainJ++; // advance the game
            }

            // check if level is complete
            if (GameValue.mainJ >= GameValue.Level.get(GameValue.mainI + 1)) {
                if (!GameValue.offerAcepted) {
                    // rebuild UI
                    screen.mainFrame.dispose();
                    screen = new GameScreen();
                    screen.GameScreenUi(boxs, screen);
                    GameValue.hasAction = true;
                    Banker.readyForDeal(Banker.offerMoney(boxs, GameValue.mainI + 1));

                }
                GameValue.mainI++; // move to next level
                GameValue.mainJ = 0; // reset for next level
            }

            // rebuild UI
            screen.mainFrame.dispose();
            screen = new GameScreen();
            screen.GameScreenUi(boxs, screen);
        }
    }

}

class GameScreen {
    JFrame mainFrame = new JFrame("Deal or Not Deal"); // main window
    JPanel topPanel = new JPanel(); // panel for top section
    JPanel rightPanel = new JPanel(); // panel for top section
    JPanel centerPanel = new JPanel(); // panel for top section
    JPanel leftPanel = new JPanel(); // panel for top section
    JLabel centerTitle = new JLabel("Deal or Not Deal");

    JPanel[] goldPanels = new JPanel[13];
    JLabel[] goldJLabels = new JLabel[13];

    JPanel[] silverPanels = new JPanel[13];
    JLabel[] silverJLabels = new JLabel[13];

    JPanel mainPanel = new JPanel();
    JPanel msgJPanel = new JPanel();
    JPanel actionPanel = new JPanel();
    JButton[] boxButton = new JButton[26];
    JButton[] actButtons = new JButton[2];
    JLabel msgJLabel = new JLabel();

    void GameScreenUi(Box[] boxs, GameScreen screen) {

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

        rightPanel.setPreferredSize(new Dimension(topPanel.getPreferredSize().width * 2, 750));
        rightPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        rightPanel.setLayout(new GridLayout(13, 1));
        rightPanel.setBackground(new Color(255, 215, 0));

        // in loop Money chart Displaying
        for (int i = 0; i < 13; i++) {
            goldJLabels[i] = new JLabel("" + GameValue.moneyArr[i + 13]);
            goldPanels[i] = new JPanel(new GridBagLayout());
            goldPanels[i].add(goldJLabels[i]);
            goldPanels[i].setBorder(BorderFactory.createEtchedBorder());

            boolean isOpened = false;
            for (int j = 0; j < 26; j++) {
                if (boxs[j].boxMoney == GameValue.moneyArr[i + 13] && boxs[j].boxOpen) {
                    isOpened = true;
                    break;
                }
            }
            Color bgColor = isOpened ? new Color(143, 121, 4) : new Color(255, 215, 0);
            goldPanels[i].setBackground(bgColor);

            rightPanel.add(goldPanels[i]);
        }

        // Left panel settings
        leftPanel.setPreferredSize(new Dimension(topPanel.getPreferredSize().width * 2, 750));
        leftPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        leftPanel.setLayout(new GridLayout(13, 1));
        leftPanel.setBackground(new Color(192, 192, 192));

        // in loop Money chart Displaying
        for (int i = 0; i < 13; i++) {
            silverJLabels[i] = new JLabel("" + GameValue.moneyArr[i]);
            silverPanels[i] = new JPanel(new GridBagLayout());
            silverPanels[i].add(silverJLabels[i]);
            silverPanels[i].setBorder(BorderFactory.createEtchedBorder());

            // Check if any box with this money is opened
            boolean isOpened = false;
            for (int j = 0; j < 26; j++) {
                if (boxs[j].boxMoney == GameValue.moneyArr[i] && boxs[j].boxOpen) {
                    isOpened = true;
                    break;
                }
            }

            Color bgColor = isOpened ? new Color(138, 128, 128) : new Color(192, 192, 192);
            silverPanels[i].setBackground(bgColor);

            leftPanel.add(silverPanels[i]);
        }

        // main panel
        mainPanel.setLayout(new GridLayout(6, 5));
        for (int j = 0; j < 26; j++) {
            if (boxs[j].boxNum != GameValue.chosedBox) {
                boxButton[j] = new JButton("" + ((boxs[j].boxOpen) ? boxs[j].boxMoney : boxs[j].boxNum + 1));
                boxButton[j].setPreferredSize(new Dimension(150, 75));
                Color bgColor = (boxs[j].boxOpen)
                        ? new Color(140, 140, 140) // Open color
                        : new Color(237, 237, 237); // Not open color
                boxButton[j].setBackground(bgColor);
                if (!GameValue.hasAction) {
                    boxButton[j].addActionListener(new BoxButtonAction(j, boxs, screen));
                }

                mainPanel.add(boxButton[j]);
            }

        }

        // action panel
        actButtons[0] = new JButton("NO");
        actButtons[1] = new JButton("YES");
        actButtons[0].setPreferredSize(new Dimension(150, 50));
        actButtons[1].setPreferredSize(new Dimension(150, 50));
        actButtons[0].setBackground(new Color(252, 61, 61));
        actButtons[1].setBackground(new Color(81, 245, 66));
        actButtons[0].addActionListener(new actButtonsActions(false, boxs, screen));
        actButtons[1].addActionListener(new actButtonsActions(true, boxs, screen));
        actionPanel.add(actButtons[0]);
        actionPanel.add(actButtons[1]);

        // center panel settings
        centerPanel.setPreferredSize(new Dimension(topPanel.getPreferredSize().width * 6, 750));
        centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(209, 207, 207));
        centerPanel.add(mainPanel);
        if (GameValue.hasMsg) {
            msgJLabel = new JLabel(GameValue.msg);
            msgJPanel.add(msgJLabel);
            msgJPanel.setBackground(new Color(5, 28, 207));
            centerPanel.add(msgJPanel);
        }
        if (GameValue.hasAction) {
            centerPanel.add(actionPanel);
            actionPanel.setBackground(new Color(5, 249, 6));
        }

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

        Game.mainGameLoop();
    }
}