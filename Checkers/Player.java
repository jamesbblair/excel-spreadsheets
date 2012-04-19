/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Checkers;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author iandardik
 */
public class Player {
    private Checker.Color myColor;
    private int time;

    public Player(Checker.Color c) {
        myColor = c;
        time = 600; // 600 right?
    }

    public Board nextBoard(Board board) {
        return max(board, depth(board));
    }

    public Move nextMove(Board board) {
        return max(board, depth(board)).getLastMove();
    }

    public int time() {
        return time;
    }

    public void setTime(int t) {
        time = t;
    }

    private Board max(Board board, int depth) {

        LinkedList<Board> adjacentBoards = new LinkedList<Board>();

        for (Move m : board.legalMoves(board.turn()))//myColor))
            adjacentBoards.add(board.nextBoard(m));

        return maxEval(adjacentBoards, depth);
    }

    private Board maxEval(LinkedList<Board> boards, int depth) {
        Board max = null;
        for (Board b : boards) {
            int score;
            if (maxDepth(depth, b))
                score = b.evaluate(this);
            else
                score = min(b, depth - 1).evaluate(this);

            if (max == null || score > max.evaluate(this))
                max = b;
        }
        return max;
    }

    private Checker.Color opposite(Checker.Color c) {
        if (c.equals(Checker.Color.black))
            return Checker.Color.white;
        return Checker.Color.black;
    }

    private Board min(Board board, int depth) {
        LinkedList<Board> adjacentBoards = new LinkedList<Board>();

        for (Move m : board.legalMoves(board.turn()))//opposite(myColor)))
            adjacentBoards.add(board.nextBoard(m));

        return minEval(adjacentBoards, depth);
    }

    private Board minEval(LinkedList<Board> boards, int depth) {
        Board min = null;
        for (Board b : boards) {
            int score;
            if (maxDepth(depth, b))
                score = b.evaluate(this);
            else
                score = max(b, depth - 1).evaluate(this);

            if (min == null || score < min.evaluate(this))
                min = b;
        }
        return min;
    }

    private boolean maxDepth(int depth, Board b) {
        return depth <= 0 || b.noMoves(b.turn()); // includes wins.  this is so we dont king any1 midmove
    }

    private int depth(Board b) {
//        if (b.noPieces() < 4)
//            return 10;
//        if (b.noPieces(myColor) < 4)
//            return 8;
        return 6;
    }

    public int evaluate(Board board) {
        return 4 * board.pieceAdvantage(myColor) + board.noBackRow(myColor);
    }

}