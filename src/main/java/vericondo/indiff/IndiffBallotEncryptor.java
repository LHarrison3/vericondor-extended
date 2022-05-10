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

    public IndiffBallot makeBallot(int voter, int[][] vote) {

        BigInteger[][] b = new BigInteger[num_candidates][num_candidates];
        BigInteger[][] y = new BigInteger[num_candidates][num_candidates];

        BigInteger[][] x = generateRandomness();

        for (int i = 0; i < num_candidates; i++) {
            for (int j = 0; j < num_candidates; j++) {
                if (i == j) {
                    b[i][j] = BigInteger.ONE;
                    y[i][j] = BigInteger.ONE;
                } else {
                    b[i][j] = g0.modPow(x[i][j], p).multiply(g1.modPow(BigInteger.valueOf(vote[i][j]), p)).mod(p);
                    y[i][j] = g1.modPow(x[i][j], p);
                }
            }
        }

        // initialise arrays to hold each conjunction of the NIZKP Pwf
        // there's five conjunctions and each nizkp has a different size

        // y(i,j) = g1^x(i,j)
        BigInteger[][][] nizkp0 = genNIZKP0(voter, x, y);

        // b(i,j) = g0^x(i,j) || b(i,j) / g0 = g0^x(i,j)
        BigInteger[][][] nizkp1 = genNIZKP1(voter, vote, x, b);

        // row sums equal + entries different
        BigInteger[][][] nizkp2 = genNIZKP2(voter, vote, x, b, y);

        // row sums different + entries equal
        BigInteger[][][] nizkp3 = genNIZKP3(voter, vote, x, b, y);

        return new IndiffBallot(voter, vote, b, y, x, nizkp0, nizkp1, nizkp2, nizkp3);
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
                r = w.subtract(c.multiply(x[i][j])).mod(p.subtract(BigInteger.ONE));
                nizkp0[i][j][0] = c;
                nizkp0[i][j][1] = r;
            }
        }

        return nizkp0;

    }

    // helper function to generate NIZKP for the second conjunction
    private BigInteger[][][] genNIZKP1(int voter, int[][] vote, BigInteger[][] x, BigInteger[][] b) {

        BigInteger limit = q.subtract(BigInteger.ONE);

        BigInteger[][][] nizkp1 = new BigInteger[num_candidates][num_candidates][4];

        // the goal is to prove that b(i,j) is the encrpytion of a zero or a one
        // the prover (the object of this class) knows which case is real
        // the proof strategy changes depending on which case is real
        // we provide a proof for the real case and a simulated one for the fake case

        BigInteger u_a;
        BigInteger u_b;
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
                u_a = genRandom(limit);
                u_b = genRandom(limit);
                w = genRandom(limit);
                // here we know the real case is an entry that is 0
                if (vote[i][j] == 0) {
                    t1 = g0.modPow(u_a,p);
                    t2 = (b[i][j].multiply(g1.modInverse(p)).mod(p)).modPow(w,p).multiply(g0.modPow(u_b,p)).mod(p);
                    BigInteger[] arr = {g0, g1, BigInteger.valueOf(voter), b[i][j], t1, t2};
                    c = hash(arr);
                    c2 = w;
                    c1 = c.subtract(c2).mod(q);
                    r1 = u_a.subtract(c1.multiply(x[i][j])).mod(q);
                    r2 = u_b;
                // here we know the real case is an entry that is 1
                } else {
                    t1 = b[i][j].modPow(w,p).multiply(g0.modPow(u_a,p)).mod(p);
                    t2 = g0.modPow(u_b,p);
                    BigInteger[] arr = {g0, g1, BigInteger.valueOf(voter), b[i][j], t1, t2};
                    c = hash(arr);
                    c1 = w;
                    c2 = c.subtract(c1).mod(q);
                    r1 = u_a;
                    r2 = u_b.subtract(c2.multiply(x[i][j])).mod(q);
                }
                nizkp1[i][j][0] = c1;
                nizkp1[i][j][1] = c2;
                nizkp1[i][j][2] = r1;
                nizkp1[i][j][3] = r2;
            }
        }

        return nizkp1;

    }

    // helper function to generate NIZKP for the third conjunction
    // TODO: rewrite triple disjunctive proof here
    private BigInteger[][][] genNIZKP2(int voter, int[][] vote, BigInteger[][] x,
        BigInteger[][] b, BigInteger[][] y) {

        BigInteger limit = q.subtract(BigInteger.ONE);

        BigInteger[][][] nizkp2 = new BigInteger[num_candidates][num_candidates][12];

        BigInteger u_a;
        BigInteger u_a_p;
        BigInteger u_b;
        BigInteger u_b_p;
        BigInteger w_a;
        BigInteger w_a_p;
        BigInteger w_b;
        BigInteger w_b_p;
        BigInteger s;
        BigInteger s_p;
        BigInteger c;
        BigInteger c1;
        BigInteger c1_p;
        BigInteger c2;
        BigInteger c2_p;
        BigInteger c3;
        BigInteger c3_p;
        BigInteger r1;
        BigInteger r1_p;
        BigInteger r2;
        BigInteger r2_p;
        BigInteger r3;
        BigInteger r3_p;
        BigInteger t1;
        BigInteger t1_p;
        BigInteger t2;
        BigInteger t2_p;
        BigInteger t3;
        BigInteger t3_p;
        BigInteger y_prod_min;
        BigInteger b_prod_min;
        BigInteger x_sum_min;
        BigInteger v_sum_min;

        BigInteger[][] v = new BigInteger[num_candidates][num_candidates];
        for (int i = 0; i < num_candidates; i++) {
            for (int j = 0; j < num_candidates; j++) {
                v[i][j] = BigInteger.valueOf(vote[i][j]);
            }
        }

        for (int i = 0; i < num_candidates; i++) {

            BigInteger y_il_prod = rowProductAt(y, i);
            BigInteger b_il_prod = rowProductAt(b, i);

            BigInteger x_il_sum = rowSumAt(x, i);

            BigInteger v_il_sum = rowSumAt(v, i);

            for (int j = 0; j < num_candidates; j++) {
                // this property doesn't make sense for comparions between the same candidate
                // so we just skip it for the NIZKP between the same candidate
                if (i == j) {
                    continue;
                }

                BigInteger y_jl_prod = rowProductAt(y, j);
                BigInteger b_jl_prod = rowProductAt(b, j);

                BigInteger x_jl_sum = rowSumAt(x, j);

                BigInteger v_jl_sum = rowSumAt(v, j);

                y_prod_min = y_il_prod.multiply(y_jl_prod.modInverse(p)).mod(p);
                b_prod_min = b_il_prod.multiply(b_jl_prod.modInverse(p)).mod(p);

                //System.out.println("y_prod_min is: " + y_prod_min.toString());

                x_sum_min = x_il_sum.subtract(x_jl_sum).mod(p.subtract(BigInteger.ONE));
                //System.out.println("x_sum_min is: " + x_sum_min.toString());

                v_sum_min = v_il_sum.subtract(v_jl_sum).mod(p.subtract(BigInteger.ONE));

                u_a = genRandom(limit);
                u_a_p = genRandom(limit);
                u_b = genRandom(limit);
                u_b_p = genRandom(limit);
                w_a = genRandom(limit);
                w_a_p = genRandom(limit);
                w_b = genRandom(limit);
                w_b_p = genRandom(limit);
                s = genRandom(limit);
                s_p = genRandom(limit);

                // v_ij = 0 && v_ji = 1
                if (vote[i][j] == 0 && vote[j][i] == 1) {
                    t1 = b_prod_min.modPow(w_a,p).multiply(g0.modPow(u_a,p)).mod(p);
                    t1_p = y_prod_min.modPow(w_a_p,p).multiply(g1.modPow(u_a_p,p)).mod(p);
                    t2 = g0.modPow(s,p);
                    t2_p = g0.modPow(s_p,p);
                    t3 = (b[i][j].multiply(g1.modInverse(p)).mod(p)).modPow(w_b,p).multiply(g0.modPow(u_b,p)).mod(p);
                    t3_p = b[j][i].modPow(w_b_p,p).multiply(g0.modPow(u_b_p,p)).mod(p);
                    BigInteger[] arr = {g0, g1, b_prod_min, y_prod_min, b[i][j], b[j][i], t1, t1_p, t2, t2_p, t3, t3_p};
                    c = hash(arr);
                    c1 = w_a;
                    c1_p = w_a_p;
                    c3 = w_b;
                    c3_p = w_b_p;
                    c2 = c.subtract(c1).subtract(c3).mod(p.subtract(BigInteger.ONE));
                    c2_p = c.subtract(c1_p).subtract(c3_p).mod(p.subtract(BigInteger.ONE));
                    r1 = u_a;
                    r1_p = u_a_p;
                    r2 = s.subtract(c2.multiply(x[i][j])).mod(p.subtract(BigInteger.ONE));
                    r2_p = s_p.subtract(c2_p.multiply(x[j][i])).mod(p.subtract(BigInteger.ONE));
                    r3 = u_b;
                    r3_p = u_b_p;
                // v_ij = 1 && v_ji = 0
                } else if (vote[i][j] == 1 && vote[j][i] == 0) {
                    t1 = b_prod_min.modPow(w_a,p).multiply(g0.modPow(u_a,p)).mod(p);
                    t1_p = y_prod_min.modPow(w_a_p,p).multiply(g1.modPow(u_a_p,p)).mod(p);
                    t2 = b[i][j].modPow(w_b,p).multiply(g0.modPow(u_b,p)).mod(p);
                    t2_p = (b[j][i].multiply(g1.modInverse(p))).modPow(w_b_p,p).multiply(g0.modPow(u_b_p,p)).mod(p);
                    t3 = g0.modPow(s,p);
                    t3_p = g0.modPow(s_p,p);
                    BigInteger[] arr = {g0, g1, b_prod_min, y_prod_min, b[i][j], b[j][i], t1, t1_p, t2, t2_p, t3, t3_p};
                    c = hash(arr);
                    c1 = w_a;
                    c1_p = w_a_p;
                    c2 = w_b;
                    c2_p = w_b_p;
                    c3 = c.subtract(c1).subtract(c2).mod(p.subtract(BigInteger.ONE));
                    c3_p = c.subtract(c1_p).subtract(c2_p).mod(p.subtract(BigInteger.ONE));
                    r1 = u_a;
                    r1_p = u_a_p;
                    r2 = u_b;
                    r2_p  = u_b_p;
                    r3 = s.subtract(c3.multiply(x[i][j])).mod(p.subtract(BigInteger.ONE));
                    r3_p = s_p.subtract(c3_p.multiply(x[j][i])).mod(p.subtract(BigInteger.ONE));
                } else {
                    //System.out.println("generated for (" + i + "," + j + ")");
                    //System.out.println("v_sum_min is: " + v_sum_min.toString());
                    //System.out.println("a is: " + x_sum_min.toString());
                    //System.out.println("this should be true: " + (b_prod_min.compareTo(g0.modPow(x_sum_min,p)) == 0));
                    //System.out.println("this should also be true: " + (y_prod_min.compareTo(g1.modPow(x_sum_min.mod(p.subtract(BigInteger.ONE)),p)) == 0));
                    t1 = g0.modPow(s,p);
                    t1_p = g1.modPow(s_p,p);
                    t2 = b[i][j].modPow(w_a,p).multiply(g0.modPow(u_a,p)).mod(p);
                    t2_p = (b[j][i].multiply(g1.modInverse(p)).mod(p)).modPow(w_a_p,p).multiply(g0.modPow(u_a_p,p)).mod(p);
                    t3 = (b[i][j].multiply(g1.modInverse(p)).mod(p)).modPow(w_b,p).multiply(g0.modPow(u_b,p)).mod(p);
                    t3_p = b[j][i].modPow(w_b_p,p).multiply(g0.modPow(u_b_p,p)).mod(p);
                    BigInteger[] arr = {g0, g1, b_prod_min, y_prod_min, b[i][j], b[j][i], t1, t1_p, t2, t2_p, t3, t3_p};
                    c = hash(arr);
                    c2 = w_a;
                    c2_p = w_a_p;
                    c3 = w_b;
                    c3_p = w_b_p;
                    c1 = c.subtract(c2).subtract(c3).mod(p.subtract(BigInteger.ONE));
                    c1_p = c.subtract(c2_p).subtract(c3_p).mod(p.subtract(BigInteger.ONE));
                    r1 = s.subtract(c1.multiply(x_sum_min)).mod(p.subtract(BigInteger.ONE));
                    r1_p = s_p.subtract(c1_p.multiply(x_sum_min)).mod(p.subtract(BigInteger.ONE));
                    r2 = u_a;
                    r2_p = u_a_p;
                    r3 = u_b;
                    r3_p = u_b_p;
                    //System.out.println("t1_p should be: " + t1_p.toString().substring(0,4));
                }

                nizkp2[i][j][0] = c1;
                nizkp2[i][j][1] = c1_p;
                nizkp2[i][j][2] = c2;
                nizkp2[i][j][3] = c2_p;
                nizkp2[i][j][4] = c3;
                nizkp2[i][j][5] = c3_p;
                nizkp2[i][j][6] = r1;
                nizkp2[i][j][7] = r1_p;
                nizkp2[i][j][8] = r2;
                nizkp2[i][j][9] = r2_p;
                nizkp2[i][j][10] = r3;
                nizkp2[i][j][11] = r3_p;
            }
        }
        return nizkp2;
    }

    private BigInteger[][][] genNIZKP3(int voter, int[][] vote, BigInteger[][] x,
        BigInteger[][] b, BigInteger[][] y) {

        BigInteger limit = q.subtract(BigInteger.ONE);
        BigInteger[][][] nizkp3 = new BigInteger[num_candidates][num_candidates][8];

        BigInteger[][] v = new BigInteger[num_candidates][num_candidates];
        for (int i = 0; i < num_candidates; i++) {
            for (int j = 0; j < num_candidates; j++) {
                v[i][j] = BigInteger.valueOf(vote[i][j]);
            }
        }

        BigInteger r;
        BigInteger r_alpha;
        BigInteger r_alpha_p;
        BigInteger r_beta;
        BigInteger r_beta_p;
        BigInteger t1;
        BigInteger t1_p;
        BigInteger t2;
        BigInteger c;
        BigInteger c1;
        BigInteger c2;
        BigInteger s1;
        BigInteger s1_p;
        BigInteger s2;
        BigInteger y_prod_min;
        BigInteger b_prod_min;
        BigInteger x_sum_min;
        BigInteger v_sum_min;
        BigInteger T;
        BigInteger P;
        BigInteger Q;
        BigInteger phi;
        BigInteger psi;
        for (int i = 0; i < num_candidates; i++) {

            BigInteger y_il_prod = rowProductAt(y, i);
            BigInteger b_il_prod = rowProductAt(b, i);

            BigInteger x_il_sum = rowSumAt(x, i);
            BigInteger v_il_sum = rowSumAt(v, i);

            for (int j = 0; j < num_candidates; j++) {

                if (i == j) {
                    continue;
                }

                BigInteger y_jl_prod = rowProductAt(y, j);
                BigInteger b_jl_prod = rowProductAt(b, j);

                BigInteger x_jl_sum = rowSumAt(x, j);
                BigInteger v_jl_sum = rowSumAt(v, j);

                y_prod_min = y_il_prod.multiply(y_jl_prod.modInverse(p)).mod(p);
                b_prod_min = b_il_prod.multiply(b_jl_prod.modInverse(p)).mod(p);

                x_sum_min = x_il_sum.subtract(x_jl_sum).mod(p.subtract(BigInteger.ONE));
                v_sum_min = v_il_sum.subtract(v_jl_sum).mod(p.subtract(BigInteger.ONE));

                // generate necessary randomness for both disjunctive branches
                r = genRandom(limit);
                r_alpha = genRandom(limit);
                r_alpha_p = genRandom(limit);
                r_beta = genRandom(limit);
                r_beta_p = genRandom(limit);

                P = y_prod_min;
                Q = b_prod_min;

                if (!(vote[i][j] == 1 && vote[j][i] == 1)) {

                    T = g1.modPow(v_sum_min,p).modPow(r,p).modInverse(p);
                    phi = r.multiply(x_sum_min).mod(p.subtract(BigInteger.ONE));
                    psi = r.negate().mod(p.subtract(BigInteger.ONE));

                    t1 = g0.modPow(r_alpha,p).multiply(Q.modPow(r_alpha_p,p)).mod(p);
                    t1_p = g1.modPow(r_alpha,p).multiply(P.modPow(r_alpha_p,p)).mod(p);
                    t2 = (b[i][j].multiply(b[j][i]).mod(p)).multiply((g1.modPow(BigInteger.valueOf(2),p)).modInverse(p)).modPow(r_beta,p).multiply(g0.modPow(r_beta_p,p)).mod(p);
                    BigInteger[] arr = {g0,g1,Q,P,T,BigInteger.ONE,t1,t1_p,t2};
                    c = hash(arr).mod(p.subtract(BigInteger.ONE));
                    c2 = r_beta;
                    c1 = c.subtract(c2).mod(p.subtract(BigInteger.ONE));
                    s1 = r_alpha.add(phi.multiply(c1).mod(p.subtract(BigInteger.ONE))).mod(p.subtract(BigInteger.ONE));
                    s1_p = r_alpha_p.add(psi.multiply(c1).mod(p.subtract(BigInteger.ONE))).mod(p.subtract(BigInteger.ONE));
                    s2 = r_beta_p;
                } else {

                    T = g1.modPow(r,p);

                    t1 = g0.modPow(r_alpha,p).multiply(Q.modPow(r_alpha_p,p)).mod(p).multiply(T.modPow(r_beta_p,p).modInverse(p)).mod(p);
                    t1_p = g1.modPow(r_alpha,p).multiply(P.modPow(r_alpha_p,p)).mod(p);
                    t2 = g0.modPow(r_beta,p);
                    BigInteger[] arr = {g0,g1,Q,P,T,BigInteger.ONE,t1,t1_p,t2};
                    c = hash(arr).mod(p.subtract(BigInteger.ONE));
                    c1 = r_beta_p;
                    c2 = c.subtract(c1).mod(p.subtract(BigInteger.ONE));
                    s1 = r_alpha;
                    s1_p = r_alpha_p;
                    s2 = r_beta.subtract((x[i][j].add(x[j][i])).mod(p.subtract(BigInteger.ONE)).multiply(c2).mod(p.subtract(BigInteger.ONE))).mod(p.subtract(BigInteger.ONE));

                }

                nizkp3[i][j][0] = c1;
                nizkp3[i][j][1] = c2;
                nizkp3[i][j][2] = s1;
                nizkp3[i][j][3] = s1_p;
                nizkp3[i][j][4] = s2;
                nizkp3[i][j][5] = T;

            }

        }

        return nizkp3;

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
