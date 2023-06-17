package vericondo;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.math.BigInteger;


/**
This class represents votes/matrices containing indifferences
*/
public class IndiffVote {

    // votes have a component for strict preferences and a component for indifferences
    private int[][] preferences;
    private int[][] indifferences;

    public IndiffVote(int[][] preferences, int[][] indifferences) {
        this.preferences = preferences;
        this.indifferences = indifferences;
    }

    public int[][] getPreferences() {
        return this.preferences;
    }

    public int[][] getIndifferences() {
        return this.indifferences;
    }

    @Override
    public String toString() {

        int[][] combined = new int[preferences.length][preferences.length];
        for (int i = 0; i < preferences.length; i++) {
            for (int j = 0; j < preferences.length; j++) {
                combined[i][j] = preferences[i][j] + indifferences[i][j];
            }
        }

        return Arrays.deepToString(combined);

    }

}