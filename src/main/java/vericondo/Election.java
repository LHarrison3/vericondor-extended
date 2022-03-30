package vericondo;

import java.util.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// different methods to determine the winner of the election
enum Method {
    MINIMAX,
    SCHULZE,
    RANKEDPAIRS,
    COPELAND
}

public class Election {

    // parameters concerning candidates
    public final int num_candidates;
    private final int[] candidates;

    // other information about the election
    String election_name;
    String election_date;

    // parameters specifically for VERICONDO
    public final BigInteger g0;
    public final BigInteger g1;
    public final BigInteger p;
    public final BigInteger q;

    // total count of voters in the election
    private int num_voters;

    // mapping of voters to ballots
    private HashMap<Integer, Ballot> ballots;

    // the bulletin board
    private BulletinBoard bb;

    // helper for creating encrypted ballots and their associated NIZKPs
    private BallotEncryptor encryptor;

    // create an election
    public Election(String election_name, String election_date, int num_candidates, int security_bits) {

        this.election_name = election_name;
        this.election_date = election_date;

        // values for p, q and g0 are taken from NIST
        // default to 128-bit security
        String p_str = "90066455B5CFC38F9CAA4A48B4281F292C260FEEF01FD61037E56258A7795A1C7AD46076982CE6BB956936C6AB4DCFE05E6784586940CA544B9B2140E1EB523F009D20A7E7880E4E5BFA690F1B9004A27811CD9904AF70420EEFD6EA11EF7DA129F58835FF56B89FAA637BC9AC2EFAAB903402229F491D8D3485261CD068699B6BA58A1DDBBEF6DB51E8FE34E8A78E542D7BA351C21EA8D8F1D29F5D5D15939487E27F4416B0CA632C59EFD1B1EB66511A5A0FBF615B766C5862D0BD8A3FE7A0E0DA0FB2FE1FCB19E8F9996A8EA0FCCDE538175238FC8B0EE6F29AF7F642773EBE8CD5402415A01451A840476B2FCEB0E388D30D4B376C37FE401C2A2C2F941DAD179C540C1C8CE030D460C4D983BE9AB0B20F69144C1AE13F9383EA1C08504FB0BF321503EFE43488310DD8DC77EC5B8349B8BFE97C2C560EA878DE87C11E3D597F1FEA742D73EEC7F37BE43949EF1A0D15C3F3E3FC0A8335617055AC91328EC22B50FC15B941D3D1624CD88BC25F3E941FDDC6200689581BFEC416B4B2CB73";

        String q_str = "CFA0478A54717B08CE64805B76E5B14249A77A4838469DF7F7DC987EFCCFB11D";

        String g0_str = "5E5CBA992E0A680D885EB903AEA78E4A45A469103D448EDE3B7ACCC54D521E37F84A4BDD5B06B0970CC2D2BBB715F7B82846F9A0C393914C792E6A923E2117AB805276A975AADB5261D91673EA9AAFFEECBFA6183DFCB5D3B7332AA19275AFA1F8EC0B60FB6F66CC23AE4870791D5982AAD1AA9485FD8F4A60126FEB2CF05DB8A7F0F09B3397F3937F2E90B9E5B9C9B6EFEF642BC48351C46FB171B9BFA9EF17A961CE96C7E7A7CC3D3D03DFAD1078BA21DA425198F07D2481622BCE45969D9C4D6063D72AB7A0F08B2F49A7CC6AF335E08C4720E31476B67299E231F8BD90B39AC3AE3BE0C6B6CACEF8289A2E2873D58E51E029CAFBD55E6841489AB66B5B4B9BA6E2F784660896AFF387D92844CCB8B69475496DE19DA2E58259B090489AC8E62363CDF82CFD8EF2A427ABCD65750B506F56DDE3B988567A88126B914D7828E2B63A6D7ED0747EC59E0E0A23CE7D8A74C1D2C2A7AFB6A29799620F00E11C33787F7DED3B30E1A22D09F1FBDA1ABBBFBF25CAE05A13F812E34563F99410E73B";

        // 112-bit security
        if (security_bits == 112) {
            p_str = "C196BA05AC29E1F9C3C72D56DFFC6154A033F1477AC88EC37F09BE6C5BB95F51C296DD20D1A28A067CCC4D4316A4BD1DCA55ED1066D438C35AEBAABF57E7DAE428782A95ECA1C143DB701FD48533A3C18F0FE23557EA7AE619ECACC7E0B51652A8776D02A425567DED36EABD90CA33A1E8D988F0BBB92D02D1D20290113BB562CE1FC856EEB7CDD92D33EEA6F410859B179E7E789A8F75F645FAE2E136D252BFFAFF89528945C1ABE705A38DBC2D364AADE99BE0D0AAD82E5320121496DC65B3930E38047294FF877831A16D5228418DE8AB275D7D75651CEFED65F78AFC3EA7FE4D79B35F62A0402A1117599ADAC7B269A59F353CF450E6982D3B1702D9CA83";

            q_str = "90EAF4D1AF0708B1B612FF35E0A2997EB9E9D263C9CE659528945C0D";

            g0_str = "A59A749A11242C58C894E9E5A91804E8FA0AC64B56288F8D47D51B1EDC4D65444FECA0111D78F35FC9FDD4CB1F1B79A3BA9CBEE83A3F811012503C8117F98E5048B089E387AF6949BF8784EBD9EF45876F2E6A5A495BE64B6E770409494B7FEE1DBB1E4B2BC2A53D4F893D418B7159592E4FFFDF6969E91D770DAEBD0B5CB14C00AD68EC7DC1E5745EA55C706C4A1C5C88964E34D09DEB753AD418C1AD0F4FDFD049A955E5D78491C0B7A2F1575A008CCD727AB376DB6E695515B05BD412F5B8C2F4C77EE10DA48ABD53F5DD498927EE7B692BBBCDA2FB23A516C5B4533D73980B2A3B60E384ED200AE21B40D273651AD6060C13D97FD69AA13C5611A51B9085";
        }

        this.p = new BigInteger(p_str, 16);
        this.q = new BigInteger(q_str, 16);
        this.g0 = new BigInteger(g0_str, 16);

        BigInteger counter = BigInteger.ZERO;

        // next we must find a suitable g1
        // we do this by computing g1 = H(g0, election_string, counter)
        // and check if the result has the prime order q

        BigInteger g1_temp = BigInteger.ONE;

        // compute a string specific to this election
        String election_string = election_name + election_date;
        for (int i = 0; i < num_candidates; i++) {
            election_string += i;
        }
        BigInteger election_data = new BigInteger(election_string.getBytes());

        String hash_alg = "SHA-256";
        while (true) {

            try {

                MessageDigest md = MessageDigest.getInstance(hash_alg);
                md.update(g0.toByteArray());
                md.update(election_data.toByteArray());
                md.update(counter.toByteArray());

                byte[] result = md.digest();
                g1_temp = new BigInteger(1, result);

                // check if result gives the prime order q
                if (g1_temp.compareTo(BigInteger.ONE) != 0 && g1_temp.modPow(q,p).compareTo(BigInteger.ONE) != 0) {
                    break;
                }

                counter = counter.add(BigInteger.ONE);

            } catch (NoSuchAlgorithmException e) {
                System.out.println("Hashing algorithm not recognised");
            }

        }

        // set the g1 that we found
        this.g1 = g1_temp;

        this.num_candidates = num_candidates;
        this.candidates = new int[num_candidates];

        // assign identifiers to the candidates
        for (int i = 0; i < this.candidates.length; i++) {
            this.candidates[i] = i;
        }

        this.bb = new BulletinBoard(this.num_candidates, this.g0, this.g1, this.p, this.q);

        this.encryptor = new BallotEncryptor(this.num_candidates, this.g0, this.g1, this.p, this.q);

        this.num_voters = 0;
        this.ballots = new HashMap<Integer, Ballot>();

    }

