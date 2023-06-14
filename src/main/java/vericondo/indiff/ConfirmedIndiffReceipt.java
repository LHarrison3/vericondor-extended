package vericondo;

import java.math.BigInteger;

/**
This class represents confirmed receipts.
*/
public class ConfirmedIndiffReceipt extends IndiffReceipt {

    private final Type type;

    public ConfirmedIndiffReceipt(int voter_id, BigInteger[][] b, BigInteger[][] y,
        BigInteger[][] b_indiff, BigInteger[][] y_indiff, BigInteger[][][] nizkp0, 
        BigInteger[][][] nizkp1, BigInteger[][][] nizkp2, BigInteger[][][] nizkp3,
        BigInteger[][] nizkp4, BigInteger[][][] nizkp5, BigInteger[][][] nizkp6,
        BigInteger[][][][] nizkp7) {

        super(voter_id, b, y, b_indiff, y_indiff, nizkp0, nizkp1, nizkp2, nizkp3, nizkp4, nizkp5, nizkp6, nizkp7);
        this.type = Type.CONFIRMED;
    }

}
