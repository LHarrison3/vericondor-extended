package vericondo;

import java.math.BigInteger;

/**
This class represents confirmed receipts.
*/
public class ConfirmedBaseReceipt extends BaseReceipt {

    private final Type type;

    public ConfirmedBaseReceipt(int voter_id, BigInteger[][] b, BigInteger[][] y,
        BigInteger[][][] nizkp0, BigInteger[][] nizkp1, BigInteger[][][] nizkp2,
        BigInteger[][][] nizkp3, BigInteger[][] nizkp4) {

        super(voter_id, b, y, nizkp0, nizkp1, nizkp2, nizkp3, nizkp4);
        this.type = Type.CONFIRMED;
    }

}
