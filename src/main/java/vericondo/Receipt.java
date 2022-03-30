package vericondo;

import java.math.BigInteger;

/**
This class represents receipts posted on the bulletin board.
*/
public class Receipt {

    // ID of receipt is the same as voter ID
    private final int voter_id;

    // the receipt includes encrypted ballots
    private final BigInteger[][] b;
    private final BigInteger[][] y;

    // the receipt includes the NIZKPs
    private final BigInteger[][][] nizkp0;
    private final BigInteger[][] nizkp1;
    private final BigInteger[][][] nizkp2;
    private final BigInteger[][][] nizkp3;
    private final BigInteger[][] nizkp4;

    // truncate the big integers to its first n digits
    // so that these are easier to read and not very long
    protected int n_digits;

    public Receipt(int voter_id, BigInteger[][] b, BigInteger[][] y,
        BigInteger[][][] nizkp0, BigInteger[][] nizkp1, BigInteger[][][] nizkp2,
        BigInteger[][][] nizkp3, BigInteger[][] nizkp4) {

        this.voter_id = voter_id;
        this.b = b;
        this.y = y;
        this.nizkp0 = nizkp0;
        this.nizkp1 = nizkp1;
        this.nizkp2 = nizkp2;
        this.nizkp3 = nizkp3;
        this.nizkp4 = nizkp4;
        this.n_digits = 4;
    }

    // output the receipt to the screen
    public void display() {

        System.out.println("----------------------------------------------------");
        System.out.println("RECEIPT FOR VOTER (" + voter_id + ")");
        System.out.println("----------------------------------------------------");
        System.out.println("BALLOT");
        System.out.println("----------------------------------------------------");
        System.out.println("b:");
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < b.length; j++) {
                String element = b[i][j].toString();
                if (i == j) {
                    System.out.print(element + " ");
                } else {
                    String first = element.substring(0, n_digits);
                    System.out.print(first + " ");
                }
            }
            System.out.println("");
        }
        System.out.println("Y:");
        for (int i = 0; i < y.length; i++) {
            for (int j = 0; j < y.length; j++) {
                String element = y[i][j].toString();
                if (i == j) {
                    System.out.print(element + " ");
                } else {
                    String first = element.substring(0, n_digits);
                    System.out.print(first + " ");
                }
            }
            System.out.println("");
        }
        System.out.println("----------------------------------------------------");
        System.out.println("NIZKPs");
        System.out.println("----------------------------------------------------");
        System.out.println("NIZKP 0:");
        for (int i = 0; i < y.length; i++) {
            for (int j = 0; j < y.length; j++) {
                String element0 = nizkp0[i][j][0].toString();
                String element1 = nizkp0[i][j][1].toString();
                String first0 = element0.substring(0, n_digits);
                String first1 = element1.substring(0, n_digits);
                System.out.print("(" + first0 + "," + first1 + ") ");
            }
            System.out.println("");
        }
        System.out.println("NIZKP 1:");
        for (int i = 0; i < b.length; i++) {
            String element0 = nizkp1[i][0].toString();
            String element1 = nizkp1[i][1].toString();
            String first0 = element0.substring(0, n_digits);
            String first1 = element1.substring(0, n_digits);
            System.out.println("(" + first0 + "," + first1 + ") ");
        }
        System.out.println("NIZKP 2:");
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < b.length; j++) {
                String element0 = nizkp2[i][j][0].toString();
                String element1 = nizkp2[i][j][1].toString();
                String element2 = nizkp2[i][j][2].toString();
                String element3 = nizkp2[i][j][3].toString();
                String first0 = element0.substring(0, n_digits);
                String first1 = element1.substring(0, n_digits);
                String first2 = element2.substring(0, n_digits);
                String first3 = element3.substring(0, n_digits);
                System.out.print("(" + first0 + "," + first1 + "," + first2 + "," + first3 + ") ");
            }
            System.out.println("");
        }
        System.out.println("NIZKP 3:");
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < b.length; j++) {
                if (i == j) {
                    System.out.print("- ");
                } else {
                    String element0 = nizkp3[i][j][0].toString();
                    String element1 = nizkp3[i][j][1].toString();
                    String element2 = nizkp3[i][j][2].toString();
                    String element3 = nizkp3[i][j][3].toString();
                    String element4 = nizkp3[i][j][4].toString();
                    String element5 = nizkp3[i][j][5].toString();
                    String element6 = nizkp3[i][j][6].toString();
                    String element7 = nizkp3[i][j][7].toString();
                    String first0 = element0.substring(0, n_digits);
                    String first1 = element1.substring(0, n_digits);
                    String first2 = element2.substring(0, n_digits);
                    String first3 = element3.substring(0, n_digits);
                    String first4 = element4.substring(0, n_digits);
                    String first5 = element5.substring(0, n_digits);
                    String first6 = element6.substring(0, n_digits);
                    String first7 = element7.substring(0, n_digits);
                    System.out.print("(" + first0 + "," + first1 + "," + first2 + "," + first3 + "," + first4 + "," + first5 + "," + first6 + "," + first7 + ") ");
                }
            }
            System.out.println("");
        }
        System.out.println("NIZKP 4:");
        for (int i = 0; i < b.length; i++) {
            System.out.print("(");
            int j = 0;
            for (j = 0; j < (2*b.length) - 1; j++) {

                String element = nizkp4[i][j].toString();
                String first = element.substring(0, n_digits);
                System.out.print(first + ",");
            }
            String element = nizkp4[i][j].toString();
            String first = element.substring(0, n_digits);
            System.out.println(first + ")");
        }
        System.out.println("----------------------------------------------------");

    }

    public int id() {
        return this.voter_id;
    }

    public BigInteger[][] b() {
        return this.b;
    }

    public BigInteger[][] y() {
        return this.y;
    }

    public BigInteger[][][] getNIZKP0() {
        return this.nizkp0;
    }

    public BigInteger[][] getNIZKP1() {
        return this.nizkp1;
    }

    public BigInteger[][][] getNIZKP2() {
        return this.nizkp2;
    }

    public BigInteger[][][] getNIZKP3() {
        return this.nizkp3;
    }

    public BigInteger[][] getNIZKP4() {
        return this.nizkp4;
    }

}
