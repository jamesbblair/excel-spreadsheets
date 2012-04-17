/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Checkers;

/**
 *
 * @author iandardik
 */
public class Checker {
    private Color color;
    private boolean isKing;
    
    public Checker(Color c) {
        color = c;
        isKing = false;
    }
    
    public Color color() { return color; }
    public boolean isKing() { return isKing; }
    public void setKing() { isKing = true; }
    
    public enum Color {
        white, black;

        @Override
        public String toString() {
            return this.name().substring(0, 1);
        }
    }
    
    @Override
    public String toString() {
        return color.toString();
    }
}
