package vericondo;

import java.util.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
This class creates ballots from votes.
*/
public class PrefRelBallotEncryptor extends BallotEncryptor {

    // sets up a new encryptor
    public PrefRelBallotEncryptor(int num_candidates, BigInteger g0, BigInteger g1,
        BigInteger p, BigInteger q) {

        super(num_candidates, g0, g1, p, q);

    }

    // generates a triangle of random numbers
    protected BigInteger[][] generateRandomness() {
        BigInteger limit = q.subtract(BigInteger.ONE);
        BigInteger[][] x = new BigInteger[num_candidates - 1][];
        for (int i = 0; i < num_candidates - 1; i++) {
            x[i] = new BigInteger[num_candidates - 1 - i];
            for (int j = 0; j < num_candidates - 1 - i; j++) {
                // ensures each x(i,j) is between 1 and q-1 (i.e. in Z*q)
                x[i][j] = genRandom(limit);
            }
        }
        return x;
    }

    // constructs a ballot from a given preference relation
    public PrefRelBallot makeBallot(int voter, int[][] vote) {

        BigInteger[][] b = new BigInteger[num_candidates - 1][];
        BigInteger[][] y = new BigInteger[num_candidates - 1][];

        BigInteger[][] x = generateRandomness();

        for (int i = 0; i < num_candidates - 1; i++) {
            b[i] = new BigInteger[num_candidates - 1 - i];
            y[i] = new BigInteger[num_candidates - 1 - i];
            for (int j = 0; j < num_candidates - 1 - i; j++) {
                b[i][j] = g0.modPow(x[i][j].add(BigInteger.valueOf(vote[i][j])), p);
                y[i][j] = g1.modPow(x[i][j], p);
            }
        }

        // initialise arrays to hold each conjunction of the NIZKP Pwf
        // there's five conjunctions and each nizkp has a different size

        // y(c,c') = g1^x(c,c')
        BigInteger[][][] nizkp0 = genNIZKP0(voter, x, y);

        // b(c,c') = g0^x(c,c') || b(c,c') / g0 = g0^x(c,c')
        BigInteger[][][] nizkp1 = genNIZKP1(voter, vote, x, b);

        // g_0^c * (b_prod(i,c) / b_prod(c,i)) = g_0^(x_sum(i,c) - x_sum(c,i)) * g_0^j
        BigInteger[][] nizkp2 = genNIZKP2(voter, vote, x, b);

        return new PrefRelBallot(voter, vote, b, y, x, nizkp0, nizkp1, nizkp2);
    }

    /*
    TODO: from this point, change the NIZKP functions for the new equations on paper
    */


    // helper function to generate NIZKP for the first conjunction
    private BigInteger[][][] genNIZKP0(int voter, BigInteger[][] x, BigInteger[][] y) {

        BigInteger limit = q.subtract(BigInteger.ONE);

        BigInteger[][][] nizkp0 = new BigInteger[num_candidates - 1][][];

        BigInteger w;
        BigInteger t;
        BigInteger c;
        BigInteger r;
        for (int i = 0; i < num_candidates - 1; i++) {
            nizkp0[i] = new BigInteger[num_candidates - 1 - i][2];
            for (int j = 0; j < num_candidates - 1 - i; j++) {
                w = genRandom(limit);
                t = g1.modPow(w, p);
                BigInteger[] arr = {BigInteger.valueOf(voter), g1, y[i][j], t};
                c = hash(arr);
                r = w.subtract(c.multiply(x[i][j])).mod(q);
                nizkp0[i][j][0] = c;
                nizkp0[i][j][1] = r;
            }
        }

        return nizkp0;

    }

    // helper function to generate NIZKP for the third conjunction
    private BigInteger[][][] genNIZKP1(int voter, int[][] vote, BigInteger[][] x, BigInteger[][] b) {

        BigInteger limit = q.subtract(BigInteger.ONE);

        BigInteger[][][] nizkp1 = new BigInteger[num_candidates - 1][][];

        // the goal is to prove that b(i,j) is the encrpytion of a zero or a one
        // the prover (the object of this class) knows which case is real
        // the proof strategy changes depending on which case is real
        // we provide a proof for the real case and a simulated one for the fake case

        BigInteger u1;
        BigInteger u2;
        BigInteger w;
        BigInteger t1;
        BigInteger t2;
        BigInteger c;
        BigInteger c1;
        BigInteger c2;
        BigInteger r1;
        BigInteger r2;
        for (int i = 0; i < num_candidates - 1; i++) {
            nizkp1[i] = new BigInteger[num_candidates - 1 - i][4];
            for (int j = 0; j < num_candidates - 1 - i; j++) {
                u1 = genRandom(limit);
                u2 = genRandom(limit);
                w = genRandom(limit);
                // here we know the real case is an entry that is 0
                if (vote[i][j] == 0) {
                    t1 = b[i][j].modPow(w,p).multiply(g0.modPow(u1,p)).mod(p);
                    t2 = g0.modPow(u2,p);
                    BigInteger[] arr = {BigInteger.valueOf(voter), g0, b[i][j], t1, t2};
                    c = hash(arr);
                    c1 = w;
                    c2 = c.subtract(c1).mod(q);
                    r1 = u1;
                    r2 = u2.subtract(c2.multiply(x[i][j].subtract(BigInteger.ONE))).mod(q);
                // here we know the real case is an entry that is 1
                } else {
                    t1 = g0.modPow(u1,p);
                    t2 = b[i][j].multiply(g0.modInverse(p)).mod(p).modPow(w,p).multiply(g0.modPow(u2,p)).mod(p);
                    BigInteger[] arr = {BigInteger.valueOf(voter), g0, b[i][j], t1, t2};
                    c = hash(arr);
                    c2 = w;
                    c1 = c.subtract(c2).mod(q);
                    r2 = u2;
                    r1 = u1.subtract(c1.multiply(x[i][j].add(BigInteger.ONE))).mod(q);
                }
                nizkp1[i][j][0] = c1;
                nizkp1[i][j][1] = c2;
                nizkp1[i][j][2] = r1;
                nizkp1[i][j][3] = r2;
            }
        }

        return nizkp1;

    }


