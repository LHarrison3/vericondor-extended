package vericondo;

import java.util.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
This class creates ballots from votes.
*/
public class IndiffBallotEncryptor extends BallotEncryptor {

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

    // helper function to generate NIZKP for fourth conjunction
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
                    t1_p = b[j][i].modPow(cr,p).modInverse(p).multiply(g0.modPow(s1_p,p)).mod(p);
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
                    t0_p = b[j][i].multiply(g1.modInverse(p)).mod(p).modPow(cl,p).modInverse(p).multiply(g0.modPow(s0_p,p)).mod(p);
                    BigInteger[] arr = {g0, b[i][j], b[i][j].multiply(g1.modInverse(p)).mod(p), b[j][i], b[j][i].multiply(g1.modInverse(p)).mod(p), t0, t0_p, t1, t1_p};
                    c = hash(arr).mod(q);
                    cr = c.subtract(cl).mod(q);
                    s1 = r.add(x[i][j].multiply(cr).mod(q)).mod(q);
                    s1_p = r_p.add(x[j][i].multiply(cr).mod(q)).mod(q);

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

    // helper function to generate NIZKP for fifth conjunction
    private BigInteger[][] genNIZKP4(int voter, int[][] vote, BigInteger[][] x, 
        BigInteger[][] b) {

        BigInteger limit = q.subtract(BigInteger.ONE);

        BigInteger[][] nizkp4 = new BigInteger[num_candidates][num_candidates * 2];

        int index = 0;

        // compute needed row sums and row products
        BigInteger[][] vote_big = new BigInteger[num_candidates][num_candidates];
        for (int i = 0; i < num_candidates; i++) {
            for (int j = 0; j < num_candidates; j++) {
                vote_big[i][j] = BigInteger.valueOf(vote[i][j]);
            }

        }

        BigInteger[] x_rs = computeRowSums(x);
        BigInteger[] vote_rs = computeRowSums(vote_big);
        BigInteger[] b_ps = computeRowProducts(b);

        BigInteger u = BigInteger.ZERO;
        BigInteger[] r = new BigInteger[num_candidates];
        BigInteger[] t = new BigInteger[num_candidates];
        BigInteger[] c = new BigInteger[num_candidates];
        BigInteger[] arr = new BigInteger[2 + 2*num_candidates];
        BigInteger c_grand;
        for (int j = 0; j < num_candidates; j++) {

            arr[0] = BigInteger.valueOf(voter);
            arr[1] = BigInteger.valueOf(j);
            BigInteger c_sum = BigInteger.ZERO;

            for (int i = 0; i < num_candidates; i++) {

                // check if this disjunction is the true one
                if (BigInteger.valueOf(j).compareTo(vote_rs[i]) == 0) {
                    u = genRandom(limit);
                    t[i] = g0.modPow(u, p);
                } else {
                    r[i] = genRandom(limit);
                    c[i] = genRandom(limit);
                    t[i] = g0.modPow(r[i],p).multiply(b_ps[i].multiply(g1.modInverse(p).modPow(BigInteger.valueOf(j),p)).modPow(c[i],p).mod(p)).mod(p);
                }

                arr[i+2] = b_ps[i];
                arr[i+2+num_candidates] = t[i];

            }

            c_grand = hash(arr).mod(q);

            for (int i = 0; i < num_candidates; i++) {
                if (BigInteger.valueOf(j).compareTo(vote_rs[i]) == 0) {
                    continue;
                }
                c_sum = c_sum.add(c[i]).mod(q);
            }

            for (int i = 0; i < num_candidates; i++) {
                if (BigInteger.valueOf(j).compareTo(vote_rs[i]) == 0) {
                    nizkp4[j][i] = c_grand.subtract(c_sum).mod(q);
                    nizkp4[j][i+num_candidates] = u.subtract(nizkp4[j][i].multiply(x_rs[i])).mod(q);
                } else {
                    nizkp4[j][i] = c[i];
                    nizkp4[j][i+num_candidates] = r[i];
                }

            }

        }

        return nizkp4;

    }

