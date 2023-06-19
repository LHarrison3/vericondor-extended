package vericondo;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.math.BigInteger;

public class Program {

    // try out the class
    public static void main(String[] args) {

        int num_candidates = 8;

        IndiffElection election = new IndiffElection("Sample Election", "05/05/22", num_candidates, 112);
        int[][] ranking =  {{0,2},{1},{4,3},{6,5,7}}; 

        IndiffVote vote = election.convertToMatrix(ranking);

        System.out.println(Arrays.deepToString(vote.getPreferences()));
        System.out.println(Arrays.deepToString(vote.getIndifferences()));

        election.addBallot(vote.getPreferences(), vote.getIndifferences(), false);
        election.addBallot(vote.getPreferences(), vote.getIndifferences(), false);
        election.addBallot(vote.getPreferences(), vote.getIndifferences(), false);
        election.addBallot(vote.getPreferences(), vote.getIndifferences(), false);
        election.addBallot(vote.getPreferences(), vote.getIndifferences(), false);
        election.addBallot(vote.getPreferences(), vote.getIndifferences(), false);

        election.finaliseBallot(0, Type.CONFIRMED);
        election.finaliseBallot(1, Type.CONFIRMED);
        election.finaliseBallot(2, Type.AUDITED);
        election.finaliseBallot(3, Type.CONFIRMED);
        election.finaliseBallot(4, Type.CONFIRMED);
        election.finaliseBallot(5, Type.CONFIRMED);

        System.out.println(election.verifyNIZKPSforReceipt(election.getBulletinBoard().getReceipt(0)));

        System.out.println(election.verifyElection());

    }

}
