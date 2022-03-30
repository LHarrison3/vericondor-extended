package vericondo;

import java.util.*;

/**
This class pairs two candidates together.
It is helpful for implementing the Ranked Pairs method.
*/
public class CandidatePair implements Comparable<CandidatePair> {

    int fst;
    int snd;
    int[][] t;
    ArrayList<Integer> tiebreaker;

    public CandidatePair(int fst, int snd, int[][] t, ArrayList<Integer> tiebreaker) {
        this.fst = fst;
        this.snd = snd;
        this.t = t;
        this.tiebreaker = tiebreaker;
    }

    public int fst() {
        return this.fst;
    }

    public int snd() {
        return this.snd;
    }

    @Override
    public String toString() {
        return "(" + this.fst + "," + this.snd + ")";
    }

    // compares two pairs as specified by the Ranked Pairs algorithm
    @Override
    public int compareTo(CandidatePair pair) {
        int fst = pair.fst();
        int snd = pair.snd();

        // apply the tie breaking algorithm
        if (t[this.fst][this.snd] > t[fst][snd]) {
            return 1;
        } else if (t[this.fst][this.snd] < t[fst][snd]) {
            return -1;
        } else {
            // use the tiebreaker to break the tie
            if (tiebreaker.indexOf(this.fst) < tiebreaker.indexOf(fst)) {
                return 1;
            } else if (tiebreaker.indexOf(this.fst) > tiebreaker.indexOf(fst)) {
                return -1;
            } else {
                if (tiebreaker.indexOf(this.snd) > tiebreaker.indexOf(snd)) {
                    return 1;
                } else if (tiebreaker.indexOf(this.snd) < tiebreaker.indexOf(snd)) {
                    return -1;
                } else {
                    // the pairs we are comparing must be the same pair
                    return 0;
                }
            }
        }
    }

}
