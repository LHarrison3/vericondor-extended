package vericondo;

public class Program {

    // try out the class
    public static void main(String[] args) {

        int num_candidates = 4;

        BaseElection election = new BaseElection("Sample Election", "11/05/21", num_candidates, 112);

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
