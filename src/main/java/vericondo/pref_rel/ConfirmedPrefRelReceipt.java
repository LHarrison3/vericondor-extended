package vericondo;

import java.math.BigInteger;

/**
This class represents confirmed receipts.
*/
public class ConfirmedPrefRelReceipt extends PrefRelReceipt {

    private final Type type;

    public ConfirmedPrefRelReceipt(int voter_id, BigInteger[][] b, BigInteger[][] y,
        BigInteger[][][] nizkp0, BigInteger[][][] nizkp1, BigInteger[][] nizkp2) {

        super(voter_id, b, y, nizkp0, nizkp1, nizkp2);
        this.type = Type.CONFIRMED;
    }

}
