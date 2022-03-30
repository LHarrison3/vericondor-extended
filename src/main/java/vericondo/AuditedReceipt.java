package vericondo;

import java.math.BigInteger;

/**
Audited receipts contain extra information regarding the plaintext vote and
randomness used to generate the ballots.
*/
public class AuditedReceipt extends Receipt {

    private final BigInteger[][] x;
    private final int[][] vote;
    private final Type type;

    public AuditedReceipt(int voter_id, BigInteger[][] b, BigInteger[][] y,
        BigInteger[][][] nizkp0, BigInteger[][] nizkp1, BigInteger[][][] nizkp2,
        BigInteger[][][] nizkp3, BigInteger[][] nizkp4, int[][] vote, BigInteger[][] x) {

            super(voter_id, b, y, nizkp0, nizkp1, nizkp2, nizkp3, nizkp4);
            this.x = x;
            this.vote = vote;
            this.type = Type.AUDITED;
    }

    public BigInteger[][] x() {
        return this.x;
    }

    public int[][] vote() {
        return this.vote;
    }

    private void superDisplay() {
        super.display();
    }

    @Override
    public void display() {
        superDisplay();
        System.out.println("VOTE");
        System.out.println("----------------------------------------------------");
        for (int i = 0; i < vote.length; i++) {
            for (int j = 0; j < vote.length; j++) {
                System.out.print(vote[i][j] + " ");
            }
            System.out.println("");
        }
        System.out.println("----------------------------------------------------");
        System.out.println("RANDOMNESS");
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
    }

}
