package vericondo;

import java.util.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
This class creates ballots from votes.
*/
public class BaseBallotEncryptor extends BallotEncryptor {


    // sets up a new encryptor
    public BaseBallotEncryptor(int num_candidates, BigInteger g0, BigInteger g1,
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

    public BaseBallot makeBallot(int voter, int[][] vote) {

        BigInteger[][] b = new BigInteger[num_candidates][num_candidates];
        BigInteger[][] y = new BigInteger[num_candidates][num_candidates];

        BigInteger[][] x = generateRandomness();

        for (int i = 0; i < num_candidates; i++) {
            for (int j = 0; j < num_candidates; j++) {
                if (i == j) {
                    b[i][j] = BigInteger.ONE;
                    y[i][j] = BigInteger.ONE;
                } else {
                    b[i][j] = g0.modPow(x[i][j].add(BigInteger.valueOf(vote[i][j])), p);
                    y[i][j] = g1.modPow(x[i][j], p);
                }
            }
        }

        // initialise arrays to hold each conjunction of the NIZKP Pwf
        // there's five conjunctions and each nizkp has a different size

        // y(i,j) = g1^x(i,j)
        BigInteger[][][] nizkp0 = genNIZKP0(voter, x, y);

        // b(i,i) = g0^x(i,i)
        BigInteger[][] nizkp1 = genNIZKP1(voter, x, b);

        // b(i,j) = g0^x(i,j) || b(i,j) / g0 = g0^x(i,j)
        BigInteger[][][] nizkp2 = genNIZKP2(voter, vote, x, b);

        // (b(i,j) = g0^x(i,j) && b(j,i)/g0 = g0^x(j,i))
        // || (b(i,j)/g0 = g0^x(i,j) && b(j,i) = g0^x(j,i))
        BigInteger[][][] nizkp3 = genNIZKP3(voter, vote, x, b);

        // b_prod(i) = g_0^(x_sum(i) + j)
        BigInteger[][] nizkp4 = genNIZKP4(voter, vote, x, b);

        return new BaseBallot(voter, vote, b, y, x, nizkp0, nizkp1, nizkp2, nizkp3, nizkp4);
    }

    // helper function to generate NIZKP for the first conjunction
    private BigInteger[][][] genNIZKP0(int voter, BigInteger[][] x, BigInteger[][] y) {

        BigInteger limit = q.subtract(BigInteger.ONE);

        BigInteger[][][] nizkp0 = new BigInteger[num_candidates][num_candidates][2];

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
                r = w.subtract(c.multiply(x[i][j])).mod(q);
                nizkp0[i][j][0] = c;
                nizkp0[i][j][1] = r;
            }
        }

