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

    public Move(String strMove) {
        String[] moves = strMove.split(":");
        from = map(moves[0], moves[1]);
        to = map(moves[moves.length-2], moves[moves.length-1]); // right?
        jumps = new LinkedList<Jump>();
        if (strJumps(from, to))
            for (int i = 0; i < moves.length - 2; i+=2) {
                int f = map(moves[i], moves[i+1]);
                int t = map(moves[i+2], moves[i+3]);
                int o = over(f, t);
                addJump(new Jump(f, t, o));
            }
    }

    private int map(String l, String r) {
        int y = Integer.parseInt(l.substring(1, 2));
        int x = Integer.parseInt(r.substring(0,1));
        return map(y,x);
    }

    private int map(int y, int x) {
        int inv = 7 - y;
        int base = 8 * (inv / 2) + (inv / 2);
        int shift = x/2 + 1 + 4*(1 - x % 2);
        return base + shift;
    }

    private boolean strJumps(int f, int t) {
        int d = Math.abs(f - t);
        return d != 4 && d != 5;
    }

    private int over(int f, int t) {
        return (f + t) / 2;
    }

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
        StringBuilder sb = new StringBuilder("Move from ");
        sb.append(from);
        for (Jump j : jumps)
            sb.append(" to ").append(j.to()).append(" over ").append(j.over());
        if (jumps.isEmpty())
            sb.append(" to ").append(to);

        return sb.toString();
    }
}