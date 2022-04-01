package vericondo;

import java.util.*;
import java.math.BigInteger;

public class Program {

    // try out the class
    public static void main(String[] args) {

        /*
        BigInteger g0 = BigInteger.valueOf(5);
        BigInteger g1 = BigInteger.valueOf(7);
        BigInteger p = BigInteger.valueOf(23);
        BigInteger q = BigInteger.valueOf(11);

        PrefRelBallotEncryptor be = new PrefRelBallotEncryptor(4,g0,g1,p,q);

        //BigInteger[][] r = {{BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO},{BigInteger.ONE,BigInteger.ONE},{BigInteger.ZERO}};//be.generateRandomness();
        //BigInteger[][] r = {{BigInteger.ONE, BigInteger.ONE, BigInteger.ONE},{BigInteger.ZERO, BigInteger.ONE}, {BigInteger.ZERO}};
        BigInteger[][] r = {{BigInteger.ONE, BigInteger.ONE, BigInteger.ONE}, {BigInteger.ONE, BigInteger.ONE}, {BigInteger.ONE}};
        System.out.println(Arrays.deepToString(r));

        System.out.println(be.computePrefDiffsAt(0, r));
        System.out.println(be.computePrefDiffsAt(1, r));
        System.out.println(be.computePrefDiffsAt(2, r));
        System.out.println(be.computePrefDiffsAt(3, r));

        int[][] v = {{0,0,0},{0,0},{0}};

        PrefRelBallot b = be.makeBallot(0, v);
        */
        /*
        int num_candidates = 4;

        PrefRelElection election = new PrefRelElection("Sample Election", "01/04/22", num_candidates, 112);

        int[] ranking = {1,2,0,3};
        int[][] vote = election.convertToPrefRel(ranking);

        int[] ranking1 = {3,2,1,0};
        int[][] vote1 = election.convertToPrefRel(ranking1);

        int[] ranking2 = {0,1,2,3};
        int[][] vote2 = election.convertToPrefRel(ranking2);

        System.out.println(Arrays.deepToString(vote));
        System.out.println(Arrays.deepToString(vote1));
        System.out.println(Arrays.deepToString(vote2));
        */


        int num_candidates = 4;

        PrefRelElection election = new PrefRelElection("Sample Election", "11/05/21", num_candidates, 112);

        int[] ranking = {1,2,0,3};

        int[][] vote = election.convertToPrefRel(ranking);

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

        //System.out.println(election.determineWinner(Method.MINIMAX));
        //System.out.println(election.determineWinner(Method.COPELAND));
        //System.out.println(election.determineWinner(Method.RANKEDPAIRS));
        //System.out.println(election.determineWinner(Method.SCHULZE));
        System.out.println(election.verifyElection());


    }

}
