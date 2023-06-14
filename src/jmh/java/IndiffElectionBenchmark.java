package vericondo;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadLocalRandom;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.annotations.Setup;

public class IndiffElectionBenchmark {

    // TODO: change to work with indifferences

    @State(Scope.Benchmark)
    public static class ElectionState {

        // specified number of candidates for which to run the benchmarks on
        @Param({"2","3","4","5","6","7","8","9","10"})
        public String candidates_val;

        // n-bit security levels
        @Param({"112","128"})
        public String security_bits;

        // number of candidates as an integer
        public int num_candidates;

        // each benchmark gets multiple random votes
        public int[][][] votes;

        // an election so we can use methods from it for easier setup
        public IndiffElection election;

        @Setup(Level.Invocation)
        public void setUp() {

            int num_candidates = Integer.parseInt(candidates_val);
            int bits = Integer.parseInt(security_bits);
            int num_votes = 50;

            // set election object
            election = new IndiffElection("Benchmarking Election", "10/05/22", num_candidates, bits);

            // generate some random votes
            votes = new int[num_votes][num_candidates][num_candidates];
            for (int i = 0; i < num_votes; i++) {

                int n = num_candidates;
                ArrayList<Integer> sizes = new ArrayList<Integer>();
                while (n > 0) {
                    int next = ThreadLocalRandom.current().nextInt(1,n+1);
                    sizes.add(next);
                    n = n - next;
                }

                int[][] vote_array = new int[sizes.size()][];
                for (int j = 0; j < sizes.size(); j++) {
                    vote_array[j] = new int[sizes.get(j)];
                }

                Stack<Integer> perm = new Stack<Integer>();
                for (int j = 0; j < num_candidates; j++) {
                    perm.push(j);
                }
                Collections.shuffle(perm);

                for (int j = 0; j < vote_array.length; j++) {
                    for (int k = 0; k < vote_array[j].length; k++) {
                        vote_array[j][k] = perm.pop();
                    }
                }

                votes[i] = election.convertToMatrix(vote_array);
                election.addBallot(votes[i], new int[num_candidates][num_candidates], false);

            }

            // finalise the ballots
            for (int i = 0; i < num_votes; i++) {
                election.finaliseBallot(i, Type.CONFIRMED);
            }

        }

    }

    // benchmark creation of ballots for a single vote
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(value = 1, warmups = 3)
    @Measurement(iterations = 5, time = 250, timeUnit = TimeUnit.MILLISECONDS)
    @Warmup(iterations = 5, time = 250, timeUnit = TimeUnit.MILLISECONDS)
    public void benchmarkBallotCreation(Blackhole blackhole, ElectionState state) {

        IndiffElection e = state.election;

        int voter = e.addBallot(state.votes[0], new int[state.num_candidates][state.num_candidates], false);
        blackhole.consume(voter);
    }

    // benchmark verification of tallies on the bulletin board
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(value = 1, warmups = 3)
    @Measurement(iterations = 3, time = 250, timeUnit = TimeUnit.MILLISECONDS)
    @Warmup(iterations = 3, time = 250, timeUnit = TimeUnit.MILLISECONDS)
    public void benchmarkTallyVerification(Blackhole blackhole, ElectionState state) {

        IndiffElection e = state.election;

        boolean verified = e.verifyTallies();
        blackhole.consume(verified);

    }

    // benchmark verification of NIZKPs on the bulletin board for a single ballot
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(value = 1, warmups = 3)
    @Measurement(iterations = 5, time = 250, timeUnit = TimeUnit.MILLISECONDS)
    @Warmup(iterations = 5, time = 250, timeUnit = TimeUnit.MILLISECONDS)
    public void benchmarkNIZKPsVerification(Blackhole blackhole, ElectionState state) {

        IndiffElection e = state.election;

        IndiffReceipt receipt = e.getBulletinBoard().getReceipt(0);

        boolean verified = e.verifyNIZKPSforReceipt(receipt);
        blackhole.consume(verified);

    }

}
