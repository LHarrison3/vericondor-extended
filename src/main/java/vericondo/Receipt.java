package vericondo;

import java.math.BigInteger;

/**
This class represents receipts posted on the bulletin board.
*/
public class Receipt {

    // ID of receipt is the same as voter ID
    protected final int voter_id;

    // the receipt includes encrypted ballots
    protected final BigInteger[][] b;
    protected final BigInteger[][] y;

    // truncate the big integers to its first n digits
    // so that these are easier to read and not very long
    protected int n_digits;

    public Receipt(int voter_id, BigInteger[][] b, BigInteger[][] y) {
        this.voter_id = voter_id;
        this.b = b;
        this.y = y;
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

}
