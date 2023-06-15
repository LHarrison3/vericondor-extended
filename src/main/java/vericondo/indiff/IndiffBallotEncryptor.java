package vericondo;

import java.util.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
This class creates ballots from votes.
*/
public class IndiffBallotEncryptor extends BallotEncryptor {

    // TODO: change this code to work with the new protocol for indifference

    // sets up a new encryptor
    public IndiffBallotEncryptor(int num_candidates, BigInteger g0, BigInteger g1,
        BigInteger p, BigInteger q) {

        super(num_candidates, g0, g1, p, q);

    }

    // generates a matrix of random numbers
    protected BigInteger[][] generateRandomness() {
        BigInteger limit = q.subtract(BigInteger.ONE);
        BigInteger[][] x = new BigInteger[num_candidates][num_candidates];
        for (int i = 0; i < num_candidates; i++) {
            for (int j = 0; j < num_candidates; j++) {
                if (i == j) {
                    x[i][j] = BigInteger.ZERO;
                } else {
                    // ensures each x(i,j) is between 1 and q-1 (i.e. in Z*q)
                    x[i][j] = genRandom(limit);
                }
            }
        }
        return x;
    }

    public IndiffBallot makeBallot(int voter, int[][] vote, int[][] vote_indiff) {

        BigInteger[][] b = new BigInteger[num_candidates][num_candidates];
        BigInteger[][] y = new BigInteger[num_candidates][num_candidates];

        BigInteger[][] b_indiff = new BigInteger[num_candidates][num_candidates];
        BigInteger[][] y_indiff = new BigInteger[num_candidates][num_candidates];


        BigInteger[][] x = generateRandomness();
        BigInteger[][] x_indiff = generateRandomness();

        for (int i = 0; i < num_candidates; i++) {
            for (int j = 0; j < num_candidates; j++) {
                if (i == j) {
                    b[i][j] = BigInteger.ONE;
                    y[i][j] = BigInteger.ONE;
                    b_indiff[i][j] = BigInteger.ONE;
                    y_indiff[i][j] = BigInteger.ONE;
                } else {
                    b[i][j] = g0.modPow(x[i][j], p).multiply(g1.modPow(BigInteger.valueOf(vote[i][j]), p)).mod(p);
                    y[i][j] = g1.modPow(x[i][j], p);
                    b_indiff[i][j] = g0.modPow(x_indiff[i][j], p).multiply(g1.modPow(BigInteger.valueOf(vote_indiff[i][j]), p)).mod(p);
                    y_indiff[i][j] = g1.modPow(x_indiff[i][j], p);
                }
            }
        }

        // initialise arrays to hold each conjunction of the NIZKP Pwf
        // there's eight conjunctions and each nizkp has a different size

        // y_a(i,j) = g1^x_a(i,j)
        BigInteger[][][] nizkp0 = genNIZKP0(voter, x, y);

        // y_b(i,j) = g1^x_b(i,j)
        BigInteger[][][] nizkp1 = genNIZKP1(voter, x_indiff, y_indiff);

        // b_a(i,j) = g0^x_a(i,j) || b_a(i,j) / g1 = g0^x_a(i,j)
        BigInteger[][][] nizkp2 = genNIZKP2(voter, vote, x, b);

        // (b_a(i,j) =  g0^x_a(i,j) && b_a(j,i) / g1 = g0^x_a(j,i)) || (b_a(i,j) / g1 =  g0^x_a(i,j) && b_a(j,i) = g0^x_a(j,i))
        BigInteger[][][] nizkp3 = genNIZKP3(voter, vote, x, b);

        // b_a_prod(i) = g_0^(x_a_sum(i) + j)
        BigInteger[][] nizkp4 = genNIZKP4(voter, vote, x, b);

        // b_b(i,j) = g0^x_b(i,j) || b_b(i,j) / g1 = g0^x_b(i,j) 
        BigInteger[][][] nizkp5 = genNIZKP5(voter, vote_indiff, x_indiff, b_indiff);

        // b_a(i,j) = g0^x_a(i,j) || b_b(i,j) = g0^x_b(i,j)
        BigInteger[][][] nizkp6 = genNIZKP6(voter, vote, x, b, vote_indiff, x_indiff, b_indiff);

        // b_b(i,j) = g0^x_b(i,j) || (k != i != j) : b_a(i,k) * b_b(i,k) = b_a(j,k) * b_b(i,k)
        BigInteger[][][][] nizkp7 = genNIZKP7(voter, vote, x, b, vote_indiff, x_indiff, b_indiff);

        return new IndiffBallot(voter, vote, vote_indiff, b, y, x, b_indiff, y_indiff, x_indiff, nizkp0, nizkp1, nizkp2, nizkp3, nizkp4, nizkp5, nizkp6, nizkp7);
    }

