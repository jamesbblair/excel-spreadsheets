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
    private Checker.Color myColor;
    private int time;
    private int defaultDepth = 6;
    private long startTime;
    private int maxTime = 10;
    private Evaluation evalFunc = new Evaluation();
    private Board curBoard;
    private int lrnCount;
    
    

    
//    public static double evaluate(Board board) {
////        if (myColor.equals(Checker.Color.white))
////            return board.pieceAdvantage(board.turn());
//        
//        return 20 * board.pieceAdvantage(board.turn()) 
//                + 4 * board.noBackRow(board.turn()) 
//                + 3 * board.pawnAdvancement(board.turn()) 
//                + 1 * board.kingAttack(board.turn())
//                + 1 * board.pullThrough(board.turn())
//                + 50 * board.pieceRatio(board.turn()); // maybe a "trap" factor where a piece waits for the opponent to move close
//        // move away trap factor, your in a diagnal below (if youre white), theyre above, you move away and theyre trapped
//        // ^^anticipate the other team using a pull through against you
//    }
    
    
    
    public Player(Checker.Color c) {
        myColor = c;
        time = 600; // 600 right?
    }
    
    
    public Board nextBoard(Board board) {
        lrnCount = 0;
        startTime = System.nanoTime();
        curBoard = board;
        Board b = max(board, depth(board), Integer.MIN_VALUE, Integer.MAX_VALUE);
        System.out.println(myColor+"'s old eval: "+evalFunc);
        if (b.getEval() != null)
            evalFunc = b.getEval();
        System.out.println("\nnew eval: "+evalFunc);
        System.out.println("lrnCount: "+lrnCount);
        
        return b;
    }
    
    public Move nextMove(Board board) {
        lrnCount = 0;
        startTime = System.nanoTime();
        curBoard = board;
        Board b = max(board, depth(board), Integer.MIN_VALUE, Integer.MAX_VALUE);
        System.out.println(myColor+"'s old eval: "+evalFunc);
        if (b.getEval() != null)
            evalFunc = b.getEval();
        System.out.println("\nnew eval: "+evalFunc);
        System.out.println("lrnCount: "+lrnCount);
        
        return b.getLastMove();
    }
    
    /**
     * right now we have blind pruning (alpha-beta).  see if we can make "smart"
     * pruning, or hueristic based pruning
     * Idea: consider only moves of "idle" pieces/kings
     * 
     * @param board
     * @param depth
     * @param alpha
     * @param beta
     * @return 
     */
    private Board max(Board board, int depth, int alpha, int beta) {
        
        LinkedList<Board> adjacentBoards = new LinkedList<Board>();
        
        for (Move m : board.legalMoves(myColor))
            adjacentBoards.add(Board.nextBoard(m, board));
        
        return maxEval(adjacentBoards, depth, alpha, beta);
    }
    
    private Board maxEval(LinkedList<Board> boards, int depth, int alpha, int beta) {
        Board maxBoard = null;
        double maxScore = Double.MIN_VALUE;
        
        // should do some preprocessing: order boards to make the alpha-beta pruning more efficient
        
        for (Board b : boards) {
            double score;// = evalFunc.eval(b);
            if (!maxDepth(depth, b)) {
                Board bf = min(b, depth - 1, alpha, beta);
                score = evalFunc.eval(bf);
                b.setEval(bf.getEval());
            } else {
                Evaluation e = Evaluation.evalAndLearn(b, curBoard, evalFunc);
                lrnCount++;
                score = e.eval(b);
                b.setEval(e);
            }
            
            if (score >= beta)
                return b;
            
            if (maxBoard == null || score > maxScore) {
                maxBoard = b;
                maxScore = score;
            }
        }
        return maxBoard;
    }
    
    private Board min(Board board, int depth, int alpha, int beta) {
        LinkedList<Board> adjacentBoards = new LinkedList<Board>();
        
        for (Move m : board.legalMoves(Checker.Color.opposite(myColor)))
            adjacentBoards.add(Board.nextBoard(m, board));
        
        return minEval(adjacentBoards, depth, alpha, beta);
    }
    
    private Board minEval(LinkedList<Board> boards, int depth, int alpha, int beta) {
        Board minBoard = null;
        double minScore = Double.MAX_VALUE;
        
        for (Board b : boards) {
            double score;// = evalFunc.eval(b);
            if (!maxDepth(depth, b)) {
                Board bf = max(b, depth - 1, alpha, beta);
                score = evalFunc.eval(bf);
                b.setEval(bf.getEval());
            } else {
//                Evaluation e = Evaluation.evalAndLearn(b, curBoard, evalFunc);
                score = evalFunc.eval(b); // learning need not take place from min's side
//                b.setEval(e);
//                System.out.println("min eval");
            }
            
            if (score <= alpha)
                return b;
            
            if (minBoard == null || score < minScore) {
                minBoard = b;
                minScore = score;
            }
        }
        return minBoard;
    }
    
    public long runningTime() {
        return System.nanoTime() - startTime;
    }
    
    private boolean timeUp() {
        return false;
//        return runningTime() > maxTime * 1000000000L;
    }
    
    public int time() {
        return time;
    }
    
    public void setTime(int t) {
        time = t;
    }
    
    private boolean maxDepth(int depth, Board b) {
//        if (timeUp())
//            System.out.println("cut off at a depth of "+(defaultDepth - depth));
        return depth <= 0 || b.noMoves(b.turn()) || timeUp(); // includes wins.  this is so we dont king any1 midmove
    }
    
    private int depth(Board b) {
//        if (b.noPieces() < 4)
//            return 10;
//        if (b.noPieces(myColor) < 4)
//            return 8;
        return defaultDepth;
    }
    
    
    public static void main(String[] args) {
        Player p1 = new Player(Checker.Color.black);
        Player p2 = new Player(Checker.Color.white);
        Board b = new Board();
        System.out.println(b);
        Move m = null;
        
        while (!b.noMoves(Checker.Color.white) && !b.noMoves(Checker.Color.black)) {
//            System.out.println("1st"+b.turn()); // hmm... turns not working
            
            b = p1.nextBoard(b);
            m = b.getLastMove();
            System.out.println(m.outputForm());
            System.out.println(b);
            
            pause();
            
//            b.switchTurn();
//            System.out.println("2nd"+b.turn());
            
            if (b.noMoves(Checker.Color.white)) {
                System.out.println("black wins!");
                return;
            }
            
            b = p2.nextBoard(b);
            m = b.getLastMove();
            System.out.println(m.outputForm());
            System.out.println(b);
            
            pause();
            
//            System.out.println("3rd"+b.turn());
            
            if (b.noMoves(Checker.Color.black)) {
                System.out.println("white wins!");
                return;
            }
        }
    }
    
    public static synchronized void pause() {
        try {
            Player.class.wait(1000L);
        } catch (InterruptedException ex) {}
    }
}
