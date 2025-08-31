//Box class represent A Box
class Box {

    int boxNum;
    int boxMoney;
    boolean boxOpen; // true : open , false : closed
    boolean boxClass; // true : golden , false : silver

    // Money values
    static int[] moneyArr = { 1, 5, 10, 25, 50, 100, 250, 500,
            1000, 2500, 5000, 7500, 10000, 15000,
            20000, 25000, 50000, 75000, 100000, 250000,
            500000, 1000000, 2500000, 5000000, 7500000, 10000000 };

    // take values
    static int[] takenArray = new int[26];

    // constructor to initialze Box's
    Box(int boxNum, int boxMoney) {

        this.boxNum = boxNum;
        this.boxMoney = boxMoney;
        this.boxOpen = false;
        this.boxClass = isGoldenBoxClass(boxMoney);
    }

    // method for find box class
    private boolean isGoldenBoxClass(int boxMoney) {
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
                if (takenArray[randomNum] == 0) {
                    takenArray[randomNum] = 1;
                    break;
                }
            }
            box[i] = new Box(i, moneyArr[randomNum]);
        }
        return box;
    }

    // open a box
    int openBox(int boxNum) {
        int money = 0;
        if (0 <= boxNum && boxNum <= 25) {
            this.boxOpen = true;
            money = this.boxMoney;
        }

        return money;
    }

    // private void boxView(Box[] boxs) {
    // for (Box box : boxs) {
    // System.out.println(box.boxNum);
    // System.out.println(box.boxMoney);
    // System.out.println(box.boxOpen);
    // System.out.println(box.boxClass);
    // }
    // }
}

class Main {
    void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void main(String[] args) {

        Box[] boxs = Box.initBox();

    }
}