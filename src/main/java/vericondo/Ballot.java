package vericondo;

import java.math.BigInteger;

enum Type {
    AUDITED,
    CONFIRMED,
}

/**
This class represents an encrypted ballot of the form <b_k, Y_k>.
b_k and Y_k are defined as in the VERICONDO specification.
*/
public class Ballot {

    // voter ID connected to ballot
    private final int voter;

    // plaintext vote
    private int[][] vote;

    // the type of the ballot
    private Type type;

    // components of a ballot
    private final BigInteger[][] b;
    private final BigInteger[][] y;

    // the random numbers used to generate the ballot
    private final BigInteger[][] x;

    // the NIZKP separated into its five conjunctions
    private BigInteger[][][] nizkp0;
    private BigInteger[][] nizkp1;
    private BigInteger[][][] nizkp2;
    private BigInteger[][][] nizkp3;
    private BigInteger[][] nizkp4;

    public Ballot(int voter, int[][] vote, BigInteger[][] b, BigInteger[][] y,
        BigInteger[][] x, BigInteger[][][] nizkp0, BigInteger[][] nizkp1,
        BigInteger[][][] nizkp2, BigInteger[][][] nizkp3, BigInteger[][] nizkp4) {
        this.voter = voter;
        this.vote = vote;
        this.b = b;
        this.y = y;
        this.x = x;
        this.nizkp0 = nizkp0;
        this.nizkp1 = nizkp1;
        this.nizkp2 = nizkp2;
        this.nizkp3 = nizkp3;
        this.nizkp4 = nizkp4;
    }

    public int id() {
        return this.voter;
    }

    public BigInteger[][] b() {
        return this.b;
    }

    public BigInteger[][] y() {
        return this.y;
    }

    public int[][] vote() {
        return this.vote;
    }

    public BigInteger[][] x() {
        return this.x;
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