    private BigInteger[][] genNIZKP2(int voter, int[][] vote, BigInteger[][] x, BigInteger[][] b) {

        BigInteger limit = q.subtract(BigInteger.ONE);

        BigInteger[][] nizkp2 = new BigInteger[num_candidates][num_candidates * 2];

        int index = 0;

        BigInteger[][] vote_big = new BigInteger[num_candidates - 1][];
        for (int i = 0; i < num_candidates - 1; i++) {
            vote_big[i] = new BigInteger[num_candidates - 1 - i];
            for (int j = 0; j < num_candidates - 1 - i; j++) {
                vote_big[i][j] = BigInteger.valueOf(vote[i][j]);
            }
        }

        // compute needed differences of sums and divisions of products
        BigInteger[] x_rs = new BigInteger[num_candidates];
        BigInteger[] vote_rs = new BigInteger[num_candidates];
        BigInteger[] b_ps = new BigInteger[num_candidates];
        for (int c = 0; c < num_candidates; c++) {
            x_rs[c] = computePrefDiffsAt(c, x);
            vote_rs[c] = computePrefDiffsAt(c, vote_big).add(BigInteger.valueOf(c)).mod(p);
            b_ps[c] = computePrefDivsAt(c, b).multiply(g0.modPow(BigInteger.valueOf(c),p)).mod(p);
        }

        BigInteger u = BigInteger.ZERO;
        BigInteger[] r = new BigInteger[num_candidates];
        BigInteger[] t = new BigInteger[num_candidates];
        BigInteger[] ch = new BigInteger[num_candidates];
        BigInteger[] arr = new BigInteger[2 + 2*num_candidates];
        BigInteger ch_grand;
        for (int j = 0; j < num_candidates; j++) {

            arr[0] = BigInteger.valueOf(voter);
            arr[1] = BigInteger.valueOf(j);
            BigInteger ch_sum = BigInteger.ZERO;

            for (int i = 0; i < num_candidates; i++) {

                // check if this disjunction is the true one
                if (BigInteger.valueOf(j).compareTo(vote_rs[i]) == 0) {
                    u = genRandom(limit);
                    t[i] = g0.modPow(u, p);
                } else {
                    r[i] = genRandom(limit);
                    ch[i] = genRandom(limit);
                    t[i] = g0.modPow(r[i],p).multiply(b_ps[i].multiply(g0.modInverse(p).modPow(BigInteger.valueOf(j),p)).modPow(ch[i],p).mod(p)).mod(p);
                }

                arr[i+2] = b_ps[i];
                arr[i+2+num_candidates] = t[i];

            }

            ch_grand = hash(arr).mod(q);

            for (int i = 0; i < num_candidates; i++) {
                if (BigInteger.valueOf(j).compareTo(vote_rs[i]) == 0) {
                    continue;
                }
                ch_sum = ch_sum.add(ch[i]).mod(q);
            }

            for (int i = 0; i < num_candidates; i++) {
                if (BigInteger.valueOf(j).compareTo(vote_rs[i]) == 0) {
                    nizkp2[j][i] = ch_grand.subtract(ch_sum).mod(q);
                    nizkp2[j][i+num_candidates] = u.subtract(nizkp2[j][i].multiply(x_rs[i])).mod(q);
                } else {
                    nizkp2[j][i] = ch[i];
                    nizkp2[j][i+num_candidates] = r[i];
                }

            }

        }

        return nizkp2;

    }


    // helper function to compute differences within a preference relation
    // parameterised over c
    public BigInteger computePrefDiffsAt(int c, BigInteger[][] m) {

        BigInteger a = BigInteger.ZERO;
        BigInteger b = BigInteger.ZERO;

        for (int i = c+1; i < num_candidates; i++) {
            a = a.add(m[c][i-c-1]);
        }

        for (int i = 0; i < c; i++) {
            b = b.add(m[i][c-i-1]);
        }
        //return a.subtract(b).add(BigInteger.valueOf(c)).mod(p);
        return a.subtract(b).mod(p);

    }

    // helper function to compute divisions within a preference relation
    // parameterised over c
    public BigInteger computePrefDivsAt(int c, BigInteger[][] m) {

        BigInteger a = BigInteger.ONE;
        BigInteger b = BigInteger.ONE;

        for (int i = c+1; i < num_candidates; i++) {
            a = a.multiply(m[c][i-c-1]);
        }

        for (int i = 0; i < c; i++) {
            b = b.multiply(m[i][c-i-1]);
        }

        return a.multiply(b.modInverse(p)).mod(p);

    }

}
