package vericondo;

import java.math.BigInteger;

/**
This class represents an encrypted ballot of the form <b_k, Y_k>.
b_k and Y_k are defined as in the VERICONDO specification.
*/
public class BaseBallot extends Ballot {

    // the NIZKP separated into its five conjunctions
    private BigInteger[][][] nizkp0;
    private BigInteger[][] nizkp1;
    private BigInteger[][][] nizkp2;
    private BigInteger[][][] nizkp3;
    private BigInteger[][] nizkp4;

    public BaseBallot(int voter, int[][] vote, BigInteger[][] b, BigInteger[][] y,
        BigInteger[][] x, BigInteger[][][] nizkp0, BigInteger[][] nizkp1,
        BigInteger[][][] nizkp2, BigInteger[][][] nizkp3, BigInteger[][] nizkp4) {

        super(voter, vote, b, y, x);

        this.nizkp0 = nizkp0;
        this.nizkp1 = nizkp1;
        this.nizkp2 = nizkp2;
        this.nizkp3 = nizkp3;
        this.nizkp4 = nizkp4;
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