    // helper function to generate NIZKP for sixth conjunction
    private BigInteger[][][] genNIZKP5(int voter, int[][] vote, BigInteger[][] x_indiff, 
        BigInteger[][] b_indiff) {

        // the proof is the same as the third albeit with different values
        return genNIZKP2(voter, vote, x_indiff, b_indiff);

    }

    // helper function to generate NIZKP for seventh conjunction
    private BigInteger[][][] genNIZKP6(int voter, int[][] vote, BigInteger[][] x, 
        BigInteger[][] b, int[][] vote_indiff, BigInteger[][] x_indiff, 
        BigInteger[][] b_indiff) {

        BigInteger limit = q.subtract(BigInteger.ONE);

        BigInteger[][][] nizkp6 = new BigInteger[num_candidates][num_candidates][6];

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

                // va_ij = 0 (strict preference)
                if (vote[i][j] == 0) {

                    r = genRandom(limit);
                    t0 = g0.modPow(r,p);
                    cr = genRandom(limit);
                    s1 = genRandom(limit);
                    t1 = b_indiff[i][j].modPow(cr,p).modInverse(p).multiply(g0.modPow(s1,p)).mod(p);
                    BigInteger[] arr = {BigInteger.valueOf(voter), g0, b[i][j], b_indiff[i][j], t0, t1};
                    c = hash(arr).mod(q);
                    cl = c.subtract(cr).mod(q);
                    s0 = r.add(x[i][j].multiply(cl).mod(q)).mod(q);

                // vb_ij = 0 (indifference)
                } else {

                    r = genRandom(limit);
                    t1 = g0.modPow(r,p);
                    cl = genRandom(limit);
                    s0 = genRandom(limit);
                    t0 = b[i][j].modPow(cl,p).modInverse(p).multiply(g0.modPow(s0,p)).mod(p);
                    BigInteger[] arr = {BigInteger.valueOf(voter), g0, b[i][j], b_indiff[i][j], t0, t1};
                    c = hash(arr).mod(q);
                    cr = c.subtract(cl).mod(q);
                    s1 = r.add(x_indiff[i][j].multiply(cr).mod(q)).mod(q);

                }

                nizkp6[i][j][0] = t0;
                nizkp6[i][j][1] = t1;
                nizkp6[i][j][2] = s0;
                nizkp6[i][j][3] = s1;
                nizkp6[i][j][4] = cl;
                nizkp6[i][j][5] = cr;

            }
        }

        return nizkp6;


        

    }

    // helper function to generate NIZKP for final conjunction
    private BigInteger[][][][] genNIZKP7(int voter, int[][] vote, BigInteger[][] x, 
        BigInteger[][] b, int[][] vote_indiff, BigInteger[][] x_indiff,
        BigInteger[][] b_indiff) {

        BigInteger limit = q.subtract(BigInteger.ONE);

        BigInteger[][][][] nizkp7 = new BigInteger[num_candidates][num_candidates][num_candidates][13];

        BigInteger r;
        BigInteger r_p;

        BigInteger t0;
        BigInteger t0_p;
        BigInteger t1;
        BigInteger t1_p;
        BigInteger t2;

        BigInteger s0;
        BigInteger s0_p;
        BigInteger s1;
        BigInteger s1_p;
        BigInteger s2;
        BigInteger cf = BigInteger.ZERO;

        BigInteger cl;
        BigInteger cr;
        BigInteger c;

        for (int i = 0; i < num_candidates; i++) {
            for (int j = 0; j < num_candidates; j++) {
                if (i == j) {
                    continue;
                }

                // we have three disjunctive branches to consider for the final NIZKP
                // the first branch concerns whether we have a 0 entry in the indifference matrix at (i,j)
                if (vote_indiff[i][j] == 0) {

                    r = genRandom(limit);
                    t2 = g0.modPow(r,p);

                    // we fake the proofs for the other two branches (which are parameterised over all k s.t. k != i != j)
                    for (int k = 0; k < num_candidates; k++) {
                        if (k == i || k == j) {
                            continue;
                        }

                        cl = genRandom(limit);
                        cr = genRandom(limit);
                        s0 = genRandom(limit);
                        s0_p = genRandom(limit);
                        s1 = genRandom(limit);
                        s1_p = genRandom(limit);

                        t0 = b[i][k].multiply(b_indiff[i][k]).mod(p).modPow(cr,p).modInverse(p).multiply(g0.modPow(s0,p)).mod(p);
                        t0_p = b[j][k].multiply(b_indiff[j][k]).mod(p).modPow(cr,p).modInverse(p).multiply(g0.modPow(s0_p,p)).mod(p);
                        t1 = b[i][k].multiply(b_indiff[i][k]).mod(p).multiply(g1.modInverse(p)).mod(p).modPow(cl,p).modInverse(p).multiply(g0.modPow(s1,p)).mod(p);
                        t1_p = b[j][k].multiply(b_indiff[j][k]).mod(p).multiply(g1.modInverse(p)).mod(p).modPow(cl,p).modInverse(p).multiply(g0.modPow(s1_p,p)).mod(p);

                        BigInteger[] arr = {g0, b[i][k].multiply(b_indiff[i][k]).mod(p), b[j][k].multiply(b_indiff[j][k]).mod(p), t0, t0_p, t1, t1_p, t2};
                        c = hash(arr);

                        cf = c.subtract(cl).mod(q).subtract(cr).mod(q);
                        s2 = r.add(x_indiff[i][j].multiply(cf).mod(q)).mod(q);

                        nizkp7[i][j][k][0] = t0;
                        nizkp7[i][j][k][1] = t0_p;
                        nizkp7[i][j][k][2] = t1;
                        nizkp7[i][j][k][3] = t1_p;
                        nizkp7[i][j][k][4] = t2;
                        nizkp7[i][j][k][5] = s0;
                        nizkp7[i][j][k][6] = s0_p;
                        nizkp7[i][j][k][7] = s1;
                        nizkp7[i][j][k][8] = s1_p;
                        nizkp7[i][j][k][9] = s2;
                        nizkp7[i][j][k][10] = cf;
                        nizkp7[i][j][k][11] = cl;
                        nizkp7[i][j][k][12] = cr;

                    }

                // if the first branch does not hold, we need to determine which of the remaining two branches hold over all k != i != j
                } else {
                
                    cf = genRandom(limit);
                    s2 = genRandom(limit);
                    t2 = b_indiff[i][j].modPow(cf,p).modInverse(p).multiply(g0.modPow(s2,p)).mod(p);

                    for (int k = 0; k < num_candidates; k++) {
                        if (k == i || k == j) {
                            continue;
                        }

                        r = genRandom(limit);
                        r_p = genRandom(limit);

                        // the second branch concerns whether v[i][k] + v_indiff[i][k] == v[j][k] + v_indiff[j][k] == 1
                        if (vote[i][k] + vote_indiff[i][k] == 1 && vote[j][k] + vote_indiff[j][k] == 1) {

                            t1 = g0.modPow(r,p);
                            t1_p = g0.modPow(r_p,p);

                            cr = genRandom(limit);
                            s0 = genRandom(limit);
                            s0_p = genRandom(limit);

                            t0 = b[i][k].multiply(b_indiff[i][k]).mod(p).modPow(cr,p).modInverse(p).multiply(g0.modPow(s0,p)).mod(p);
                            t0_p = b[j][k].multiply(b_indiff[j][k]).mod(p).modPow(cr,p).modInverse(p).multiply(g0.modPow(s0_p,p)).mod(p);

                            BigInteger[] arr = {g0, b[i][k].multiply(b_indiff[i][k]).mod(p), b[j][k].multiply(b_indiff[j][k]).mod(p), t0, t0_p, t1, t1_p, t2};
                            c = hash(arr);

                            cl = c.subtract(cr).mod(q).subtract(cf).mod(q);
                            s1 = r.add(x[i][k].add(x_indiff[i][k]).mod(q).multiply(cl).mod(q)).mod(q);
                            s1_p = r_p.add(x[j][k].add(x_indiff[j][k]).mod(q).multiply(cl).mod(q)).mod(q);

                        // the final branch must be that v[i][k] + v_indiff[i][k] == v[j][k] + v_indiff[j][k] == 0
                        } else {

                            t0 = g0.modPow(r,p);
                            t0_p = g0.modPow(r_p,p);

                            cl = genRandom(limit);
                            s1 = genRandom(limit);
                            s1_p = genRandom(limit);

                            t1 = b[i][k].multiply(b_indiff[i][k]).mod(p).multiply(g1.modInverse(p)).mod(p).modPow(cl,p).modInverse(p).multiply(g0.modPow(s1,p)).mod(p);
                            t1_p = b[j][k].multiply(b_indiff[j][k]).mod(p).multiply(g1.modInverse(p)).mod(p).modPow(cl,p).modInverse(p).multiply(g0.modPow(s1_p,p)).mod(p);

                            BigInteger[] arr = {g0, b[i][k].multiply(b_indiff[i][k]).mod(p), b[j][k].multiply(b_indiff[j][k]).mod(p), t0, t0_p, t1, t1_p, t2};
                            c = hash(arr);

                            cr = c.subtract(cl).mod(q).subtract(cf).mod(q);
                            s0 = r.add(x[i][k].add(x_indiff[i][k]).mod(q).multiply(cr).mod(q)).mod(q);
                            s0_p = r_p.add(x[j][k].add(x_indiff[j][k]).mod(q).multiply(cr).mod(q)).mod(q);

                        }

                        nizkp7[i][j][k][0] = t0;
                        nizkp7[i][j][k][1] = t0_p;
                        nizkp7[i][j][k][2] = t1;
                        nizkp7[i][j][k][3] = t1_p;
                        nizkp7[i][j][k][4] = t2;
                        nizkp7[i][j][k][5] = s0;
                        nizkp7[i][j][k][6] = s0_p;
                        nizkp7[i][j][k][7] = s1;
                        nizkp7[i][j][k][8] = s1_p;
                        nizkp7[i][j][k][9] = s2;
                        nizkp7[i][j][k][10] = cf;
                        nizkp7[i][j][k][11] = cl;
                        nizkp7[i][j][k][12] = cr;

                    }

                }

            }

        }

        return nizkp7;

    }

    // helper function to compute row products
    public BigInteger[] computeRowProducts(BigInteger[][] m) {
        BigInteger[] r = new BigInteger[num_candidates];
        for (int i = 0; i < num_candidates; i++) {
            r[i] = m[i][0];
        }
        for (int i = 0; i < num_candidates; i++) {
            for (int j = 1; j < num_candidates; j++) {
                r[i] = r[i].multiply(m[i][j]);
            }
        }
        // we are working under modulus p
        for (int i = 0; i < num_candidates; i++) {
            r[i] = r[i].mod(p);
        }
        return r;
    }

    // helper function to compute row sums
    protected BigInteger[] computeRowSums(BigInteger[][] m) {
        BigInteger[] r = new BigInteger[num_candidates];
        for (int i = 0; i < num_candidates; i++) {
            r[i] = m[i][0];
        }
        for (int i = 0; i < num_candidates; i++) {
            for (int j = 1; j < num_candidates; j++) {
                r[i] = r[i].add(m[i][j]);
            }
        }
        // we are working under modulus p
        for (int i = 0; i < num_candidates; i++) {
            r[i] = r[i].mod(p);
        }
        return r;
    }


}
