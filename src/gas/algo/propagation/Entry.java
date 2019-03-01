/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gas.algo.propagation;

/**
 *
 * @author Martin
 */
public class Entry {
    
    private long runtime;
    private int leafReductions;
    private int serialReductions;
    private int parallelReductions;
    private String name;
    private int numberOfEdges;
    private int numberOfNodes;
    private int numberOfSinks;
    private int numberOfSources;   
    private int numberOfTerminals;  
    private int reducedNumberOfEdges;
    private int reducedNumberOfNodes;
    private int reducedNumberOfSinks;
    private int reducedNumberOfSources;
    private String scenarioName;

    public String getScenarioName() {
        return scenarioName;
    }

    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }
    
    

    public int getNumberOfTerminals() {
        return numberOfTerminals;
    }

    public void setNumberOfTerminals(int numberOfTerminals) {
        this.numberOfTerminals = numberOfTerminals;
    }

    
    
    public long getRuntime() {
        return runtime;
    }

    public void setRuntime(long runtime) {
        this.runtime = runtime;
    }

    public int getLeafReductions() {
        return leafReductions;
    }

    public void setLeafReductions(int leafReductions) {
        this.leafReductions = leafReductions;
    }

    public int getSerialReductions() {
        return serialReductions;
    }

    public void setSerialReductions(int serialReductions) {
        this.serialReductions = serialReductions;
    }

    public int getParallelReductions() {
        return parallelReductions;
    }

    public void setParallelReductions(int parallelReductions) {
        this.parallelReductions = parallelReductions;
    }    
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfEdges() {
        return numberOfEdges;
    }

    public void setNumberOfEdges(int numberOfEdges) {
        this.numberOfEdges = numberOfEdges;
    }

    public int getNumberOfNodes() {
        return numberOfNodes;
    }

    public void setNumberOfNodes(int numberOfNodes) {
        this.numberOfNodes = numberOfNodes;
    }

    public int getNumberOfSinks() {
        return numberOfSinks;
    }

    public void setNumberOfSinks(int numberOfSinks) {
        this.numberOfSinks = numberOfSinks;
    }

    public int getNumberOfSources() {
        return numberOfSources;
    }

    public void setNumberOfSources(int numberOfSources) {
        this.numberOfSources = numberOfSources;
    }

    public int getReducedNumberOfEdges() {
        return reducedNumberOfEdges;
    }

    public void setReducedNumberOfEdges(int reducedNumberOfEdges) {
        this.reducedNumberOfEdges = reducedNumberOfEdges;
    }

    public int getReducedNumberOfNodes() {
        return reducedNumberOfNodes;
    }

    public void setReducedNumberOfNodes(int reducedNumberOfNodes) {
        this.reducedNumberOfNodes = reducedNumberOfNodes;
    }

    public int getReducedNumberOfSinks() {
        return reducedNumberOfSinks;
    }

    public void setReducedNumberOfSinks(int reducedNumberOfSinks) {
        this.reducedNumberOfSinks = reducedNumberOfSinks;
    }

    public int getReducedNumberOfSources() {
        return reducedNumberOfSources;
    }

    public void setReducedNumberOfSources(int reducedNumberOfSources) {
        this.reducedNumberOfSources = reducedNumberOfSources;
    }
    
    
}
