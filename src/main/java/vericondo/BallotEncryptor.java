package vericondo;

import java.util.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
This class creates ballots from votes.
*/
public class BallotEncryptor {

    // parameters helpful for creating a ballot
    protected final int num_candidates;

    protected final BigInteger g0;
    protected final BigInteger g1;
    protected final BigInteger p;
    protected final BigInteger q;

    // sets up a new encryptor
    public BallotEncryptor(int num_candidates, BigInteger g0, BigInteger g1,
        BigInteger p, BigInteger q) {
        this.num_candidates = num_candidates;
        this.g0 = g0;
        this.g1 = g1;
        this.p = p;
        this.q = q;
    }

    // helper function to generate a random big integer from 1 to a limit
    protected BigInteger genRandom(BigInteger limit) {
        Random gen = new Random();
        BigInteger random;
        do {
            random = new BigInteger(limit.bitLength(), gen);
        } while (random.compareTo(limit) >= 0 || random.compareTo(BigInteger.ZERO) == 0);
        return random;
    }

    // helper function for hashing commitments
    public static BigInteger hash(BigInteger[] t) {

        String hash_alg = "SHA-256";

        try {

            MessageDigest md = MessageDigest.getInstance(hash_alg);
            for (int i = 0; i < t.length; i++) {
                md.update(t[i].toByteArray());
            }
            byte[] result = md.digest();

            return new BigInteger(1, result);

        } catch (NoSuchAlgorithmException e) {
            System.out.println("Hashing algorithm not recognised");
            return new BigInteger("-1");
        }
    }

}