    // convert a vote given as a permutation to a pairwise comparison matrix
    public int[][] convertToMatrix(int[] vote) {

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

        return matrix;

    }

    // get the tally of the election
    public int[][] t() {
        return this.bb.t();
    }

    // determine a winner of the election using the specified method
    public int determineWinner(Method method) {
        if (method == Method.MINIMAX) {
            return determineMinimaxWinner();
        } else if (method == Method.COPELAND) {
            return determineCopelandWinner();
        } else if (method == Method.RANKEDPAIRS) {
            return determineRankedPairsWinner();
        } else {
            return determineSchulzeWinner();
        }
    }

    // determine the minimax winner
    private int determineMinimaxWinner() {
        int[][] t = t();
        // find the greatest pairwise defeat for each candidate
        int[] defeats = new int[num_candidates];
        for (int i = 0; i < num_candidates; i++) {
            for (int j = 0; j < num_candidates; j++) {
                if (t[j][i] > defeats[i]) {
                    defeats[i] = t[j][i];
                }
            }
        }
        int winner = 0;
        double max = Double.POSITIVE_INFINITY;
        // the winner has the smallest pairwise defeat compared to all other candidates
        for (int i = 0; i < num_candidates; i++) {
            if (defeats[i] < max) {
                max = (double) defeats[i];
                winner = i;
            }
        }
        return winner;
    }

