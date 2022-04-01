package vericondo;

import java.math.BigInteger;

/**
This class represents receipts posted on the bulletin board.
*/
public class PrefRelReceipt extends Receipt {

    // the receipt includes the NIZKPs
    private final BigInteger[][][] nizkp0;
    private final BigInteger[][][] nizkp1;
    private final BigInteger[][] nizkp2;

    public PrefRelReceipt(int voter_id, BigInteger[][] b, BigInteger[][] y,
        BigInteger[][][] nizkp0, BigInteger[][][] nizkp1, BigInteger[][] nizkp2) {

        super(voter_id, b, y);

        this.nizkp0 = nizkp0;
        this.nizkp1 = nizkp1;
        this.nizkp2 = nizkp2;
    }

    // output the receipt to the screen
    @Override
    public void display() {

        super.display();

        System.out.println("----------------------------------------------------");
        System.out.println("NIZKPs");
        System.out.println("----------------------------------------------------");
        System.out.println("NIZKP 0:");
        for (int i = 0; i < y.length; i++) {
            for (int j = 0; j < y[i].length; j++) {
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
            for (int j = 0; j < b[i].length; j++) {
                String element0 = nizkp1[i][j][0].toString();
                String element1 = nizkp1[i][j][1].toString();
                String element2 = nizkp1[i][j][2].toString();
                String element3 = nizkp1[i][j][3].toString();
                String first0 = element0.substring(0, n_digits);
                String first1 = element1.substring(0, n_digits);
                String first2 = element2.substring(0, n_digits);
                String first3 = element3.substring(0, n_digits);
                System.out.print("(" + first0 + "," + first1 + "," + first2 + "," + first3 + ") ");
            }
            System.out.println("");
        }
        System.out.println("NIZKP 2:");
        for (int i = 0; i < b.length; i++) {
            System.out.print("(");
            int j = 0;
            for (j = 0; j < (2*b.length) - 1; j++) {

                String element = nizkp2[i][j].toString();
                String first = element.substring(0, n_digits);
                System.out.print(first + ",");
            }
            String element = nizkp2[i][j].toString();
            String first = element.substring(0, n_digits);
            System.out.println(first + ")");
        }
        System.out.println("----------------------------------------------------");

    }

    public BigInteger[][][] getNIZKP0() {
        return this.nizkp0;
    }

    public BigInteger[][][] getNIZKP1() {
        return this.nizkp1;
    }

    public BigInteger[][] getNIZKP2() {
        return this.nizkp2;
    }

}
