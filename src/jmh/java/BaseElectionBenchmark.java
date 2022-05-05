package vericondo;

import java.util.*;
import java.util.concurrent.TimeUnit;

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

public class BaseElectionBenchmark {

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
        public BaseElection election;

        @Setup(Level.Invocation)
        public void setUp() {

            int num_candidates = Integer.parseInt(candidates_val);
            int bits = Integer.parseInt(security_bits);
            int num_votes = 50;

            // set election object
            election = new BaseElection("Benchmarking Election", "11/05/21", num_candidates, bits);

            // generate some random votes
            votes = new int[num_votes][num_candidates][num_candidates];
            for (int i = 0; i < num_votes; i++) {
                ArrayList<Integer> vote_list = new ArrayList<Integer>();
                for (int j = 0; j < num_candidates; j++) {
                    vote_list.add(j);
                    Collections.shuffle(vote_list);
                }
                int[] vote_array = new int[num_candidates];
                for (int k = 0; k < num_candidates; k++) {
                    vote_array[k] = vote_list.get(k);
                }
                votes[i] = election.convertToMatrix(vote_array);

                // add the ballot
                election.addBallot(votes[i], false);
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

        BaseElection e = state.election;

        int voter = e.addBallot(state.votes[0], false);
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

        BaseElection e = state.election;

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

        BaseElection e = state.election;

        BaseReceipt receipt = e.getBulletinBoard().getReceipt(0);

        boolean verified = e.verifyNIZKPSforReceipt(receipt);
        blackhole.consume(verified);

    }

}
