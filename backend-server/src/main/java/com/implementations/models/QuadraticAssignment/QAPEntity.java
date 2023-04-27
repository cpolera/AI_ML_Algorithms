package com.implementations.models.QuadraticAssignment;

import com.controllers.Status;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Scanner;

import static com.implementations.models.QuadraticAssignment.QAPReporter.consoleReport;

@Entity
public class QAPEntity {

    private @Id @GeneratedValue Long id;

    private String filename;
    private int[] flowMatrixFlattened;
    private int[] distanceMatrixFlattened;

    private int[] lowestCostPermutation;
    private int[] highestCostPermutation;
    private long runSolutionDuration;

    private Status status = Status.NOT_RUN;

    public QAPEntity(){};

    public QAPEntity(String filename) throws FileNotFoundException {
        this.filename = filename;
        this.readInData();
    }

    /**
     * Setup the flow and distance matrices
     * File should be txt with data as so:
     * --
     * 2
     *
     * 0 1
     * 1 0
     *
     * 3 5
     * 3 9
     * --
     * @throws FileNotFoundException
     */
    public void readInData() throws FileNotFoundException {
        Scanner sc = new Scanner(new File(this.filename));
        int size = sc.nextInt();
        int[][] inputs = new int[2][size * size];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < size * size; j++) {
                inputs[i][j] = sc.nextInt();
            }
        }

        this.flowMatrixFlattened = inputs[1];
        this.distanceMatrixFlattened = inputs[0];

        sc.close();
    }

    public void solve() throws Exception {
        QAPSolver qapSolver = new QAPSolver(this.filename);
        
        qapSolver.runSolution(flowMatrixFlattened, distanceMatrixFlattened);
        consoleReport(qapSolver);
        setRunData(qapSolver);
    }

    // Method to set data to be stored that we dont want to generate every time
    private void setRunData(QAPSolver qapSolver){
        this.runSolutionDuration = qapSolver.getRunSolutionDuration();
        this.lowestCostPermutation = qapSolver.getBestPermutation();
        this.highestCostPermutation = qapSolver.getWorstPermutation();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setFlowMatrixFlattened(int[] flowMatrixFlattened) {
        this.flowMatrixFlattened = flowMatrixFlattened;
    }

    public void setDistanceMatrixFlattened(int[] distanceMatrixFlattened) {
        this.distanceMatrixFlattened = distanceMatrixFlattened;
    }

    public void setLowestCostPermutation(int[] lowestCostPermutation) {
        this.lowestCostPermutation = lowestCostPermutation;
    }

    public void setHighestCostPermutation(int[] highestCostPermutation) {
        this.highestCostPermutation = highestCostPermutation;
    }

    public void setRunSolutionDuration(long runSolutionDuration) {
        this.runSolutionDuration = runSolutionDuration;
    }

    public Long getId() {
        return this.id;
    }

    public String getFilename(){
        return this.filename;
    }

    public int[] getFlowMatrixFlattened() {
        return flowMatrixFlattened;
    }

    public int[] getDistanceMatrixFlattened() {
        return distanceMatrixFlattened;
    }

    public int[] getLowestCostPermutation() {
        return lowestCostPermutation;
    }

    public int[] getHighestCostPermutation() {
        return highestCostPermutation;
    }

    public long getRunSolutionDuration() {
        return runSolutionDuration;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }


    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.filename, this.runSolutionDuration);
    }

    @Override
    public String toString() {
        return "QAPEntity{" + "id=" + this.id + ", filename='" + this.filename + '}';
    }
}
