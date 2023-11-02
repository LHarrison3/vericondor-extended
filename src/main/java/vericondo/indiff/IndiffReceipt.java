package vericondo;

import java.math.BigInteger;

/**
This class represents receipts posted on the bulletin board.
*/
public class IndiffReceipt extends Receipt {

    // the receipt includes the NIZKPs
    private final BigInteger[][][] nizkp0;
    private final BigInteger[][][] nizkp1;
    private final BigInteger[][][] nizkp2;
    private final BigInteger[][][] nizkp3;
    private final BigInteger[][] nizkp4;
    private final BigInteger[][][] nizkp5;
    private final BigInteger[][][] nizkp6;
    private final BigInteger[][][][] nizkp7;
    private final BigInteger[][] nizkp8;
    private final BigInteger[][] nizkp9;

    // additional ballot contents for indifferences
    protected final BigInteger[][] b_indiff;
    protected final BigInteger[][] y_indiff;

    public IndiffReceipt(int voter_id, BigInteger[][] b, BigInteger[][] y, 
        BigInteger[][] b_indiff, BigInteger[][] y_indiff, BigInteger[][][] nizkp0, 
        BigInteger[][][] nizkp1, BigInteger[][][] nizkp2, BigInteger[][][] nizkp3,
        BigInteger[][] nizkp4, BigInteger[][][] nizkp5, BigInteger[][][] nizkp6,
        BigInteger[][][][] nizkp7, BigInteger[][] nizkp8, BigInteger[][] nizkp9) {

        super(voter_id, b, y);

        this.b_indiff = b_indiff;
        this.y_indiff = y_indiff;

        this.nizkp0 = nizkp0;
        this.nizkp1 = nizkp1;
        this.nizkp2 = nizkp2;
        this.nizkp3 = nizkp3;
        this.nizkp4 = nizkp4;
        this.nizkp5 = nizkp5;
        this.nizkp6 = nizkp6;
        this.nizkp7 = nizkp7;
        this.nizkp8 = nizkp8;
        this.nizkp9 = nizkp9;
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
            for (int j = 0; j < b.length; j++) {
                String element0 = nizkp1[i][j][0].toString();
                String element1 = nizkp1[i][j][1].toString();
                String first0 = element0.substring(0, n_digits);
                String first1 = element1.substring(0, n_digits);
                System.out.print("(" + first0 + "," + first1 + ") ");
            }
            System.out.println("");
        }
        System.out.println("NIZKP 2:");
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < b.length; j++) {
                if (i == j) {
                    System.out.print("- ");
                } else {
                    String element0 = nizkp2[i][j][0].toString();
                    String element1 = nizkp2[i][j][1].toString();
                    String element2 = nizkp2[i][j][2].toString();
                    String element3 = nizkp2[i][j][3].toString();
                    String element4 = nizkp2[i][j][4].toString();
                    String element5 = nizkp2[i][j][5].toString();
                    String first0 = element0.substring(0, n_digits);
                    String first1 = element1.substring(0, n_digits);
                    String first2 = element2.substring(0, n_digits);
                    String first3 = element3.substring(0, n_digits);
                    String first4 = element4.substring(0, n_digits);
                    String first5 = element5.substring(0, n_digits);
                    System.out.print("(" + first0 + "," + first1 + "," + first2 + "," + first3 + "," + first4 + "," + first5 + ") ");
                }
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
                    String element8 = nizkp3[i][j][8].toString();
                    String element9 = nizkp3[i][j][9].toString();
                    String first0 = element0.substring(0, n_digits);
                    String first1 = element1.substring(0, n_digits);
                    String first2 = element2.substring(0, n_digits);
                    String first3 = element3.substring(0, n_digits);
                    String first4 = element4.substring(0, n_digits);
                    String first5 = element5.substring(0, n_digits);
                    String first6 = element6.substring(0, n_digits);
                    String first7 = element7.substring(0, n_digits);
                    String first8 = element8.substring(0, n_digits);
                    String first9 = element9.substring(0, n_digits);
                    System.out.print("(" + first0 + "," + first1 + "," + first2 + "," + first3 + "," + first4 + "," + first5 + "," + first6 + "," + first7 + "," + first8 + "," + first9 + ") ");
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
        System.out.println("NIZKP 5:");
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < b.length; j++) {
                if (i == j) {
                    System.out.print("- ");
                } else {
                    String element0 = nizkp5[i][j][0].toString();
                    String element1 = nizkp5[i][j][1].toString();
                    String element2 = nizkp5[i][j][2].toString();
                    String element3 = nizkp5[i][j][3].toString();
                    String element4 = nizkp5[i][j][4].toString();
                    String element5 = nizkp5[i][j][5].toString();
                    String first0 = element0.substring(0, n_digits);
                    String first1 = element1.substring(0, n_digits);
                    String first2 = element2.substring(0, n_digits);
                    String first3 = element3.substring(0, n_digits);
                    String first4 = element4.substring(0, n_digits);
                    String first5 = element5.substring(0, n_digits);
                    System.out.print("(" + first0 + "," + first1 + "," + first2 + "," + first3 + "," + first4 + "," + first5 + ") ");
                }
            }
            System.out.println("");
        }
        System.out.println("NIZKP 6:");
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < b.length; j++) {
                if (i == j) {
                    System.out.print("- ");
                } else {
                    String element0 = nizkp6[i][j][0].toString();
                    String element1 = nizkp6[i][j][1].toString();
                    String element2 = nizkp6[i][j][2].toString();
                    String element3 = nizkp6[i][j][3].toString();
                    String element4 = nizkp6[i][j][4].toString();
                    String element5 = nizkp6[i][j][5].toString();
                    String first0 = element0.substring(0, n_digits);
                    String first1 = element1.substring(0, n_digits);
                    String first2 = element2.substring(0, n_digits);
                    String first3 = element3.substring(0, n_digits);
                    String first4 = element4.substring(0, n_digits);
                    String first5 = element5.substring(0, n_digits);
                    System.out.print("(" + first0 + "," + first1 + "," + first2 + "," + first3 + "," + first4 + "," + first5 + ") ");
                }
            }
            System.out.println("");
        }
        System.out.println("NIZKP 7:");
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < b.length; j++) {
                if (i == j) {
                    System.out.print("- ");
                } else {

                    for (int k = 0; k < b.length; k++) {
                        if (k == i || k == j) {
                            System.out.print("- ");
                        } else {
                            System.out.print("k=" + k + ": ");
                            String element0 = nizkp7[i][j][k][0].toString();
                            String element1 = nizkp7[i][j][k][1].toString();
                            String element2 = nizkp7[i][j][k][2].toString();
                            String element3 = nizkp7[i][j][k][3].toString();
                            String element4 = nizkp7[i][j][k][4].toString();
                            String element5 = nizkp7[i][j][k][5].toString();
                            String element6 = nizkp7[i][j][k][6].toString();
                            String element7 = nizkp7[i][j][k][7].toString();
                            String element8 = nizkp7[i][j][k][8].toString();
                            String element9 = nizkp7[i][j][k][9].toString();
                            String element10 = nizkp7[i][j][k][10].toString();
                            String element11 = nizkp7[i][j][k][11].toString();
                            String element12 = nizkp7[i][j][k][12].toString();
                            String first0 = element0.substring(0, n_digits);
                            String first1 = element1.substring(0, n_digits);
                            String first2 = element2.substring(0, n_digits);
                            String first3 = element3.substring(0, n_digits);
                            String first4 = element4.substring(0, n_digits);
                            String first5 = element5.substring(0, n_digits);
                            String first6 = element6.substring(0, n_digits);
                            String first7 = element7.substring(0, n_digits);
                            String first8 = element8.substring(0, n_digits);
                            String first9 = element9.substring(0, n_digits);
                            String first10 = element10.substring(0, n_digits);
                            String first11 = element11.substring(0, n_digits);
                            String first12 = element12.substring(0, n_digits);
                            System.out.print("(" + first0 + "," + first1 + "," + first2 + "," + first3 + "," + first4 + "," + first5 + "," + first6 + "," + first7 + "," + first8 + "," + first9 + "," + first10 + "," + first11 + "," + first12 + ") ");
                        }
                    } 
                    System.out.println("");
                }
            }
            System.out.println("");
        }
        System.out.println("NIZKP 8:");
        for (int i = 0; i < b.length; i++) {
            String element0 = nizkp8[i][0].toString();
            String element1 = nizkp8[i][1].toString();
            String first0 = element0.substring(0, n_digits);
            String first1 = element1.substring(0, n_digits);
            System.out.print("(" + first0 + "," + first1 + ") ");
            System.out.println("");
        }
        System.out.println("NIZKP 9:");
        for (int i = 0; i < b.length; i++) {
            String element0 = nizkp9[i][0].toString();
            String element1 = nizkp9[i][1].toString();
            String first0 = element0.substring(0, n_digits);
            String first1 = element1.substring(0, n_digits);
            System.out.print("(" + first0 + "," + first1 + ") ");
            System.out.println("");
        }
        System.out.println("----------------------------------------------------");
        
    }

    public BigInteger[][] b_indiff() {
        return this.b_indiff;
    }

    public BigInteger[][] y_indiff() {
        return this.y_indiff;
    }

    public BigInteger[][][] getNIZKP0() {
        return this.nizkp0;
    }

    public BigInteger[][][] getNIZKP1() {
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

    public BigInteger[][][] getNIZKP5() {
        return this.nizkp5;
    }

    public BigInteger[][][] getNIZKP6() {
        return this.nizkp6;
    }

    public BigInteger[][][][] getNIZKP7() {
        return this.nizkp7;
    }

    public BigInteger[][] getNIZKP8() {
        return this.nizkp8;
    }

    public BigInteger[][] getNIZKP9() {
        return this.nizkp9;
    }

}
