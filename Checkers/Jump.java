/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Checkers;

/**
 *
 * @author iandardik
 */
public class Jump {
    private int from;
    private int to;
    private int over;
    
    public Jump(int f, int t, int o) {
        from = f;
        to = t;
        over = o;
    }
    
    public int from() { return from; }
    public int to() { return to; }
    public int over() { return over; }
}
