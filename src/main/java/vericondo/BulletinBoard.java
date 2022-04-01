package vericondo;

import java.util.*;
import java.math.BigInteger;

/**
This class represents the bulletin board.
*/
public class BulletinBoard {

    // parameters for the election
    private final int num_candidates;
    private final BigInteger g0;
    private final BigInteger g1;
    private final BigInteger p;
    private final BigInteger q;

    // tally of votes and randomness
    private int[][] t;
    private BigInteger[][] s;

    public BulletinBoard(int num_candidates, BigInteger g0, BigInteger g1,
        BigInteger p, BigInteger q) {
        this.num_candidates = num_candidates;
        this.g0 = g0;
        this.g1 = g1;
        this.p = p;
        this.q = q;
        this.t = new int[num_candidates][num_candidates];
        this.s = new BigInteger[num_candidates][num_candidates];
        for (int i = 0; i < num_candidates; i++) {
            for (int j = 0; j < num_candidates; j++) {
                s[i][j] = BigInteger.ZERO;
            }
        }
    }

    // get the tally
    public int[][] t() {
        return this.t;
    }

    // get the sum of randomness
    public BigInteger[][] s() {
        return this.s;
    }

    // update the tally
    public void updateTally(int[][] vote) {
        for (int i = 0; i < vote.length; i++) {
            for (int j = 0; j < vote[i].length; j++) {
                t[i][j] += vote[i][j];
            }
        }
    }

    // update the sum of randomness
    public void updateSum(BigInteger[][] x) {
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x[i].length; j++) {
                s[i][j] = s[i][j].add(x[i][j]).mod(p);
            }
        }
    }

    // display the entire bulletin board
    public void display() {
        System.out.println("----------------------------------------------------");
        System.out.println("BULLETIN BOARD");
        System.out.println("----------------------------------------------------");
        System.out.println("PARAMETERS");
        System.out.println("g0 : " + g0.toString());
        System.out.println("g1 : " + g1.toString());
        System.out.println("p  : " + p.toString());
        System.out.println("q  : " + q.toString());
        System.out.println("----------------------------------------------------");
        System.out.println("TALLIES");
        System.out.println("t :");
        for (int i = 0; i < t.length; i++) {
            for (int j = 0; j < t[i].length; j++) {
                System.out.print(t[i][j] + " ");
            }
            System.out.println("");
        }
        System.out.println("s :");
        for (int i = 0; i < s.length; i++) {
            for (int j = 0; j < s[i].length; j++) {
                if (i == j) {
                    System.out.print(s[i][j] + " ");
                } else {
                    String element = s[i][j].toString();
                    String first = element.substring(0, 4);
                    System.out.print(first + " ");
                }

            }
            System.out.println("");
        }
        System.out.println("----------------------------------------------------");
    }

}