    // helper function to generate NIZKP for the first conjunction
    private BigInteger[][][] genNIZKP0(int voter, BigInteger[][] x, BigInteger[][] y) {

        BigInteger limit = q.subtract(BigInteger.ONE);

        BigInteger[][][] nizkp0 = new BigInteger[num_candidates][num_candidates][2];

        BigInteger r;

        BigInteger t;
        BigInteger c;
        BigInteger s;

        for (int i = 0; i < num_candidates; i++) {
            for (int j = 0; j < num_candidates; j++) {
                r = genRandom(limit);
                t = g1.modPow(r, p);
                BigInteger[] arr = {BigInteger.valueOf(voter), g1, y[i][j], t};
                c = hash(arr);
                s = r.add(c.multiply(x[i][j])).mod(p.subtract(BigInteger.ONE));
                nizkp0[i][j][0] = t;
                nizkp0[i][j][1] = s;
            }

        }

        return nizkp0;

        /*
        BigInteger w;
        BigInteger t;
        BigInteger c;
        BigInteger r;
        for (int i = 0; i < num_candidates; i++) {
            for (int j = 0; j < num_candidates; j++) {
                w = genRandom(limit);
                t = g1.modPow(w, p);
                BigInteger[] arr = {BigInteger.valueOf(voter), g1, y[i][j], t};
                c = hash(arr);
                r = w.subtract(c.multiply(x[i][j])).mod(p.subtract(BigInteger.ONE));
                nizkp0[i][j][0] = c;
                nizkp0[i][j][1] = r;
            }
        }

        return nizkp0;

        */
    }

    // helper function to generate NIZKP for the second conjunction
    private BigInteger[][][] genNIZKP1(int voter, BigInteger[][] x_indiff, BigInteger[][] y_indiff) {

        // the proof is the same as the first one albeit with different values
        return genNIZKP0(voter, x_indiff, y_indiff);

    }

    // helper function to generate NIZKP for the third conjunction
    private BigInteger[][][] genNIZKP2(int voter, int[][] vote, BigInteger[][] x,
        BigInteger[][] b) {

        BigInteger limit = q.subtract(BigInteger.ONE);

        BigInteger[][][] nizkp2 = new BigInteger[num_candidates][num_candidates][6];

        BigInteger r;

        BigInteger t0;
        BigInteger t1;
        BigInteger s0;
        BigInteger s1;
        BigInteger cl;
        BigInteger cr;
        BigInteger c;

        for (int i = 0; i < num_candidates; i++) {
            for (int j = 0; j < num_candidates; j++) {

                // v_ij = 0
                if (vote[i][j] == 0) {

                    r = genRandom(limit);
                    t0 = g0.modPow(r,p);
                    cr = genRandom(limit);
                    s1 = genRandom(limit);
                    t1 = b[i][j].multiply(g1.modInverse(p)).modPow(cr,p).modInverse(p).multiply(g0.modPow(s1,p)).mod(p);
                    BigInteger[] arr = {BigInteger.valueOf(voter), g0, b[i][j], b[i][j].multiply(g1.modInverse(p)).mod(p), t0, t1};
                    c = hash(arr).mod(q);
                    cl = c.subtract(cr).mod(q);
                    s0 = r.add(x[i][j].multiply(cl).mod(q)).mod(q);

                // v_ij = 1
                } else {

                    r = genRandom(limit);
                    t1 = g0.modPow(r,p);
                    cl = genRandom(limit);
                    s0 = genRandom(limit);
                    t0 = b[i][j].modPow(cl,p).modInverse(p).multiply(g0.modPow(s0,p)).mod(p);
                    BigInteger[] arr = {BigInteger.valueOf(voter), g0, b[i][j], b[i][j].multiply(g1.modInverse(p)).mod(p), t0, t1};
                    c = hash(arr).mod(q);
                    cr = c.subtract(cl).mod(q);
                    s1 = r.add(x[i][j].multiply(cr).mod(q)).mod(q);

                }

                nizkp2[i][j][0] = t0;
                nizkp2[i][j][1] = t1;
                nizkp2[i][j][2] = s0;
                nizkp2[i][j][3] = s1;
                nizkp2[i][j][4] = cl;
                nizkp2[i][j][5] = cr;

            }
        }

        return nizkp2;

    }