    // determine the copeland winner
    private int determineCopelandWinner() {
        int[][] t = t();
        double[][] points = new double[num_candidates][num_candidates];
        // compute copeland points
        for (int i = 0; i < num_candidates; i++) {
            for (int j = 0; j < num_candidates; j++) {
                if (t[i][j] > t[j][i]) {
                    points[i][j] = 1;
                } else if (t[i][j] == t[j][i]) {
                    points[i][j] = 0.5;
                } else {
                    points[i][j] = 0;
                }
            }
        }
        // compute copeland scores and find the maximum
        double max = 0;
        int winner = 0;
        for (int i = 0; i < num_candidates; i++) {
            double score = 0;
            for (int j = 0; j < num_candidates; j++) {
                score += points[i][j];
            }
            if (score > max) {
                max = score;
                winner = i;
            }
        }
        return winner;
    }

    // determine the ranked pairs winner
    private int determineRankedPairsWinner() {

        int[][] t = t();

        // generate a random permutation of candidates to use as the tie breaker
        ArrayList<Integer> tiebreaker = new ArrayList<Integer>();
        for (int i = 0; i < num_candidates; i++) {
            tiebreaker.add(i);
        }
        Collections.shuffle(tiebreaker);

        // create a list of all candidate pairings
        ArrayList<CandidatePair> pairings = new ArrayList<CandidatePair>();
        for (int i = 0; i < num_candidates; i++) {
            for (int j = 0; j < num_candidates; j++) {
                // does not make sense to have a pair between the same candidate
                if (i == j) {
                    continue;
                }
                CandidatePair pair = new CandidatePair(i,j,t,tiebreaker);
                pairings.add(pair);
            }
        }

        // sort the pairings so that the "best" ones appear first
        Collections.sort(pairings, Collections.reverseOrder());

        // use a directed (acyclic) graph to compute the ranking of the candidates
        DAG digraph = new DAG();
        for (int i = 0; i < num_candidates; i++) {
            digraph.addNode(i);
        }
        for (CandidatePair pair : pairings) {
            digraph.addEdge(pair.fst(), pair.snd());
        }

        // extract the ranking via a topological sort
        ArrayList<Integer> ranking = digraph.topologicalSort();

        // return the winner (first in the ranking)
        return ranking.get(0);

    }

