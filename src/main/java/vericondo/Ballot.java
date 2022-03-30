package vericondo;

import java.math.BigInteger;

/**
This class represents the structure of possible ballots.
*/
public class Ballot {

    // voter ID connected to ballot
    protected final int voter;

    // plaintext vote
    protected int[][] vote;

    // state of the ballot
    protected Type type;

    // components of a ballot
    protected final BigInteger[][] b;
    protected final BigInteger[][] y;

    // the random numbers used to generate the ballot
    protected final BigInteger[][] x;

    public Ballot(int voter, int[][] vote, BigInteger[][] b, BigInteger[][] y,
        BigInteger[][] x) {
        this.voter = voter;
        this.vote = vote;
        this.b = b;
        this.y = y;
        this.x = x;
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

}
