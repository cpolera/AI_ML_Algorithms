package com.implementations.models.QuadraticAssignment;

import com.controllers.Status;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

import java.io.FileNotFoundException;
import java.util.Objects;

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

    @Transient
    private QAPSolver qapSolver;

    public QAPEntity(){};

    public QAPEntity(String filename) throws FileNotFoundException {
        this.filename = filename;
        this.qapSolver = new QAPSolver(this.filename);
        this.qapSolver.readInData();
        this.flowMatrixFlattened = this.qapSolver.getFlowMatrixFlattened();
        this.distanceMatrixFlattened = this.qapSolver.getDistanceMatrixFlattened();
    }

    public void solve() throws Exception {
        if(this.qapSolver == null){
            this.qapSolver = new QAPSolver(this.filename);
        }
        this.qapSolver.runSolution();
        consoleReport(this.qapSolver);
        setRunData();
    }

    // Method to set data to be stored that we dont want to generate every time
    private void setRunData(){
        this.runSolutionDuration = this.qapSolver.getRunSolutionDuration();
        this.lowestCostPermutation = this.qapSolver.getBestPermutation();
        this.highestCostPermutation = this.qapSolver.getWorstPermutation();
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