    // determine the schulze winner
    private int determineSchulzeWinner() {

        int[][] t = t();
        int[][] p = new int[num_candidates][num_candidates];

        // compute strongest path strengths using Floyd-Warshall
        for (int i = 0; i < num_candidates; i++) {
            for (int j = 0; j < num_candidates; j++) {
                if (i != j) {
                    if (t[i][j] > t[j][i]) {
                        p[i][j] = t[i][j];
                    } else {
                        p[i][j] = 0;
                    }
                }
            }
        }
        for (int i = 0; i < num_candidates; i++) {
            for (int j = 0; j < num_candidates; j++) {
                if (i != j) {
                    for (int k = 0; k < num_candidates; k++) {
                        if (i != k && j != k) {
                            p[j][k] = Math.max(p[j][k], Math.min(p[j][i], p[i][k]));
                        }
                    }
                }
            }
        }

        // determine the schulze winners
        boolean[] winners = new boolean[num_candidates];
        for (int i = 0; i < num_candidates; i++) {
            winners[i] = true;
        }
        for (int i = 0; i < num_candidates; i++) {
            for (int j = 0; j < num_candidates; j++) {
                if (i != j) {
                    if (p[j][i] > p[i][j]) {
                        winners[i] = false;
                    }
                }
            }
        }

        // pick any winner
        int winner = 0;
        for (int i = 0; i < num_candidates; i++) {
            if (winners[i]) {
                winner = i;
            }
        }

        return winner;

    }

