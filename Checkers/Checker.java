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
    
    public static Checker copy(Checker c) {
        Checker ch = new Checker(c.color());
        if (c.isKing())
            ch.setKing();
        return ch;
    }
    
    public int value() {
        if (isKing)
            return 3;
        return 2;
    }
    
    public boolean isColor(Color c) { return color.equals(c); }
    public Color color() { return color; }
    public boolean isKing() { return isKing; }
    public void setKing() { isKing = true; }
    
    public enum Color {
        white, black, test;
        
        public static Color opposite(Color c) {
            if (c.equals(white))
                return black;
            return white;
        }
        
        public String toString() {
            if (this.equals(test))
                return " ";
            
            return this.name().substring(0, 1);
        }
    }
    
    @Override
    public String toString() {
        if (isKing)
            if (color.equals(Color.black))
                return "B";
            else
                return "W";
        
        return color.toString();
    }
}
