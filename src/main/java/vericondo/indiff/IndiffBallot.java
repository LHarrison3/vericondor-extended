package vericondo;

import java.math.BigInteger;

/**
This class represents an encrypted ballot of the form <b_ka, b_kb Y_ka, Y_kb>.
b_ka, b_kb, Y_ka and Y_kb are defined as in the VERICONDO specification.
*/
public class IndiffBallot extends Ballot {

    // plaintext indifference matrix
    protected int[][] vote_indiff;

    // extra components necessary for indifference
    protected final BigInteger[][] b_indiff;
    protected final BigInteger[][] y_indiff;

    // extra random numbers for indifference
    protected final BigInteger[][] x_indiff;

    // the NIZKP separated into its eight conjunctions
    private BigInteger[][][] nizkp0;
    private BigInteger[][][] nizkp1;
    private BigInteger[][][] nizkp2;
    private BigInteger[][][] nizkp3;
    private BigInteger[][] nizkp4;
    private BigInteger[][][] nizkp5;
    private BigInteger[][][] nizkp6;
    private BigInteger[][][][] nizkp7;
    private BigInteger[][] nizkp8;
    private BigInteger[][] nizkp9;

    public IndiffBallot(int voter, int[][] vote, int[][] vote_indiff, BigInteger[][] b, 
        BigInteger[][] y, BigInteger[][] x, BigInteger[][] b_indiff, BigInteger[][] y_indiff, 
        BigInteger[][] x_indiff, BigInteger[][][] nizkp0, BigInteger[][][] nizkp1,
        BigInteger[][][] nizkp2, BigInteger[][][] nizkp3, BigInteger[][] nizkp4,
        BigInteger[][][] nizkp5, BigInteger[][][] nizkp6, BigInteger[][][][] nizkp7,
        BigInteger[][] nizkp8, BigInteger[][] nizkp9) {

        super(voter, vote, b, y, x);

        this.vote_indiff = vote_indiff;

        this.b_indiff = b_indiff;
        this.y_indiff = y_indiff;
        this.x_indiff = x_indiff;

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

    public int[][] vote_indiff() {
        return this.vote_indiff;
    }

    public BigInteger[][] b_indiff() {
        return this.b_indiff;
    }

    public BigInteger[][] y_indiff() {
        return this.y_indiff;
    }

    public BigInteger[][] x_indiff() {
        return this.x_indiff;
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