    // add a ballot to the election
    public int addBallot(int[][] vote, boolean displayPartialReceipt) {

        // make the ballot with its associated NIZKPs
        Ballot ballot = this.encryptor.makeBallot(num_voters, vote);

        // add the ballot to the list
        ballots.put(num_voters, ballot);

        // create a partial receipt for the ballot
        Receipt receipt = new Receipt(num_voters, ballot.b(), ballot.y(), ballot.getNIZKP0(),
            ballot.getNIZKP1(), ballot.getNIZKP2(), ballot.getNIZKP3(), ballot.getNIZKP4());
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
    public Ballot getBallot(int voter) {
        return ballots.get(voter);
    }

    // retrieve the bulletin board
    public BulletinBoard getBulletinBoard() {
        return this.bb;
    }

    // audit or confirm a ballot
    public void finaliseBallot(int voter, Type type) {

        Ballot ballot = ballots.get(voter);

        if (type == Type.AUDITED) {

            AuditedReceipt final_receipt = new AuditedReceipt(voter, ballot.b(), ballot.y(),
                ballot.getNIZKP0(), ballot.getNIZKP1(), ballot.getNIZKP2(), ballot.getNIZKP3(),
                ballot.getNIZKP4(), ballot.vote(), ballot.x());

            bb.addReceipt(voter, final_receipt);

        } else {

            ConfirmedReceipt final_receipt = new ConfirmedReceipt(voter, ballot.b(),
                ballot.y(), ballot.getNIZKP0(), ballot.getNIZKP1(), ballot.getNIZKP2(),
                ballot.getNIZKP3(), ballot.getNIZKP4());

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
        ArrayList<Receipt> confirmed_receipts = bb.getConfirmedReceipts();
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
    public boolean verifyNIZKPSforReceipt(Receipt receipt) {

        int voter = receipt.id();

        boolean hold = true;
        hold = verifyNIZKP0(voter, receipt.y(), receipt.getNIZKP0());
        hold = verifyNIZKP1(voter, receipt.b(), receipt.getNIZKP1());
        hold = verifyNIZKP2(voter, receipt.b(), receipt.getNIZKP2());
        hold = verifyNIZKP3(voter, receipt.b(), receipt.getNIZKP3());
        hold = verifyNIZKP4(voter, receipt.b(), receipt.getNIZKP4());
        return hold;

    }

    // verify all NIZKPs for all voters
    public boolean verifyNIZKPS() {

        boolean hold = true;

        HashMap<Integer, Receipt> receipts = bb.getReceipts();
        for (HashMap.Entry<Integer, Receipt> entry : receipts.entrySet()) {
            Receipt receipt = entry.getValue();
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
    private boolean verifyNIZKP1(int voter, BigInteger[][] b, BigInteger[][] nizkp1) {

        boolean hold = true;

        BigInteger t1;
        BigInteger c1;
        BigInteger c;
        BigInteger r;
        for (int i = 0; i < num_candidates; i++) {
            c = nizkp1[i][0];
            r = nizkp1[i][1];
            t1 = g0.modPow(r,p).multiply(b[i][i].modPow(c,p)).mod(p);
            BigInteger[] arr = {BigInteger.valueOf(voter), g0, b[i][i], t1};
            c1 = BallotEncryptor.hash(arr);
            if (c.compareTo(c1) != 0) {
                hold = false;
            }
        }

        return hold;
    }

    // helper function to verify NIZKP for third conjunction
    private boolean verifyNIZKP2(int voter, BigInteger[][] b, BigInteger[][][] nizkp2) {

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
                c1 = nizkp2[i][j][0];
                c2 = nizkp2[i][j][1];
                r1 = nizkp2[i][j][2];
                r2 = nizkp2[i][j][3];
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

    // helper function to verify NIZKP for fourth conjunction
    private boolean verifyNIZKP3(int voter, BigInteger[][] b, BigInteger[][][] nizkp3) {

        boolean hold = true;

        BigInteger t11;
        BigInteger t12;
        BigInteger t21;
        BigInteger t22;
        BigInteger c11;
        BigInteger c12;
        BigInteger c21;
        BigInteger c22;
        BigInteger r11;
        BigInteger r12;
        BigInteger r21;
        BigInteger r22;
        BigInteger h_a;
        BigInteger h_b;
        for (int i = 0; i < num_candidates; i++) {
            for (int j = 0; j < num_candidates; j++) {
                if (i == j) {
                    continue;
                }
                c11 = nizkp3[i][j][0];
                c21 = nizkp3[i][j][1];
                r11 = nizkp3[i][j][2];
                r21 = nizkp3[i][j][3];
                c12 = nizkp3[i][j][4];
                c22 = nizkp3[i][j][5];
                r12 = nizkp3[i][j][6];
                r22 = nizkp3[i][j][7];
                t11 = b[i][j].modPow(c11,p).multiply(g0.modPow(r11,p)).mod(p);
                t12 = b[j][i].multiply(g0.modInverse(p)).mod(p).modPow(c12,p).multiply(g0.modPow(r12,p)).mod(p);
                t21 = b[i][j].multiply(g0.modInverse(p)).mod(p).modPow(c21,p).multiply(g0.modPow(r21,p)).mod(p);
                t22 = b[j][i].modPow(c22,p).multiply(g0.modPow(r22,p)).mod(p);
                BigInteger[] arr_a = {g0, b[i][j], b[j][i], t11, t21};
                BigInteger[] arr_b = {g0, b[i][j], b[j][i], t12, t22};
                h_a = c11.add(c21).mod(q);
                h_b = c12.add(c22).mod(q);
                if (h_a.compareTo(BallotEncryptor.hash(arr_a).mod(q)) != 0 || h_b.compareTo(BallotEncryptor.hash(arr_b).mod(q)) != 0) {
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
                t[i] = g0.modPow(r,p).multiply(b_ps[i].multiply(g0.modInverse(p).modPow(BigInteger.valueOf(j),p)).modPow(c,p).mod(p)).mod(p);
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

    // try out the class
    public static void main(String[] args) {

        int num_candidates = 4;

        Election election = new Election("Sample Election", "11/05/21", num_candidates, 112);

        int[] ranking = {1,2,0,3};

        int[][] vote = election.convertToMatrix(ranking);

        election.addBallot(vote, false);
        election.addBallot(vote, false);
        election.addBallot(vote, false);
        election.addBallot(vote, false);
        election.addBallot(vote, false);
        election.addBallot(vote, false);
        election.addBallot(vote, false);

        election.finaliseBallot(0, Type.AUDITED);
        election.finaliseBallot(1, Type.CONFIRMED);
        election.finaliseBallot(2, Type.AUDITED);
        election.finaliseBallot(3, Type.CONFIRMED);
        election.finaliseBallot(4, Type.CONFIRMED);
        election.finaliseBallot(6, Type.AUDITED);

        System.out.println(election.determineWinner(Method.MINIMAX));
        System.out.println(election.determineWinner(Method.COPELAND));
        System.out.println(election.determineWinner(Method.RANKEDPAIRS));
        System.out.println(election.determineWinner(Method.SCHULZE));
        System.out.println(election.verifyElection());

    }

}