    private BigInteger[][][] genNIZKP3(int voter, int[][] vote, BigInteger[][] x,
        BigInteger[][] b) {

        BigInteger limit = q.subtract(BigInteger.ONE);

        BigInteger[][][] nizkp3 = new BigInteger[num_candidates][num_candidates][10];

        BigInteger r;
        BigInteger r_p;

        BigInteger t0;
        BigInteger t0_p;
        BigInteger t1;
        BigInteger t1_p;
        BigInteger s0;
        BigInteger s0_p;
        BigInteger s1;
        BigInteger s1_p;
        BigInteger cl;
        BigInteger cr;
        BigInteger c;

        for (int i = 0; i < num_candidates; i++) {
            for (int j = 0; j < num_candidates; j++) {

                // skip over diagonals
                if (i == j) {
                    continue;
                }

                // v_ij = 0 && v_ji = 1
                if (vote[i][j] == 0 && vote[j][i] == 1) {

                    r = genRandom(limit);
                    r_p = genRandom(limit);
                    t0 = g0.modPow(r,p);
                    t0_p = g0.modPow(r_p,p);
                    cr = genRandom(limit);
                    s1 = genRandom(limit);
                    s1_p = genRandom(limit);
                    t1 = b[i][j].multiply(g1.modInverse(p)).mod(p).modPow(cr, p).modInverse(p).multiply(g0.modPow(s1,p)).mod(p);
                    t1_p = b[j][i].modPow(cr,p).modInverse(p).multiply(g0.modPow(s1,p)).mod(p);
                    BigInteger[] arr = {g0, b[i][j], b[i][j].multiply(g1.modInverse(p)).mod(p), b[j][i], b[j][i].multiply(g1.modInverse(p)).mod(p), t0, t0_p, t1, t1_p};
                    c = hash(arr).mod(q);
                    cl = c.subtract(cr).mod(q);
                    s0 = r.add(x[i][j].multiply(cl).mod(q)).mod(q);
                    s0_p = r_p.add(x[j][i].multiply(cl).mod(q)).mod(q);

                // v_ij = 1 && v_ji = 0
                } else {

                    r = genRandom(limit);
                    r_p = genRandom(limit);
                    t1 = g0.modPow(r,p);
                    t1_p = g0.modPow(r_p,p);
                    cl = genRandom(limit);
                    s0 = genRandom(limit);
                    s0_p = genRandom(limit);
                    t0 = b[i][j].modPow(cl,p).modInverse(p).multiply(g0.modPow(s0,p)).mod(p);
                    t0_p = b[j][i].multiply(g1.modInverse(p)).mod(p).modPow(cl,p).modInverse(p).multiply(g0.modPow(s0,p)).mod(p);
                    BigInteger[] arr = {g0, b[i][j], b[i][j].multiply(g1.modInverse(p)).mod(p), b[j][i], b[j][i].multiply(g1.modInverse(p)).mod(p), t0, t0_p, t1, t1_p};
                    c = hash(arr).mod(q);
                    cr = c.subtract(cl).mod(q);
                    s1 = r.add(x[i][j].multiply(cl).mod(q)).mod(q);
                    s1_p = r_p.add(x[j][i].multiply(cl).mod(q)).mod(q);

                }

                //System.out.println("s0: " + s0.toString());

                nizkp3[i][j][0] = t0;
                nizkp3[i][j][1] = t0_p;
                nizkp3[i][j][2] = t1;
                nizkp3[i][j][3] = t1_p;
                nizkp3[i][j][4] = s0;
                nizkp3[i][j][5] = s0_p;
                nizkp3[i][j][6] = s1;
                nizkp3[i][j][7] = s1_p;
                nizkp3[i][j][8] = cl;
                nizkp3[i][j][9] = cr;

            }

        }

        return nizkp3;

    }

    private BigInteger[][] genNIZKP4(int voter, int[][] vote, BigInteger[][] x, 
        BigInteger[][] b) {

        return (new BigInteger[num_candidates][77]);

    }

    private BigInteger[][][] genNIZKP5(int voter, int[][] vote, BigInteger[][] x_indiff, 
        BigInteger[][] b_indiff) {

        return (new BigInteger[num_candidates][num_candidates][77]);

    }

    private BigInteger[][][] genNIZKP6(int voter, int[][] vote, BigInteger[][] x, 
        BigInteger[][] b, int[][] vote_indiff, BigInteger[][] x_indiff, 
        BigInteger[][] b_indiff) {

        return (new BigInteger[num_candidates][num_candidates][77]);

    }

    private BigInteger[][][][] genNIZKP7(int voter, int[][] vote, BigInteger[][] x, 
        BigInteger[][] b, int[][] vote_indiff, BigInteger[][] x_indiff,
        BigInteger[][] b_indiff) {

        return (new BigInteger[num_candidates][num_candidates][num_candidates - 2][77]);

    }

    // helper function to compute specific row product
    public BigInteger rowProductAt(BigInteger[][] m, int i) {
        BigInteger r = BigInteger.ONE;
        for (int l = 0; l < num_candidates; l++) {
            r = r.multiply(m[i][l]);
        }
        r = r.mod(p);
        return r;
    }

    // helper function to compute specific row sum
    public BigInteger rowSumAt(BigInteger[][] m, int i) {
        BigInteger r = BigInteger.ZERO;
        for (int l = 0; l < num_candidates; l++) {
            r = r.add(m[i][l]);
        }
        r = r.mod(p.subtract(BigInteger.ONE));
        return r;
    }

}
