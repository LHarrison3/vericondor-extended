package vericondo;

import java.math.BigInteger;

/**
Audited receipts contain extra information regarding the plaintext vote and
randomness used to generate the ballots.
*/
public class AuditedIndiffReceipt extends IndiffReceipt {

    private final BigInteger[][] x;
    private final int[][] vote;
    private final Type type;

    private final BigInteger[][] x_indiff;
    private final int[][] vote_indiff;

    public AuditedIndiffReceipt(int voter_id, BigInteger[][] b, BigInteger[][] y,
        BigInteger[][] b_indiff, BigInteger[][] y_indiff, BigInteger[][][] nizkp0, 
        BigInteger[][][] nizkp1, BigInteger[][][] nizkp2, BigInteger[][][] nizkp3, 
        BigInteger[][] nizkp4, BigInteger[][][] nizkp5, BigInteger[][][] nizkp6,
        BigInteger[][][][] nizkp7, int[][] vote, BigInteger[][] x, int[][] vote_indiff,
        BigInteger[][] x_indiff) {

            super(voter_id, b, y, b_indiff, y_indiff, nizkp0, nizkp1, nizkp2, nizkp3, nizkp4, nizkp5, nizkp6, nizkp7);
            this.x = x;
            this.vote = vote;
            this.x_indiff = x_indiff;
            this.vote_indiff = vote_indiff;
            this.type = Type.AUDITED;
    }

    public BigInteger[][] x() {
        return this.x;
    }

    public int[][] vote() {
        return this.vote;
    }

    public BigInteger[][] x_indiff() {
        return this.x_indiff;
    }

    public int[][] vote_indiff() {
        return this.vote_indiff;
    }

    @Override
    public void display() {

        super.display();
        System.out.println("PREFERENCE MATRIX");
        System.out.println("----------------------------------------------------");
        for (int i = 0; i < vote.length; i++) {
            for (int j = 0; j < vote.length; j++) {
                System.out.print(vote[i][j] + " ");
            }
            System.out.println("");
        }
        System.out.println("----------------------------------------------------");
        System.out.println("INDIFFERENCE MATRIX");
        System.out.println("----------------------------------------------------");
        for (int i = 0; i < vote_indiff.length; i++) {
            for (int j = 0; j < vote_indiff.length; j++) {
                System.out.print(vote_indiff[i][j] + " ");
            }
            System.out.println("");
        }
        System.out.println("----------------------------------------------------");
        System.out.println("PREFERENCE RANDOMNESS");
        System.out.println("----------------------------------------------------");
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x.length; j++) {
                String element = x[i][j].toString();
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
        System.out.println("INDIFFERENCE RANDOMNESS");
        System.out.println("----------------------------------------------------");
        for (int i = 0; i < x_indiff.length; i++) {
            for (int j = 0; j < x_indiff.length; j++) {
                String element = x_indiff[i][j].toString();
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
        
    }

}
