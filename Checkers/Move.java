/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Checkers;
import java.util.LinkedList;

/**
 *
 * @author iandardik
 */
public class Move {
    private int from;
    private int to;
    private LinkedList<Jump> jumps;
    
    public Move(int f, int t) {
        jumps = new LinkedList<Jump>();
        from = f;
        to = t;
    }
    
    public Move(int f, int t, int o) {
        jumps = new LinkedList<Jump>();
        from = f;
        to = t;
        addJump(new Jump(f, t, o));
    }
    
    public Move(Jump j) {
        jumps = new LinkedList<Jump>();
        from = j.from();
        to = j.to();
        addJump(j);
    }
    
    public void addJump(Jump j) {
        to = j.to();
        jumps.add(j);
    }
    
    public static Move addJumpTo(Move m, Jump j) {
        Move newMove = new Move(m.from(), m.to());
        newMove.jumps = m.jumps(); // hopefully not a reference
        newMove.addJump(j);
        
        return newMove;
    }
    
    public int from() { return from; }
    public int to() { return to; }
    public boolean isJump() { return !jumps.isEmpty(); }
    public LinkedList<Jump> jumps() { return jumps; }

    public String outputForm() {
        StringBuilder sb = new StringBuilder();

        sb.append("(").append(y(from)).append(":").append(x(from)).append(")");

        for (Jump j : jumps)
            sb.append(":(").append(y(j.to())).append(":").append(x(j.to())).append(")");

        if (jumps.isEmpty())
            sb.append(":(").append(y(to)).append(":").append(x(to)).append(")");

        return sb.toString();
     }

     public static int y(int p) {
         int inv = 35 - p;
         return (inv - (inv / 9)) / 4;
     }

     public static int x(int p) {
         int mod = p % 9;
         return 2 * mod - 1 - 9 * (mod / 5);
     }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(from).append(":"); /* from needs to be coordinates (A:B) */
        for (Jump j : jumps)
            sb.append(j.to()).append(" over ").append(j.over()).append(" to ").append(j.to());
        if (jumps.isEmpty())
            sb.append(to);
        
        return sb.toString();
    }
}