        return nizkp0;

    }

    // helper function to generate NIZKP for the second conjunction
    private BigInteger[][] genNIZKP1(int voter, BigInteger[][] x, BigInteger[][] b) {

        BigInteger limit = q.subtract(BigInteger.ONE);

        BigInteger[][] nizkp1 = new BigInteger[num_candidates][2];

        BigInteger w;
        BigInteger t;
        BigInteger c;
        BigInteger r;
        for (int i = 0; i < num_candidates; i++) {
            w = genRandom(limit);
            t = g0.modPow(w, p);
            BigInteger[] arr = {BigInteger.valueOf(voter), g0, b[i][i], t};
            c = hash(arr);
            r = w.subtract(c.multiply(x[i][i])).mod(q);
            nizkp1[i][0] = c;
            nizkp1[i][1] = r;
        }

        return nizkp1;

    }

    // helper function to generate NIZKP for the third conjunction
    private BigInteger[][][] genNIZKP2(int voter, int[][] vote, BigInteger[][] x, BigInteger[][] b) {

        BigInteger limit = q.subtract(BigInteger.ONE);

        BigInteger[][][] nizkp2 = new BigInteger[num_candidates][num_candidates][4];

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
        for (int i = 0; i < num_candidates; i++) {
            for (int j = 0; j < num_candidates; j++) {
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
                nizkp2[i][j][0] = c1;
                nizkp2[i][j][1] = c2;
                nizkp2[i][j][2] = r1;
                nizkp2[i][j][3] = r2;
            }
        }

        return nizkp2;

    }

    // helper function to generate NIZKP for the fourth conjunction
    private BigInteger[][][] genNIZKP3(int voter, int[][] vote, BigInteger[][] x, BigInteger[][] b) {

        BigInteger limit = q.subtract(BigInteger.ONE);

        BigInteger[][][] nizkp3 = new BigInteger[num_candidates][num_candidates][8];

        BigInteger u11;
        BigInteger u12;
        BigInteger u21;
        BigInteger u22;
        BigInteger w_a;
        BigInteger w_b;
        BigInteger t11;
        BigInteger t12;
        BigInteger t21;
        BigInteger t22;
        BigInteger c_a;
        BigInteger c_b;
        BigInteger c11;
        BigInteger c12;
        BigInteger c21;
        BigInteger c22;
        BigInteger r11;
        BigInteger r12;
        BigInteger r21;
        BigInteger r22;
        for (int i = 0; i < num_candidates; i++) {
            for (int j = 0; j < num_candidates; j++) {
                // this property doesn't make sense for comparions between the same candidate
                // so we just skip it for the NIZKP between the same candidate
                if (i == j) {
                    continue;
                }
                u11 = genRandom(limit);
                u12 = genRandom(limit);
                u21 = genRandom(limit);
                u22 = genRandom(limit);
                w_a = genRandom(limit);
                w_b = genRandom(limit);
                // v_ij = 0 && v_ji = 1
                if (vote[i][j] == 0) {
                    t11 = b[i][j].modPow(w_a, p).multiply(g0.modPow(u11,p)).mod(p);
                    t12 = b[j][i].multiply(g0.modInverse(p)).mod(p).modPow(w_b,p).multiply(g0.modPow(u12,p)).mod(p);
                    t21 = g0.modPow(u21, p);
                    t22 = g0.modPow(u22, p);
                    BigInteger[] arr_a = {g0, b[i][j], b[j][i], t11, t21};
                    c_a = hash(arr_a);
                    BigInteger[] arr_b = {g0, b[i][j], b[j][i], t12, t22};
                    c_b = hash(arr_b);
                    c11 = w_a;
                    c12 = w_b;
                    c21 = c_a.subtract(c11).mod(q);
                    c22 = c_b.subtract(c12).mod(q);
                    r11 = u11;
                    r12 = u12;
                    r21 = u21.subtract(c21.multiply(x[i][j].subtract(BigInteger.ONE))).mod(q);
                    r22 = u22.subtract(c22.multiply(x[j][i].add(BigInteger.ONE))).mod(q);
                // v_ij = 1 && v_ji = 0
                } else {
                    t11 = g0.modPow(u11, p);
                    t12 = g0.modPow(u12, p);
                    t21 = b[i][j].multiply(g0.modInverse(p)).mod(p).modPow(w_a,p).multiply(g0.modPow(u21,p)).mod(p);
                    t22 = b[j][i].modPow(w_b, p).multiply(g0.modPow(u22,p)).mod(p);
                    BigInteger[] arr_a = {g0, b[i][j], b[j][i], t11, t21};
                    c_a = hash(arr_a);
                    BigInteger[] arr_b = {g0, b[i][j], b[j][i], t12, t22};
                    c_b = hash(arr_b);
                    c21 = w_a;
                    c22 = w_b;
                    c11 = c_a.subtract(c21).mod(q);
                    c12 = c_b.subtract(c22).mod(q);
                    r21 = u21;
                    r22 = u22;
                    r11 = u11.subtract(c11.multiply(x[i][j].add(BigInteger.ONE))).mod(q);
                    r12 = u12.subtract(c12.multiply(x[j][i].subtract(BigInteger.ONE))).mod(q);
                }

                nizkp3[i][j][0] = c11;
                nizkp3[i][j][1] = c21;
                nizkp3[i][j][2] = r11;
                nizkp3[i][j][3] = r21;
                nizkp3[i][j][4] = c12;
                nizkp3[i][j][5] = c22;
                nizkp3[i][j][6] = r12;
                nizkp3[i][j][7] = r22;
            }
        }
        return nizkp3;
    }

    private BigInteger[][] genNIZKP4(int voter, int[][] vote, BigInteger[][] x, BigInteger[][] b) {

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
                    t[i] = g0.modPow(r[i],p).multiply(b_ps[i].multiply(g0.modInverse(p).modPow(BigInteger.valueOf(j),p)).modPow(c[i],p).mod(p)).mod(p);
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
