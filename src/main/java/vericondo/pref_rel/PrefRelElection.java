package vericondo;

import java.util.*;
import java.lang.Math;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PrefRelElection extends Election {

    // mapping of voters to ballots
    private HashMap<Integer, PrefRelBallot> ballots;

    // the bulletin board
    private PrefRelBulletinBoard bb;

    // helper for creating encrypted ballots and their associated NIZKPs
    private PrefRelBallotEncryptor encryptor;

    // create an election
    public PrefRelElection(String election_name, String election_date, int num_candidates, int security_bits) {

        super(election_name, election_date, num_candidates, security_bits);

        this.bb = new PrefRelBulletinBoard(this.num_candidates, this.g0, this.g1, this.p, this.q);

        this.encryptor = new PrefRelBallotEncryptor(this.num_candidates, this.g0, this.g1, this.p, this.q);

        this.num_voters = 0;
        this.ballots = new HashMap<Integer, PrefRelBallot>();

    }

    // get the tally of the election
    public int[][] t() {
        return this.bb.t();
    }

    // convert the tally as a preference relation to a pairwise comparison matrix
    public int[][] tallyAsMatrix() {

        int[][] t = t();
        int[][] m = new int[num_candidates][num_candidates];

        for (int i = 0; i < num_candidates - 1; i++) {
            System.arraycopy(t[i], 0, m[i], i+1, num_candidates - i - 1);
        }

        for (int i = 0; i < num_candidates; i++) {
            for (int j = i+1; j < num_candidates && j != i; j++) {
                m[j][i] = Math.abs(this.bb.getConfirmedReceipts().size() - m[i][j]);
            }
        }

        return m;

    }

    // determine a winner of the election using the specified method
    public int determineWinner(Method method) {

        // convert the preference relation t to a pairwise comparison matrix
        int[][] m = tallyAsMatrix();

        if (method == Method.MINIMAX) {
            return determineMinimaxWinner(m);
        } else if (method == Method.COPELAND) {
            return determineCopelandWinner(m);
        } else if (method == Method.RANKEDPAIRS) {
            return determineRankedPairsWinner(m);
        } else {
            return determineSchulzeWinner(m);
        }
    }

    // add a ballot to the election
    public int addBallot(int[][] vote, boolean displayPartialReceipt) {

        // make the ballot with its associated NIZKPs
        PrefRelBallot ballot = this.encryptor.makeBallot(num_voters, vote);

        // add the ballot to the list
        ballots.put(num_voters, ballot);

        // create a partial receipt for the ballot
        PrefRelReceipt receipt = new PrefRelReceipt(num_voters, ballot.b(), ballot.y(), ballot.getNIZKP0(),
            ballot.getNIZKP1(), ballot.getNIZKP2());
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
    public PrefRelBallot getBallot(int voter) {
        return ballots.get(voter);
    }

    // retrieve the bulletin board
    public PrefRelBulletinBoard getBulletinBoard() {
        return this.bb;
    }

    // audit or confirm a ballot
    public void finaliseBallot(int voter, Type type) {

        PrefRelBallot ballot = ballots.get(voter);

        if (type == Type.AUDITED) {

            AuditedPrefRelReceipt final_receipt = new AuditedPrefRelReceipt(voter, ballot.b(), ballot.y(),
                ballot.getNIZKP0(), ballot.getNIZKP1(), ballot.getNIZKP2(), ballot.vote(), ballot.x());

            bb.addReceipt(voter, final_receipt);

        } else {

            ConfirmedPrefRelReceipt final_receipt = new ConfirmedPrefRelReceipt(voter, ballot.b(),
                ballot.y(), ballot.getNIZKP0(), ballot.getNIZKP1(), ballot.getNIZKP2());

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
        ArrayList<PrefRelReceipt> confirmed_receipts = bb.getConfirmedReceipts();
        BigInteger[][] b;
        BigInteger[][] y;
        boolean hold = true;
        for (int i = 0; i < num_candidates - 1; i++) {
            for (int j = 0; j < num_candidates - 1 - i; j++) {
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
                BigInteger lhs0 = g0.modPow(s[i][j],p).multiply(g0.modPow(BigInteger.valueOf(t[i][j]),p)).mod(p);
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
    public boolean verifyNIZKPSforReceipt(PrefRelReceipt receipt) {

        int voter = receipt.id();

        boolean hold0 = verifyNIZKP0(voter, receipt.y(), receipt.getNIZKP0());
        boolean hold1 = verifyNIZKP1(voter, receipt.b(), receipt.getNIZKP1());
        boolean hold2 = verifyNIZKP2(voter, receipt.b(), receipt.getNIZKP2());

        return (hold0 && hold1 && hold2);

    }

    // verify all NIZKPs for all voters
    public boolean verifyNIZKPS() {

        boolean hold = true;

        HashMap<Integer, PrefRelReceipt> receipts = bb.getReceipts();
        for (HashMap.Entry<Integer, PrefRelReceipt> entry : receipts.entrySet()) {
            PrefRelReceipt receipt = entry.getValue();
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
        for (int i = 0; i < num_candidates - 1; i++) {
            for (int j = 0; j < num_candidates - 1 - i; j++) {
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

    // helper function to verify NIZKP for third conjunction
    private boolean verifyNIZKP1(int voter, BigInteger[][] b, BigInteger[][][] nizkp1) {

        boolean hold = true;

        BigInteger t1;
        BigInteger t2;
        BigInteger c1;
        BigInteger c2;
        BigInteger r1;
        BigInteger r2;
        BigInteger h;
        for (int i = 0; i < num_candidates - 1; i++) {
            for (int j = 0; j < num_candidates - 1 - i; j++) {
                c1 = nizkp1[i][j][0];
                c2 = nizkp1[i][j][1];
                r1 = nizkp1[i][j][2];
                r2 = nizkp1[i][j][3];
                t1 = g0.modPow(r1,p).multiply(b[i][j].modPow(c1,p)).mod(p);
                t2 = g0.modPow(r2,p).multiply(b[i][j].multiply(g0.modInverse(p)).mod(p).modPow(c2,p)).mod(p);
                BigInteger[] arr = {BigInteger.valueOf(voter), g0, b[i][j], t1, t2};
                h = c1.add(c2).mod(q);
                if (h.compareTo(BallotEncryptor.hash(arr).mod(q)) != 0) {
                    hold = false;
                }
            }
        }

        return hold;

    }

    // helper function to verify NIZKP for fifth conjunction
    private boolean verifyNIZKP2(int voter, BigInteger[][] b, BigInteger[][] nizkp2) {

        BigInteger[] b_ps = new BigInteger[num_candidates];
        for (int c = 0; c < num_candidates; c++) {
            b_ps[c] = this.encryptor.computePrefDivsAt(c, b).multiply(g0.modPow(BigInteger.valueOf(c),p)).mod(p);
        }

        boolean hold = true;

        BigInteger r;
        BigInteger l;
        BigInteger ch;
        BigInteger ch_grand;
        BigInteger[] t = new BigInteger[num_candidates];

        BigInteger[] arr = new BigInteger[2 + 2*num_candidates];
        for (int j = 0; j < num_candidates; j++) {
            BigInteger ch_sum = BigInteger.ZERO;
            for (int i = 0; i < num_candidates; i++) {
                ch = nizkp2[j][i];
                r = nizkp2[j][i+num_candidates];
                l = g0.modPow(r,p);
                t[i] = g0.modPow(r,p).multiply(b_ps[i].multiply(g0.modInverse(p).modPow(BigInteger.valueOf(j),p)).modPow(ch,p).mod(p)).mod(p);
                ch_sum = ch_sum.add(ch).mod(q);
            }

            arr[0] = BigInteger.valueOf(voter);
            arr[1] = BigInteger.valueOf(j);
            for (int i = 0; i < num_candidates; i++) {
                arr[i+2] = b_ps[i];
            }
            for (int i = 0; i < num_candidates; i++) {
                arr[i+2+num_candidates] = t[i];
            }

            ch_grand = BallotEncryptor.hash(arr).mod(q);
            if (ch_sum.mod(q).compareTo(ch_grand) != 0) {
                hold = false;
            }

        }

        return hold;

    }

    // convert a vote given as a permutation to a preference relation
    public int[][] convertToPrefRel(int[] vote) {

        ArrayList<Integer> seen = new ArrayList<Integer>();

        int[][] matrix = new int[num_candidates][num_candidates];
        for (int i = 0; i < num_candidates; i++) {
            int candidate = vote[i];
            for (int j = 0; j < num_candidates; j++) {
                if (!seen.contains(j)) {
                    matrix[candidate][j] = 1;
                    matrix[j][candidate] = 0;
                }
            }
            seen.add(candidate);
        }

        // trim the matrix down into a preference relation
        int[][] rel = new int[num_candidates - 1][];
        for (int i = 0; i < num_candidates - 1; i++) {
            rel[i] = new int[num_candidates - 1 - i];
        }
        for (int i = 0; i < num_candidates - 1; i++) {
            System.arraycopy(matrix[i], i+1, rel[i], 0, num_candidates - 1 - i);
        }

        return rel;

    }

}
