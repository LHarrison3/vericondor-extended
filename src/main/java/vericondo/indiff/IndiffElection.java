package vericondo;

import java.util.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class IndiffElection extends Election {

    // TODO: change this code to work with the new protocol for indifference

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
    public int addBallot(int[][] vote, int[][] vote_indiff, boolean displayPartialReceipt) {

        // make the ballot with its associated NIZKPs
        IndiffBallot ballot = this.encryptor.makeBallot(num_voters, vote, vote_indiff);

        // add the ballot to the list
        ballots.put(num_voters, ballot);

        // create a partial receipt for the ballot
        IndiffReceipt receipt = new IndiffReceipt(num_voters, ballot.b(), ballot.y(), ballot.b_indiff(),
            ballot.y_indiff(), ballot.getNIZKP0(), ballot.getNIZKP1(), ballot.getNIZKP2(), ballot.getNIZKP3(),
            ballot.getNIZKP4(), ballot.getNIZKP5(), ballot.getNIZKP6(), ballot.getNIZKP7());
        
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
                ballot.b_indiff(), ballot.y_indiff(), ballot.getNIZKP0(), ballot.getNIZKP1(), 
                ballot.getNIZKP2(), ballot.getNIZKP3(), ballot.getNIZKP4(), ballot.getNIZKP5(), 
                ballot.getNIZKP6(), ballot.getNIZKP7(), ballot.vote(), ballot.x(), ballot.vote_indiff(),
                ballot.x_indiff());

            bb.addReceipt(voter, final_receipt);

        } else {

            ConfirmedIndiffReceipt final_receipt = new ConfirmedIndiffReceipt(voter, ballot.b(),
                ballot.y(), ballot.b_indiff(), ballot.y_indiff(), ballot.getNIZKP0(), ballot.getNIZKP1(), 
                ballot.getNIZKP2(), ballot.getNIZKP3(), ballot.getNIZKP4(), ballot.getNIZKP5(),
                ballot.getNIZKP6(), ballot.getNIZKP7());

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

        // TODO: verify all NIZKPs from 0 to 7
        boolean hold0 = verifyNIZKP0(voter, receipt.y(), receipt.getNIZKP0());
        boolean hold1 = verifyNIZKP1(voter, receipt.y_indiff(), receipt.getNIZKP1());
        boolean hold2 = verifyNIZKP2(voter, receipt.b(), receipt.getNIZKP2());
        boolean hold3 = verifyNIZKP3(voter, receipt.b(), receipt.getNIZKP3());
        boolean hold4 = verifyNIZKP4(voter, receipt.b(), receipt.getNIZKP4());
        boolean hold5 = verifyNIZKP5(voter, receipt.b_indiff(), receipt.getNIZKP5());
        boolean hold6 = verifyNIZKP6(voter, receipt.b(), receipt.b_indiff(), receipt.getNIZKP6());
        return hold6;

        /*
        boolean hold0 = verifyNIZKP0(voter, receipt.y(), receipt.getNIZKP0());
        boolean hold1 = verifyNIZKP1(voter, receipt.b(), receipt.getNIZKP1());
        boolean hold2 = verifyNIZKP2(voter, receipt.b(), receipt.y(), receipt.getNIZKP2());
        boolean hold3 = verifyNIZKP3(voter, receipt.b(), receipt.y(), receipt.getNIZKP3());
        return (hold0 && hold1 && hold2 && hold3);
        */

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

        for (int i = 0; i < num_candidates; i++) {
            for (int j = 0; j < num_candidates; j++) {
                BigInteger t = nizkp0[i][j][0];
                BigInteger s = nizkp0[i][j][1];
                BigInteger[] arr = {BigInteger.valueOf(voter), g1, y[i][j], t};
                BigInteger c = BallotEncryptor.hash(arr);
                BigInteger left = g1.modPow(s,p);
                BigInteger right = y[i][j].modPow(c,p).multiply(t).mod(p);
                if (left.compareTo(right) != 0) {
                    hold = false;
                }
            }
        }

        return hold;
    }


    // helper function to verify NIZKP for second conjunction
    private boolean verifyNIZKP1(int voter, BigInteger[][] y, BigInteger[][][] nizkp1) {

        // the verification is the same as the first conjunction
        return verifyNIZKP0(voter, y, nizkp1);

    }

    // helper function to verify NIZKP for third conjunction
    private boolean verifyNIZKP2(int voter, BigInteger[][] b, BigInteger[][][] nizkp2) {

        boolean hold = true;

        for (int i = 0; i < num_candidates; i++) {
            for (int j = 0; j < num_candidates; j++) {
                BigInteger t0 = nizkp2[i][j][0];
                BigInteger t1 = nizkp2[i][j][1];
                BigInteger s0 = nizkp2[i][j][2];
                BigInteger s1 = nizkp2[i][j][3];
                BigInteger cl = nizkp2[i][j][4];
                BigInteger cr = nizkp2[i][j][5];

                BigInteger left0 = g0.modPow(s0,p);
                BigInteger right0 = b[i][j].modPow(cl,p).multiply(t0).mod(p);
                BigInteger left1 = g0.modPow(s1,p);
                BigInteger right1 = b[i][j].multiply(g1.modInverse(p)).mod(p).modPow(cr,p).multiply(t1).mod(p);
                if (left0.compareTo(right0) != 0 || left1.compareTo(right1) != 0) {
                    hold = false;
                }
            }
        } 

        return hold;

    }

    // helper function to verify NIZKP for fourth conjunction
    private boolean verifyNIZKP3(int voter, BigInteger[][] b, BigInteger[][][] nizkp3) {

        boolean hold = true;
        
        for (int i = 0; i < num_candidates; i++) {
            for (int j = 0; j < num_candidates; j++) {

                if (i == j) {
                    continue;
                }

                BigInteger t0 = nizkp3[i][j][0];
                BigInteger t0_p = nizkp3[i][j][1];
                BigInteger t1 = nizkp3[i][j][2];
                BigInteger t1_p = nizkp3[i][j][3];
                BigInteger s0 = nizkp3[i][j][4];
                BigInteger s0_p = nizkp3[i][j][5];
                BigInteger s1 = nizkp3[i][j][6];
                BigInteger s1_p = nizkp3[i][j][7];
                BigInteger cl = nizkp3[i][j][8];
                BigInteger cr = nizkp3[i][j][9];

                BigInteger left0 = g0.modPow(s0,p);
                BigInteger right0 = b[i][j].modPow(cl,p).multiply(t0).mod(p);
                BigInteger left0_p = g0.modPow(s0_p,p);
                BigInteger right0_p = b[j][i].multiply(g1.modInverse(p)).mod(p).modPow(cl,p).multiply(t0_p).mod(p);
                BigInteger left1 = g0.modPow(s1,p);
                BigInteger right1 = b[i][j].multiply(g1.modInverse(p)).mod(p).modPow(cr,p).multiply(t1).mod(p);
                BigInteger left1_p = g0.modPow(s1_p,p);
                BigInteger right1_p = b[j][i].modPow(cr,p).multiply(t1_p).mod(p);

                if (left0.compareTo(right0) != 0 || left0_p.compareTo(right0_p) != 0  || left1.compareTo(right1) != 0 || left1_p.compareTo(right1_p) != 0) {
                    hold = false;
                }
            }
        }

        return hold;

    }

    // helper function to verify NIZKP for fifth conjunction
    private boolean verifyNIZKP4(int voter, BigInteger[][] b, BigInteger[][] nizkp4) {

        BigInteger[] b_ps = this.encryptor.computeRowProducts(b);

        boolean hold = true;

        BigInteger r;
        BigInteger l;
        BigInteger c;
        BigInteger c_grand;
        BigInteger[] t = new BigInteger[num_candidates];

        BigInteger[] arr = new BigInteger[2 + 2*num_candidates];
        for (int j = 0; j < num_candidates; j++) {
            BigInteger c_sum = BigInteger.ZERO;
            for (int i = 0; i < num_candidates; i++) {
                c = nizkp4[j][i];
                r = nizkp4[j][i+num_candidates];
                l = g0.modPow(r,p);
                t[i] = g0.modPow(r,p).multiply(b_ps[i].multiply(g1.modInverse(p).modPow(BigInteger.valueOf(j),p)).modPow(c,p).mod(p)).mod(p);
                c_sum = c_sum.add(c).mod(q);
            }

            arr[0] = BigInteger.valueOf(voter);
            arr[1] = BigInteger.valueOf(j);
            for (int i = 0; i < num_candidates; i++) {
                arr[i+2] = b_ps[i];
            }
            for (int i = 0; i < num_candidates; i++) {
                arr[i+2+num_candidates] = t[i];
            }

            c_grand = BallotEncryptor.hash(arr).mod(q);
            if (c_sum.mod(q).compareTo(c_grand) != 0) {
                hold = false;
            }

        }

        return hold;

    }

    // helper function to verify NIZKP for sixth conjunction
    private boolean verifyNIZKP5(int voter, BigInteger[][] b_indiff, BigInteger[][][] nizkp5) {

        // the verification is the same as the third conjunction
        return verifyNIZKP2(voter, b_indiff, nizkp5);

    }

    // helper function to verify NIZKP for seventh conjunction
    private boolean verifyNIZKP6(int voter, BigInteger[][] b, BigInteger[][] b_indiff, 
        BigInteger[][][] nizkp6) {

        boolean hold = true;

        for (int i = 0; i < num_candidates; i++) {
            for (int j = 0; j < num_candidates; j++) {
                BigInteger t0 = nizkp6[i][j][0];
                BigInteger t1 = nizkp6[i][j][1];
                BigInteger s0 = nizkp6[i][j][2];
                BigInteger s1 = nizkp6[i][j][3];
                BigInteger cl = nizkp6[i][j][4];
                BigInteger cr = nizkp6[i][j][5];

                BigInteger left0 = g0.modPow(s0,p);
                BigInteger right0 = b[i][j].modPow(cl,p).multiply(t0).mod(p);
                BigInteger left1 = g0.modPow(s1,p);
                BigInteger right1 = b_indiff[i][j].modPow(cr,p).multiply(t1).mod(p);
                if (left0.compareTo(right0) != 0 || left1.compareTo(right1) != 0) {
                    hold = false;
                }
            }
        } 

        return hold;

    }

    // helper function to verify NIZKP for final conjunction
    private boolean verifyNIZKP7() {
        return false;
    }

    // convert a vote to a pairwise comparison matrix permitting indifference
    public IndiffVote convertToMatrix(int[][] vote) {

        // first we flatten the vote to strict preferences only
        int[] flat = new int[num_candidates];
        int index = 0;
        for (int i = 0; i < vote.length; i++) {
            for (int j = 0; j < vote[i].length; j++) {
                flat[index] = vote[i][j];
                index++;
            }
        }

        // then we convert the flattened vote to a matrix of strict preferences 
        ArrayList<Integer> seen = new ArrayList<Integer>();
        int[][] preferences = new int[num_candidates][num_candidates];
        for (int i = 0; i < num_candidates; i++) {
            int candidate = flat[i];
            for (int j = 0; j < num_candidates; j++) {
                if (!seen.contains(j)) {
                    preferences[candidate][j] = 1;
                    preferences[j][candidate] = 0;
                }
            }
            seen.add(candidate);
        }

        // finally we produce the indifference matrix
        // two candidates are indifferent if they appear in the same group in the linear vote
        int[][] indifferences = new int[num_candidates][num_candidates];
        for (int i = 0; i < num_candidates; i++) {
            for (int j = 0; j < num_candidates; j++) {
                if (i == j || preferences[i][j] == 1) {
                    continue;
                }
                for (int k = 0; k < vote.length; k++) {
                    int[] group = vote[k];
                    boolean seen_first = false;
                    boolean seen_second = false;
                    for (int l = 0; l < group.length; l++) {
                        if (group[l] == i) {
                            seen_first = true;
                        }
                        if (group[l] == j) {
                            seen_second = true;
                        }
                    }
                    if (seen_first && seen_second) {
                        indifferences[i][j] = 1;
                        break;
                    }
                }
            }
        }

        return new IndiffVote(preferences, indifferences);

        /*
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
    */
    }

}
