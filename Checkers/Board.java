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
public class Board {
    private Checker[] board;
    private Checker.Color turn;
    private Move lastMove;
    private int eval;
    
    public Board() {
        board = new Checker[36];
        turn = Checker.Color.black;
        lastMove = null;
        eval = -9999;
        initCheckers();
    }
    
    public int evaluate(Player p) {
        if (eval != -9999)
            return eval;
        return p.evaluate(this);
    }
    
    public Checker.Color turn() {
        return turn;
    }
    
    public int pieceAdvantage(Checker.Color color) {
        int sum = 0;
        for (int i = 1; i < board.length; i++)
            if (isCheckerAt(i))
                if (checkerAt(i).color().equals(color))
                    sum++;
                 else
                    sum--;
        return sum;
    }
    
    public LinkedList<Move> legalMoves(Checker.Color color) {
        LinkedList<Move> moves = new LinkedList<Move>();
        LinkedList<Move> jumps = new LinkedList<Move>();
        
        for (int i = 1; i < board.length; i++)
            if (isCheckerAt(i) && checkerAt(i).color().equals(color))
                for (Move m : getAllMoves(checkerAt(i), i))
                    if (m.isJump())
                        jumps.add(m);
                    else
                        moves.add(m);
        
        if (!jumps.isEmpty())
            return jumps;
        return moves;
    }
    
    public Board nextBoard(Move move) {
        Board newBoard = new Board();
        newBoard.board = new Checker[36];
        for (int i = 1; i < 36; i++)
            newBoard.board[i] = this.board[i];
        newBoard.turn = this.turn;
        
        Checker c = newBoard.removeCheckerFrom(move.from());
        for (Jump j : move.jumps())
            newBoard.removeCheckerFrom(j.over());
        newBoard.placeCheckerAt(move.to(), c);
        newBoard.setLastMove(move);
        newBoard.switchTurn();
        
        return newBoard;
    }
    
    public void setLastMove(Move m) {
        lastMove = m;
    }
    public Move getLastMove() {
        return lastMove;
    }
    
    /**
     * operating on a board that is in the middle of a move, this adds another
     * jump
     * @param j
     * @return 
     */
    private Board midMoveNextBoard(Jump j) {
        Board newBoard = new Board();
        newBoard.board = new Checker[36];
        for (int i = 1; i < 36; i++)
            newBoard.board[i] = this.board[i];
        newBoard.turn = this.turn;
        
        Checker c = newBoard.removeCheckerFrom(j.from());
        newBoard.removeCheckerFrom(j.over());
        newBoard.placeCheckerAt(j.to(), c);
        
        return newBoard;
    }
    
    
    private void initCheckers() {
        for (int i = 1; i <= 13; i++)
            if (i % 9 != 0)
                board[i] = new Checker(Checker.Color.black);
        for (int i = 23; i <= 35; i++)
            if (i % 9 != 0)
                board[i] = new Checker(Checker.Color.white);
    }
    
    public void switchTurn() {
        if (turn == Checker.Color.black || turn.equals(Checker.Color.black))
            turn = Checker.Color.white;
        turn = Checker.Color.black;
    }
    
    private Checker removeCheckerFrom(int loc) {
        Checker c = board[loc];
        board[loc] = null;
        return c;
    }
    
    private void placeCheckerAt(int loc, Checker c) {
        board[loc] = c;
    }
    
    private boolean isCheckerAt(int loc) {
        return board[loc] != null;
    }
    
    private Checker checkerAt(int loc) {
        return board[loc];
    }
    
