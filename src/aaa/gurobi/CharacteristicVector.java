/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aaa.gurobi;

import ds.graph.Identifiable;
import ds.graph.Node;

/**
 *
 * @author Martin
 */
public class CharacteristicVector<T extends Identifiable> {
    
    private long number;
    
    public CharacteristicVector(long number) {
        this.number = number;
    }
    
    public void setNumber(long number) {
        this.number = number;
    }
    
    public boolean contains(T element) {
        return ((1 << element.id()) & number) > 0;
    }
    
    public String toString() {
        return Long.toBinaryString(number);
    }
}
