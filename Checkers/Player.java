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
public class Player {
    Checker.Color myColor;
    
    public Player(Checker.Color c) {
        myColor = c;
    }
    
    public Board nextBoard(Board board) {
        return max(board, 0);
    }
    
    public Move nextMove(Board board) {
        return max(board, 0).getLastMove();
    }
    
    private Board max(Board board, int depth) {
        LinkedList<Board> adjacentBoards = new LinkedList<Board>();
        
        for (Move m : board.legalMoves(myColor))
            adjacentBoards.add(board.nextBoard(m));
        
        return maxEval(adjacentBoards, depth);
    }
    
    private Board maxEval(LinkedList<Board> boards, int depth) {
        Board max = null;
        for (Board b : boards) {
            int score;
            if (maxDepth(depth))
                score = b.evaluate(this);
            else
                score = min(b, depth + 1).evaluate(this);
            
            if (max == null || score > max.evaluate(this))
                max = b;
        }
        return max;
    }
    
    private Board min(Board board, int depth) {
        LinkedList<Board> adjacentBoards = new LinkedList<Board>();
        
        for (Move m : board.legalMoves(myColor))
            adjacentBoards.add(board.nextBoard(m));
        
        return minEval(adjacentBoards, depth);
    }
    
    private Board minEval(LinkedList<Board> boards, int depth) {
        Board min = null;
        for (Board b : boards) {
            int score;
            if (maxDepth(depth))
                score = b.evaluate(this);
            else
                score = max(b, depth + 1).evaluate(this);
            
            if (min == null || score < min.evaluate(this))
                min = b;
        }
        return min;
    }
    
    private boolean maxDepth(int depth) {
        return depth >= 6;
    }
    
    
    public int compare(Move m1, Move m2, Board b) {
        return evaluate(b.nextBoard(m1)) - evaluate(b.nextBoard(m2));
    }
    
    public int evaluate(Board board) {
        return board.pieceAdvantage(myColor);
    }

}
