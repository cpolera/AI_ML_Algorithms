package com.implementations.models.QuadraticAssignment;

import static com.implementations.models.QuadraticAssignment.QAPSolver.log;

public class QAPReporter {

    public static void consoleReport(QAPSolver qapSolver) {
        double seconds = (double) qapSolver.getRunSolutionDuration() / 1000000000.0;
        log("Time to calculate in seconds: " + seconds, 1);
        log("Min cost : " + qapSolver.getMin() + " ::::: Permutation is "
                + qapSolver.getPermutationString(qapSolver.getBestPermutation()), 1);
        log("Max cost : " + qapSolver.getMax() + " ::::: Permutation is "
                + qapSolver.getPermutationString(qapSolver.getWorstPermutation()), 1);
        log("Total permutations ran: " + qapSolver.countResults(qapSolver.getResults()), 1);
    }
}
