package vericondo;

import java.util.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class IndiffElection extends Election {

    // mapping of voters to ballots
    private HashMap<Integer, IndiffBallot> ballots;

    // the bulletin board
    private IndiffBulletinBoard bb;

    // helper for creating encrypted ballots and their associated NIZKPs
    private IndiffBallotEncryptor encryptor;

    // create an election
    public IndiffElection(String election_name, String election_date, int num_candidates, int security_bits) {

        super(election_name, election_date, num_candidates, security_bits);

        this.bb = new IndiffBulletinBoard(this.num_candidates, this.g0, this.g1, this.p, this.q);

        this.encryptor = new IndiffBallotEncryptor(this.num_candidates, this.g0, this.g1, this.p, this.q);

        this.num_voters = 0;
        this.ballots = new HashMap<Integer, IndiffBallot>();

    }

    // get the tally of the election
    public int[][] t() {
        return this.bb.t();
    }

    // determine a winner of the election using the specified method
    public int determineWinner(Method method) {
        if (method == Method.MINIMAX) {
            return determineMinimaxWinner(t());
        } else if (method == Method.COPELAND) {
            return determineCopelandWinner(t());
        } else if (method == Method.RANKEDPAIRS) {
            return determineRankedPairsWinner(t());
        } else {
            return determineSchulzeWinner(t());
        }
    }

    // add a ballot to the election
    public int addBallot(int[][] vote, boolean displayPartialReceipt) {

        // make the ballot with its associated NIZKPs
        IndiffBallot ballot = this.encryptor.makeBallot(num_voters, vote);

        // add the ballot to the list
        ballots.put(num_voters, ballot);

        // create a partial receipt for the ballot
        IndiffReceipt receipt = new IndiffReceipt(num_voters, ballot.b(), ballot.y(), ballot.getNIZKP0(),
            ballot.getNIZKP1(), ballot.getNIZKP2(), ballot.getNIZKP3());
        // add it to the BB
        bb.addReceipt(num_voters, receipt);
        // and display it to the screen if the flag is set
        if (displayPartialReceipt) {
            receipt.display();
        }

        // set ID of next voter
        num_voters++;

        // return ID of newly added ballot
        return (num_voters - 1);

    }

    // retrieve a ballot for a specified voter ID
    public IndiffBallot getBallot(int voter) {
        return ballots.get(voter);
    }

    // retrieve the bulletin board
    public IndiffBulletinBoard getBulletinBoard() {
        return this.bb;
    }

    // audit or confirm a ballot
    public void finaliseBallot(int voter, Type type) {

        IndiffBallot ballot = ballots.get(voter);

        if (type == Type.AUDITED) {

            AuditedIndiffReceipt final_receipt = new AuditedIndiffReceipt(voter, ballot.b(), ballot.y(),
                ballot.getNIZKP0(), ballot.getNIZKP1(), ballot.getNIZKP2(), ballot.getNIZKP3(),
                ballot.vote(), ballot.x());

            bb.addReceipt(voter, final_receipt);

        } else {

            ConfirmedIndiffReceipt final_receipt = new ConfirmedIndiffReceipt(voter, ballot.b(),
                ballot.y(), ballot.getNIZKP0(), ballot.getNIZKP1(), ballot.getNIZKP2(),
                ballot.getNIZKP3());

            bb.addReceipt(voter, final_receipt);
            bb.updateTally(ballot.vote());
            bb.updateSum(ballot.x());

        }

        // we no longer need the ballot with everything on the bulletin board
        ballots.remove(voter);

    }

    public void displayBulletinBoard() {
        this.bb.display();
    }

    // verify the entire election
    public boolean verifyElection() {
        return (verifyTallies() && verifyNIZKPS());
    }

    // perform the tally verification equations
    public boolean verifyTallies() {
        int[][] t = bb.t();
        BigInteger[][] s = bb.s();
        ArrayList<IndiffReceipt> confirmed_receipts = bb.getConfirmedReceipts();
        BigInteger[][] b;
        BigInteger[][] y;
        boolean hold = true;
        for (int i = 0; i < num_candidates; i++) {
            for (int j = 0; j < num_candidates; j++) {
                BigInteger prod0 = BigInteger.ONE;
                BigInteger prod1 = BigInteger.ONE;
                for (int k = 0; k < confirmed_receipts.size(); k++) {
                    b = confirmed_receipts.get(k).b();
                    y = confirmed_receipts.get(k).y();
                    prod0 = prod0.multiply(b[i][j]).mod(p);
                    prod1 = prod1.multiply(y[i][j]).mod(p);
                }
                prod0 = prod0.mod(p);
                prod1 = prod1.mod(p);
                BigInteger lhs0 = g0.modPow(s[i][j],p).multiply(g1.modPow(BigInteger.valueOf(t[i][j]),p)).mod(p);
                BigInteger lhs1 = g1.modPow(s[i][j],p);
                if (lhs0.compareTo(prod0) != 0) {
                    hold = false;
                }
                if (lhs1.compareTo(prod1) != 0) {
                    hold = false;
                }
            }
        }
        return hold;
    }

    // verify NIZKPs for a given receipt
    public boolean verifyNIZKPSforReceipt(IndiffReceipt receipt) {

        int voter = receipt.id();

        boolean hold0 = verifyNIZKP0(voter, receipt.y(), receipt.getNIZKP0());
        boolean hold1 = verifyNIZKP1(voter, receipt.b(), receipt.getNIZKP1());
        boolean hold2 = verifyNIZKP2(voter, receipt.b(), receipt.y(), receipt.getNIZKP2());
        boolean hold3 = verifyNIZKP3(voter, receipt.b(), receipt.y(), receipt.getNIZKP3());
        return (hold0 && hold1 && hold2 && hold3);

    }

    // verify all NIZKPs for all voters
    public boolean verifyNIZKPS() {

        boolean hold = true;

        HashMap<Integer, IndiffReceipt> receipts = bb.getReceipts();
        for (HashMap.Entry<Integer, IndiffReceipt> entry : receipts.entrySet()) {
            IndiffReceipt receipt = entry.getValue();
            hold = verifyNIZKPSforReceipt(receipt);
        }

        return hold;

    }

    // helper function to verify NIZKP for first conjunction
    private boolean verifyNIZKP0(int voter, BigInteger[][] y, BigInteger[][][] nizkp0) {

        boolean hold = true;

        BigInteger t1;
        BigInteger c1;
        BigInteger c;
        BigInteger r;
        for (int i = 0; i < num_candidates; i++) {
            for (int j = 0; j < num_candidates; j++) {
                c = nizkp0[i][j][0];
                r = nizkp0[i][j][1];
                t1 = g1.modPow(r,p).multiply(y[i][j].modPow(c,p)).mod(p);
                BigInteger[] arr = {BigInteger.valueOf(voter), g1, y[i][j], t1};
                c1 = BallotEncryptor.hash(arr);
                if (c.compareTo(c1) != 0) {
                    hold = false;
                }
            }
        }

        return hold;
    }


    // helper function to verify NIZKP for second conjunction
    private boolean verifyNIZKP1(int voter, BigInteger[][] b, BigInteger[][][] nizkp1) {

        boolean hold = true;

        BigInteger t1;
        BigInteger t2;
        BigInteger c1;
        BigInteger c2;
        BigInteger r1;
        BigInteger r2;
        BigInteger h;
        for (int i = 0; i < num_candidates; i++) {
            for (int j = 0; j < num_candidates; j++) {
                c1 = nizkp1[i][j][0];
                c2 = nizkp1[i][j][1];
                r1 = nizkp1[i][j][2];
                r2 = nizkp1[i][j][3];
                t1 = g0.modPow(r1,p).multiply(b[i][j].modPow(c1,p)).mod(p);
                t2 = g0.modPow(r2,p).multiply(b[i][j].multiply(g1.modInverse(p)).mod(p).modPow(c2,p)).mod(p);
                BigInteger[] arr = {g0, g1, BigInteger.valueOf(voter), b[i][j], t1, t2};
                h = c1.add(c2).mod(q);
                if (h.compareTo(BallotEncryptor.hash(arr).mod(q)) != 0) {
                    hold = false;
                }
            }
        }

        return hold;

    }

    // helper function to verify NIZKP for third conjunction
    private boolean verifyNIZKP2(int voter, BigInteger[][] b, BigInteger[][] y, BigInteger[][][] nizkp2) {

        boolean hold = true;

        BigInteger t1;
        BigInteger t1_p;
        BigInteger t2;
        BigInteger t2_p;
        BigInteger t3;
        BigInteger t3_p;
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
        BigInteger y_prod_min;
        BigInteger b_prod_min;
        BigInteger h1;
        BigInteger h2;
        for (int i = 0; i < num_candidates; i++) {

            BigInteger y_il_prod = encryptor.rowProductAt(y, i);
            BigInteger b_il_prod = encryptor.rowProductAt(b, i);

            for (int j = 0; j < i; j++) {

                BigInteger y_jl_prod = encryptor.rowProductAt(y, j);
                BigInteger b_jl_prod = encryptor.rowProductAt(b, j);

                y_prod_min = y_il_prod.multiply(y_jl_prod.modInverse(p)).mod(p);
                b_prod_min = b_il_prod.multiply(b_jl_prod.modInverse(p)).mod(p);

                c1 = nizkp2[i][j][0];
                c1_p = nizkp2[i][j][1];
                c2 = nizkp2[i][j][2];
                c2_p = nizkp2[i][j][3];
                c3 = nizkp2[i][j][4];
                c3_p = nizkp2[i][j][5];
                r1 = nizkp2[i][j][6];
                r1_p = nizkp2[i][j][7];
                r2 = nizkp2[i][j][8];
                r2_p = nizkp2[i][j][9];
                r3 = nizkp2[i][j][10];
                r3_p = nizkp2[i][j][11];

                t1 = b_prod_min.modPow(c1,p).multiply(g0.modPow(r1,p)).mod(p);
                t1_p = y_prod_min.modPow(c1_p,p).multiply(g1.modPow(r1_p,p)).mod(p);
                t2 = b[i][j].modPow(c2,p).multiply(g0.modPow(r2,p)).mod(p);
                t2_p = (b[j][i].multiply(g1.modInverse(p)).mod(p)).modPow(c2_p,p).multiply(g0.modPow(r2_p,p)).mod(p);
                t3 = (b[i][j].multiply(g1.modInverse(p)).mod(p)).modPow(c3,p).multiply(g0.modPow(r3,p)).mod(p);
                t3_p = b[j][i].modPow(c3_p,p).multiply(g0.modPow(r3_p,p)).mod(p);

                BigInteger[] arr = {g0, g1, b_prod_min, y_prod_min, b[i][j], b[j][i], t1, t1_p, t2, t2_p, t3, t3_p};

                h1 = c1.add(c2).add(c3).mod(p.subtract(BigInteger.ONE));
                h2 = c1_p.add(c2_p).add(c3_p).mod(p.subtract(BigInteger.ONE));

                if (h1.compareTo(BallotEncryptor.hash(arr)) != 0 || h2.compareTo(BallotEncryptor.hash(arr)) != 0) {
                    hold = false;
                }
            }
        }

        return hold;

    }

    // helper function to verify NIZKP for final conjunction
    private boolean verifyNIZKP3(int voter, BigInteger[][] b, BigInteger[][] y, BigInteger[][][] nizkp3) {

        boolean hold = true;

        BigInteger t1;
        BigInteger t1_p;
        BigInteger t2;
        BigInteger c1;
        BigInteger c2;
        BigInteger s1;
        BigInteger s1_p;
        BigInteger s2;
        BigInteger s2_p;
        BigInteger T;
        BigInteger y_prod_min;
        BigInteger b_prod_min;
        BigInteger h;
        for (int i = 0; i < num_candidates; i++) {

            BigInteger y_il_prod = encryptor.rowProductAt(y, i);
            BigInteger b_il_prod = encryptor.rowProductAt(b, i);

            for (int j = 0; j < i; j++) {

                BigInteger y_jl_prod = encryptor.rowProductAt(y, j);
                BigInteger b_jl_prod = encryptor.rowProductAt(b, j);

                y_prod_min = y_il_prod.multiply(y_jl_prod.modInverse(p)).mod(p);
                b_prod_min = b_il_prod.multiply(b_jl_prod.modInverse(p)).mod(p);

                c1 = nizkp3[i][j][0];
                c2 = nizkp3[i][j][1];
                s1 = nizkp3[i][j][2];
                s1_p = nizkp3[i][j][3];
                s2 = nizkp3[i][j][4];
                T = nizkp3[i][j][5];

                BigInteger Q = b_prod_min;
                BigInteger P = y_prod_min;

                t1 = g0.modPow(s1,p).multiply(Q.modPow(s1_p,p)).mod(p).multiply(T.modPow(c1,p).modInverse(p)).mod(p);
                t1_p = g1.modPow(s1,p).multiply(P.modPow(s1_p,p)).mod(p);
                t2 = (b[i][j].multiply(b[j][i]).mod(p)).multiply((g1.modPow(BigInteger.valueOf(2),p)).modInverse(p)).modPow(c2,p).multiply(g0.modPow(s2,p)).mod(p);


                BigInteger[] arr = {g0,g1,Q,P,T,BigInteger.ONE,t1,t1_p,t2};
                h = c1.add(c2).mod(p.subtract(BigInteger.ONE));

                if (T.compareTo(BigInteger.ONE) == 0) {
                    hold = false;
                }

                if (h.compareTo(BallotEncryptor.hash(arr).mod(p.subtract(BigInteger.ONE))) != 0) {
                    hold = false;
                }
            }
        }

        return hold;

    }

    // convert a vote to a pairwise comparison matrix permitting indifference
    public int[][] convertToMatrix(int[][] vote) {

        ArrayList<Integer> seen = new ArrayList<Integer>();

        int[][] matrix = new int[num_candidates][num_candidates];
        for (int i = 0; i < vote.length; i++) {
            int[] candidates = vote[i];
            // equal candidates beat themselves
            for (int j = 0; j < candidates.length; j++) {
                for (int k = 0; k < candidates.length; k++) {
                    if (j != k) {
                        int candidate = candidates[j];
                        int candidate_other = candidates[k];
                        matrix[candidate][candidate_other] = 1;
                        matrix[candidate_other][candidate] = 1;
                    }
                }
            }
            // record higher ranked candidates
            for (int j = 0; j < candidates.length; j++) {
                seen.add(candidates[j]);
            }
            // higher ranked candidates beat lower ranked ones
            for (int c = 0; c < num_candidates; c++) {
                if (!seen.contains(c)) {
                    for (int k = 0; k < candidates.length; k++) {
                        matrix[candidates[k]][c] = 1;
                        matrix[c][candidates[k]] = 0;
                    }
                }
            }
        }
        return matrix;
    }

}