    private LinkedList<Move> getAllMoves(Checker c, int from) {
        LinkedList<Move> moves = new LinkedList<Move>();
        int to;
        
        if (isValidLocation(to = moveLeft(from, c.color())) && isEmpty(to))
            moves.add(new Move(from, to));
        if (isValidLocation(to = moveRight(from, c.color())) && isEmpty(to))
            moves.add((new Move(from, to)));
        if (c.isKing()) {
            if (isValidLocation(to = moveLeft(from, opposite(c.color()))) && isEmpty(to))
                moves.add(new Move(from, to));
            if (isValidLocation(to = moveRight(from, opposite(c.color()))) && isEmpty(to))
                moves.add((new Move(from, to)));
        }
        
        moves.addAll(jumps(c, from, c.color(), this, null));
        
        return moves;
    }
    
    private LinkedList<Move> jumps(Checker c, int loc, Checker.Color color, Board b, Move midMove) {
        LinkedList<Move> moves = new LinkedList<Move>();
        int to;
        int over;
        
        if (b.hasJump(to = jumpLeft(loc, color), over = moveLeft(loc, color), c)) { // left moves
            Jump theJump = new Jump(loc, to, over);
            Move theNewMove = generateMove(midMove, theJump);
            
            LinkedList<Move> additionalJumps = jumps(c, to, c.color(), b.midMoveNextBoard(theJump), theNewMove);
            if (additionalJumps.isEmpty())
                moves.add(theNewMove);
            else
                moves.addAll(additionalJumps);
        }
        if (b.hasJump(to = jumpRight(loc, color), over = moveRight(loc, color), c)) { // right moves
            Jump theJump = new Jump(loc, to, over);
            Move theNewMove = generateMove(midMove, theJump);
            
            LinkedList<Move> additionalJumps = jumps(c, to, c.color(), b.midMoveNextBoard(theJump), theNewMove);
            if (additionalJumps.isEmpty())
                moves.add(theNewMove);
            else
                moves.addAll(additionalJumps);
        }
        
        if (c.isKing() && c.color().equals(color)) // kings backwards moves
            moves.addAll(jumps(c, loc, opposite(color), b, midMove));
        
        return moves;
    }
    
    private Move generateMove(Move midMove, Jump j) {
        Move nextMove;
        if (midMove == null)
            nextMove = new Move(j);
        else
            nextMove = Move.addJumpTo(midMove, j); // make sure this is a copy
        
        return nextMove;
    }
    
    private boolean hasJump(int to, int over, Checker c) {
        return isValidLocation(to) && isEmpty(to) && isCheckerAt(over) && !checkerAt(over).color().equals(c.color());
    }
    
    private boolean isValidLocation(int loc) {
        if (loc % 9 == 0 || loc < 1 || loc > 35)
            return false;
        return true;
    }
    
    private boolean isEmpty(int loc) {
        return !isCheckerAt(loc);
    }
    
    private int moveLeft(int loc, Checker.Color c) {
        if (c.equals(Checker.Color.black))
            return loc + 4;
        return loc - 5;
    }
    
    private int moveRight(int loc, Checker.Color c) {
        if (c.equals(Checker.Color.black))
            return loc + 5;
        return loc - 4;
    }
    
    private int jumpLeft(int loc, Checker.Color c) {
        if (c.equals(Checker.Color.black))
            return loc + 8;
        return loc - 10;
    }
    
    private int jumpRight(int loc, Checker.Color c) {
        if (c.equals(Checker.Color.black))
            return loc + 10;
        return loc - 8;
    }
    
    private Checker.Color opposite(Checker.Color c) {
        if (c.equals(Checker.Color.black))
            return Checker.Color.white;
        return Checker.Color.black;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean shift = true;
        int count = 0;
        
        for (int i = 1; i < board.length; i++)
            if (i % 9 != 0) {
                if (count % 4 == 0) {
                    sb.append("\n");
                    if (shift) {
                        sb.append("   ");
                        shift = false;
                    } else
                        shift = true;
                }
                count++;
                if (isCheckerAt(i))
                    sb.append("   ").append(checkerAt(i).toString()).append("   ");
                else
                    sb.append("   X   ");
            }
        sb.append("\n");
        
        return sb.toString();
    }
}
